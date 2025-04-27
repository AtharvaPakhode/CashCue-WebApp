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
import com.codelab.expensetracker.services.*;
import com.codelab.expensetracker.specification.ExpenseSpecification;
import com.codelab.expensetracker.specification.IncomeSpecification;
import com.itextpdf.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import java.io.IOException;
import java.nio.file.*;
import java.security.Principal;
import java.time.*;
import java.util.*;



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
    private IncomeService incomeService;
    
    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private ExpenseSpecification ExpenseSpecifications;

    @Autowired
    private ReportService reportService;

    @Autowired
    private PDFservice pdfService;


    @Value("${user.images.path}")
    private String imageStoragePath;
    
    @Value("${chart.line.image.path}")
    private String lineChartImagePath;

    @Value("${chart.pie.image.path}")
    private String pieChartImagePath;

    @Value("${chart.line.table.path}")
    private String lineTableImagePath;

    @Value("${chart.pie.table.path}")
    private String pieTableImagePath;

    /**
     * Handles the user dashboard page.
     * Displays total balance, monthly income, monthly expenses, and savings
     * based on the currently logged-in user.
     *
     * @param model     the Spring model to pass data to the view
     * @param principal contains the name of the currently authenticated user
     * @return the Thymeleaf view name for the dashboard page
     */
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
        LocalDateTime endOfMonth = LocalDate.now()
                .withDayOfMonth(LocalDate.now().lengthOfMonth())
                .atTime(LocalTime.MAX);

        try {
            /**
             * Fetch values from database for income and expenses.
             * These may return null if no records exist.
             */
            sumOfIncomeByUser = incomeRepository.findSumOfIncomeOfUserByUser(user);
            sumOfExpenseByUser = expenseRepository.findSumOfExpensesOfUserByUser(user);
            Double monthlyIncomeRaw = incomeRepository.findSumOfIncomeForCurrentMonth(user, startOfMonth, endOfMonth);
            Double monthlyExpenseRaw = expenseRepository.findSumOfExpensesForCurrentMonth(user, startOfMonth, endOfMonth);

            // Handle nulls safely to avoid NullPointerExceptions
            sumOfIncomeByUser = (sumOfIncomeByUser != null) ? sumOfIncomeByUser : 0.0;
            sumOfExpenseByUser = (sumOfExpenseByUser != null) ? sumOfExpenseByUser : 0.0;
            monthlyIncome = (monthlyIncomeRaw != null) ? monthlyIncomeRaw : 0.0;
            monthlyExpense = (monthlyExpenseRaw != null) ? monthlyExpenseRaw : 0.0;

            // Calculate balance and monthly savings
            totalBalance = sumOfIncomeByUser - sumOfExpenseByUser;
            totalBalance =(totalBalance >0)? totalBalance : 0.0;
            monthlySavings = monthlyIncome - monthlyExpense;
            monthlySavings =(monthlySavings >0)? monthlySavings : 0.0;

            // Add calculated values to the model
            model.addAttribute("totalBalance", totalBalance);
            model.addAttribute("monthlyIncome", monthlyIncome);
            model.addAttribute("monthlyExpense", monthlyExpense);
            model.addAttribute("monthlySavings", monthlySavings);

        } catch (Exception e) {
            e.printStackTrace();

            /**
             * If any exception occurs during data fetching,
             * populate the model with default zero values.
             */
            model.addAttribute("totalBalance", 0.0);
            model.addAttribute("monthlyIncome", 0.0);
            model.addAttribute("monthlyExpense", 0.0);
            model.addAttribute("monthlySavings", 0.0);
        }

        // Add user information and a CSS class for page-specific styling
        model.addAttribute("user", user);
        model.addAttribute("page", "dashboard"); // for page specific CSS (Active page on UI)

        // Return the Thymeleaf template for the dashboard view
        return "user-access-url/dashboard";
    }


