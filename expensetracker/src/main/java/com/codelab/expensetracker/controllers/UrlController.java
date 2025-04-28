package com.codelab.expensetracker.controllers;

import com.codelab.expensetracker.helper.CustomDisplayMessage;
import com.codelab.expensetracker.helper.OTPgenerator;
import com.codelab.expensetracker.models.User;
import com.codelab.expensetracker.repositories.UserRepository;
import com.codelab.expensetracker.securityconfiguration.CustomUserDetailsService;
import com.codelab.expensetracker.services.EmailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import javax.validation.Valid;



/**
 * UrlController handles all the user-related actions like registration, login, password reset,
 * and OTP verification. It communicates with the service layer and the repository layer to
 * process user requests and returns the appropriate views or redirects.
 */
@Controller
public class UrlController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private OTPgenerator otpGenerator;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    /**
     * Displays the registration form to the user.
     *
     * @param model The Model object that will hold attributes for the view.
     * @return The registration page view.
     */
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("title", "Register Page");
        model.addAttribute("user", new User());  // Initialize a new user object for the form
        model.addAttribute("page","register");
        return "open-url/register";
    }

    /**
     * Handles the form submission for user registration.
     * Validates the input and saves the user if everything is correct.
     *
     * @param user The user object with the form data.
     * @param result The result of the validation.
     * @param agreement The checkbox indicating whether the user agrees to terms and conditions.
     * @param model The Model object to pass data to the view.
     * @param session The HTTP session to store custom messages.
     * @return The appropriate page based on the result of the registration.
     */
    @PostMapping("/signup")
    public String register(@Valid @ModelAttribute("user") User user,
                           BindingResult result,
                           @RequestParam(value = "registercheckbox", defaultValue = "false") boolean agreement,
                           Model model,
                           HttpSession session) {

        try {
            // Check if user has agreed to the terms and conditions
            if (!agreement) {
                session.setAttribute("customMessage", new CustomDisplayMessage("Please agree to the terms and conditions", "alert-danger"));
                throw new Exception("Please agree to the terms and conditions");
            }

            // Validate that password and confirm password are not empty and match
            if (user.getUserPassword() != null && user.getConfirmPassword() != null) {
                if (!user.getUserPassword().equals(user.getConfirmPassword())) {
                    result.rejectValue("confirmPassword", "error.user", "Passwords do not match.");
                }
            } else {
                result.rejectValue("userPassword", "error.user", "Password cannot be empty.");
                result.rejectValue("confirmPassword", "error.user", "Confirm password cannot be empty.");
            }

            // If validation errors exist, return to the registration form
            if (result.hasErrors()) {
                model.addAttribute("page","register");
                return "open-url/register";
            }

            // Set default properties for the new user
            user.setUserRole("ROLE_USER");
            user.setUserStatus(true);
            user.setUserImageURL("userImageDefault.jpg");

            // Encrypt the password before saving to the database
            user.setUserPassword(bCryptPasswordEncoder.encode(user.getUserPassword()));

            // Save the user to the database
            this.userRepository.save(user);

            // Add user data and success message to session
            model.addAttribute("user", user);
            session.setAttribute("customMessage", new CustomDisplayMessage("Successfully Registered - Please login to proceed", "alert-success"));

        } catch (DataIntegrityViolationException e) {
            // Handle email already taken error
            session.setAttribute("customMessage", new CustomDisplayMessage("This user email is already registered", "alert-danger"));
            model.addAttribute("page","register");
            return "open-url/register";
        } catch (Exception e) {
            // Handle generic error
            e.printStackTrace();
            session.setAttribute("customMessage", new CustomDisplayMessage("Something went wrong", "alert-danger"));
            return "open-url/register";
        }
        
        model.addAttribute("page","login");

        // Redirect to login page after successful registration
        return "open-url/login";
    }

    /**
     * Displays the login form to the user.
     *
     * @param model The Model object that will hold attributes for the view.
     * @return The login page view.
     */
    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("title", "Login Page");
        model.addAttribute("user", new User());  // Initialize a new user object for the form
        model.addAttribute("page","login");
        return "open-url/login";
    }

    /**
     * Displays the 2FA verification page to the user.
     *
     * This method checks for the presence of a username in the session.
     * If the session is invalid or the user is not found, it redirects back to the login page with an appropriate message.
     *
     * @param model   The Model object to pass attributes to the view.
     * @param session The HttpSession to retrieve the username for 2FA verification.
     * @return The 2FA verification page view or redirect to the login page.
     */
    @GetMapping("/2fa-verification")
    public String show2faPage(Model model, HttpSession session) {
        try {
            // Retrieve the username stored in session after successful password login
            String username = (String) session.getAttribute("2fa_username");

            if (username == null) {
                // If session expired or user accessed 2FA page directly
                model.addAttribute("customMessage", new CustomDisplayMessage("Session expired. Please log in again.", "alert-danger"));
                return "redirect:/login";
            }

            // Fetch user details from the database
            User user = userRepository.getUserByName(username);

            if (user == null) {
                // User record does not exist in the database
                model.addAttribute("customMessage", new CustomDisplayMessage("User not found.", "alert-danger"));
                return "redirect:/login";
            }

            // Set attributes for the 2FA verification page
            model.addAttribute("page", "2faVerification");
            model.addAttribute("userEmail", username);

            return "open-url/2fa-verification";

        } catch (Exception e) {
            // Handle any unexpected exceptions (e.g., database connection issues)
            model.addAttribute("customMessage", new CustomDisplayMessage("An error occurred. Please try again later.", "alert-danger"));
            return "redirect:/login";
        }
    }


    /**
     * Verifies the 2FA OTP submitted by the user.
     *
     * If the OTP matches the one stored in the session, the user is authenticated manually
     * and redirected to the dashboard. Otherwise, the user is shown an error message.
     *
     * @param otpInput The OTP entered by the user.
     * @param session  The HttpSession to retrieve stored OTP and username.
     * @param model    The Model object to pass attributes to the view.
     * @return Redirects to dashboard on success or redisplays the 2FA page on failure.
     */
    @PostMapping("/verify-2fa-otp")
    public String verifyOtp(@RequestParam("otp") String otpInput, HttpSession session, Model model) {
        try {
            // Retrieve OTP and username from the session
            String otpSession = String.valueOf(session.getAttribute("otp"));
            String username = (String) session.getAttribute("2fa_username");

            // Check if session contains OTP and validate
            if (otpSession != null && otpSession.equals(otpInput)) {
                // OTP is correct: manually authenticate the user
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);

                // Clear sensitive session data
                session.removeAttribute("otp");
                session.removeAttribute("2fa_username");

                return "redirect:/user/dashboard";
            } else {
                // OTP did not match: show error message and reload 2FA page
                model.addAttribute("customMessage", new CustomDisplayMessage("Invalid OTP. Please try again.", "alert-danger"));
                model.addAttribute("page", "verifyOTP");
                model.addAttribute("userEmail", username);
                return "open-url/2fa-verification";
            }
        } catch (Exception e) {
            // Handle unexpected errors (e.g., userDetailsService failures)
            model.addAttribute("customMessage", new CustomDisplayMessage("An error occurred. Please try again later.", "alert-danger"));
            return "redirect:/login";
        }
    }


    /**
     * Handles resending a new 2FA OTP to the user's email.
     *
     * If the user exists, generates a new OTP and sends it to their email address.
     * Otherwise, shows an error message. If sending fails, displays an appropriate error message.
     *
     * @param model     The Model object to pass attributes to the view.
     * @param userEmail The email address of the user requesting a new OTP.
     * @param session   The HttpSession to store OTP and messages.
     * @return Returns the 2FA verification page view.
     */
    @PostMapping("/resend-2fa-otp")
    public String resend2FAOtp(Model model,
                               @RequestParam("userEmail") String userEmail,
                               HttpSession session) {
        try {
            // Check if the user exists
            User userexisted = userRepository.getUserByName(userEmail);

            if (userexisted == null) {
                // No user found: set error message in session
                session.setAttribute("customMessage", new CustomDisplayMessage("No user found with this email address", "alert-danger"));
            } else {
                // Generate new OTP
                int otp = otpGenerator.generateOTP();
                otpGenerator.setOtp(otp);

                try {
                    // Send OTP via email
                    emailService.sendEmail("noreply.cswiz@gmail.com", userEmail, "WELCOME", "Your new OTP is: " + otpGenerator.getOtp());
                } catch (Exception e) {
                    // Email sending failed: show error
                    session.setAttribute("customMessage", new CustomDisplayMessage("Failed to send OTP. Please try again.", "alert-danger"));
                    model.addAttribute("page", "verifyOTP");
                    return "open-url/2fa-verification";
                }

                // Store new OTP and username in session
                session.setAttribute("otp", otp);
                session.setAttribute("2fa_username", userEmail);

                // Set success message
                session.setAttribute("customMessage", new CustomDisplayMessage("A new OTP has been sent to your email.", "alert-success"));

                // Add email back to model for the verification page
                model.addAttribute("userEmail", userEmail);
            }

            model.addAttribute("page", "verifyOTP");
            return "open-url/2fa-verification";

        } catch (Exception e) {
            // Handle unexpected errors (e.g., database issues)
            model.addAttribute("customMessage", new CustomDisplayMessage("An unexpected error occurred. Please try again later.", "alert-danger"));
            return "redirect:/login";
        }
    }


    /**
     * Displays the Terms and Conditions page to the user.
     *
     * @param model The Model object that will hold attributes for the view.
     * @return The terms and conditions page view.
     */
    @GetMapping("/terms")
    public String termsAndConditions(Model model) {
        // Set the page title for the view
        model.addAttribute("title", "Terms And Conditions");

        // Initialize a new user object to bind to the form (if needed)
        model.addAttribute("user", new User());

        // Set the page identifier for the current page view (used in the view template)
        model.addAttribute("page", "terms");

        // Return the view name for terms and conditions page
        return "open-url/terms-and-conditions";
    }


    /**
     * Displays the forgot password page to the user.
     *
     * @param model The Model object that will hold attributes for the view.
     * @return The forgot password page view.
     */
    @GetMapping("/forgot-password")
    public String forgotPassword(Model model) {
        model.addAttribute("page","forgotPassword");
        return "open-url/forgot-password";
    }

    /**
     * Sends OTP for email verification when the user has forgotten their password.
     *
     * This method checks if the user exists, generates an OTP, and sends it via email.
     * If the OTP is successfully sent, the user is redirected to the OTP verification page.
     * Otherwise, an error message is shown.
     *
     * @param model The Model object that will hold attributes for the view.
     * @param user The user object containing the email provided by the user.
     * @param session The HTTP session to store custom messages.
     * @return The appropriate page after OTP is generated or if an error occurs.
     */
    @PostMapping("/forgot-password-otp")
    public String EmailVerificationForm(Model model,
                                        @ModelAttribute("user") User user,
                                        HttpSession session) {
        model.addAttribute("title", "Forgot Password");
        String userEmailTo = user.getUserEmail();

        // Check if the user exists in the database
        User userexisted = userRepository.getUserByName(userEmailTo);

        if (userexisted == null) {
            // If user not found, set error message and return to forgot password page
            session.setAttribute("customMessage", new CustomDisplayMessage("No user found with this email address", "alert-danger"));
            return "open-url/forgot-password";
        } else {
            // Generate and store OTP for email verification
            int number = otpGenerator.generateOTP();
            otpGenerator.setOtp(number);

            // Try sending the OTP via email
            try {
                emailService.sendEmail("noreply.cswiz@gmail.com", userEmailTo, "WELCOME", "Your one-time OTP is: " + otpGenerator.getOtp());
                // OTP sent successfully: set success message in session
                session.setAttribute("customMessage", new CustomDisplayMessage("OTP has been sent to your email.", "alert-success"));
            } catch (Exception e) {
                // If OTP sending fails, set error message and return to forgot password page
                session.setAttribute("customMessage", new CustomDisplayMessage("Failed to send OTP. Please try again.", "alert-danger"));
                return "open-url/forgot-password";
            }

            // Add email and page identifier to the model for OTP verification page
            model.addAttribute("userEmail", userEmailTo);
            model.addAttribute("page", "verifyOTP");
        }

        // Return the OTP verification page
        return "open-url/verify-OTP-FP";
    }


    /**
     * Verifies the OTP entered by the user during the password reset process.
     *
     * This method checks whether the OTP entered by the user matches the one stored in the session.
     * If the OTP is valid, the user is redirected to the password reset page; otherwise, an error message is shown.
     *
     * @param model The Model object that will hold attributes for the view.
     * @param userEmail The email address of the user requesting OTP verification.
     * @param otpEntered The OTP entered by the user for verification.
     * @param session The HTTP session to store custom messages and retrieve OTP.
     * @return The appropriate page based on OTP verification success or failure.
     */
    @PostMapping("/verify-otp")
    public String verifyOtp(Model model,
                            @RequestParam("userEmail") String userEmail,
                            @RequestParam("otp") int otpEntered,
                            HttpSession session) {
        model.addAttribute("title", "Verify OTP");

        // Retrieve OTP stored in the session or generated in the previous steps
        int generatedOtp = otpGenerator.getOtp();

        // Check if the entered OTP matches the generated one
        if (otpEntered == generatedOtp) {
            // OTP matched: Proceed to the password reset page
            model.addAttribute("userEmail", userEmail);
            model.addAttribute("page", "createNewPassword");
            return "open-url/create-new-password";
        } else {
            // OTP mismatch: Set an error message and re-display the OTP verification page
            session.setAttribute("customMessage", new CustomDisplayMessage("Invalid OTP. Please try again.", "alert-danger"));
            model.addAttribute("userEmail", userEmail);  // Pre-fill the email in the form
            model.addAttribute("page", "verifyOTP");

            return "open-url/verify-OTP-FP";
        }
    }


    /**
     * Resends OTP to the user's email if they request it.
     *
     * @param model The Model object that will hold attributes for the view.
     * @param userEmail The email address of the user requesting a new OTP.
     * @param session The HTTP session to store custom messages.
     * @return The OTP verification page with a new OTP sent to the user.
     */
    @PostMapping("/resend-otp")
    public String resendOtp(Model model,
                            @RequestParam("userEmail") String userEmail,
                            HttpSession session) {

        User userexisted = userRepository.getUserByName(userEmail);

        if (userexisted == null) {
            session.setAttribute("customMessage", new CustomDisplayMessage("No user found with this email address", "alert-danger"));
        } else {
            // Generate and send new OTP
            int otp = otpGenerator.generateOTP();
            otpGenerator.setOtp(otp);
            try {
                emailService.sendEmail("noreply.cswiz@gmail.com", userEmail, "WELCOME", "Your new OTP is: " + otpGenerator.getOtp());
            } catch (Exception e) {
                session.setAttribute("customMessage", new CustomDisplayMessage("Failed to send OTP. Please try again.", "alert-danger"));
                model.addAttribute("page", "verifyOTP");
                return "open-url/verify-OTP-FP";
            }

            // Add email to the model for OTP verification
            model.addAttribute("userEmail", userEmail);
            session.setAttribute("customMessage", new CustomDisplayMessage("A new OTP has been sent to your email.", "alert-success"));
        }
        model.addAttribute("page", "verifyOTP");
        return "open-url/verify-OTP-FP";
    }

    /**
     * Resets the user's password after successful OTP verification.
     *
     * @param userEmail The email address of the user.
     * @param newPassword The new password entered by the user.
     * @param confirmPassword The confirmation of the new password.
     * @param model The Model object to pass data to the view.
     * @param session The HTTP session to store custom messages.
     * @return The appropriate page based on password reset success or failure.
     */
    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam("userEmail") String userEmail,
                                @RequestParam("newPassword") String newPassword,
                                @RequestParam("confirmPassword") String confirmPassword,
                                Model model, HttpSession session) {

        // Validation: Check if passwords match
        if (!newPassword.equals(confirmPassword)) {
            session.setAttribute("customMessage", new CustomDisplayMessage("Password do not matched", "alert-danger"));
            model.addAttribute("userEmail", userEmail);
            model.addAttribute("page","createNewPassword");
            return "open-url/create-new-password"; // Show error and stay on the same page
        }

        try {
            // Fetch the user by email
            User existingUser = userRepository.getUserByName(userEmail);
            if (existingUser == null) {
                session.setAttribute("customMessage", new CustomDisplayMessage("No user found with this email address", "alert-danger"));
                model.addAttribute("page","createNewPassword");
                return "open-url/create-new-password"; // Show error and stay on the same page
            }

            // Encode new password and save
            existingUser.setUserPassword(bCryptPasswordEncoder.encode(newPassword));
            userRepository.save(existingUser);
            session.setAttribute("customMessage", new CustomDisplayMessage("Password reset successfully.", "alert-success"));

            // Optional: Send confirmation email after password reset
            try {
                emailService.sendEmail("noreply.cswiz@gmail.com", userEmail, "Password Reset Confirmation", "Your password has been reset successfully.");
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Redirect to login page after successful password reset
            return "redirect:/login";  // Redirect to login page

        } catch (Exception e) {
            session.setAttribute("customMessage", new CustomDisplayMessage("Something Went wrong", "alert-danger"));
            model.addAttribute("page","createNewPassword");
            return "open-url/create-new-password"; // Show error and stay on the same page
        }
    }


    @GetMapping("/home")
    public String homePage(Model model){

        model.addAttribute("page","home");
        
        return "open-url/home";
    }

    


    @GetMapping("/about-us")
    public String AboutUs(Model model){

        model.addAttribute("page","about");

        return "open-url/about";
    }






}



    