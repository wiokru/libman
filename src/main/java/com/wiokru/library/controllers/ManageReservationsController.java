package com.wiokru.library.controllers;

import com.wiokru.library.entity.Book;
import com.wiokru.library.entity.Borrowed;
import com.wiokru.library.entity.Reserved;
import com.wiokru.library.entity.User;
import com.wiokru.library.repositories.BookRepository;
import com.wiokru.library.repositories.BorrowedRepository;
import com.wiokru.library.repositories.ReservedRepository;
import com.wiokru.library.repositories.UserRepository;
import com.wiokru.library.utils.Const;
import org.apache.commons.lang3.StringUtils;
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
public class ManageReservationsController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReservedRepository reservedRepository;

    @Autowired
    private BorrowedRepository borrowedRepository;

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);


    @GetMapping("/user/{id}/manage_books/reservations")
    public ModelAndView manageReservations(@PathVariable("id") Long id) {
        ModelAndView modelAndView = new ModelAndView("manage_reservations");
        Optional<User> currentUser = userRepository.findById(id);

        List<Reserved> reservations = reservedRepository.findAll().stream()
                .sorted(Comparator.comparing(Reserved::getDueDate))
                .collect(Collectors.toList());

        LOGGER.setLevel(Level.INFO);
        LOGGER.info(Const.ALL_RESERVATIONS_SIZE + reservations.size());

        modelAndView.addObject("reservedList", reservations);
        modelAndView.addObject("currentUser", currentUser.get());
        return modelAndView;
    }

    @PostMapping("/user/{id}/manage_books/reservations")
    public ModelAndView searchReservations(@PathVariable("id") Long id,
                                    @ModelAttribute("search_text") String search_text) {

        ModelAndView modelAndView = new ModelAndView("manage_books");
        Optional<User> currentUser = userRepository.findById(id);

        List<Reserved> reservations = reservedRepository.findAll().stream()
                .filter(reserved -> StringUtils.containsIgnoreCase(reserved.getBook().getTitle(), search_text)
                        || StringUtils.containsIgnoreCase(reserved.getBook().listAuthors(), search_text)
                        || StringUtils.containsIgnoreCase(reserved.getUser().getEmail(), search_text)
                        || StringUtils.containsIgnoreCase(reserved.getUser().getName(), search_text)
                        || StringUtils.containsIgnoreCase(reserved.getUser().getSurname(), search_text))
                .sorted(Comparator.comparing(Reserved::getId))
                .collect(Collectors.toList());

        LOGGER.setLevel(Level.INFO);
        LOGGER.info(Const.ALL_RESERVATIONS_SIZE + reservations.size());

        modelAndView.addObject("reservedList", reservations);
        modelAndView.addObject("currentUser", currentUser.get());
        return modelAndView;
    }

    @GetMapping("/user/{id}/manage_books/reservations/reject/{reserved_id}")
    @DeleteMapping("/user/{id}/manage_books/reservations/reject/{reserved_id}")
    public ModelAndView rejectReserved(@PathVariable("id") Long id,
                                       @PathVariable("reserved_id") Long reserved_id) {
        User currentUser = userRepository.findById(id).get();
        Reserved reserved = reservedRepository.findById(reserved_id).get();

        reservedRepository.delete(reserved);

        LOGGER.setLevel(Level.INFO);
        LOGGER.info(Const.RESERVATION_REJECTED_LOG);

        return new ModelAndView ("redirect:/user/" + currentUser.getId() + "/manage_books/reservations");
    }

    @GetMapping("/user/{id}/manage_books/reservations/accept/{reserved_id}")
    public ModelAndView acceptReserved(@PathVariable("id") Long id,
                                       @PathVariable("reserved_id") Long reserved_id) {
        User currentUser = userRepository.findById(id).get();
        Reserved reserved = reservedRepository.findById(reserved_id).get();

        Borrowed borrowed = new Borrowed(reserved.getBook(), reserved.getUser());

        reservedRepository.delete(reserved);
        borrowedRepository.save(borrowed);

        LOGGER.setLevel(Level.INFO);
        LOGGER.info(Const.RESERVATION_ACCEPTED_LOG);

        return new ModelAndView ("redirect:/user/" + currentUser.getId() + "/manage_books/reservations");
    }

}
