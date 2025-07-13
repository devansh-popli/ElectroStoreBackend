package com.lcwd.store.services;

import com.lcwd.store.dtos.PageableResponse;
import com.lcwd.store.dtos.RoleDto;
import com.lcwd.store.dtos.ScreenPermissionRequest;
import com.lcwd.store.dtos.UserDto;
import com.lcwd.store.entities.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserService {
    UserDto createUser(UserDto user);
    UserDto updateUser(UserDto user,String userId);
    UserDto getUserById(String userId);
    UserDto getUserByEmail(String email);
    void deleteUser(String userId) ;
    PageableResponse<UserDto> getAllUsers(int pageNumber, int pageSize, String sortBy, String sortDir);
    List<UserDto> searchUser(String keyword);
    void updateScreenPermissions(String userId, Set<ScreenPermissionRequest> screenPermissionsRequest);
    Optional<User> getUserByEmailForGoogleAuth(String email);


    List<RoleDto> getRoles();

    PageableResponse<UserDto> getAllUsersByRole(int pageNumber, int pageSize, String sortBy, String sortDir, String roleName);
}
