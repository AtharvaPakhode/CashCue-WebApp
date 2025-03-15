package com.codelab.expensetracker.controllers;


import com.codelab.expensetracker.models.User;
import com.codelab.expensetracker.repositories.CategoryRepository;
import com.codelab.expensetracker.repositories.ExpenseRepository;
import com.codelab.expensetracker.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/user")
public class UserAccessUrlController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private CategoryRepository categoryRepository;




    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {

        String name = principal.getName();
        User user = this.userRepository.getUserByName(name);




        // Adding attributes to the model to pass to Thymeleaf template
//        model.addAttribute("savings", savings);
//        model.addAttribute("expenses", monthlySpending);
//        model.addAttribute("savingsPercentage", savingsPercentage);
//        model.addAttribute("expensesPercentage", expensesPercentage);


        model.addAttribute("title", "Dashboard");


        return "user-access-url/dashboard";
    }

}
