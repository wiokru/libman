package com.wiokru.library;

import com.wiokru.library.controllers.UserHomeController;
import com.wiokru.library.entity.*;
import com.wiokru.library.repositories.*;
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
import java.util.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserHomeControllerTest {

    @Autowired
    @InjectMocks
    private UserHomeController userHomeController;

    private UserRepository userRepository = Mockito.mock(UserRepository.class);
    private RoleRepository roleRepository = Mockito.mock(RoleRepository.class);
    private BookRepository bookRepository = Mockito.mock(BookRepository.class);
    private AuthorRepository authorRepository = Mockito.mock(AuthorRepository.class);
    private BookCategoryRepository bookCategoryRepository = Mockito.mock(BookCategoryRepository.class);
    private BorrowedRepository borrowedRepository = Mockito.mock(BorrowedRepository.class);
    private ReservedRepository reservedRepository = Mockito.mock(ReservedRepository.class);

    @MockBean
    private Model model;

    @MockBean
    private HttpServletResponse response;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        BookCategory category = new BookCategory("Test");
        category.setId((long) 1);
        Set<BookCategory> categories = new HashSet<BookCategory>() {{add(category);}};
        Mockito.when(bookCategoryRepository.findAll()).thenReturn(Arrays.asList(category));
        Mockito.when(bookCategoryRepository.findById((long) 1)).thenReturn(Optional.of(category));

        Author author = new Author("Test Author");
        author.setId((long) 1);
        Set<Author> authors = new HashSet<Author>() {{add(author);}};
        Mockito.when(authorRepository.findById((long) 1)).thenReturn(Optional.of(author));
        Mockito.when(authorRepository.findAll()).thenReturn(Arrays.asList(author));

        Book book1 = new Book("Test1", "Test1", "10.10.2015", "test book1", 324 );
        book1.setId((long) 1);
        book1.setCategories(categories);
        book1.setAuthors(authors);
        Book book2 = new Book("Test2", "Test2", "15.11.2016", "test book2", 225 );
        book2.setId((long) 2);
        book2.setCategories(categories);
        book2.setAuthors(authors);
        Book book3 = new Book("Test3", "Test3", "17.10.2017", "test book3", 390 );
        book3.setId((long) 3);
        book3.setCategories(categories);
        book3.setAuthors(authors);
        Book book4 = new Book("Test4", "Test4", "25.01.2020", "test book4", 340 );
        List<Book> books = new ArrayList<Book>(){{add(book1); add(book2); add(book3);}};
        Mockito.when(bookRepository.findAll()).thenReturn(books);
        Mockito.when(bookRepository.findById((long) 1)).thenReturn(Optional.of(book1));
        Mockito.when(bookRepository.findById((long) 2)).thenReturn(Optional.of(book2));
        Mockito.when(bookRepository.findById((long) 3)).thenReturn(Optional.of(book3));
        Mockito.doThrow(DataIntegrityViolationException.class).when(bookRepository).save(book3);
        Mockito.doThrow(DataIntegrityViolationException.class).when(bookRepository).save(book4);

        Role userRole = new Role(Roles.USER.toString());
        userRole.setId((long) 1);
        Mockito.when(roleRepository.findByName(Roles.USER.toString())).thenReturn(userRole);
        Mockito.when(roleRepository.findById((long) 1)).thenReturn(Optional.of(userRole));
        Mockito.when(roleRepository.findAll()).thenReturn(Arrays.asList(userRole));

        User user = new User("test@mail.com", "Test", "Test",
                "password", "Lublin", "789456123");
        user.addRole(roleRepository.findByName(Roles.USER.toString()));
        user.setId((long) 1);

        Mockito.when(userRepository.findById((long) 1)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        Mockito.when(userRepository.findAll()).thenReturn(Arrays.asList(user));

        Reserved reserved = new Reserved(book1, user);
        reserved.setId((long) 1);
        Mockito.when(reservedRepository.findAll()).thenReturn(Arrays.asList(reserved));
        Mockito.when(reservedRepository.findAllByUser(user)).thenReturn(Arrays.asList(reserved));
        Mockito.when(reservedRepository.findById((long) 1)).thenReturn(Optional.of(reserved));
        Mockito.when(reservedRepository.findByBook(book1)).thenReturn(Optional.of(reserved));
        Mockito.when(reservedRepository.findAllByUser(user)).thenReturn(Arrays.asList(reserved));

        Borrowed borrowed = new Borrowed(book2, user);
        borrowed.setId((long) 1);
        Mockito.when(borrowedRepository.findAll()).thenReturn(Arrays.asList(borrowed));
        Mockito.when(borrowedRepository.findAllByUser(user)).thenReturn(Arrays.asList(borrowed));
        Mockito.when(borrowedRepository.findById((long) 1)).thenReturn(Optional.of(borrowed));
        Mockito.when(borrowedRepository.findByBook(book2)).thenReturn(Optional.of(borrowed));
    }

    @Test
    public void userHomePageTest() {
        ModelAndView modelAndView = userHomeController.userHomePage((long) 1);
        Assertions.assertEquals("user_home", modelAndView.getViewName());
    }

    @Test
    public void userBooksPageTest() {
        ModelAndView modelAndView = userHomeController.userPostPage((long) 1);
        Assertions.assertEquals("user_books", modelAndView.getViewName());
        Assertions.assertEquals(1, ((List<Borrowed>) modelAndView.getModelMap().get("borrowedList")).size());
        Assertions.assertEquals(1, ((List<Reserved>) modelAndView.getModelMap().get("reservedList")).size());
    }

    @Test
    public void userInfoPageTest() {
        ModelAndView modelAndView = userHomeController.userInfoPage((long) 1);
        Assertions.assertEquals("user_info", modelAndView.getViewName());
    }

    @Test
    public void saveEditUserTest() {
        ModelAndView modelAndView = userHomeController.saveEditUserInfo((long) 1, "Test",
                "Update", "test.update@mail.com", "Lublin",
                "789456123");
        Assertions.assertEquals("user_info", modelAndView.getViewName());
        Assertions.assertEquals(Boolean.TRUE, modelAndView.getModelMap().get("is_success"));
        Assertions.assertEquals(Const.USER_UPDATED_SUCCESS, modelAndView.getModelMap().get("message"));
    }

    @Test
    public void searchBooksTest() {
        ModelAndView modelAndView = userHomeController.searchAllBooks((long) 1, "Test");
        Assertions.assertEquals("user_home", modelAndView.getViewName());
        Assertions.assertEquals(3, ((List<Book>) modelAndView.getModel().get("bookList")).size());
    }

    @Test
    public void confirmReservationTest() {
        ModelAndView modelAndView = userHomeController.confirmReservationBook((long) 1, (long) 3);
        Assertions.assertEquals("book_reservation_confirm", modelAndView.getViewName());
    }

    @Test
    public void reserveBookTest() {
        ModelAndView modelAndView = userHomeController.reserveBook((long) 1, (long) 3);
        Assertions.assertEquals("redirect:/user/1/home", modelAndView.getViewName());
    }

    @Test
    public void reserveBookFailTest() {
        ModelAndView modelAndView = userHomeController.reserveBook((long) 1, (long) 2);
        Assertions.assertEquals("book_reservation_confirm", modelAndView.getViewName());
        Assertions.assertEquals(Boolean.TRUE, modelAndView.getModelMap().get("failed"));
        Assertions.assertEquals(Const.RESERVATION_FAILED_USER_INFO, modelAndView.getModelMap().get("message"));
    }

}