//----------------------------------------------------------------------------------------------------------------


    
    /**
     * Handles the settings page view for the user.
     * Displays user info and the current state of two-factor authentication (2FA).
     *
     * @param model     the Spring model to pass data to the view
     * @param principal contains the name of the currently authenticated user
     * @return the Thymeleaf view name for the settings page
     */
    @GetMapping("/settings")
    public String settings(Model model, Principal principal) {
        model.addAttribute("title", "Dashboard");

        // Get the currently authenticated user's name and details
        String name = principal.getName();
        User user = this.userRepository.getUserByName(name);

        // Check if two-factor authentication is enabled for the user
        boolean tfaValue = user.isTwoFactorAuthentication();

        /**
         * Add the 2FA status to the model.
         * This value is used on the frontend to reflect the current toggle state.
         */
        if (tfaValue) {
            model.addAttribute("tfaValue", "true");
        } else {
            model.addAttribute("tfaValue", "false");
        }

        // Add user info and page identifier for CSS styling or highlighting in the UI
        model.addAttribute("user", user);
        model.addAttribute("page", "settings"); // for page specific CSS (Active page on UI)

        // Return the Thymeleaf template for the settings page
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
                                     HttpSession session) {
        try {
            // Retrieve current user
            String username = principal.getName();
            User user = this.userRepository.getUserByName(username);

            // Prepare image storage directory
            File directory = new File(imageStoragePath);
            if (!directory.exists()) {
                directory.mkdirs();
                System.out.println("foldercreated");
            }

            // Delete old profile image if it's not the default
            String oldImage = user.getUserImageURL();
            if (oldImage != null && !oldImage.equals("userDefault.png")) {
                File oldFile = new File(directory, oldImage);
                if (oldFile.exists() && oldFile.isFile()) {
                    oldFile.delete();
                }
            }

            if (profileImage.isEmpty()) {
                user.setUserImageURL("userDefault.png");
                session.setAttribute("customMessage", new CustomDisplayMessage("Please select a valid image file", "alert-danger"));
            } else {
                // Create a new unique image name
                String originalFileName = profileImage.getOriginalFilename();
                String fileExtension = originalFileName.substring(originalFileName.lastIndexOf('.'));
                String imageName = user.getUserId() + "_PROFILE_IMAGE" + fileExtension;

                // Save file to target directory
                Path targetPath = Paths.get(directory.getAbsolutePath(), imageName);
                Files.copy(profileImage.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                // Update user profile image URL and save
                user.setUserImageURL(imageName);
                this.userRepository.save(user);

                model.addAttribute("user", user);
                session.setAttribute("customMessage", new CustomDisplayMessage("Profile image updated successfully", "alert-success"));
            }

        } catch (Exception e) {
            session.setAttribute("customMessage", new CustomDisplayMessage("Something went wrong", "alert-danger"));
        }

        return "redirect:/user/settings";
    }




    /**
     * Handles the form submission for enabling or disabling
     * two-factor authentication (2FA) for the user.
     *
     * @param model                       the Spring model
     * @param principal                   contains the name of the currently authenticated user
     * @param twoFactorAuthenticationValue the boolean value of the 2FA checkbox
     * @param session                     the current HTTP session used to store feedback messages
     * @return redirect to the settings page after saving the preference
     */
    @PostMapping("/2fa-form")
    public String twoFactorAuthentication(
            Model model,
            Principal principal,
            @RequestParam(name = "checkboxValue", defaultValue = "false") boolean twoFactorAuthenticationValue,
            HttpSession session) {

        // Get the currently authenticated user
        String name = principal.getName();
        User user = userRepository.getUserByName(name);

        try {
            /**
             * Update the user's 2FA preference based on the checkbox input.
             * Add a session message indicating success or recommendation.
             */
            if (twoFactorAuthenticationValue) {
                user.setTwoFactorAuthentication(true);

                // Success message if 2FA is enabled
                session.setAttribute("customMessage", new CustomDisplayMessage(
                        "Two factor authentication is enabled",
                        "alert-success"
                ));
            } else {
                user.setTwoFactorAuthentication(false);

                // Recommendation message if 2FA is disabled
                session.setAttribute("customMessage", new CustomDisplayMessage(
                        "Two-Factor Authentication is currently disabled. For enhanced security, we recommend enabling it.",
                        "alert-danger"
                ));
            }

            // Save the updated user object to the database
            userRepository.save(user);

        } catch (Exception e) {
            e.printStackTrace();

            // On error, redirect back to the settings page
            return "redirect:/user/settings";
        }

        // On success, redirect to the settings page
        return "redirect:/user/settings";
    }




//----------------------------------------------------------------------------------------------------------------



    /**
     * Displays the form for adding a new income entry.
     * Initializes a blank Income object and passes user and page context to the view.
     *
     * @param model     The Spring model used to pass data to the view.
     * @param principal Contains the name of the currently authenticated user.
     * @return The Thymeleaf view name for the add-income form page.
     */
    @GetMapping("/add-income")
    public String addIncome(Model model, Principal principal) {

        // Get the currently authenticated user's name and retrieve user details from the repository
        String name = principal.getName();
        User user = this.userRepository.getUserByName(name);

        // Add user details and page-specific information to the model
        model.addAttribute("user", user); // Pass the user details to the view
        model.addAttribute("page", "addIncome"); // Use this for setting the active page in the UI (CSS styling)

        // Create and add a new empty Income object for form binding
        model.addAttribute("income", new Income());

        // Return the Thymeleaf template for the add-income form page
        return "user-access-url/add-income";
    }




    /**
     * Handles the submission of the income form.
     * Validates input, attaches user details, and saves the income entry.
     *
     * @param income         The submitted income form, validated.
     * @param bindingResult  Holds the validation results from the form.
     * @param model          The Spring model to pass data to the view.
     * @param principal      Contains the name of the currently authenticated user.
     * @param session        The current HTTP session for displaying feedback messages.
     * @return The Thymeleaf view for the add-income page (same page, with feedback).
     */
    @PostMapping("/add-income-process")
    public String addIncomeForm(
            @Valid @ModelAttribute("income") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Income income,
            BindingResult bindingResult,
            Model model,
            Principal principal,
            HttpSession session) {

        // Get the currently authenticated user's name and retrieve user details from the repository
        String name = principal.getName();
        User user = this.userRepository.getUserByName(name);

        try {
            /**
             * If validation errors exist in the form, return to the same page with an error message.
             */
            if (bindingResult.hasErrors()) {
                session.setAttribute("customMessage", new CustomDisplayMessage(
                        "Something went wrong",
                        "alert-danger"
                ));
                model.addAttribute("page", "addIncome"); // for page-specific CSS (Active page on UI)
                return "user-access-url/add-income";
            }

            /**
             * If a date was entered, convert it to a LocalDateTime using the current time.
             * This ensures that we save the precise timestamp (with current time) even if only the date is selected.
             */
            if (income.getDate() != null) {
                LocalTime currentTime = LocalTime.now();
                income.setLocalDateTime(LocalDateTime.of(income.getDate(), currentTime));
            }

            // Attach the user and save the income entry to the repository
            income.setUser(user);
            this.incomeRepository.save(income);

            // Success message after saving the income entry
            session.setAttribute("customMessage", new CustomDisplayMessage(
                    "Income entry added successfully.",
                    "alert-success"
            ));

            System.out.println(income.getDate());  // For debugging purposes, prints the date

        } catch (Exception e) {
            // Catch any unexpected errors and print stack trace for debugging
            e.printStackTrace();
        }

        // Add user info back to the model for consistency
        model.addAttribute("user", user);

        model.addAttribute("page", "addIncome"); // for page-specific CSS (Active page on UI)

        // Return to the same form page (could be improved to redirect for PRG pattern)
        return "user-access-url/add-income";
    }




    /**
     * Displays the user's income history with optional filters for amount range and date range.
     * The results are paginated and sorted in descending order by date.
     * Filters are applied only if provided by the user.
     *
     * @param model        The Spring model to pass data to the view.
     * @param principal    Contains the name of the currently authenticated user.
     * @param minAmount    Optional filter for minimum income amount.
     * @param maxAmount    Optional filter for maximum income amount.
     * @param startDate    Optional filter for the start date of the income entries.
     * @param endDate      Optional filter for the end date of the income entries.
     * @param page         The page number for pagination.
     * @return The Thymeleaf view for the income history page, with filtered and paginated results.
     */
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
        try {
            // Retrieve the name of the currently authenticated user
            String name = principal.getName();
            User user = userRepository.getUserByName(name);

            // Add user details and page-specific information to the model
            model.addAttribute("user", user);
            model.addAttribute("page", "incomeHistory");

            // Set up pagination with a page size of 6, sorted by dateTime in descending order
            Pageable pageable = PageRequest.of(page, 6, Sort.by(Sort.Direction.DESC, "dateTime"));

            Specification<Income> spec;

            // Determine if no filters are applied (if all filter values are null)
            boolean noFilters = (
                    minAmount == null &&
                            maxAmount == null &&
                            startDate == null &&
                            endDate == null
            );

            // If no filters are applied, create a simple specification for retrieving incomes by user
            if (noFilters) {
                spec = (root, query, cb) -> cb.equal(root.get("user"), user);
            } else {
                // Otherwise, apply the filters using a custom specification
                spec = IncomeSpecification.withFilters(user, minAmount, maxAmount, startDate, endDate);
            }

            // Retrieve the filtered and paginated income entries
            Page<Income> filteredIncomes = incomeRepository.findAll(spec, pageable);

            // Add filtered results and pagination details to the model
            model.addAttribute("userTransactions", filteredIncomes);
            model.addAttribute("totalPages", filteredIncomes.getTotalPages());
            model.addAttribute("currentPage", page);

            // Preserve filter values for displaying in the form
            model.addAttribute("minAmount", minAmount);
            model.addAttribute("maxAmount", maxAmount);
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);

            // Return the Thymeleaf template for displaying the income history
            return "user-access-url/income-history";

        } catch (Exception e) {
            // Log the error for debugging purposes
            e.printStackTrace();

            // Add a custom error message to the model
            model.addAttribute("customMessage", new CustomDisplayMessage("An error occurred while fetching the income history. Please try again.", "alert-danger"));

            // Return the error page or redirect as needed
            return "error-page";  // Adjust this based on your error handling strategy
        }
    }




    /**
     * Deletes an income entry by its ID.
     *
     * @param id The ID of the income entry to be deleted.
     * @return A ResponseEntity containing the result of the deletion operation.
     */
    @DeleteMapping("/deleteIncome/{id}")
    public ResponseEntity<String> deleteIncome(@PathVariable("id") int id) {
        try {
            // Check if the income entry exists before attempting to delete
            Optional<Income> income = incomeRepository.findById(id);

            if (!income.isPresent()) {
                // If the income entry is not found, return a 404 (Not Found) response
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Income entry not found.");
            }

            // Proceed to delete the income entry if it exists
            boolean isDeleted = incomeService.deleteIncomeById(id);

            if (isDeleted) {
                // Success response with a custom message
                return ResponseEntity.ok("Income entry deleted successfully.");
            } else {
                // Failure response in case of an internal issue
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete income entry.");
            }

        } catch (Exception e) {
            // Log the exception and return a generic error message
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while trying to delete the income entry.");
        }
    }


    /**
     * Handles the update of an existing income entry.
     *
     * @param incomeId       The ID of the income to be updated.
     * @param sourceNameChange The updated source name.
     * @param amountChange   The updated amount.
     * @param model          The model to pass data to the view.
     * @param principal      Contains the name of the currently authenticated user.
     * @return A redirect to the income history page.
     */
    @PostMapping("/updateIncome/{incomeId}")
    public String updateIncome(@PathVariable("incomeId") int incomeId,
                               @RequestParam String sourceNameChange,
                               @RequestParam double amountChange,
                               Model model, Principal principal) {

        // Get the currently authenticated user's name
        String name = principal.getName();
        User user = this.userRepository.getUserByName(name);
        model.addAttribute("user", user);

        // Find the existing income record
        Income existingIncome = incomeRepository.searchIncomeByIncomeId(incomeId);

        if (existingIncome == null) {
            // If the income is not found, show an error message
            model.addAttribute("customMessage", new CustomDisplayMessage("Income entry not found.", "alert-danger"));
            return "redirect:/user/income-history/0";  // Redirect back to the income history page
        }

        try {
            // Retain the original fields (date, description)
            LocalDateTime originalDateTime = existingIncome.getLocalDateTime();
            String originalDescription = existingIncome.getDescription();
            LocalDate originalDate = originalDateTime.toLocalDate();

            // Update the income with the new details
            existingIncome.setLocalDateTime(originalDateTime); // Retain original date
            existingIncome.setDescription(originalDescription); // Retain original description
            existingIncome.setDate(originalDate);
            existingIncome.setSource(sourceNameChange);
            existingIncome.setAmount(amountChange);
            existingIncome.setUser(user);  // Ensure the user is associated with the income entry

            // Save the updated income entry
            incomeRepository.save(existingIncome);

            // Optional: Save the user again if needed (though it's likely not necessary in this case)
            // userRepository.save(user);

            // Optional: Add a success message
            model.addAttribute("customMessage", new CustomDisplayMessage("Income entry updated successfully.", "alert-success"));

        } catch (Exception e) {
            // Log the exception and provide a failure message
            e.printStackTrace();
            model.addAttribute("customMessage", new CustomDisplayMessage("An error occurred while updating the income entry.", "alert-danger"));
        }

        // Redirect back to the income history page
        return "redirect:/user/income-history/0";
    }






    //----------------------------------------------------------------------------------------------------------------

    /**
     * Displays the "Add Expense" form for the user, including a list of categories.
     *
     * @param model     The Spring model used to pass data to the view.
     * @param principal Contains the name of the currently authenticated user.
     * @return The Thymeleaf template for the "Add Expense" form page.
     */
    @GetMapping("/add-expense")
    public String addExpense(Model model, Principal principal) {
        // Get the currently authenticated user's name
        String username = principal.getName();

        // Fetch the user from the repository using the username
        User user = this.userRepository.getUserByName(username);

        if (user == null) {
            // Handle case where the user doesn't exist (in case of authentication issues)
            model.addAttribute("customMessage", new CustomDisplayMessage("User not found.", "alert-danger"));
            return "redirect:/login";  // Redirect to login page if the user is not found
        }

        // Get the list of categories associated with the user
        List<Category> categoryList = this.categoryRepository.ListOfCategoryByUser(user.getUserId());

        // Add the categories, user, and other necessary attributes to the model
        model.addAttribute("categories", categoryList);
        model.addAttribute("user", user);
        model.addAttribute("page", "addExpense"); // Add the page-specific CSS class
        model.addAttribute("expense", new Expense()); // Initialize an empty Expense object

        // Return the Thymeleaf view for the "Add Expense" form
        return "user-access-url/add-expense";
    }



    /**
     * Handles the submission of the expense form, including validation and saving the expense.
     *
     * @param expense        The submitted expense form (validated).
     * @param bindingResult  Holds validation results.
     * @param model          The Spring model to pass data to the view.
     * @param principal      Contains the name of the currently authenticated user.
     * @return The Thymeleaf template for the add-expense form page (with validation feedback).
     */
    @PostMapping("/add-expense-process")
    public String addExpenseForm(@Valid @ModelAttribute("expense") Expense expense,
                                 BindingResult bindingResult,
                                 Model model,
                                 Principal principal) {

        String username = principal.getName();
        User user = this.userRepository.getUserByName(username);
        model.addAttribute("user", user);

        // If there are validation errors, return the form with error messages
        if (bindingResult.hasErrors()) {
            List<Category> categoryList = this.categoryRepository.ListOfCategoryByUser(user.getUserId());
            model.addAttribute("categories", categoryList);
            model.addAttribute("page", "addExpense");
            return "user-access-url/add-expense";
        }

        // Set user and datetime for the expense
        expense.setUser(user);
        if (expense.getDate() != null) {
            expense.setLocalDateTime(LocalDateTime.of(expense.getDate(), LocalTime.now()));
        }

        // Save the expense to the repository
        this.expenseRepository.save(expense);

        // Optional: Add a success message if needed
        model.addAttribute("customMessage", new CustomDisplayMessage("Expense added successfully!", "alert-success"));

        // Redirect to the expense history page after adding the expense (PRG Pattern)
        return "redirect:/user/expense-history/0";  // Adjust the URL as needed for your history page
    }


    /**
     * Handles the display of the expense history for the currently authenticated user.
     *
     * This method supports various filters such as categories, amount ranges, payment methods,
     * and date ranges to allow the user to view their expenses based on selected criteria.
     * It also implements pagination for displaying expenses across multiple pages.
     *
     * @param model          The Spring model to pass data to the view.
     * @param principal      The currently authenticated user.
     * @param categoryNames  List of category names for filtering expenses (optional).
     * @param minAmount      The minimum amount for filtering expenses (optional).
     * @param maxAmount      The maximum amount for filtering expenses (optional).
     * @param paymentMethod  The payment method for filtering expenses (optional).
     * @param startDate      The start date for filtering expenses (optional).
     * @param endDate        The end date for filtering expenses (optional).
     * @param page           The current page number for pagination.
     * @return The Thymeleaf template for the expense history page.
     */
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
        // Get the currently authenticated user's details
        String name = principal.getName();
        User user = userRepository.getUserByName(name);

        model.addAttribute("user", user);
        model.addAttribute("page", "expenseHistory");

        Pageable pageable = PageRequest.of(page, 6, Sort.by(Sort.Direction.DESC, "dateTime"));

        // Initialize categories list
        List<Category> categories = new ArrayList<>();
        try {
            // Get the categories for filtering (if any)
            if (categoryNames != null && !categoryNames.isEmpty()) {
                categories = categoryRepository.findByCategoryNameInAndUserId(categoryNames, user.getUserId());
            }
        } catch (Exception e) {
            // Log the error and show a user-friendly message
            e.printStackTrace();
            model.addAttribute("errorMessage", "Failed to load categories. Please try again later.");
        }

        // Determine if any filters are applied
        boolean noFilters = (categoryNames == null || categoryNames.isEmpty()) &&
                minAmount == null &&
                maxAmount == null &&
                (paymentMethod == null || paymentMethod.isEmpty()) &&
                startDate == null &&
                endDate == null;

        // Create the specification based on filter conditions
        Specification<Expense> spec = null;
        try {
            if (noFilters) {
                spec = (root, query, cb) -> cb.equal(root.get("user"), user);  // No filters applied, get all expenses for user
            } else {
                spec = ExpenseSpecifications.withFilters(user, categories, minAmount, maxAmount, paymentMethod, startDate, endDate);
            }
        } catch (Exception e) {
            // Log the error and show a user-friendly message
            e.printStackTrace();
            model.addAttribute("errorMessage", "Failed to apply filters. Please try again.");
        }

        // Fetch filtered expenses from the repository
        Page<Expense> filteredExpenses = null;
        try {
            filteredExpenses = expenseRepository.findAll(spec, pageable);
        } catch (Exception e) {
            // Log the error and show a user-friendly message
            e.printStackTrace();
            model.addAttribute("errorMessage", "Failed to fetch expense records. Please try again later.");
        }
        
        
        

        if (filteredExpenses != null) {
            // Add attributes to the model
            model.addAttribute("userTransactions", filteredExpenses);
            model.addAttribute("userCategories", categoryRepository.ListOfCategoryByUser(user.getUserId()));
            model.addAttribute("totalPages", filteredExpenses.getTotalPages());
            model.addAttribute("currentPage", page);
        }

        // Preserve the filter parameters in the model
        model.addAttribute("filterCategories", categoryNames);
        model.addAttribute("minAmount", minAmount);
        model.addAttribute("maxAmount", maxAmount);
        model.addAttribute("paymentMethod", paymentMethod);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "user-access-url/expense-history";
    }




    /**
     * Handles the deletion of an expense entry based on the provided expense ID.
     *
     * This method deletes an expense from the database using the provided ID. It returns
     * a response indicating whether the deletion was successful or not.
     *
     * @param id The ID of the expense to be deleted.
     * @return A ResponseEntity with a status code and a message indicating the result of the deletion operation.
     */
    @DeleteMapping("/deleteExpense/{id}")
    public ResponseEntity<String> deleteExpense(@PathVariable("id") int id) {
        // Call the service layer to delete the expense by its ID
        boolean isDeleted = expenseService.deleteIncomeById(id);

        if (isDeleted) {
            // If deletion is successful, return a success response
            return ResponseEntity.ok("Income entry deleted successfully.");
        } else {
            // If deletion fails, return an error response with a failure message
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete expense entry.");
        }
    }


    /**
     * Handles the updating of an existing expense entry based on the provided expense ID.
     *
     * This method updates the details of an expense entry (title and amount), while preserving
     * the original date and description. After updating, it redirects the user back to the expense
     * history page.
     *
     * @param expenseId    The ID of the expense entry to be updated.
     * @param titleChange  The new title (name) for the expense.
     * @param amountChange The new amount for the expense.
     * @param model        The Spring model to pass data to the view.
     * @param principal    The currently authenticated user.
     * @return A redirect to the expense history page after updating the expense.
     */
    @PostMapping("/updateExpense/{expenseId}")
    public String updateExpense(@PathVariable("expenseId") int expenseId,
                                @RequestParam String titleChange,
                                @RequestParam double amountChange,
                                Model model, Principal principal) {

        // Get the username of the currently authenticated user
        String name = principal.getName();
        // Retrieve the user from the repository based on the username
        User user = this.userRepository.getUserByName(name);
        int id = user.getUserId();
        model.addAttribute("user", user);

        // Find the existing expense record by its ID
        Expense existingExpense = expenseRepository.searchExpenseByExpenseId(expenseId);

        try {
            // If the expense is found, proceed to update it
            if (existingExpense != null) {
                // Preserve the original date and description
                LocalDateTime originalDateTime = existingExpense.getLocalDateTime();  // Original date-time
                String originalDescription = existingExpense.getDescription(); // Original description
                LocalDate originalDate = originalDateTime.toLocalDate(); // Extract the original date

                // Set the new title and amount while keeping the original date and description
                existingExpense.setLocalDateTime(originalDateTime); // Retain the original date-time
                existingExpense.setDescription(originalDescription); // Retain the original description
                existingExpense.setDate(originalDate); // Retain the original date
                existingExpense.setName(titleChange); // Update the title (name)
                existingExpense.setAmount(amountChange); // Update the amount
                existingExpense.setUser(user); // Associate the updated expense with the current user

                // Save the updated expense back to the repository
                this.expenseRepository.save(existingExpense);
                this.userRepository.save(user); // Save any changes to the user (if needed)
            }
        } catch (Exception e) {
            // Print the stack trace if an exception occurs
            e.printStackTrace();
        }

        // Redirect back to the expense history page after the update
        return "redirect:/user/expense-history/0";  // Adjust the URL if needed
    }






    //----------------------------------------------------------------------------------------------------------------
    /**
     * Handles the display of categories for the currently authenticated user.
     *
     * This method supports pagination for displaying categories and provides
     * a search functionality to filter categories by a search term. If no search
     * term is provided, it returns a paginated list of categories; otherwise, it
     * returns categories that match the search term.
     *
     * @param page    The current page number for pagination.
     * @param search  The search term for filtering categories (optional).
     * @param model   The Spring model to pass data to the view.
     * @param principal The currently authenticated user.
     * @return The Thymeleaf template for displaying the user's categories.
     */
    @GetMapping("/category/{page}")
    public String category(@PathVariable("page") Integer page,
                           @RequestParam(name = "search", required = false) String search,
                           Model model, Principal principal){

        // Get the username of the currently authenticated user
        String name = principal.getName();
        // Retrieve the user from the repository based on the username
        User user = this.userRepository.getUserByName(name);

        int id = user.getUserId();

        // If no search term is provided, retrieve categories with pagination
        if(search == null || search.isEmpty()){

            // Set up pagination (8 categories per page)
            Pageable pageable = PageRequest.of(page, 8);

            // Retrieve the paginated categories for the user
            Page<Category> CategoryList = this.categoryRepository.findCategoriesByUser(id, pageable);

            // If no categories are found, set totalPages to 1 to prevent pagination errors
            int totalPages = (CategoryList.getTotalElements() == 0) ? 0 : CategoryList.getTotalPages();

            // Add the categories and pagination information to the model
            model.addAttribute("totalPages", CategoryList.getTotalPages());
            model.addAttribute("categories", CategoryList);
        }
        // If a search term is provided, search for categories matching the term
        else {
            Category categories = categoryService.searchCategories(search, id);
            model.addAttribute("categories", categories);
        }

        // Add the current page, user, and category model to the view
        model.addAttribute("currentPage", page);
        model.addAttribute("user", user);
        model.addAttribute("category", new Category()); // To initialize a new Category object
        model.addAttribute("page", "category");

        // Return the Thymeleaf view for the user's category page
        return "user-access-url/view-user-category";
    }


    /**
     * Handles the saving of a new category for the currently authenticated user.
     *
     * This method processes the form submission for creating a new category. It checks
     * whether the category already exists for the user. If not, it saves the category
     * to the database and redirects back to the category page. If the category already
     * exists, it simply redirects back to the category page.
     *
     * @param category             The category object to be saved.
     * @param model                The Spring model to pass data to the view.
     * @param principal            The currently authenticated user.
     * @param session              The HTTP session for the current user session.
     * @param categoryName         The name of the category entered by the user.
     * @param categoryMonthlyBudget The monthly budget for the category.
     * @return A redirect to the category page after processing the form.
     */
    @PostMapping("/process-save-category")
    public String saveCategory(@ModelAttribute("category") Category category,
                               Model model, Principal principal,
                               HttpSession session,
                               @RequestParam("categoryName") String categoryName,
                               @RequestParam("categoryMonthlyBudget") double categoryMonthlyBudget) {

        try {
            // Retrieve the current authenticated user's username
            String name = principal.getName();
            // Get the user details from the repository based on the username
            User user = this.userRepository.getUserByName(name);
            model.addAttribute("user", user);

            // Check if the category with the provided name already exists for the user
            boolean isCategoryAlreadyExisted = categoryRepository.isThisCategoryExisted(categoryName.toUpperCase(), user.getUserId());

            // If the category does not exist, proceed to save it
            if (!isCategoryAlreadyExisted) {
                // Set the user and other category properties
                category.setUser(user);
                category.setCategoryName(categoryName.toUpperCase());
                category.setCategoryMonthlyBudget(categoryMonthlyBudget);

                // Save the new category to the database
                this.categoryRepository.save(category);
                this.userRepository.save(user);  // Save the user (if any updates are necessary)
            } else {
                // If the category already exists, redirect to the category page without saving
                return "redirect:/user/category/0";  // Redirect back to category page after checking existence
            }
        } catch (Exception e) {
            // Print any exceptions that occur for troubleshooting
            e.printStackTrace();
        }

        // Redirect back to the category page after the category has been saved
        return "redirect:/user/category/0";  // Return to the category page after saving
    }



    /**
     * Handles the deletion of a category based on its name.
     *
     * This method attempts to delete a category from the database using the provided category name.
     * It returns a response indicating whether the deletion was successful or not.
     *
     * @param name The name of the category to be deleted.
     * @return A ResponseEntity with a status code and a message indicating the result of the deletion operation.
     */
    @DeleteMapping("/deleteCategory/{name}")
    public ResponseEntity<String> deleteCategory(@PathVariable("name") String name) {
        // Call the service to delete the category by its name
        boolean isDeleted = categoryService.deleteCategoryByName(name);

        if (isDeleted) {
            // If deletion is successful, return a success response
            return ResponseEntity.ok("Category deleted successfully.");
        } else {
            // If deletion fails, return an error response with a failure message
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete category.");
        }
    }


    /**
     * Handles the updating of an existing category for the currently authenticated user.
     *
     * This method allows the user to update the name and monthly budget of a category.
     * After updating the category, it redirects back to the category list page.
     *
     * @param category             The category object being updated.
     * @param categoryName         The original name of the category to be updated.
     * @param categoryNameChange   The new name for the category.
     * @param categoryMonthlyBudget The new monthly budget for the category.
     * @param model                The Spring model to pass data to the view.
     * @param principal            The currently authenticated user.
     * @return A redirect to the category page after updating the category.
     */
    @PostMapping("/updateCategory/{name}")
    public String updateCategory(@ModelAttribute("category") Category category,
                                 @PathVariable("name") String categoryName,
                                 @RequestParam String categoryNameChange,
                                 @RequestParam double categoryMonthlyBudget,
                                 Model model, Principal principal) {

        // Retrieve the currently authenticated user's details using their username
        String name = principal.getName();
        User user = this.userRepository.getUserByName(name);
        int id = user.getUserId();

        // Add the user and the category to the model for view access
        model.addAttribute("user", user);
        model.addAttribute("category", category);

        // Logic to find the existing category by its original name and user ID
        category = categoryService.searchCategories(categoryName, id);

        // Update the category with the new name and monthly budget
        category.setCategoryName(categoryNameChange);
        category.setCategoryMonthlyBudget(categoryMonthlyBudget);

        // Save the updated category to the database
        this.categoryRepository.save(category);

        // Redirect back to the category page after the update
        return "redirect:/user/category/0";  // Redirect to the category page after updating
    }


