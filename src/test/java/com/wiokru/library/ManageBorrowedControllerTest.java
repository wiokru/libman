package com.wiokru.library;

import com.wiokru.library.controllers.ManageBorrowedController;
import com.wiokru.library.entity.*;
import com.wiokru.library.repositories.BorrowedRepository;
import com.wiokru.library.repositories.RoleRepository;
import com.wiokru.library.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ManageBorrowedControllerTest {

    @Autowired
    @InjectMocks
    private ManageBorrowedController manageBorrowedController;

    private UserRepository userRepository = Mockito.mock(UserRepository.class);
    private RoleRepository roleRepository = Mockito.mock(RoleRepository.class);
    private BorrowedRepository borrowedRepository = Mockito.mock(BorrowedRepository.class);

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        BookCategory category = new BookCategory("Test");
        category.setId((long) 1);
        Set<BookCategory> categories = new HashSet<BookCategory>() {{add(category);}};

        Author author = new Author("Test Author");
        author.setId((long) 1);
        Set<Author> authors = new HashSet<Author>() {{add(author);}};

        Book book1 = new Book("Test1", "Test1", "10.10.2015",
                "test book1", 324 );
        book1.setId((long) 1);
        book1.setCategories(categories);
        book1.setAuthors(authors);

        Role userRole = new Role(Roles.USER.toString());
        Mockito.when(roleRepository.findByName(Roles.USER.toString())).thenReturn(userRole);

        User user = new User("test@mail.com", "Test", "Test", "password",
                "Lublin", "789456123");
        user.addRole(roleRepository.findByName(Roles.USER.toString()));
        user.setId((long) 1);
        Mockito.when(userRepository.findById((long) 1)).thenReturn(Optional.of(user));

        Borrowed borrowed = new Borrowed(book1, user);
        borrowed.setId((long) 1);
        Mockito.when(borrowedRepository.findAll()).thenReturn(Arrays.asList(borrowed));
        Mockito.when(borrowedRepository.findById((long) 1)).thenReturn(Optional.of(borrowed));
    }

    @Test
    public void manageReservationsFormTest() {
        ModelAndView modelAndView = manageBorrowedController.manageReservations((long) 1);
        Assertions.assertEquals("manage_borrowed", modelAndView.getViewName());
        Assertions.assertEquals(1, ((List<Borrowed>) modelAndView.getModelMap().get("borrowedList")).size());
    }

    @Test
    public void rejectReservedTest() {
        ModelAndView modelAndView = manageBorrowedController.rejectReserved((long) 1, (long) 1);
        Assertions.assertEquals("redirect:/user/1/manage_books/borrowed", modelAndView.getViewName());
    }
}
