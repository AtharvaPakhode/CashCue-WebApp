package com.codelab.expensetracker.services;

import com.codelab.expensetracker.models.Expense;
import com.codelab.expensetracker.models.Income;
import com.codelab.expensetracker.models.User;
import com.codelab.expensetracker.repositories.ExpenseRepository;
import com.codelab.expensetracker.repositories.IncomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ReportService {
    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private IncomeRepository incomeRepository;


    public List<Double> getMonthlyExpenseSums(User user) {
        // Fetch the monthly expense sums with their respective months
        List<Object[]> monthlyExpenseSums = expenseRepository.getMonthlyExpenseSumsWithMonth(user);

        // Initialize a list of 12 months, all set to 0.0
        List<Double> paddedMonthlyExpenseSums = new ArrayList<>(Collections.nCopies(12, 0.0));

        // Iterate over the query results and populate the list
        for (Object[] result : monthlyExpenseSums) {
            Integer month = ((Number) result[0]).intValue();  // Month (1 = January, 2 = February, ...)
            Double amount = ((Number) result[1]).doubleValue();  // Expense for that month

            // Set the expense for the respective month (0-indexed list, so subtract 1 from the month)
            paddedMonthlyExpenseSums.set(month - 1, amount);
        }

        return paddedMonthlyExpenseSums;
    }

    public List<Double> getMonthlyIncomeSums(User user) {
        // Fetch the monthly income sums with their respective months
        List<Object[]> monthlyIncomeSums = incomeRepository.getMonthlyIncomeSumsWithMonth(user);

        // Initialize a list of 12 months, all set to 0.0
        List<Double> paddedMonthlyIncomeSums = new ArrayList<>(Collections.nCopies(12, 0.0));

        // Iterate over the query results and populate the list
        for (Object[] result : monthlyIncomeSums) {
            Integer month = ((Number) result[0]).intValue();  // Month (1 = January, 2 = February, ...)
            Double amount = ((Number) result[1]).doubleValue();  // Income for that month

            // Set the income for the respective month (0-indexed list, so subtract 1 from the month)
            paddedMonthlyIncomeSums.set(month - 1, amount);
        }

        return paddedMonthlyIncomeSums;
    }

    public List<Double> getQuarterlyIncomeSums(User user) {
        List<Object[]> results = incomeRepository.getQuarterlyIncomeSumsWithQuarter(user);

    // Create a list of 4 quarters initialized with 0.0
    List<Double> paddedQuarterlySums = new ArrayList<>(Collections.nCopies(4, 0.0));

    // Fill in actual data
    for (Object[] row : results) {
        Integer quarter = ((Number) row[0]).intValue(); // 1 to 4
        Double amount = ((Number) row[1]).doubleValue();

        // Subtract 1 to convert to 0-indexed list
        paddedQuarterlySums.set(quarter - 1, amount);
    }

    return paddedQuarterlySums;
    }

    public List<Double> getQuarterlyExpenseSums(User user) {
        List<Object[]> results = expenseRepository.getQuarterlyExpenseSumsWithQuarter(user);

        // Initialize list with 0s for 4 quarters
        List<Double> paddedQuarterlySums = new ArrayList<>(Collections.nCopies(4, 0.0));

        // Fill in data
        for (Object[] row : results) {
            Integer quarter = ((Number) row[0]).intValue(); // 1-4
            Double amount = ((Number) row[1]).doubleValue();

            paddedQuarterlySums.set(quarter - 1, amount); // 0-based index
        }

        return paddedQuarterlySums;
    }

    public List<Double> getYearlyExpenseSums(User user) {
        List<Object[]> yearlyExpenseSums = expenseRepository.getYearlyExpenseSumsWithYear(user);

        int currentYear = Year.now().getValue();
        int startYear = currentYear - 4; // last 5 years

        // Initialize a list of 5 years with 0s
        List<Double> paddedSums = new ArrayList<>(Collections.nCopies(5, 0.0));

        for (Object[] row : yearlyExpenseSums) {
            int year = ((Number) row[0]).intValue();
            double sum = ((Number) row[1]).doubleValue();

            if (year >= startYear && year <= currentYear) {
                int index = year - startYear;
                paddedSums.set(index, sum);
            }
        }

        return paddedSums;
    
    }

    public List<Double> getYearlyIncomeSums(User user) {
         List<Object[]> yearlyIncomeSums = incomeRepository.getYearlyIncomeSumsWithYear(user);

    int currentYear = Year.now().getValue();
    int startYear = currentYear - 4; // last 5 years

    // Initialize list with 0s for last 5 years
    List<Double> paddedSums = new ArrayList<>(Collections.nCopies(5, 0.0));

    for (Object[] row : yearlyIncomeSums) {
        int year = ((Number) row[0]).intValue();
        double sum = ((Number) row[1]).doubleValue();

        if (year >= startYear && year <= currentYear) {
            int index = year - startYear;
            paddedSums.set(index, sum);
        }
    }

    return paddedSums;
    }
}
