package com.wiokru.library;

import com.wiokru.library.controllers.SignUpController;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
public class SignUpControllerTest {

    @Autowired
    @InjectMocks
    private SignUpController signUpController;

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
        Mockito.when(roleRepository.findByName(Roles.USER.toString())).thenReturn(userRole);

        User user = new User("test@mail.com", "Test", "Test",
                "password", "Lublin", "789456123");
        user.addRole(roleRepository.findByName(Roles.USER.toString()));
        user.setId((long) 1);

        User userFailure = new User("test.fail@mail.com", "Test", "Failure",
                "password", "Lublin", "789456123");
        userFailure.addRole(roleRepository.findByName(Roles.USER.toString()));
        Mockito.when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        Mockito.doThrow(DataIntegrityViolationException.class).when(userRepository).save(userFailure);

    }

    @Test
    public void homeTest() {
        ModelAndView modelAndView = signUpController.signup(model);
        Assertions.assertEquals("signup", modelAndView.getViewName());
    }

    @Test
    public void registerUserTest() {
        ModelAndView modelAndView = signUpController.registerUser(model,
                response, "Test1", "Test1", "test1@mail.com", "City",
                "password1", "789456132");

        Assertions.assertEquals("redirect:/", modelAndView.getViewName());
    }

    @Test
    public void registerUserFailureTest() {
        ModelAndView modelAndView = signUpController.registerUser(model,
                response, "Test", "Failure", "test.fail@mail.com", "Lublin",
                "password", "789456123");

        Assertions.assertEquals("signup", modelAndView.getViewName());
        Assertions.assertEquals(Const.SIGNUP_ERROR_MESSAGE, modelAndView.getModelMap().get("error_message"));
    }
}
