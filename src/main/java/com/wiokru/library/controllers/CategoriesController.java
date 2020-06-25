package com.wiokru.library.controllers;

import com.wiokru.library.entity.BookCategory;
import com.wiokru.library.entity.User;
import com.wiokru.library.repositories.BookCategoryRepository;
import com.wiokru.library.repositories.UserRepository;
import com.wiokru.library.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
public class CategoriesController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookCategoryRepository categoryRepository;

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);


    @GetMapping("user/{id}/manage_books/add_category")
    public ModelAndView addBookForm(@PathVariable("id") Long id) {
        User currentUser = userRepository.findById(id).get();

        ModelAndView modelAndView = new ModelAndView("add_category_form");
        modelAndView.addObject("currentUser", currentUser);
        return modelAndView;
    }

    @PostMapping("user/{id}/manage_books/add_category")
    public ModelAndView addNewBook(@PathVariable("id") Long id,
                                   @ModelAttribute("name") String name) {
        User currentUser = userRepository.findById(id).get();
        BookCategory category = new BookCategory(name);

        try {
            categoryRepository.save(category);

            LOGGER.setLevel(Level.INFO);
            LOGGER.info(Const.CATEGORY_ADDED_LOG);
        }
        catch (Exception e){
            LOGGER.setLevel(Level.INFO);
            LOGGER.info(Const.SAVING_CATEGORY_ERROR_LOG);

            ModelAndView modelAndView = new ModelAndView("add_category_form");
            modelAndView.addObject("currentUser", currentUser);
            modelAndView.addObject("message", Const.SAVING_CATEGORY_ERROR_LOG);
            return modelAndView;
        }

        return new ModelAndView("redirect:/user/" + currentUser.getId() + "/manage_books");
    }

    @DeleteMapping("/user/{id}/manage_books/delete_category/{category_id}")
    public ModelAndView deleteUser(@PathVariable("id") Long id,
                                   @PathVariable("category_id") Long categoryId) {
        User currentUser = userRepository.findById(id).get();
        BookCategory category = categoryRepository.findById(categoryId).get();
        categoryRepository.delete(category);

        LOGGER.setLevel(Level.INFO);
        LOGGER.info(Const.CATEGORY_DELETED_LOG);

        return new ModelAndView ("redirect:/user/" + currentUser.getId() + "/manage_books");
    }
}
