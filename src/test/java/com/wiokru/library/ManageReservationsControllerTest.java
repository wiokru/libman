package com.wiokru.library;

import com.wiokru.library.controllers.ManageReservationsController;
import com.wiokru.library.entity.*;
import com.wiokru.library.repositories.ReservedRepository;
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
public class ManageReservationsControllerTest {

    @Autowired
    @InjectMocks
    private ManageReservationsController manageReservationsController;

    private UserRepository userRepository = Mockito.mock(UserRepository.class);
    private RoleRepository roleRepository = Mockito.mock(RoleRepository.class);
    private ReservedRepository reservedRepository = Mockito.mock(ReservedRepository.class);

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        BookCategory category = new BookCategory("Test");
        category.setId((long) 1);
        Set<BookCategory> categories = new HashSet<BookCategory>() {{
            add(category);
        }};

        Author author = new Author("Test Author");
        author.setId((long) 1);
        Set<Author> authors = new HashSet<Author>() {{
            add(author);
        }};

        Book book1 = new Book("Test1", "Test1", "10.10.2015",
                "test book1", 324);
        book1.setId((long) 1);
        book1.setCategories(categories);
        book1.setAuthors(authors);

        Role userRole = new Role(Roles.USER.toString());
        Mockito.when(roleRepository.findByName(Roles.USER.toString())).thenReturn(userRole);

        User user = new User("test@mail.com", "Test", "Test",
                "password", "Lublin", "789456123");
        user.addRole(roleRepository.findByName(Roles.USER.toString()));
        user.setId((long) 1);
        Mockito.when(userRepository.findById((long) 1)).thenReturn(Optional.of(user));

        Reserved reserved = new Reserved(book1, user);
        reserved.setId((long) 1);
        Mockito.when(reservedRepository.findAll()).thenReturn(Arrays.asList(reserved));
        Mockito.when(reservedRepository.findById((long) 1)).thenReturn(Optional.of(reserved));
    }

    @Test
    public void manageReservationsFormTest() {
        ModelAndView modelAndView = manageReservationsController.manageReservations((long) 1);
        Assertions.assertEquals("manage_reservations", modelAndView.getViewName());
        Assertions.assertEquals(1, ((List<Reserved>) modelAndView.getModelMap().get("reservedList")).size());
    }

    @Test
    public void searchReservationsTest() {
        ModelAndView modelAndView = manageReservationsController.searchReservations((long) 1, "test");
        Assertions.assertEquals("manage_reservations", modelAndView.getViewName());
        Assertions.assertEquals(1, ((List<Reserved>) modelAndView.getModelMap().get("reservedList")).size());
    }

    @Test
    public void rejectReservedTest() {
        ModelAndView modelAndView = manageReservationsController.rejectReserved((long) 1, (long) 1);
        Assertions.assertEquals("redirect:/user/1/manage_books/reservations", modelAndView.getViewName());
    }


    @Test
    public void acceptReservedTest() {
        ModelAndView modelAndView = manageReservationsController.rejectReserved((long) 1, (long) 1);
        Assertions.assertEquals("redirect:/user/1/manage_books/reservations", modelAndView.getViewName());
    }
}
