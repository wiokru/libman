package com.wiokru.library.controllers;

import com.wiokru.library.entity.Author;
import com.wiokru.library.entity.User;
import com.wiokru.library.repositories.AuthorRepository;
import com.wiokru.library.repositories.UserRepository;
import com.wiokru.library.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
public class AuthorsController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorRepository authorRepository;

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);


    @GetMapping("user/{id}/manage_books/add_author")
    public ModelAndView addAuthorForm(@PathVariable("id") Long id) {
        User currentUser = userRepository.findById(id).get();

        ModelAndView modelAndView = new ModelAndView("add_author_form");
        modelAndView.addObject("currentUser", currentUser);
        return modelAndView;
    }

    @PostMapping("user/{id}/manage_books/add_author")
    public ModelAndView addNewAuthor(@PathVariable("id") Long id,
                                     @ModelAttribute("name") String name) {
        User currentUser = userRepository.findById(id).get();
        Author newAuthor = new Author(name);

        try {
            authorRepository.save(newAuthor);

            LOGGER.setLevel(Level.INFO);
            LOGGER.info(Const.AUTHOR_ADDED_LOG);
        } catch (Exception e) {
            LOGGER.setLevel(Level.INFO);
            LOGGER.info(Const.SAVING_AUTHOR_ERROR_LOG);

            ModelAndView modelAndView = new ModelAndView("add_author_form");
            modelAndView.addObject("currentUser", currentUser);
            modelAndView.addObject("message", Const.SAVING_AUTHOR_ERROR_LOG);
            return modelAndView;
        }

        return new ModelAndView("redirect:/user/" + currentUser.getId() + "/manage_books");
    }

    @DeleteMapping("/user/{id}/manage_books/delete_author/{author_id}")
    public ModelAndView deleteAuthor(@PathVariable("id") Long id,
                                     @PathVariable("author_id") Long authorId) {
        User currentUser = userRepository.findById(id).get();
        Author author = authorRepository.findById(authorId).get();
        authorRepository.delete(author);

        LOGGER.setLevel(Level.INFO);
        LOGGER.info(Const.AUTHOR_DELETED_LOG);

        return new ModelAndView("redirect:/user/" + currentUser.getId() + "/manage_books");
    }

    @PutMapping("/user/{id}/manage_books/edit_author/{author_id}")
    public ModelAndView updateAuthor(@PathVariable("id") Long id,
                                     @PathVariable("author_id") Long authorId,
                                     @ModelAttribute("name") String name) {
        User currentUser = userRepository.findById(id).get();
        Author author = authorRepository.findById(authorId).get();
        author.setName(name);
        authorRepository.save(author);

        LOGGER.setLevel(Level.INFO);
        LOGGER.info(Const.AUTHOR_UPDATED_LOG);

        return new ModelAndView("redirect:/user/" + currentUser.getId() + "/manage_books");
    }
}
