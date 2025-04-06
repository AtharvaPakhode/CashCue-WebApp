package com.codelab.expensetracker.controllers;


import com.codelab.expensetracker.helper.CustomDisplayMessage;
import com.codelab.expensetracker.models.Category;
import com.codelab.expensetracker.models.Expense;
import com.codelab.expensetracker.models.Income;
import com.codelab.expensetracker.models.User;
import com.codelab.expensetracker.repositories.CategoryRepository;
import com.codelab.expensetracker.repositories.ExpenseRepository;
import com.codelab.expensetracker.repositories.IncomeRepository;
import com.codelab.expensetracker.repositories.UserRepository;
import com.codelab.expensetracker.services.CategoryService;
import com.codelab.expensetracker.specification.ExpenseSpecification;
import com.codelab.expensetracker.specification.IncomeSpecification;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;


@Controller
@RequestMapping("/user")
public class UserAccessUrlController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExpenseRepository expenseRepository;
    
    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ExpenseSpecification ExpenseSpecifications;




    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        model.addAttribute("title", "Dashboard");
        

        String name = principal.getName();
        User user = this.userRepository.getUserByName(name);

        System.out.println(user.getUserName());
        
        model.addAttribute("user", user);
        model.addAttribute("page","dashboard"); // for page specific CSS



        // Adding attributes to the model to pass to Thymeleaf template
