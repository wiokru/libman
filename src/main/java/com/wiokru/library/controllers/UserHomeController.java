package com.wiokru.library.controllers;

import com.wiokru.library.entity.Book;
import com.wiokru.library.entity.Borrowed;
import com.wiokru.library.entity.Reserved;
import com.wiokru.library.entity.User;
import com.wiokru.library.repositories.BookRepository;
import com.wiokru.library.repositories.BorrowedRepository;
import com.wiokru.library.repositories.ReservedRepository;
import com.wiokru.library.repositories.UserRepository;
import com.wiokru.library.utils.BookUtils;
import com.wiokru.library.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
public class UserHomeController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BorrowedRepository borrowedRepository;

    @Autowired
    private ReservedRepository reservedRepository;

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);


    @GetMapping("/user/{id}/home")
    public ModelAndView userHomePage(@PathVariable("id") Long id) {
        ModelAndView modelAndView = new ModelAndView("user_home");
        Optional<User> currentUser = userRepository.findById(id);

        List<Book> bookList = bookRepository.findAll().stream()
                .sorted(Comparator.comparing(Book::getTitle))
                .collect(Collectors.toList());

        LOGGER.setLevel(Level.INFO);
        LOGGER.info(Const.ALL_BOOKS_SIZE + bookList.size());

        modelAndView.addObject("bookList", bookList);
        modelAndView.addObject("currentUser", currentUser.get());
        return modelAndView;
    }

    @GetMapping("/user/{id}/my_books")
    public ModelAndView userPostPage(@PathVariable("id") Long id) {
        ModelAndView modelAndView = new ModelAndView("user_books");
        Optional<User> currentUser = userRepository.findById(id);

        List<Borrowed> borrowedList = borrowedRepository.findAllByUser(currentUser.get()).stream()
                .sorted(Comparator.comparing(Borrowed::getDueDate).reversed())
                .collect(Collectors.toList());

        List<Reserved> reservedList = reservedRepository.findAllByUser(currentUser.get())
                .stream()
                .sorted(Comparator.comparing(Reserved::getDueDate).reversed())
                .collect(Collectors.toList());

        LOGGER.setLevel(Level.INFO);
        LOGGER.info(Const.USER_BOOKS_SIZE + borrowedList.size());
        LOGGER.info(Const.ALL_RESERVATIONS_SIZE + reservedList.size());

        modelAndView.addObject("borrowedList", borrowedList);
        modelAndView.addObject("reservedList", reservedList);
        modelAndView.addObject("currentUser", currentUser.get());
        return modelAndView;
    }

    @GetMapping("/user/{id}/my_info")
    public ModelAndView userInfoPage(@PathVariable("id") Long id) {
        ModelAndView modelAndView = new ModelAndView("user_info");
        Optional<User> currentUser = userRepository.findById(id);

        modelAndView.addObject("currentUser", currentUser.get());
        return modelAndView;
    }

    @PostMapping("/user/{id}/my_info")
    @PutMapping("/user/{id}/my_info")
    public ModelAndView saveEditUserInfo(@PathVariable("id") Long id,
                                         @ModelAttribute("name") String name,
                                         @ModelAttribute("surname") String surname,
                                         @ModelAttribute("email") String email,
                                         @ModelAttribute("city") String city,
                                         @ModelAttribute("phone") String phone) {
        User currentUser = userRepository.findById(id).get();
        try {
            currentUser.setName(name);
            currentUser.setSurname(surname);
            currentUser.setEmail(email);
            currentUser.setCity(city);
            currentUser.setPhone(phone);

            userRepository.save(currentUser);

            LOGGER.setLevel(Level.INFO);
            LOGGER.info(email + Const.USER_UPDATED_SUCCESS_LOG);

            ModelAndView modelAndView = new ModelAndView("user_info");
            modelAndView.addObject("currentUser", currentUser);
            modelAndView.addObject("is_success", Boolean.TRUE);
            modelAndView.addObject("message", Const.USER_UPDATED_SUCCESS);
            return modelAndView;
        } catch (Exception e) {
            LOGGER.setLevel(Level.INFO);
            LOGGER.info(email + Const.USER_UPDATED_ERROR_LOG + e.getMessage());

            ModelAndView modelAndView = new ModelAndView("user_info");
            modelAndView.addObject("currentUser", currentUser);
            modelAndView.addObject("is_success", Boolean.FALSE);
            modelAndView.addObject("message", Const.USER_UPDATED_ERROR + e.getMessage());
            return modelAndView;
        }
    }

    @PostMapping("/user/{id}/home")
    public ModelAndView searchAllBooks(@PathVariable("id") Long id,
                                       @ModelAttribute("search_text") String searchedText) {

        ModelAndView modelAndView = new ModelAndView("user_home");
        Optional<User> currentUser = userRepository.findById(id);
        String[] searchWords = searchedText.toLowerCase().split(" ");

        List<Book> bookList = BookUtils.filterBooks(searchWords, bookRepository);

        LOGGER.setLevel(Level.INFO);
        LOGGER.info(Const.ALL_BOOKS_SIZE + bookList.size());

        modelAndView.addObject("bookList", bookList);
        modelAndView.addObject("currentUser", currentUser.get());
        return modelAndView;
    }

    @GetMapping("/user/{id}/home/borrow_book/{book_id}")
    public ModelAndView confirmReservationBook(@PathVariable("id") Long id,
                                               @PathVariable("book_id") Long bookId) {
        User currentUser = userRepository.findById(id).get();
        Book book = bookRepository.findById(bookId).get();

        ModelAndView modelAndView = new ModelAndView("book_reservation_confirm");
        modelAndView.addObject("selectedBook", book);
        modelAndView.addObject("currentUser", currentUser);
        return modelAndView;
    }

    @PostMapping("/user/{id}/home/borrow_book/{book_id}")
    public ModelAndView reserveBook(@PathVariable("id") Long id,
                                    @PathVariable("book_id") Long bookId) {
        User currentUser = userRepository.findById(id).get();
        Book book = bookRepository.findById(bookId).get();

        ModelAndView modelAndView = new ModelAndView("book_reservation_confirm");
        modelAndView.addObject("selectedBook", book);
        modelAndView.addObject("currentUser", currentUser);

        if (reservedRepository.findByBook(book).isPresent()
                || borrowedRepository.findByBook(book).isPresent()) {
            modelAndView.addObject("failed", Boolean.TRUE);
            modelAndView.addObject("message", Const.RESERVATION_FAILED_USER_INFO);

            return modelAndView;
        } else if (reservedRepository.findAllByUser(currentUser).size() >= Const.RESERVED_PER_USER_LIMIT) {
            modelAndView.addObject("failed", Boolean.TRUE);
            modelAndView.addObject("message", Const.RESERVATION_FAILED_LIMIT_INFO);

            return modelAndView;
        } else {
            Reserved reserved = new Reserved(book, currentUser);
            reservedRepository.save(reserved);

            LOGGER.setLevel(Level.INFO);
            LOGGER.info(Const.BOOK_RESERVED_LOG);

            return new ModelAndView("redirect:/user/" + currentUser.getId() + "/home");
        }
    }
}
