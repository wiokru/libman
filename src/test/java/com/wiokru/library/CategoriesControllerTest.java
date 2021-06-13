package com.wiokru.library;

import com.wiokru.library.controllers.CategoriesController;
import com.wiokru.library.entity.BookCategory;
import com.wiokru.library.entity.Role;
import com.wiokru.library.entity.Roles;
import com.wiokru.library.entity.User;
import com.wiokru.library.repositories.BookCategoryRepository;
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
public class CategoriesControllerTest {

    @Autowired
    @InjectMocks
    private CategoriesController categoriesController;

    private UserRepository userRepository = Mockito.mock(UserRepository.class);
    private RoleRepository roleRepository = Mockito.mock(RoleRepository.class);
    private BookCategoryRepository bookCategoryRepository = Mockito.mock(BookCategoryRepository.class);

    @MockBean
    private Model model;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        BookCategory category = new BookCategory("Fantasy");
        BookCategory category1 = new BookCategory("Humor");
        category1.setId((long) 1);
        Mockito.doThrow(DataIntegrityViolationException.class).when(bookCategoryRepository).save(category);
        Mockito.when(bookCategoryRepository.findById((long) 1)).thenReturn(Optional.of(category1));

        Role userRole = new Role(Roles.USER.toString());
        Mockito.when(roleRepository.findByName(Roles.USER.toString())).thenReturn(userRole);

        User user = new User("test@mail.com", "Test", "Test", "password", "Lublin", "789456123");
        user.addRole(roleRepository.findByName(Roles.USER.toString()));
        user.setId((long) 1);
        Mockito.when(userRepository.findById((long) 1)).thenReturn(Optional.of(user));
    }

    @Test
    public void addCategoryFormTest() {
        ModelAndView modelAndView = categoriesController.addCategoryForm((long) 1);
        Assertions.assertEquals("add_category_form", modelAndView.getViewName());
    }

    @Test
    public void addANewCategoryTest() {
        ModelAndView modelAndView = categoriesController.addNewCategory((long) 1, "Biography");
        Assertions.assertEquals("redirect:/user/1/manage_books", modelAndView.getViewName());
    }

    @Test
    public void addANewCategoryFailedTest() {
        ModelAndView modelAndView = categoriesController.addNewCategory((long) 1, "Fantasy");
        Assertions.assertEquals("add_category_form", modelAndView.getViewName());
        Assertions.assertEquals(Const.SAVING_CATEGORY_ERROR_LOG, modelAndView.getModel().get("message"));
    }

    @Test
    public void deleteCategoryTest() {
        ModelAndView modelAndView = categoriesController.deleteCategory((long) 1, (long) 1);
        Assertions.assertEquals("redirect:/user/1/manage_books", modelAndView.getViewName());
    }

    @Test
    public void updateCategoryTest() {
        ModelAndView modelAndView = categoriesController.updateCategory((long) 1, (long) 1, "Humor / General");
        Assertions.assertEquals("redirect:/user/1/manage_books", modelAndView.getViewName());
    }

}
