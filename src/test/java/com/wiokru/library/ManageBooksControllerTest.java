package com.wiokru.library;

import com.wiokru.library.controllers.ManageBooksController;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ManageBooksControllerTest {

    @Autowired
    @InjectMocks
    private ManageBooksController manageBooksController;

    private UserRepository userRepository = Mockito.mock(UserRepository.class);
    private RoleRepository roleRepository = Mockito.mock(RoleRepository.class);
    private BookRepository bookRepository = Mockito.mock(BookRepository.class);
    private AuthorRepository authorRepository = Mockito.mock(AuthorRepository.class);
    private BookCategoryRepository bookCategoryRepository = Mockito.mock(BookCategoryRepository.class);

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

        Book book1 = new Book("Test1", "Test1", "10.10.2015",
                "test book1", 324 );
        book1.setId((long) 1);
        book1.setCategories(categories);
        book1.setAuthors(authors);
        Book book2 = new Book("Test2", "Test2", "15.11.2016",
                "test book2", 225 );
        book2.setId((long) 2);
        book2.setCategories(categories);
        book2.setAuthors(authors);
        Book book3 = new Book("Test3", "Test3", "17.10.2017",
                "test book3", 390 );
        book3.setId((long) 3);
        book3.setCategories(categories);
        book3.setAuthors(authors);
        Book book4 = new Book("Test4", "Test4", "25.01.2020",
                "test book4", 340 );
        List<Book> books = new ArrayList<Book>(){{add(book1); add(book2); add(book3);}};
        Mockito.when(bookRepository.findAll()).thenReturn(books);
        Mockito.when(bookRepository.findById((long) 1)).thenReturn(Optional.of(book1));
        Mockito.when(bookRepository.findById((long) 3)).thenReturn(Optional.of(book3));
        Mockito.doThrow(DataIntegrityViolationException.class).when(bookRepository).save(book3);
        Mockito.doThrow(DataIntegrityViolationException.class).when(bookRepository).save(book4);

        Role userRole = new Role(Roles.USER.toString());
        Mockito.when(roleRepository.findByName(Roles.USER.toString())).thenReturn(userRole);

        User user = new User("test@mail.com", "Test", "Test", "password",
                "Lublin", "789456123");
        user.addRole(roleRepository.findByName(Roles.USER.toString()));
        user.setId((long) 1);
        Mockito.when(userRepository.findById((long) 1)).thenReturn(Optional.of(user));
    }

    @Test
    public void manageBooksFormTest() {
        ModelAndView modelAndView = manageBooksController.manageBooks((long) 1);
        Assertions.assertEquals("manage_books", modelAndView.getViewName());
    }

    @Test
    public void searchBooksTest() {
        ModelAndView modelAndView = manageBooksController.searchBooks((long) 1, "Test");
        Assertions.assertEquals("manage_books", modelAndView.getViewName());
        Assertions.assertEquals(3, ((List<Book>) modelAndView.getModel().get("bookList")).size());
    }

    @Test
    public void deleteBookTest() {
        ModelAndView modelAndView = manageBooksController.deleteBook((long) 1, (long) 1);
        Assertions.assertEquals("redirect:/user/1/manage_books", modelAndView.getViewName());
    }

    @Test
    public void editBookFormTest() {
        ModelAndView modelAndView = manageBooksController.deleteBook((long) 1, (long) 1);
        Assertions.assertEquals("redirect:/user/1/manage_books", modelAndView.getViewName());
    }

    @Test
    public void saveEditBookTest() {
        ModelAndView modelAndView = manageBooksController.saveEditBook((long) 1, (long) 1,"Test1Update",
                "test book1 update", "Test1 update",  "10.10.2015", 324);
        Assertions.assertEquals("book_edit_form", modelAndView.getViewName());
        Assertions.assertEquals(Const.BOOK_UPDATED_SUCCESS, modelAndView.getModelMap().get("message"));
        Assertions.assertTrue((Boolean) modelAndView.getModelMap().get("is_success"));
    }

    @Test
    public void saveEditBookFailureTest() {
        ModelAndView modelAndView = manageBooksController.saveEditBook((long) 1, (long) 3,"Test3",
                "test book3", "Test3",  "17.10.2017", 390);
        Assertions.assertEquals("book_edit_form", modelAndView.getViewName());
        Assertions.assertEquals(Const.BOOK_UPDATED_ERROR, modelAndView.getModelMap().get("message"));
        Assertions.assertFalse((Boolean) modelAndView.getModelMap().get("is_success"));
    }

    @Test
    public void addBookFormTest() {
        ModelAndView modelAndView = manageBooksController.addBookForm((long) 1);
        Assertions.assertEquals("add_book_form", modelAndView.getViewName());
    }

    @Test
    public void addNewBookTest() {
        ModelAndView modelAndView = manageBooksController.addNewBook((long) 1,"New Book",
                "test new book insert", "Test",  "10.10.2015",
                "324", Arrays.asList((long) 1), Arrays.asList((long) 1));
        Assertions.assertEquals("redirect:/user/1/manage_books", modelAndView.getViewName());
    }

    @Test
    public void addNewBookFailureTest() {
        ModelAndView modelAndView = manageBooksController.addNewBook((long) 1,"Test4",
                "test book4", "Test4",  "25.01.2020", "340",
                null, null);
        Assertions.assertEquals("add_book_form", modelAndView.getViewName());
        Assertions.assertEquals(Const.SAVING_BOOK_ERROR_LOG, modelAndView.getModelMap().get("message"));
    }
}
