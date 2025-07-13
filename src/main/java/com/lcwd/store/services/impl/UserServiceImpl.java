package com.lcwd.store.services.impl;

import com.lcwd.store.dtos.*;
import com.lcwd.store.entities.*;
import com.lcwd.store.exceptions.ResourceNotFoundException;
import com.lcwd.store.helper.HelperUtils;
import com.lcwd.store.repositories.ReferralRespository;
import com.lcwd.store.repositories.RoleRepository;
import com.lcwd.store.repositories.UserRepository;
import com.lcwd.store.services.OrderService;
import com.lcwd.store.services.UserService;
import io.netty.util.internal.StringUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Value("${user.profile.image.path}")
    private String imageUploadPath;

    @Value("${normal.role.id}")
    private String normalRoleId;
    @Value("${business.role.id}")
    private String businessRoleId;

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ReferralRespository referralRespository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OrderService orderService;
    @Autowired
    private ModelMapper modelMapper;

    public static String generateReferralCode() {
        // Generate a UUID and take the first 8 characters for the referral code
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        String userId = UUID.randomUUID().toString();
        userDto.setUserId(userId);
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        User user = dtoToEntity(userDto);
        Role role = roleRepository.findById(normalRoleId).get();
        if (userDto.getAccountType() != null && userDto.getAccountType().equalsIgnoreCase("Business")) {
            role = roleRepository.findById(businessRoleId).get();
        }
        user.setRoles(Collections.singleton(role));
        if (userDto.getAccountType() != null && userDto.getAccountType().equalsIgnoreCase("Business")) {
            Referral referral = new Referral();
            String referralId = UUID.randomUUID().toString();
            referral.setReferralid(referralId);
            referral.setReferralCode(generateReferralCode());
            referral.setUser(user);
            user.setReferral(referral);
            if(!StringUtil.isNullOrEmpty(user.getParentReferralCode())) {
                Optional<Referral> parentReferral = referralRespository.findByReferralCode(user.getParentReferralCode());
                if (parentReferral.isPresent()) {
                    User parentUser = parentReferral.get().getUser();
                    parentUser.setInActiveMoney(parentUser.getInActiveMoney() != null ? parentUser.getInActiveMoney() + 200 : 200);
                    if(!StringUtil.isNullOrEmpty(parentUser.getParentReferralCode())) {
                        Optional<Referral> parentsParentReferral = referralRespository.findByReferralCode(parentUser.getParentReferralCode());
                        if (parentsParentReferral.isPresent()) {
                            User parentsParentUser = parentsParentReferral.get().getUser();
                            parentsParentUser.setInActiveMoney(parentsParentUser.getInActiveMoney() != null ? parentsParentUser.getInActiveMoney() + 100 : 100);
                            userRepository.save(parentsParentUser);
                        }
                    }
                    userRepository.save(parentUser);
                }
            }

        }
        User savedUser = userRepository.save(user);
        UserDto newDto = entityToDto(savedUser);
        return newDto;
    }

    private UserDto entityToDto(User user) {

        return modelMapper.map(user, UserDto.class);
    }

    private User dtoToEntity(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }

    public void updateScreenPermissions(String userId, Set<ScreenPermissionRequest> screenPermissions) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Update or create ScreenPermission entities based on the request
        Set<ScreenPermission> screenPermissionEntities = screenPermissions.stream()
                .map(request -> {
                    ScreenPermission screenPermission = user.getScreenPermissions().stream()
                            .filter(existingPermission -> existingPermission.getScreenName().equals(request.getScreenName()))
                            .findFirst()
                            .orElse(new ScreenPermission());

                    screenPermission.setScreenName(request.getScreenName());
                    screenPermission.setCanRead(request.isCanRead());
                    screenPermission.setCanWrite(request.isCanWrite());
                    screenPermission.setCanUpdate(request.isCanUpdate());
                    screenPermission.setCanDelete(request.isCanDelete());
                    User user1=new User();
                    user1.setUserId(userId);
                    screenPermission.setUser(user1);
                    return screenPermission;
                })
                .collect(Collectors.toSet());

        user.setScreenPermissions(screenPermissionEntities);

        userRepository.save(user);
    }
    @Override
    public UserDto updateUser(UserDto userDto, String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
        user.setName(userDto.getName());
        user.setAbout(userDto.getAbout());
        user.setGender(userDto.getGender());
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty() && !user.getPassword().equals(userDto.getPassword())) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        user.setImageName(userDto.getImageName());
        User updatedUser = userRepository.save(user);
        UserDto updatedUserDto = entityToDto(updatedUser);
        return updatedUserDto;
    }

    @Override
    public UserDto getUserById(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user not found"));
        return entityToDto(user);
    }

    @Override
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("user not found"));
        return entityToDto(user);
    }

    @Override
    public void deleteUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
        // delete user profile image
        String fullPath = imageUploadPath + user.getImageName();
        Path path = Paths.get(fullPath);
        try {
            Files.delete(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        userRepository.delete(user);
    }

    @Override
    public PageableResponse<UserDto> getAllUsers(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = (sortDir.equalsIgnoreCase("asc")) ? (Sort.by(sortBy).ascending()) : (Sort.by(sortBy).descending());
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<User> page = userRepository.findAll(pageable);
        Map<String, List<User>> groupByParentCodeMap = page.getContent().stream()
                .collect(Collectors.groupingBy(user ->
                        user.getParentReferralCode() == null ? "UNKNOWN" : user.getParentReferralCode()));
        List<UserDto> usersWithEarningsHistory = page.getContent().stream()
                .map(user -> {
                    // Calculate earnings history for each user
                    List<EarningsHistoryDto> indirectCommissionList = null;
                    if (user.getReferral() != null) {
                        List<User> childUsers = groupByParentCodeMap.get(user.getReferral().getReferralCode());
                        if (childUsers != null) {
                            indirectCommissionList = childUsers.stream()
                                    .flatMap(childUser -> childUser.getOrdersByReferralUser().stream()
                                            .collect(Collectors.groupingBy(
                                                    order -> order.getOrderedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().withDayOfMonth(1),
                                                    Collectors.summingLong(Order::getOrderAmount)
                                            ))
                                            .entrySet().stream()
                                            .map(entry -> new EarningsHistoryDto(
                                                    Date.from(entry.getKey().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                                                    entry.getValue(),
                                                    Math.round(entry.getValue() * 0.01) // Calculate 2% commission
                                            ))
                                            .sorted((d1, d2) -> d2.getMonth().compareTo(d1.getMonth())) // Sort by month descending
                                    ).toList();
                        }
                    }
                    List<EarningsHistoryDto> earningsHistoryDirect = user.getOrdersByReferralUser().stream()
                            .collect(Collectors.groupingBy(
                                    order -> order.getOrderedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().withDayOfMonth(1),
                                    Collectors.summingLong(Order::getOrderAmount)
                            ))
                            .entrySet().stream()
                            .map(entry -> new EarningsHistoryDto(
                                    Date.from(entry.getKey().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                                    entry.getValue(),
                                    Math.round(entry.getValue() * 0.02) // Calculate 2% commission
                            ))
                            .sorted((d1, d2) -> d2.getMonth().compareTo(d1.getMonth())) // Sort by month descending
                            .collect(Collectors.toList());

                    // Map user entity to DTO and set earnings history
                    UserDto userDto = modelMapper.map(user, UserDto.class);
                    userDto.setDirectEarningsHistory(earningsHistoryDirect);
                    userDto.setIndirectEarningsHistory(indirectCommissionList);
                    return userDto;
                })
                .collect(Collectors.toList());
        PageableResponse<UserDto> response = new PageableResponse<>();
        response.setContent(usersWithEarningsHistory);
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setLastPage(page.isLast());
        return response;
    }

    @Override
    public List<UserDto> searchUser(String keyword) {
        List<User> users = userRepository.findByNameContaining(keyword);
        List<UserDto> userDtos = users.stream().map(user -> entityToDto(user)).collect(Collectors.toList());
        return userDtos;
    }

    @Override
    public Optional<User> getUserByEmailForGoogleAuth(String email) {
        return userRepository.findByEmail(email);
    }
    @Override
    public List<RoleDto> getRoles() {
        List<Role> roles = roleRepository.findAll();
        List<RoleDto> roleDtos = roles.stream().map(role -> roleToDto(role)).collect(Collectors.toList());
        return roleDtos;
    }
    private RoleDto roleToDto(Role user) {

        return modelMapper.map(user,RoleDto.class) ;
    }

    @Override
    public PageableResponse<UserDto> getAllUsersByRole(int pageNumber, int pageSize, String sortBy, String sortDir, String roleName) {
        Sort sort = (sortDir.equalsIgnoreCase("asc")) ? (Sort.by(sortBy).ascending()) : (Sort.by(sortBy).descending());
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Role role =roleRepository.findByRoleName(roleName).orElseThrow(()-> new ResourceNotFoundException("Role Not found with role name"+roleName));
        Page<User> page = userRepository.findByRoles(pageable,role);
        PageableResponse<UserDto> response = HelperUtils.getPageableResponse(page, UserDto.class);
        return response;
    }
}
