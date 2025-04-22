package com.codelab.expensetracker.securityconfiguration;

import com.codelab.expensetracker.helper.CustomDisplayMessage;
import com.codelab.expensetracker.helper.OTPgenerator;
import com.codelab.expensetracker.repositories.UserRepository;
import com.codelab.expensetracker.services.EmailService;
import jakarta.servlet.ServletException;
import com.codelab.expensetracker.models.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;


@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    
    @Autowired
    private final UserRepository userRepository;
    
    @Autowired
    private final OTPgenerator otpGenerator;
    
    @Autowired
    private final EmailService emailService;

    public CustomAuthenticationSuccessHandler(UserRepository userRepository, OTPgenerator otpGenerator, EmailService emailService) {
        this.userRepository = userRepository;
        this.otpGenerator = otpGenerator;
        this.emailService = emailService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        String email = authentication.getName();  // Gets the username (email) from login
        User user = userRepository.getUserByName(email);

        if (user == null) {
            request.getSession().setAttribute("customMessage",
                    new CustomDisplayMessage("No user found with this email address", "alert-danger"));
            response.sendRedirect("/login?error=true");
            return;
        }

        if (user.isTwoFactorAuthentication()) {
            int otp = otpGenerator.generateOTP();
            otpGenerator.setOtp(otp);

            try {
                emailService.sendEmail("noreply.cswiz@gmail.com", user.getUserEmail(), "2FA Verification Code",
                        "Your one-time OTP is: " + otp);
            } catch (Exception e) {
                request.getSession().setAttribute("customMessage",
                        new CustomDisplayMessage("Failed to send OTP. Please try again.", "alert-danger"));
                response.sendRedirect("/login?error=true");
                return;
            }

            // Save OTP and email in session for verification
            HttpSession session = request.getSession();
            session.setAttribute("otp", otp);
            session.setAttribute("2fa_username", email);

            response.sendRedirect("/2fa-verification");
        } else {
            response.sendRedirect("/user/dashboard");
        }
    }
}
