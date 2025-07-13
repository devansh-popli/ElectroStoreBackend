package com.lcwd.store;

import com.lcwd.store.dtos.UserDto;
import com.lcwd.store.entities.Role;
import com.lcwd.store.entities.User;
import com.lcwd.store.repositories.RoleRepository;
import com.lcwd.store.repositories.UserRepository;
import com.lcwd.store.services.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;
import java.util.Optional;

@SpringBootTest
public class UserServiceTest {

    @MockBean
    RoleRepository roleRepository;

    @MockBean
    UserRepository userRepository;

        @Autowired
    UserService userService;

    String roleId;
    User user;
    Role role;
    @Autowired
    ModelMapper modelMapper;
    @BeforeEach
    public void init(){
        roleId="awgfasfwsdqtgasfwqgsdtg";
        role=Role.builder().roleName("NORMAL").roleId("awgfasfwsdqtgasfwqgsdtg").build();
        user=User.builder().imageName("abc.jpg").userId("dummysuer").roles(Collections.singleton(role)).about("about user").password("admin").email("dummy@gmail.com").gender("male").name("dummy user").build();

    }
    @Test
    public void createUserTest(){

        Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);
        Mockito.when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        UserDto userDto=userService.createUser(modelMapper.map(user, UserDto.class));
        Assertions.assertNotNull(userDto);
        Assertions.assertEquals("dummy user",userDto.getName());
    }
    @Test
    public void deleteUserTest(){
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));
        userService.deleteUser("dummysuer");
        Mockito.verify(userRepository,Mockito.times(1)).delete(user);
    }
}
