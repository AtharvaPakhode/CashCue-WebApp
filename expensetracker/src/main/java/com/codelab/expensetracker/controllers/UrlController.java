package com.codelab.expensetracker.controllers;

import com.codelab.expensetracker.helper.CustomDisplayMessage;
import com.codelab.expensetracker.helper.OTPgenerator;
import com.codelab.expensetracker.models.User;
import com.codelab.expensetracker.repositories.UserRepository;
import com.codelab.expensetracker.services.EmailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

import static java.lang.Thread.*;


@Controller
public class UrlController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private OTPgenerator otpGenerator;

    @Autowired
    private  EmailService emailService;





    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("title", "Register Page");
        model.addAttribute("user", new User());  // Initialize a new user object for the form
        return "open-url/register";
    }


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

            // check if password & confirm password fields are not empty and both are equal or not
            if (user.getUserPassword() != null && user.getConfirmPassword() != null) {
                if (!user.getUserPassword().equals(user.getConfirmPassword())) {
                    System.out.println(user.getUserPassword());
                    System.out.println(user.getConfirmPassword());
                    result.rejectValue("confirmPassword", "error.user", "Passwords do not match.");
                }
            } else {
                System.out.println(user.getUserPassword());
                System.out.println(user.getConfirmPassword());
                result.rejectValue("userPassword", "error.user", "Password cannot be empty.");
                result.rejectValue("confirmPassword", "error.user", "Confirm password cannot be empty.");
            }

            // If there are validation errors, return the user to the signup form
            if (result.hasErrors()) {
                System.out.println("ERROR: " + result.toString());
                return "open-url/register";
            }
            
            
                   
            

            // Set default properties for the new user
            user.setUserRole("ROLE_USER");
            user.setUserStatus(true);
            user.setUserImageURL("contactDefault.png");
            

            // Encrypt the password before saving
            user.setUserPassword(bCryptPasswordEncoder.encode(user.getUserPassword()));


            // Save the user to the database
            User saved_user = this.userRepository.save(user);

            
            model.addAttribute("user", user);

            // Add a success message to the session
            session.setAttribute("customMessage", new CustomDisplayMessage("Successfully Registered -Please login to proceed", "alert-success"));

        } catch (DataIntegrityViolationException e) {
            
            // Handle any errors, log them and display an error message
            session.setAttribute("customMessage", new CustomDisplayMessage("This user email is already registered", "alert-danger"));
            return "open-url/register";
            
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("customMessage", new CustomDisplayMessage("Something went wrong", "alert-danger"));
            return "open-url/register";
        }

        // Redirect back to the register page
        return "open-url/login";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("title", "Login Page");
        model.addAttribute("user", new User());  // Initialize a new user object for the form
        return "open-url/login";
    }


    @GetMapping("/terms")
    public String terms(Model model) {
        model.addAttribute("title", "Terms And Conditions");
        model.addAttribute("user", new User());  // Initialize a new user object for the form
        return "open-url/terms-and-conditions";
    }

    @GetMapping("/forgot-password")
    public String fp(Model model) {

        return "open-url/forgot-password";
    }

    @PostMapping("/forgot-password-otp")
    public String fpr(Model model,
                      @ModelAttribute("user") User user,
                      HttpSession session) {
        model.addAttribute("title", "Forgot Password");
        String userEmailTo = user.getUserEmail();

        // Check if the user exists
        User userexisted = userRepository.getUserByName(userEmailTo);

        if (userexisted == null) {
            session.setAttribute("customMessage", new CustomDisplayMessage("No user found with this email address", "alert-danger"));
            return "open-url/forgot-password";
        } else {
            // Generate and store OTP
            int number = otpGenerator.generateOTP();
            otpGenerator.setOtp(number);

            // Send OTP via email
            try {
                emailService.sendEmail("noreply.cswiz@gmail.com", userEmailTo, "WELCOME",
                        "Your one-time OTP is: " + otpGenerator.getOtp());
            } catch (Exception e) {
                session.setAttribute("customMessage", new CustomDisplayMessage("Failed to send OTP. Please try again.", "alert-danger"));
                return "open-url/forgot-password";
            }

            // Add email to model for OTP verification page
            model.addAttribute("userEmail", userEmailTo);
        }

        return "open-url/verify-OTP-FP";
    }



    @PostMapping("/verify-otp")
    public String verifyOtp(Model model,
                            @RequestParam("userEmail") String userEmail,
                            @RequestParam("otp") int otpEntered,
                            HttpSession session) {
        model.addAttribute("title", "Verify OTP");

        // Retrieve OTP from session or generate a new one (if expired or new session)
        int generatedOtp = otpGenerator.getOtp();

        if (otpEntered == generatedOtp) {
            // OTP matched, proceed to password reset page
            model.addAttribute("userEmail", userEmail);
            return "open-url/create-new-password";
        } else {
            // OTP mismatch
            session.setAttribute("customMessage", new CustomDisplayMessage("Invalid OTP. Please try again.", "alert-danger"));
            model.addAttribute("userEmail", userEmail);  // Pre-fill email in the form
            return "open-url/verify-OTP-FP";
        }
    }



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
                return "open-url/verify-OTP-FP";
            }

            // Add email to the model for OTP verification
            model.addAttribute("userEmail", userEmail);
            session.setAttribute("customMessage", new CustomDisplayMessage("A new OTP has been sent to your email.", "alert-success"));
        }

        return "open-url/verify-OTP-FP";
    }


    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam("userEmail") String userEmail,
                                @RequestParam("newPassword") String newPassword,
                                @RequestParam("confirmPassword") String confirmPassword,
                                Model model, HttpSession session) {

        // Validation: Check if passwords match
        if (!newPassword.equals(confirmPassword)) {
            session.setAttribute("customMessage", new CustomDisplayMessage("Password do not matched", "alert-danger"));
            model.addAttribute("userEmail", userEmail);
            return "open-url/create-new-password"; // Show error and stay on the same page
        }

        try {
            // Fetch the user by email
            User existingUser = userRepository.getUserByName(userEmail);
            if (existingUser == null) {
                session.setAttribute("customMessage", new CustomDisplayMessage("No user found with this email address", "alert-danger"));
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


            return "redirect:/login";  // Redirect to login page

        } catch (Exception e) {
            session.setAttribute("customMessage", new CustomDisplayMessage("Something Went wrong", "alert-danger"));
            return "open-url/create-new-password"; // Show error and stay on the same page
        }
    }






}
