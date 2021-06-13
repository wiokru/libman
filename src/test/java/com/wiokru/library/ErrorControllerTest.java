package com.wiokru.library;

import com.wiokru.library.controllers.ErrorController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.servlet.ModelAndView;

@SpringBootTest
@AutoConfigureMockMvc
public class ErrorControllerTest {

    @Autowired
    private ErrorController errorController;

    @Test
    public void errorFormTest() {
        ModelAndView modelAndView = errorController.error();
        Assertions.assertEquals("error", modelAndView.getViewName());
    }
}
