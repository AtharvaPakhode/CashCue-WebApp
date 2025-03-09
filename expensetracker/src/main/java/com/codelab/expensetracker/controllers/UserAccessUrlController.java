package com.codelab.expensetracker.controllers;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/user")
public class UserAccessUrlController {

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {


        double monthlyIncome = 500000;
        double monthlySpending = 250000;

        // Calculating savings
        double savings = monthlyIncome - monthlySpending;

        // Calculating percentage for savings and expenses
        double savingsPercentage = (savings / monthlyIncome) * 100;
        double expensesPercentage = (monthlySpending / monthlyIncome) * 100;

        // Adding attributes to the model to pass to Thymeleaf template
        model.addAttribute("savings", savings);
        model.addAttribute("expenses", monthlySpending);
        model.addAttribute("savingsPercentage", savingsPercentage);
        model.addAttribute("expensesPercentage", expensesPercentage);


        model.addAttribute("title", "Dashboard");


        return "user-access-url/dashboard";
    }

}
