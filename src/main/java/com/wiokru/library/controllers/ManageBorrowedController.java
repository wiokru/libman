package com.wiokru.library.controllers;

import com.wiokru.library.entity.Borrowed;
import com.wiokru.library.entity.Reserved;
import com.wiokru.library.entity.User;
import com.wiokru.library.repositories.BorrowedRepository;
import com.wiokru.library.repositories.ReservedRepository;
import com.wiokru.library.repositories.UserRepository;
import com.wiokru.library.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
public class ManageBorrowedController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReservedRepository reservedRepository;

    @Autowired
    private BorrowedRepository borrowedRepository;

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    @GetMapping("/user/{id}/manage_books/borrowed")
    public ModelAndView manageReservations(@PathVariable("id") Long id) {
        ModelAndView modelAndView = new ModelAndView("manage_borrowed");
        Optional<User> currentUser = userRepository.findById(id);

        List<Borrowed> borrowed = borrowedRepository.findAll().stream()
                .sorted(Comparator.comparing(Borrowed::getDueDate))
                .collect(Collectors.toList());

        LOGGER.setLevel(Level.INFO);
        LOGGER.info(Const.ALL_BORROWED_SIZE + borrowed.size());

        modelAndView.addObject("borrowedList", borrowed);
        modelAndView.addObject("currentUser", currentUser.get());
        return modelAndView;
    }


    @GetMapping("/user/{id}/manage_books/borrowed/return/{borrowed_id}")
    public ModelAndView rejectReserved(@PathVariable("id") Long id,
                                       @PathVariable("borrowed_id") Long borrowed_id) {
        User currentUser = userRepository.findById(id).get();
        Borrowed borrowed = borrowedRepository.findById(borrowed_id).get();

        borrowedRepository.delete(borrowed);

        LOGGER.setLevel(Level.INFO);
        LOGGER.info(Const.BOOK_RETURNED_LOG);

        return new ModelAndView("redirect:/user/" + currentUser.getId() + "/manage_books/borrowed");
    }
}
