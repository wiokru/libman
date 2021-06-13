package com.wiokru.library;

import com.wiokru.library.controllers.AuthorsController;
import com.wiokru.library.entity.Author;
import com.wiokru.library.entity.Role;
import com.wiokru.library.entity.Roles;
import com.wiokru.library.entity.User;
import com.wiokru.library.repositories.AuthorRepository;
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

import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthorsControllerTest {

    @Autowired
    @InjectMocks
    private AuthorsController authorsController;

    private UserRepository userRepository = Mockito.mock(UserRepository.class);
    private RoleRepository roleRepository = Mockito.mock(RoleRepository.class);
    private AuthorRepository authorRepository = Mockito.mock(AuthorRepository.class);

    @MockBean
    private Model model;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        Author author = new Author("Adam Mickiewicz");
        Author author1 = new Author("Robert C. Martin");
        author1.setId((long) 1);
        Mockito.doThrow(DataIntegrityViolationException.class).when(authorRepository).save(author);
        Mockito.when(authorRepository.findById((long) 1)).thenReturn(Optional.of(author1));

        Role userRole = new Role(Roles.USER.toString());
        Mockito.when(roleRepository.findByName(Roles.USER.toString())).thenReturn(userRole);

        User user = new User("test@mail.com", "Test", "Test",
                "password", "Lublin", "789456123");
        user.addRole(roleRepository.findByName(Roles.USER.toString()));
        user.setId((long) 1);
        Mockito.when(userRepository.findById((long) 1)).thenReturn(Optional.of(user));
    }

    @Test
    public void addAuthorFormTest() {
        ModelAndView modelAndView = authorsController.addAuthorForm((long) 1);
        Assertions.assertEquals("add_author_form", modelAndView.getViewName());
    }

    @Test
    public void addANewAuthorTest() {
        ModelAndView modelAndView = authorsController.addNewAuthor((long) 1,
                "Olga Tokarczuk");
        Assertions.assertEquals("redirect:/user/1/manage_books", modelAndView.getViewName());
    }

    @Test
    public void addANewAuthorFailedTest() {
        ModelAndView modelAndView = authorsController.addNewAuthor((long) 1, "Adam Mickiewicz");
        Assertions.assertEquals("add_author_form", modelAndView.getViewName());
        Assertions.assertEquals(Const.SAVING_AUTHOR_ERROR_LOG, modelAndView.getModel().get("message"));
    }

    @Test
    public void deleteAuthorTest() {
        ModelAndView modelAndView = authorsController.deleteAuthor((long) 1, (long) 1);
        Assertions.assertEquals("redirect:/user/1/manage_books", modelAndView.getViewName());
    }

    @Test
    public void updateAuthorTest() {
        ModelAndView modelAndView = authorsController.updateAuthor((long) 1, (long) 1, "Robert C. Martin Update");
        Assertions.assertEquals("redirect:/user/1/manage_books", modelAndView.getViewName());
    }
}
