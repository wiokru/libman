package com.wiokru.library;

import com.wiokru.library.controllers.HomeController;
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

import java.util.Optional;


@SpringBootTest
@AutoConfigureMockMvc
public class HomeControllerTest {

    @Autowired
    @InjectMocks
    private HomeController homeController;

    private UserRepository userRepository = Mockito.mock(UserRepository.class);
    private RoleRepository roleRepository = Mockito.mock(RoleRepository.class);

    @MockBean
    private Model model;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        Role userRole = new Role(Roles.USER.toString());
        Mockito.when(roleRepository.findByName(Roles.USER.toString())).thenReturn(userRole);

        User user = new User("test@mail.com", "Test", "Test", "password", "Lublin", "789456123");
        user.addRole(roleRepository.findByName(Roles.USER.toString()));
        user.setId((long) 1);
        Mockito.when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
    }

    @Test
    public void homeTest() {
        ModelAndView modelAndView = homeController.home();
        Assertions.assertEquals("home", modelAndView.getViewName());
    }

    @Test
    public void loginTest() throws Exception {
        ModelAndView modelAndView = homeController.login(model, "test@mail.com", "password");
        Assertions.assertEquals("redirect:/user/1/home", modelAndView.getViewName());
    }

    @Test
    public void loginTestWrongPassword() throws Exception {
        ModelAndView modelAndView = homeController.login(model, "test@mail.com", "password1");
        Assertions.assertEquals(Const.INCORRECT_PASSWORD, modelAndView.getModel().get("error_message"));
    }

    @Test
    public void loginTestWrongEmail() throws Exception {
        ModelAndView modelAndView = homeController.login(model, "test1@mail.com", "password");
        Assertions.assertEquals(Const.USER_DONT_EXISTS, modelAndView.getModel().get("error_message"));
    }

}