//        model.addAttribute("savings", savings);
//        model.addAttribute("expenses", monthlySpending);
//        model.addAttribute("savingsPercentage", savingsPercentage);
//        model.addAttribute("expensesPercentage", expensesPercentage);


        


        return "user-access-url/dashboard";
    }

    @GetMapping("/settings")
    public String settings(Model model, Principal principal) {
        model.addAttribute("title", "Dashboard");


        String name = principal.getName();
        User user = this.userRepository.getUserByName(name);

        

        model.addAttribute("user", user);
        model.addAttribute("page","settings"); // for page specific CSS





        return "user-access-url/settings";
    }

    
    
    /**
     * Handles the request to change the profile image of the user.
     * It deletes the old image if it exists, and uploads a new image or sets the default image if the uploaded file is empty.
     *
     * @param model The Model object that will hold attributes for the view.
     * @param principal The Principal object that holds the details of the authenticated user.
     * @param profileImage The MultipartFile object representing the uploaded profile image.
     * @param session The HttpSession object that holds session attributes, such as success messages.
     * @return A redirect to the user settings page after the profile image has been updated.
     */
    @PostMapping("/change-profile-image")
    public String changeProfileImage(Model model, Principal principal,
                                     @RequestParam("profileImage") MultipartFile profileImage,
                                     HttpSession session){
        try {
            // Retrieve the username of the currently authenticated user
            String name = principal.getName();
            // Fetch the user details from the database based on the username
            User user = this.userRepository.getUserByName(name);

            // Get the current user's profile image URL
            String oldImage = user.getUserImageURL();
            

            // Prepare the file name for the image to be deleted (if any)
            String fileNameToDelete = oldImage;

            // Define the folder path where the profile images are stored
            String folderPath = "static/userprofileimages";
            File directory = new File(folderPath);

            // Construct the full path to the old profile image that needs to be deleted
            Path targetLocation = Paths.get(directory.getAbsolutePath() + File.separator + fileNameToDelete);
            

            // Create a File object for the image to be deleted
            File fileToDelete = targetLocation.toFile();

            // Check if the file exists and is a valid file, then delete it
            if (fileToDelete.exists() && fileToDelete.isFile()) {
                fileToDelete.delete();
               
            } 

            // If the uploaded image file is empty, set the default profile image
            if (profileImage.isEmpty()) {
                user.setUserImageURL("userDefault.png");
            } else {
                // If a new image is uploaded, process it
                String originalFileName = profileImage.getOriginalFilename();

                // Generate a unique image name based on the user's ID
                String userID = String.valueOf(user.getUserId());
                String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                String imageName = userID + "_PROFILE_IMAGE" + fileExtension;

                // Set the new profile image URL for the user
                user.setUserImageURL(imageName);

                // Define the folder path where the new image will be stored
                String folder = "static/userprofileimages";
                directory = new File(folder);

                // Create the folder if it does not exist
                if (!directory.exists()) {
                    directory.mkdir();
                }

                // Construct the target path for the new image file
                targetLocation = Paths.get(directory.getAbsolutePath() + File.separator + imageName);

                // Copy the uploaded file to the target location, replacing any existing file with the same name
                Files.copy(profileImage.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
                

                // Save the updated user data in the repository
                this.userRepository.save(user);
                model.addAttribute("user", user);

                // Set a success message in the session to be displayed on the frontend
                session.setAttribute("customMessage", new CustomDisplayMessage("Profile image updated successfully", "alert-success"));
            }

        } catch (Exception e) {
            // Log the exception if an error occurs during the file upload process
            e.printStackTrace();
        }

        // Redirect to the user settings page after the update process is complete
        return "redirect:/user/settings";
    }

    //----------------------------------------------------------------------------------------------------------------

    @GetMapping("/add-income")
    public String addIncome(Model model, Principal principal){
        String name = principal.getName();
        User user = this.userRepository.getUserByName(name);

        model.addAttribute("user",user);
        model.addAttribute("page","addIncome"); // for page specific CSS
        model.addAttribute("income", new Income());
        
        return "user-access-url/add-income";
    }


    @PostMapping("/add-income-process")
    public String addIncomeForm(@Valid @ModelAttribute("income") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Income income,
                                 BindingResult bindingResult,
                                 Model model,
                                 Principal principal){
        
        String name = principal.getName();
        User user = this.userRepository.getUserByName(name);
        model.addAttribute("user", user);

        try {
            if (bindingResult.hasErrors()) {                
                System.out.println("failed");
                System.out.println(bindingResult.getAllErrors());
                return "user-access-url/add-expense";
            }

            
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Convert the bound LocalDate to LocalDateTime
        if (income.getDate() != null) {
            LocalTime currentTime = LocalTime.now();
            income.setLocalDateTime(LocalDateTime.of(income.getDate(), currentTime));
        }



        System.out.println(income.toString());  //DEBUG

        
        System.out.println(income.getLocalDateTime());

        income.setUser(user);
        this.incomeRepository.save(income);

        return "user-access-url/add-income";
    }


    @GetMapping("/income-history/{page}")
    public String incomeHistory(
            Model model,
            Principal principal,            
            @RequestParam(value = "minAmount", required = false) Double minAmount,
            @RequestParam(value = "maxAmount", required = false) Double maxAmount,            
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PathVariable("page") int page
    ) {
        String name = principal.getName();
        User user = userRepository.getUserByName(name);

        model.addAttribute("user", user);
        model.addAttribute("page", "expenseHistory");

        Pageable pageable = PageRequest.of(page, 6, Sort.by(Sort.Direction.DESC, "dateTime"));

        Specification<Income> spec;

        boolean noFilters = (
                minAmount == null &&
                maxAmount == null &&               
                startDate == null &&
                endDate == null);

        if (noFilters) {
            spec = (root, query, cb) -> cb.equal(root.get("user"), user);
        } else {
            spec = IncomeSpecification.withFilters(user, minAmount, maxAmount, startDate, endDate);
        }

        Page<Income> filteredIncomes = incomeRepository.findAll(spec, pageable);
        
        for(Income f : filteredIncomes){
            System.out.println(f.toString());
        }

        model.addAttribute("userTransactions", filteredIncomes);
        model.addAttribute("totalPages", filteredIncomes.getTotalPages());
        model.addAttribute("currentPage", page);

        // Preserve filters
        
        model.addAttribute("minAmount", minAmount);
        model.addAttribute("maxAmount", maxAmount);        
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);



        return "user-access-url/income-history";
    }






    //----------------------------------------------------------------------------------------------------------------
    
    @GetMapping("/add-expense")
    public String addExpense(Model model, Principal principal){
        String name = principal.getName();
        User user = this.userRepository.getUserByName(name);
        
        List<Category> categoryList = this.categoryRepository.ListOfCategoryByUser(user.getUserId());
        
        model.addAttribute("categories",categoryList);
        model.addAttribute("user",user);
        model.addAttribute("page","addExpense"); // for page specific CSS
        model.addAttribute("expense", new Expense());

        return "user-access-url/add-expense";
    }

    
    @PostMapping("/add-expense-process")
    public String addExpenseForm(@Valid @ModelAttribute("expense") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Expense expense,
                                 BindingResult bindingResult,
                                 Model model,
                                 Principal principal,
                                 @RequestParam("category") Category category){
        String name = principal.getName();
        User user = this.userRepository.getUserByName(name);
        model.addAttribute("user", user);

        try {
            if (bindingResult.hasErrors()) {
                List<Category> categoryList = this.categoryRepository.ListOfCategoryByUser(user.getUserId());
                model.addAttribute("categories", categoryList);
                System.out.println("failed");
                System.out.println(bindingResult.getAllErrors());
                return "user-access-url/add-expense";
            }

            if (category == null) {
                bindingResult.rejectValue("category", "error.category", "Please select category");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Convert the bound LocalDate to LocalDateTime 
        if (expense.getDate() != null) {
            LocalTime currentTime = LocalTime.now();
            expense.setLocalDateTime(LocalDateTime.of(expense.getDate(), currentTime));
        }

        

        System.out.println(expense.toString());
        List<Category> categoryList = this.categoryRepository.ListOfCategoryByUser(user.getUserId());
        model.addAttribute("categories", categoryList);
        
        System.out.println(expense.getLocalDateTime());
        
        expense.setUser(user);
        this.expenseRepository.save(expense);

        return "user-access-url/add-expense";
    }

@GetMapping("/expense-history/{page}")
public String expenseHistory(
        Model model,
        Principal principal,
        @RequestParam(value = "categories", required = false) List<String> categoryNames,
        @RequestParam(value = "minAmount", required = false) Double minAmount,
        @RequestParam(value = "maxAmount", required = false) Double maxAmount,
        @RequestParam(value = "paymentMethod", required = false) String paymentMethod,
        @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
        @PathVariable("page") int page
) {
    String name = principal.getName();
    User user = userRepository.getUserByName(name);

    model.addAttribute("user", user);
    model.addAttribute("page", "expenseHistory");

    Pageable pageable = PageRequest.of(page, 6, Sort.by(Sort.Direction.DESC, "dateTime"));

    List<Category> categories = new ArrayList<>();
    if (categoryNames != null && !categoryNames.isEmpty()) {
        categories = categoryRepository.findByCategoryNameInAndUserId(categoryNames, user.getUserId());
    }

    Specification<Expense> spec;

    boolean noFilters = (categoryNames == null || categoryNames.isEmpty()) &&
            minAmount == null &&
            maxAmount == null &&
            (paymentMethod == null || paymentMethod.isEmpty()) &&
            startDate == null &&
            endDate == null;

    if (noFilters) {
        spec = (root, query, cb) -> cb.equal(root.get("user"), user);
    } else {
        spec = ExpenseSpecifications.withFilters(user, categories, minAmount, maxAmount, paymentMethod, startDate, endDate);
    }

    Page<Expense> filteredExpenses = expenseRepository.findAll(spec, pageable);

    model.addAttribute("userTransactions", filteredExpenses);
    model.addAttribute("userCategories", categoryRepository.ListOfCategoryByUser(user.getUserId()));
    model.addAttribute("totalPages", filteredExpenses.getTotalPages());
    model.addAttribute("currentPage", page);

    // Preserve filters
    model.addAttribute("filterCategories", categoryNames);
    model.addAttribute("minAmount", minAmount);
    model.addAttribute("maxAmount", maxAmount);
    model.addAttribute("paymentMethod", paymentMethod);
    model.addAttribute("startDate", startDate);
    model.addAttribute("endDate", endDate);

    

    return "user-access-url/expense-history";
}



    
    
//----------------------------------------------------------------------------------------------------------------
    @GetMapping("/category/{page}")
    public String category(@PathVariable("page") Integer page, 
                           @RequestParam(name = "search", required = false) String search,
                           Model model, Principal principal){
        
        String name = principal.getName();
        User user = this.userRepository.getUserByName(name);

        int id = user.getUserId();
        if(search == null || search.isEmpty()){
            
            Pageable pageable =  PageRequest.of(page, 8);
            Page<Category> CategoryList = this.categoryRepository.findCategoriesByUser(id,  pageable);
            // If there are no categories, set totalPages to 1 to avoid errors in pagination
            int totalPages = (CategoryList.getTotalElements() == 0) ? 0 : CategoryList.getTotalPages();
            model.addAttribute("totalPages", CategoryList.getTotalPages());
            model.addAttribute("categories", CategoryList);
            
        }
        else {
            Category categories = categoryService.searchCategories(search,id);
            model.addAttribute("categories", categories);
        }
        
        model.addAttribute("currentPage", page);
        model.addAttribute("user",user);
        model.addAttribute("category", new Category());
        return "user-access-url/view-user-category";
    }

    @PostMapping("/process-save-category")
    public String saveCategory(@ModelAttribute("category") Category category,
                               Model model, Principal principal,
                               HttpSession session,
                               @RequestParam("categoryName") String categoryName,
                               @RequestParam("categoryMonthlyBudget") double categoryMonthlyBudget) {

        try {
            String name = principal.getName();
            User user = this.userRepository.getUserByName(name);
            model.addAttribute("user", user);

            category.setUser(user);
            category.setCategoryName(categoryName.toUpperCase());
            category.setCategoryMonthlyBudget(categoryMonthlyBudget);

            // Save the category to the database
            this.categoryRepository.save(category);
            this.userRepository.save(user);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/user/category/0";  // Redirect back to category page after saving
    }


    @DeleteMapping("/deleteCategory/{name}")
    public ResponseEntity<String> deleteCategory(@PathVariable("name") String name) {
        // Logic to delete the category from the database or any other source
        boolean isDeleted = categoryService.deleteCategoryByName(name);

        if (isDeleted) {
            // Success response with a custom message
            return ResponseEntity.ok("Category deleted successfully.");
        } else {
            // Failure response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete category.");
        }
    }

    @PostMapping("/updateCategory/{name}")
    public String updateCategory(@ModelAttribute("category") Category category,
                                 @PathVariable("name") String categoryName,
                                 @RequestParam String categoryNameChange,
                                 @RequestParam double categoryMonthlyBudget,
                                 Model model, Principal principal) {

        String name = principal.getName();
        User user = this.userRepository.getUserByName(name);
        int id = user.getUserId();
        model.addAttribute("user", user);
        model.addAttribute("category", category);

        
        // Logic to find and update the category
        category = categoryService.searchCategories(categoryName, id);
        category.setCategoryName(categoryNameChange);
        category.setCategoryMonthlyBudget(categoryMonthlyBudget);

        // Save the updated category to the database
        this.categoryRepository.save(category);
        return "redirect:/user/category/0";  // Redirect back to category page after updating
    }


}
