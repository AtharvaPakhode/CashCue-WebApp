package com.codelab.expensetracker.controllers;


import com.codelab.expensetracker.ExpensetrackerApplication;
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
import com.codelab.expensetracker.services.ReportService;
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
import java.util.Arrays;
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
    
    @Autowired
    private ReportService reportService;




    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        model.addAttribute("title", "Dashboard");

        // Get logged-in user
        String name = principal.getName();
        User user = this.userRepository.getUserByName(name);

        // Define financial variables
        Double sumOfIncomeByUser;
        Double sumOfExpenseByUser;
        Double monthlyIncome;
        Double monthlyExpense;
        Double totalBalance;
        Double monthlySavings;

        // Define start and end of the current month
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()).atTime(LocalTime.MAX);

        try {
            // Fetch values from database (may return null)
            sumOfIncomeByUser = incomeRepository.findSumOfExpensesOfUserByUserId(user);
            sumOfExpenseByUser = expenseRepository.findSumOfExpensesOfUserByUserId(user);
            Double monthlyIncomeRaw = incomeRepository.findSumOfIncomeForCurrentMonth(user, startOfMonth, endOfMonth);
            Double monthlyExpenseRaw = expenseRepository.findSumOfExpensesForCurrentMonth(user, startOfMonth, endOfMonth);

            // Handle nulls safely
            sumOfIncomeByUser = (sumOfIncomeByUser != null) ? sumOfIncomeByUser : 0.0;
            sumOfExpenseByUser = (sumOfExpenseByUser != null) ? sumOfExpenseByUser : 0.0;
            monthlyIncome = (monthlyIncomeRaw != null) ? monthlyIncomeRaw : 0.0;
            monthlyExpense = (monthlyExpenseRaw != null) ? monthlyExpenseRaw : 0.0;

            // Calculate balance and savings
            totalBalance = sumOfIncomeByUser - sumOfExpenseByUser;
            monthlySavings = monthlyIncome - monthlyExpense;

            // Print debug info
            System.out.println("Total Balance    : ₹" + String.format("%.2f", totalBalance));
            System.out.println("Monthly Income   : ₹" + String.format("%.2f", monthlyIncome));
            System.out.println("Monthly Expense  : ₹" + String.format("%.2f", monthlyExpense));
            System.out.println("Monthly Savings  : ₹" + String.format("%.2f", monthlySavings));

            // Add to model
            model.addAttribute("totalBalance", totalBalance);
            model.addAttribute("monthlyIncome", monthlyIncome);
            model.addAttribute("monthlyExpense", monthlyExpense);
            model.addAttribute("monthlySavings", monthlySavings);

        } catch (Exception e) {
            e.printStackTrace();
            // Fallback values in case of error
            model.addAttribute("totalBalance", 0.0);
            model.addAttribute("monthlyIncome", 0.0);
            model.addAttribute("monthlyExpense", 0.0);
            model.addAttribute("monthlySavings", 0.0);
        }

        // Add user info and page class for styling
        model.addAttribute("user", user);
        model.addAttribute("page", "dashboard"); // for page-specific CSS if needed

        return "user-access-url/dashboard"; // Thymeleaf template
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
        model.addAttribute("page","category");
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

