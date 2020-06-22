package com.wiokru.library.controllers;

import com.wiokru.library.entity.User;
import com.wiokru.library.repositories.RoleRepository;
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
public class ManageUsersAdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    @GetMapping("/user/{id}/manage_users")
    public ModelAndView manageUsers(@PathVariable("id") Long id) {
        ModelAndView modelAndView = new ModelAndView("manage_users");
        Optional<User> currentUser = userRepository.findById(id);

        List<User> users = userRepository.findAll().stream()
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList());

        LOGGER.setLevel(Level.INFO);
        LOGGER.info(Const.ALL_USERS_SIZE + users.size());

        modelAndView.addObject("usersList", users);
        modelAndView.addObject("currentUser", currentUser.get());
        return modelAndView;
    }

    @PostMapping("/user/{id}/manage_users")
    public ModelAndView searchUsers(@PathVariable("id") Long id,
                                   @ModelAttribute("search_text") String search_text) {

        ModelAndView modelAndView = new ModelAndView("manage_users");
        Optional<User> currentUser = userRepository.findById(id);

        List<User> users = userRepository.findAll()
                .stream()
                .filter(user -> StringUtils.containsIgnoreCase(user.getEmail(), search_text)
                        || StringUtils.containsIgnoreCase(user.getName(), search_text)
                        || StringUtils.containsIgnoreCase(user.getSurname(), search_text))
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList());;

        LOGGER.setLevel(Level.INFO);
        LOGGER.info(Const.ALL_USERS_SIZE + users.size());

        modelAndView.addObject("usersList", users);
        modelAndView.addObject("currentUser", currentUser.get());
        return modelAndView;
    }

    @GetMapping("/user/{id}/manage_users/delete/{user_id}")
    @DeleteMapping("/user/{id}/manage_users/delete/{user_id}")
    public ModelAndView deleteUser(@PathVariable("id") Long id,
                                   @PathVariable("user_id") Long userId) {
        User currentUser = userRepository.findById(id).get();
        User user = userRepository.findById(userId).get();
        userRepository.delete(user);

        LOGGER.setLevel(Level.INFO);
        LOGGER.info(Const.USER_DELETED_LOG);

        return new ModelAndView ("redirect:/user/" + currentUser.getId() + "/manage_users");
    }

    @GetMapping("/user/{id}/manage_users/edit/{user_id}")
    public ModelAndView editUser(@PathVariable("id") Long id,
                                 @PathVariable("user_id") Long userId) {
        User currentUser = userRepository.findById(id).get();
        User user = userRepository.findById(userId).get();

        ModelAndView modelAndView = new ModelAndView("user_edit_form");
        modelAndView.addObject("selectedUser", user);
        modelAndView.addObject("currentUser", currentUser);
        modelAndView.addObject("rolesList", roleRepository.findAll());
        return modelAndView;
    }

    @PostMapping("/user/{id}/manage_users/edit/{user_id}")
    @PutMapping("/user/{id}/manage_users/edit/{user_id}")
    public ModelAndView saveEditUser(@PathVariable("id") Long id,
                                     @PathVariable("user_id") Long userId,
                                     @ModelAttribute("name") String name,
                                     @ModelAttribute("surname") String surname,
                                     @ModelAttribute("email") String email,
                                     @ModelAttribute("city") String city,
                                     @ModelAttribute("phone") String phone,
                                     @RequestParam("selected_roles") List<Long> roles) {
        User currentUser = userRepository.findById(id).get();
        User user = userRepository.findById(userId).get();

        try {
            user.setName(name);
            user.setSurname(surname);
            user.setEmail(email);
            user.setCity(city);
            user.setPhone(phone);
            user.setRoles(null);
             for(Long roleId : roles){
                 user.addRole(roleRepository.findById(roleId).get());
             }

            userRepository.save(user);

            LOGGER.setLevel(Level.INFO);
            LOGGER.info(email + Const.USER_UPDATED_SUCCESS_LOG);

            ModelAndView modelAndView = new ModelAndView("user_edit_form");
            modelAndView.addObject("currentUser", currentUser);
            modelAndView.addObject("selectedUser", user);
            modelAndView.addObject("rolesList", roleRepository.findAll());
            modelAndView.addObject("is_success", Boolean.TRUE);
            modelAndView.addObject("message", Const.USER_UPDATED_SUCCESS);
            return modelAndView;
        }
        catch (Exception e) {
            LOGGER.setLevel(Level.INFO);
            LOGGER.info(email + Const.USER_UPDATED_ERROR_LOG + e.getMessage());

            ModelAndView modelAndView = new ModelAndView("user_edit_form");
            modelAndView.addObject("currentUser", currentUser);
            modelAndView.addObject("selectedUser", user);
            modelAndView.addObject("rolesList", roleRepository.findAll());
            modelAndView.addObject("is_success", Boolean.FALSE);
            modelAndView.addObject("message", Const.USER_UPDATED_ERROR + e.getMessage());
            return modelAndView;
        }
    }


}
