package com.wiokru.library.controllers;

import com.wiokru.library.entity.Author;
import com.wiokru.library.entity.Book;
import com.wiokru.library.entity.BookCategory;
import com.wiokru.library.entity.User;
import com.wiokru.library.repositories.*;
import com.wiokru.library.utils.Const;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
public class ManageBooksController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookCategoryRepository categoryRepository;

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    @GetMapping("/user/{id}/manage_books")
    public ModelAndView manageBooks(@PathVariable("id") Long id) {
        ModelAndView modelAndView = new ModelAndView("manage_books");
        Optional<User> currentUser = userRepository.findById(id);

        List<Book> books = bookRepository.findAll().stream()
                .sorted(Comparator.comparing(Book::getId))
                .collect(Collectors.toList());

        LOGGER.setLevel(Level.INFO);
        LOGGER.info(Const.ALL_BOOKS_SIZE + books.size());

        modelAndView.addObject("bookList", books);
        modelAndView.addObject("currentUser", currentUser.get());
        return modelAndView;
    }

    @PostMapping("/user/{id}/manage_books")
    public ModelAndView searchBooks(@PathVariable("id") Long id,
                                    @ModelAttribute("search_text") String search_text) {

        ModelAndView modelAndView = new ModelAndView("manage_books");
        Optional<User> currentUser = userRepository.findById(id);

        List<Book> books = bookRepository.findAll()
                .stream()
                .filter(book -> StringUtils.containsIgnoreCase(book.getTitle(), search_text)
                        || StringUtils.containsIgnoreCase(book.listAuthors(), search_text)
                        || StringUtils.containsIgnoreCase(book.getPublisher(), search_text))
                .sorted(Comparator.comparing(Book::getTitle))
                .collect(Collectors.toList());

        LOGGER.setLevel(Level.INFO);
        LOGGER.info(Const.ALL_BOOKS_SIZE + books.size());

        modelAndView.addObject("bookList", books);
        modelAndView.addObject("currentUser", currentUser.get());
        return modelAndView;
    }

    @GetMapping("/user/{id}/manage_books/delete/{book_id}")
    @DeleteMapping("/user/{id}/manage_books/delete/{book_id}")
    public ModelAndView deleteBook(@PathVariable("id") Long id,
                                   @PathVariable("book_id") Long book_id) {
        User currentUser = userRepository.findById(id).get();
        Book book = bookRepository.findById(book_id).get();
        bookRepository.delete(book);

        LOGGER.setLevel(Level.INFO);
        LOGGER.info(Const.BOOK_DELETED_LOG);

        return new ModelAndView("redirect:/user/" + currentUser.getId() + "/manage_books");
    }

    @GetMapping("/user/{id}/manage_books/edit/{book_id}")
    public ModelAndView editBook(@PathVariable("id") Long id,
                                 @PathVariable("book_id") Long book_id) {
        User currentUser = userRepository.findById(id).get();
        Book book = bookRepository.findById(book_id).get();

        ModelAndView modelAndView = new ModelAndView("book_edit_form");
        modelAndView.addObject("selectedBook", book);
        modelAndView.addObject("currentUser", currentUser);
        return modelAndView;
    }

    @PostMapping("/user/{id}/manage_books/edit/{book_id}")
    @PutMapping("/user/{id}/manage_books/edit/{book_id}")
    public ModelAndView saveEditBook(@PathVariable("id") Long id,
                                     @PathVariable("book_id") Long book_id,
                                     @ModelAttribute("title") String title,
                                     @ModelAttribute("description") String description,
                                     @ModelAttribute("publisher") String publisher,
                                     @ModelAttribute("published_date") String publishedDate,
                                     @ModelAttribute("page_count") Integer pageCount) {
        User currentUser = userRepository.findById(id).get();
        Book book = bookRepository.findById(book_id).get();

        try {
            book.setTitle(title);
            book.setDescription(description);
            book.setPublisher(publisher);
            book.setPublishedDate(publishedDate);
            book.setPageCount(pageCount);

            bookRepository.save(book);

            LOGGER.setLevel(Level.INFO);
            LOGGER.info(book_id + Const.BOOK_UPDATED_SUCCESS_LOG);

            ModelAndView modelAndView = new ModelAndView("book_edit_form");
            modelAndView.addObject("currentUser", currentUser);
            modelAndView.addObject("selectedBook", book);
            modelAndView.addObject("is_success", Boolean.TRUE);
            modelAndView.addObject("message", Const.BOOK_UPDATED_SUCCESS);
            return modelAndView;
        } catch (Exception e) {
            LOGGER.setLevel(Level.INFO);
            LOGGER.info(book_id + Const.BOOK_UPDATED_ERROR_LOG + e.getMessage());

            ModelAndView modelAndView = new ModelAndView("book_edit_form");
            modelAndView.addObject("currentUser", currentUser);
            modelAndView.addObject("selectedBook", book);
            modelAndView.addObject("is_success", Boolean.FALSE);
            modelAndView.addObject("message", Const.BOOK_UPDATED_ERROR + e.getMessage());
            return modelAndView;
        }
    }

    @GetMapping("/user/{id}/manage_books/add_book")
    public ModelAndView addBookForm(@PathVariable("id") Long id) {
        User currentUser = userRepository.findById(id).get();

        ModelAndView modelAndView = new ModelAndView("add_book_form");
        modelAndView.addObject("authorsList", authorRepository.findAll());
        modelAndView.addObject("categoriesList", categoryRepository.findAll());
        modelAndView.addObject("currentUser", currentUser);
        return modelAndView;
    }

    @PostMapping("/user/{id}/manage_books/add_book")
    public ModelAndView addNewBook(@PathVariable("id") Long id,
                                           @ModelAttribute("title") String title,
                                           @ModelAttribute("description") String description,
                                           @ModelAttribute("publisher") String publisher,
                                           @ModelAttribute("published_date") String publishedDate,
                                           @ModelAttribute("page_count") String pageCount,
                                           @RequestParam("selected_author") List<Long> authorsIds,
                                           @RequestParam("selected_category") List<Long> categoriesIds) {
        User currentUser = userRepository.findById(id).get();
        Book newBook = setupNewBook(title, description, publisher, publishedDate, pageCount, authorsIds, categoriesIds);

        try {
            bookRepository.save(newBook);

            LOGGER.setLevel(Level.INFO);
            LOGGER.info(Const.BOOK_ADDED_LOG);
        }
        catch (Exception e){
            LOGGER.setLevel(Level.INFO);
            LOGGER.info(Const.SAVING_BOOK_ERROR_LOG);

            ModelAndView modelAndView = new ModelAndView("add_book_form");
            modelAndView.addObject("authorsList", authorRepository.findAll());
            modelAndView.addObject("categoriesList", categoryRepository.findAll());
            modelAndView.addObject("currentUser", currentUser);
            modelAndView.addObject("message", Const.SAVING_BOOK_ERROR_LOG);
            return modelAndView;
        }

        return new ModelAndView("redirect:/user/" + currentUser.getId() + "/manage_books");
    }

    private Book setupNewBook(String title, String description, String publisher, String publishedDate,
                              String pageCount, List<Long> authorsIds, List<Long> categoriesIds) {
        Book newBook = new Book(title, publisher, publishedDate, description, Integer.valueOf(pageCount));

        Set<Author> authorSet = new HashSet<>();
        for (Long authorId : authorsIds) {
            authorSet.add(authorRepository.findById(authorId).get());
        }
        newBook.setAuthors(authorSet);

        Set<BookCategory> bookCategories = new HashSet<>();
        for (Long bookCatId : categoriesIds) {
            bookCategories.add(categoryRepository.findById(bookCatId).get());
        }
        newBook.setCategories(bookCategories);
        return newBook;
    }
}
