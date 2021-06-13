package com.wiokru.library;

import com.wiokru.library.controllers.ManageUsersAdminController;
import com.wiokru.library.entity.Role;
import com.wiokru.library.entity.Roles;
import com.wiokru.library.entity.User;
import com.wiokru.library.repositories.RoleRepository;
import com.wiokru.library.repositories.UserRepository;
import com.wiokru.library.utils.Const;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
public class ManageUsersAdminControllerTest {

    @Autowired
    @InjectMocks
    private ManageUsersAdminController manageUsersAdminController;

    private UserRepository userRepository = Mockito.mock(UserRepository.class);
    private RoleRepository roleRepository = Mockito.mock(RoleRepository.class);

    @MockBean
    private Model model;

    @MockBean
    private HttpServletResponse response;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        Role userRole = new Role(Roles.USER.toString());
        userRole.setId((long) 1);
        Mockito.when(roleRepository.findByName(Roles.USER.toString())).thenReturn(userRole);
        Mockito.when(roleRepository.findById((long) 1)).thenReturn(Optional.of(userRole));
        Mockito.when(roleRepository.findAll()).thenReturn(Arrays.asList(userRole));

        User user1 = new User("test@mail.com", "Test", "Test",
                "password", "Lublin", "789456123");
        user1.addRole(roleRepository.findByName(Roles.USER.toString()));
        user1.setId((long) 1);

        User user2 = new User("test.user@mail.com", "Test", "User",
                "password", "Lublin", "789456123");
        user2.setId((long) 2);
        user2.addRole(roleRepository.findByName(Roles.USER.toString()));
        Mockito.when(userRepository.findById((long) 1)).thenReturn(Optional.of(user1));
        Mockito.when(userRepository.findById((long) 2)).thenReturn(Optional.of(user2));
        Mockito.when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(user1));
        Mockito.when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));
    }

    @Test
    public void manageUsersPageTest() {
        ModelAndView modelAndView = manageUsersAdminController.manageUsers((long) 1);
        Assertions.assertEquals("manage_users", modelAndView.getViewName());
        Assertions.assertEquals(2, ((List<User>) modelAndView.getModelMap().get("usersList")).size());
    }

    @Test
    public void searchUsersTest() {
        ModelAndView modelAndView = manageUsersAdminController.searchUsers((long) 1, "User");
        Assertions.assertEquals("manage_users", modelAndView.getViewName());
        Assertions.assertEquals(1, ((List<User>) modelAndView.getModelMap().get("usersList")).size());
    }

    @Test
    public void deleteUserTest() {
        ModelAndView modelAndView = manageUsersAdminController.deleteUser((long) 1, (long) 2);
        Assertions.assertEquals("redirect:/user/1/manage_users", modelAndView.getViewName());
    }

    @Test
    public void editUserFormTest() {
        ModelAndView modelAndView = manageUsersAdminController.editUser((long) 1, (long) 2);
        Assertions.assertEquals("user_edit_form", modelAndView.getViewName());
    }

    @Test
    public void saveEditUserTest() {
        ModelAndView modelAndView = manageUsersAdminController.saveEditUser((long) 1, (long) 2, "Test",
                "Update", "test.update@mail.com", "Lublin",
                "789456123", Arrays.asList((long) 1));
        Assertions.assertEquals("user_edit_form", modelAndView.getViewName());
        Assertions.assertEquals(Const.USER_UPDATED_SUCCESS, modelAndView.getModelMap().get("message"));
    }

    @Test
    public void saveEditUserFailureTest() {
        ModelAndView modelAndView = manageUsersAdminController.saveEditUser((long) 1, (long) 2, "Test",
                "Update", "test.update@mail.com", "Lublin",
                "789456123", Arrays.asList((long) 2));
        Assertions.assertEquals("user_edit_form", modelAndView.getViewName());
        Assertions.assertEquals(Const.USER_UPDATED_ERROR + "No value present", modelAndView.getModelMap().get("message"));
        Assertions.assertEquals(Boolean.FALSE, modelAndView.getModelMap().get("is_success"));
    }
}
