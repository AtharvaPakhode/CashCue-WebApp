package com.codelab.expensetracker.controllers;

import com.codelab.expensetracker.models.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class UrlController {

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("title", "Register Page");
        model.addAttribute("user", new User());  // Initialize a new user object for the form
        return "open-url/register";
    }
}