//----------------------------------------------------------------------------------------------------------------

    /**
     * Generates a report based on the selected period (monthly, quarterly, or yearly).
     * Displays total income, total expense, and net savings for the selected period.
     * Also generates line and pie charts for trends, as well as top spending categories.
     *
     * @param period    The period for the report (monthly, quarterly, or yearly).
     * @param model     The Spring model to pass data to the view.
     * @param principal The currently authenticated user.
     * @return The view name for the reports page.
     */
    @GetMapping("/reports")
    public String reports(@RequestParam(name = "period", required = false, defaultValue = "monthly") String period,
                          Model model, Principal principal) {

        // Get the currently authenticated user's details
        String name = principal.getName();
        User user = this.userRepository.getUserByName(name);

        // Initialize variables for total income, total expense, and net savings
        Double totalExpense = null;
        Double totalIncome = null;
        Double netSavings = null;

        // Set the time range for the current month
        LocalDateTime startOfMonth = YearMonth.now().atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = YearMonth.now().atEndOfMonth().atTime(LocalTime.MAX);

        // Get the current month and year
        int month = LocalDate.now().getMonthValue();
        int year = LocalDate.now().getYear();

        // Switch based on the selected period (monthly, quarterly, yearly)
        switch (period) {
            case "monthly":
                // Calculate monthly totals
                totalExpense = this.expenseRepository.findSumOfExpensesForCurrentMonth(user, startOfMonth, endOfMonth);
                totalIncome = this.incomeRepository.findSumOfIncomeForCurrentMonth(user, startOfMonth, endOfMonth);
                totalExpense = (totalExpense == null) ? 0.0 : totalExpense;
                totalIncome = (totalIncome == null) ? 0.0 : totalIncome;
                netSavings = totalIncome - totalExpense;
                netSavings = (netSavings <= 0.0) ? 0.0 : netSavings;
                break;

            case "quarterly":
                // Set the time range for the current quarter
                LocalDate startDate = null;
                LocalDate endDate = null;

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

                // Convert start and end date to LocalDateTime
                LocalDateTime startOfQuarter = startDate.atStartOfDay();
                LocalDateTime endOfQuarter = endDate.atTime(LocalTime.MAX);

                // Calculate quarterly totals
                totalExpense = this.expenseRepository.findSumOfExpenseForCurrentQuarter(user, startOfQuarter, endOfQuarter);
                totalIncome = this.incomeRepository.findSumOfIncomeForCurrentQuarter(user, startOfQuarter, endOfQuarter);
                totalExpense = (totalExpense != null) ? totalExpense : 0.0;
                totalIncome = (totalIncome != null) ? totalIncome : 0.0;
                netSavings = totalIncome - totalExpense;
                netSavings = (netSavings <= 0.0) ? 0.0 : netSavings;
                break;

            case "yearly":
                // Set the time range for the current year
                LocalDate startDateYear = LocalDate.of(year, 1, 1);
                LocalDate endDateYear = LocalDate.of(year, 12, 31);

                // Convert start and end date to LocalDateTime
                LocalDateTime startOfYear = startDateYear.atStartOfDay();
                LocalDateTime endOfYear = endDateYear.atTime(LocalTime.MAX);

                // Calculate yearly totals
                totalIncome = this.incomeRepository.findSumOfIncomeForCurrentYear(user, startOfYear, endOfYear);
                totalExpense = this.expenseRepository.findSumOfExpenseForCurrentYear(user, startOfYear, endOfYear);
                totalExpense = (totalExpense != null) ? totalExpense : 0.0;
                totalIncome = (totalIncome != null) ? totalIncome : 0.0;
                netSavings = totalIncome - totalExpense;
                netSavings = (netSavings <= 0.0) ? 0.0 : netSavings;
                break;

            default:
                System.out.println("Invalid option.");
                break;
        }

        // Set dynamic period labels for display
        String periodLabel = switch (period.toLowerCase()) {
            case "monthly" -> "current month";
            case "quarterly" -> "current quarter";
            case "yearly" -> "current year";
            default -> "Selected Period";
        };

        // Add basic report data to the model
        model.addAttribute("user", user);
        model.addAttribute("page", "reports");
        model.addAttribute("period", period);
        model.addAttribute("totalExpense", totalExpense);
        model.addAttribute("totalIncome", totalIncome);
        model.addAttribute("netSavings", netSavings);
        model.addAttribute("periodLabel", periodLabel);

        // Prepare descriptions for the charts
        String periodLabel2Description = periodLabel + " Expense Trend";
        String periodTrendDescription = "Line chart showing " + periodLabel.toLowerCase() + " expense trends";
        String periodLabel3Description = "Pie chart showing distribution of expenses across categories " + periodLabel.toLowerCase();

        model.addAttribute("periodTrendDescription", periodTrendDescription);
        model.addAttribute("periodLabel2Description", periodLabel2Description);
        model.addAttribute("periodLabel3Description", periodLabel3Description);

        // Fetch data for line and pie charts (income and expense trends by month, quarter, or year)
        List<Double> income;
        List<Double> expense;
        List<String> months;
        List<String> quarters;
        List<String> years;

        // Fetch data based on the selected period
        switch (period.toLowerCase()) {
            case "monthly" -> {
                income = this.reportService.getMonthlyIncomeSums(user);
                expense = this.reportService.getMonthlyExpenseSums(user);
                months = Arrays.asList("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");
                model.addAttribute("income", income);
                model.addAttribute("expense", expense);
                model.addAttribute("month", months);

                Map<String, Double> expensesByCategory = reportService.getCategoryAndAmountOfCurrentMonth(user);
                model.addAttribute("categories", new ArrayList<>(expensesByCategory.keySet()));
                model.addAttribute("expenses", new ArrayList<>(expensesByCategory.values()));
                break;
            }
            case "quarterly" -> {
                income = this.reportService.getQuarterlyIncomeSums(user);
                expense = this.reportService.getQuarterlyExpenseSums(user);
                quarters = Arrays.asList("Q1", "Q2", "Q3", "Q4");
                model.addAttribute("income", income);
                model.addAttribute("expense", expense);
                model.addAttribute("quarter", quarters);

                Map<String, Double> expensesByCategory = reportService.getCategoryAndAmountOfCurrentQuarter(user);
                model.addAttribute("categories", new ArrayList<>(expensesByCategory.keySet()));
                model.addAttribute("expenses", new ArrayList<>(expensesByCategory.values()));
                break;
            }
            case "yearly" -> {
                income = this.reportService.getYearlyIncomeSums(user);
                expense = this.reportService.getYearlyExpenseSums(user);

                // Get the current year and generate the year list
                int currentYear = Year.now().getValue();
                years = new ArrayList<>();
                for (int i = -4; i <= 0; i++) {
                    years.add(String.valueOf(currentYear + i));
                }

                model.addAttribute("income", income);
                model.addAttribute("expense", expense);
                model.addAttribute("year", years);

                Map<String, Double> expensesByCategory = reportService.getCategoryAndAmountOfCurrentYear(user);
                model.addAttribute("categories", new ArrayList<>(expensesByCategory.keySet()));
                model.addAttribute("expenses", new ArrayList<>(expensesByCategory.values()));
                break;
            }
            default -> {
                System.out.println("Invalid period.");
            }
        }

        // Fetch top spending categories based on the selected period
        Double totalExpenseByUser = null;
        Map<String, Double> currentCategorySums = null;
        Map<String, List<Double>> currentCategorySumsMonth = null;
        Map<String, Double> pastCategorySums = null;
        switch (period.toLowerCase()) {
            case "monthly" -> {
                currentCategorySumsMonth = this.categoryService.getCurrentMonthCategorySums(user);
                pastCategorySums = this.categoryService.getPastMonthCategorySums(user);
                totalExpenseByUser = this.expenseRepository.findSumOfExpensesCurrentMonth(user);
                model.addAttribute("currentCategorySums", currentCategorySumsMonth);
                break;
            }
            case "quarterly" -> {
                currentCategorySums = this.categoryService.getCurrentQuarterCategorySums(user);
                pastCategorySums = this.categoryService.getPastQuarterCategorySums(user);
                totalExpenseByUser = this.expenseRepository.findSumOfExpensesCurrentQuarter(user);
                model.addAttribute("currentCategorySums", currentCategorySums);
                break;
            }
            case "yearly" -> {
                currentCategorySums = this.categoryService.getCurrentYearCategorySums(user);
                pastCategorySums = this.categoryService.getPastYearCategorySums(user);
                totalExpenseByUser = this.expenseRepository.findSumOfExpensesCurrentYear(user);
                model.addAttribute("currentCategorySums", currentCategorySums);
                break;
            }
        }

        // Add top spending categories data to the model
        totalExpenseByUser = (totalExpenseByUser != null) ? totalExpenseByUser : 0.0;
        currentCategorySums = (currentCategorySums != null) ? currentCategorySums : new HashMap<>();
        pastCategorySums = (pastCategorySums != null) ? pastCategorySums : new HashMap<>();

       
        
        model.addAttribute("pastCategorySums", pastCategorySums);
        model.addAttribute("totalExpenseByUser", totalExpenseByUser);

        return "user-access-url/reports"; // Return the view name for reports page
    }



    @PostMapping("/save-chart-image")
    public String saveChartImage(@RequestBody ChartImageService chartImageServicerequest) {

        String chartType = chartImageServicerequest.getChartType();
        String imgData = chartImageServicerequest.getChartImgData();
        String tableData = chartImageServicerequest.getTableImgData();

        // Decode the base64 image data
        String base64Image = imgData.split(",")[1];
        String base64Table = tableData.split(",")[1];

        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
        byte[] tableBytes = Base64.getDecoder().decode(base64Table);

        // Define folder paths where the images will be stored
        String folderChart = chartType.equals("line-chart-image") ? lineChartImagePath : pieChartImagePath;
        String folderTable = chartType.equals("line-chart-image") ? lineTableImagePath : pieTableImagePath;

        // Create the directories if they don't exist
        File directory1 = new File(folderChart);
        File directory2 = new File(folderTable);

        if (!directory1.exists()) {
            directory1.mkdir();
        }

        if (!directory2.exists()) {
            directory2.mkdir();
        }

        // Generate unique file names based on the current timestamp
        String timestamp = String.valueOf(System.currentTimeMillis());
        Path targetLocation1 = Paths.get(directory1.getAbsolutePath() + File.separator + "chart.png");
        Path targetLocation2 = Paths.get(directory2.getAbsolutePath() + File.separator + "table.png");

        try {
            // Save the images
            Files.write(targetLocation1, imageBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            Files.write(targetLocation2, tableBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            // Optionally, add a success message or log the success
            return "redirect:/user/reports?success=true"; // You can add query params for success notification
        } catch (IOException e) {
            // Optionally, log the error or send a failure message
            return "redirect:/user/reports?error=true"; // Or show a different view for error feedback
        }
    }




    @GetMapping("/export-pdf")
    public void generateReport(@RequestParam("period") String period,Model model,Principal pricipal,
                                 HttpServletResponse response) throws IOException, DocumentException {
        
        String name  = pricipal.getName();
        User user = this.userRepository.getUserByName(name);
        
        this.pdfService.generatePdf(response,period,user);  // Generates the PDF
        
    }



}