//----------------------------------------------------------------------------------------------------------------

    @GetMapping("/reports")
    public String reports(@RequestParam(name = "period", required = false, defaultValue = "monthly") String period
                          ,Model model,Principal principal) {
        
        
        String name = principal.getName();
        User user = this.userRepository.getUserByName(name);

        String duration = period;
        Double totalExpense = null;
        Double totalIncome = null;
        Double netSavings = null;


        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()).atTime(LocalTime.MAX);


        int month = LocalDate.now().getMonthValue();
        int year =LocalDate.now().getYear();
        LocalDate startDate;
        LocalDate endDate;
        
         
              
        
        switch (duration) {
            case "monthly":
                totalExpense = this.expenseRepository.findSumOfExpensesForCurrentMonth(user, startOfMonth, endOfMonth);
                totalIncome = this.incomeRepository.findSumOfIncomeForCurrentMonth(user, startOfMonth, endOfMonth);
                totalExpense = (totalExpense != null) ? totalExpense : 0.0;
                totalIncome = (totalIncome != null) ? totalIncome : 0.0;
                
                netSavings=totalIncome-totalExpense;
                break;

            case "quarterly":
                if (month >= 1 && month <= 3) {
                    startDate = LocalDate.of(year, 1, 1);
                    endDate = LocalDate.of(year, 3, 31);
                } else if (month >= 4 && month <= 6) {
                    startDate = LocalDate.of(year, 4, 1);
                    endDate = LocalDate.of(year, 6, 30);
                } else if (month >= 7 && month <= 9) {
                    startDate = LocalDate.of(year, 7, 1);
                    endDate = LocalDate.of(year, 9, 30);
                } else {
                    startDate = LocalDate.of(year, 10, 1);
                    endDate = LocalDate.of(year, 12, 31);
                }
                LocalDateTime startOfQuarter = startDate.atStartOfDay();
                LocalDateTime endOfQuarter = endDate.atTime(LocalTime.MAX);

                totalExpense=this.expenseRepository.findSumOfExpenseForQuarter(user, startOfQuarter, endOfQuarter);
                totalIncome=this.incomeRepository.findSumOfIncomeForQuarter(user, startOfQuarter, endOfQuarter);
                totalExpense = (totalExpense != null) ? totalExpense : 0.0;
                totalIncome = (totalIncome != null) ? totalIncome : 0.0;
                
                netSavings=totalIncome-totalExpense;
                
                break;

            case "yearly":
                startDate=LocalDate.of(year,1,1);
                endDate=LocalDate.of(year,12,31);

                LocalDateTime startOfYear = startDate.atStartOfDay();
                LocalDateTime endOfYear = endDate.atTime(LocalTime.MAX);

                totalIncome = this.incomeRepository.findSumOfIncomeForYear(user, startOfYear, endOfYear);
                totalExpense = this.expenseRepository.findSumOfExpenseForYear(user, startOfYear, endOfYear);
                totalExpense = (totalExpense != null) ? totalExpense : 0.0;
                totalIncome = (totalIncome != null) ? totalIncome : 0.0;
                
                netSavings=totalIncome-totalExpense;
                
                break;

            default:
                System.out.println("Invalid option.");
                break;
        }


        

        model.addAttribute("user", user);
        model.addAttribute("page", "reports");
        model.addAttribute("period", period);
        model.addAttribute("totalExpense", totalExpense);
        model.addAttribute("totalIncome", totalIncome);
        model.addAttribute("netSavings", netSavings);

        
        //for dynamic trends
        String periodLabel = switch (period.toLowerCase()) {
            case "monthly" -> "current month";
            case "quarterly" -> "current quarter";
            case "yearly" -> "current year";
            default -> "Selected Period";
        };
        model.addAttribute("periodLabel", periodLabel);

        //for dynamic trends
        String periodLabel2 = switch (period.toLowerCase()) {
            case "monthly" -> "Monthly";
            case "quarterly" -> "Quarterly";
            case "yearly" -> "Yearly";
            default -> "Selected Period";
        };
        String periodLabel2Description = periodLabel2 + " Expense Trend";
        String periodTrendDescription = "Line chart showing " + periodLabel2.toLowerCase() + " expense trends";

        model.addAttribute("periodTrendDescription", periodTrendDescription);
        model.addAttribute("periodLabel2Description", periodLabel2Description);


        List<Double>income;
        List<Double>expense;
        List<String> months;
        List<String>quarters;
        List<String>years;
        // for monthly trends
          switch (period.toLowerCase()) {
            case "monthly"->{
                income= this.reportService.getMonthlyIncomeSums(user);
                expense= this.reportService.getMonthlyExpenseSums(user);
                months = Arrays.asList("January", "February", "March", "April", "May", "June","July", "August", "September", "October", "November", "December");
                model.addAttribute("income", income);
                model.addAttribute("expense", expense);
                model.addAttribute("month", months);
                break;
            }
            case "quarterly"->{
                income= this.reportService.getQuarterlyIncomeSums(user);
                expense= this.reportService.getQuarterlyExpenseSums(user);
                quarters = Arrays.asList("Q1", "Q2", "Q3", "Q4");
                model.addAttribute("income", income);
                model.addAttribute("expense", expense);
                model.addAttribute("quarter", quarters);
                break;
            }
            case "yearly"->{
                income = this.reportService.getYearlyIncomeSums(user);
                expense = this.reportService.getYearlyExpenseSums(user);

                // Get the current year
                int currentYear = java.time.Year.now().getValue();

                // Generate the year list as [current year - 4, current year - 3, current year - 2, current year - 1, current year]
                years = new ArrayList<>();
                for (int i = -4; i <= 0; i++) {
                    years.add(String.valueOf(currentYear + i));
                }

                model.addAttribute("income", income);
                model.addAttribute("expense", expense);
                model.addAttribute("year", years);
                break;
            }
            default->{
                System.out.println("invalid");
            }
        };
        
        
        
        return "user-access-url/reports";
    }


}
