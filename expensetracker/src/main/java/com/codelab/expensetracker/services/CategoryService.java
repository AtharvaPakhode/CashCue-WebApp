package com.codelab.expensetracker.services;

import com.codelab.expensetracker.models.Category;
import com.codelab.expensetracker.models.User;
import com.codelab.expensetracker.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    // Method to delete category by name
    public boolean deleteCategoryByName(String name) {
        // Check if the category exists
        if (categoryRepository.existsById(name)) {
            
            categoryRepository.deleteById(name);
            System.out.println("true");
            return true;
           
        }
        return false;  // Category not found
    }

    public Category searchCategories(String search, int id) {
        // Search by category name or monthly budget (adjust your query logic as needed)
        return categoryRepository.findByCategoryNameContainingIgnoreCase(search, id);
    }
    
    
    
    
    
    
    
    //report

    public Map<String, List<Double>> getCurrentMonthCategorySums(User user) {
        List<Object[]> monthlySortedDescending = this.categoryRepository.getCurrentMonthCategorySumsWithBudget(user);

        Map<String, List<Double>> top3CategoriesCurrent = new LinkedHashMap<>();

        int topN = Math.min(3, monthlySortedDescending.size());

        for (int i = 0; i < topN; i++) {
            String categoryName = (String) monthlySortedDescending.get(i)[0];
            Double totalAmount = (Double) monthlySortedDescending.get(i)[1];
            Double monthlyBudget = (Double) monthlySortedDescending.get(i)[2];

            top3CategoriesCurrent.put(categoryName, Arrays.asList(totalAmount, monthlyBudget));
        }

        return top3CategoriesCurrent;
    }

//    public Map<String, Double> getCurrentMonthCategorySums(User user) {
//        // Fetch the sorted monthly sums in descending order
//        List<Object[]> monthlySortedDescending = this.categoryRepository.getCurrentMonthCategorySums(user);
//
//        // Map to hold the top 3 categories
//        Map<String, Double> top3CategoriesCurrent = new LinkedHashMap<>();
//
//        // Check that the list has enough items (at least 3)
//        int topN = Math.min(3, monthlySortedDescending.size());
//
//        // Add top 3 categories to the map
//        for (int i = 0; i < topN; i++) {
//            String categoryName = (String) monthlySortedDescending.get(i)[0];
//            Double totalAmount = (Double) monthlySortedDescending.get(i)[1];
//            top3CategoriesCurrent.put(categoryName, totalAmount);
//        }
//
//        return top3CategoriesCurrent;
//    }


    public Map<String, Double> getPastMonthCategorySums(User user) {
        // Fetch the sorted monthly sums in descending order
        List<Object[]> monthlySortedDescending = this.categoryRepository.getPastMonthCategorySums(user);

        // Map to hold the top 3 categories
        Map<String, Double> top3CategoriesPast = new LinkedHashMap<>();

        // Check that the list has enough items (at least 3)
        int topN = Math.min(3, monthlySortedDescending.size());

        // Add top 3 categories to the map
        for (int i = 0; i < topN; i++) {
            String categoryName = (String) monthlySortedDescending.get(i)[0];
            Double totalAmount = (Double) monthlySortedDescending.get(i)[1];
            top3CategoriesPast.put(categoryName, totalAmount);
        }
        return top3CategoriesPast;
    }

    public Map<String, Double> getCurrentQuarterCategorySums(User user) {
        // Fetch the sorted quarterly sums in descending order
        List<Object[]> quarterlySortedDescending = this.categoryRepository.getCurrentQuarterCategorySums(user);

        // Iterate over the list and print each Object[]
        for (Object[] entry : quarterlySortedDescending) {
            // Assuming each Object[] contains 2 elements (e.g., category and sum),
            // adjust indices as needed based on your data structure
            System.out.println("quarter: " + entry[0] + ", category: " + entry[1]+ "Amount: "+entry[2]);
        }

        // Map to hold the top 3 categories
        Map<String, Double> top3CategoriesCurrent = new LinkedHashMap<>();

        int count=0;

        int month = LocalDate.now().getMonthValue();
        int currentQuarter = (month - 1) / 3 + 1;

        String categoryName=null;
        Double totalAmount=null;
        
        // Add top 3 categories to the map
        for (int i = 0; i < quarterlySortedDescending.size(); i++) {
            if(currentQuarter == (Integer)quarterlySortedDescending.get(i)[0] && count<3){
                 categoryName = (String) quarterlySortedDescending.get(i)[1];
                 totalAmount = (Double) quarterlySortedDescending.get(i)[2];
                 top3CategoriesCurrent.put(categoryName, totalAmount);
                 System.out.println(top3CategoriesCurrent);
                 count++;
            }

            
            
        }
        return top3CategoriesCurrent;
    }

    public Map<String, Double> getPastQuarterCategorySums(User user) {
        // Fetch the sorted quarterly sums in descending order
        List<Object[]> quarterlySortedDescending = this.categoryRepository.getPastQuarterCategorySums(user);

        // Map to hold the top 3 categories
        Map<String, Double> top3CategoriesPast = new LinkedHashMap<>();

        int count=0;

        int month = LocalDate.now().getMonthValue();
        int currentQuarter = (month - 1) / 3 + 1;

        String categoryName=null;
        Double totalAmount=null;

        // Add top 3 categories to the map
        for (int i = 0; i < quarterlySortedDescending.size(); i++) {
            if(currentQuarter == (Integer)quarterlySortedDescending.get(i)[0] && count<3){
                categoryName = (String) quarterlySortedDescending.get(i)[1];
                totalAmount = (Double) quarterlySortedDescending.get(i)[2];
                count++;
            }

            top3CategoriesPast.put(categoryName, totalAmount);
        }
        return top3CategoriesPast;
    }

    public Map<String, Double> getCurrentYearCategorySums(User user) {
        // Fetch the sorted yearly sums in descending order
        List<Object[]> yearlySortedDescending = this.categoryRepository.getCurrentYearCategorySums(user);

        // Map to hold the top 3 categories
        Map<String, Double> top3CategoriesCurrent = new LinkedHashMap<>();

        // Check that the list has enough items (at least 3)
        int topN = Math.min(3, yearlySortedDescending.size());

        // Add top 3 categories to the map
        for (int i = 0; i < topN; i++) {
            String categoryName = (String) yearlySortedDescending.get(i)[0];
            Double totalAmount = (Double) yearlySortedDescending.get(i)[1];
            top3CategoriesCurrent.put(categoryName, totalAmount);
        }
        return top3CategoriesCurrent;
    }

    public Map<String, Double> getPastYearCategorySums(User user) {
        // Fetch the sorted yearly sums in descending order
        List<Object[]> yearlySortedDescending = this.categoryRepository.getPastYearCategorySums(user);

        // Map to hold the top 3 categories
        Map<String, Double> top3CategoriesPast = new LinkedHashMap<>();

        // Check that the list has enough items (at least 3)
        int topN = Math.min(3, yearlySortedDescending.size());

        // Add top 3 categories to the map
        for (int i = 0; i < topN; i++) {
            String categoryName = (String) yearlySortedDescending.get(i)[0];
            Double totalAmount = (Double) yearlySortedDescending.get(i)[1];
            top3CategoriesPast.put(categoryName, totalAmount);
        }
        return top3CategoriesPast;
    }
    
    

}
