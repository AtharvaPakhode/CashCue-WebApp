package com.codelab.expensetracker.securityconfiguration;

import com.codelab.expensetracker.models.User;
import com.codelab.expensetracker.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        /*    -----get user from database-----
         *  This method accepts username (Email in our case)
         *  and returns the user object
         */

        /**
         * Spring Security passes the username parameter fetch from the login form (which is the email in your case) to this method.
         * If the userRepository.getUserByName(username) returns a valid user, it proceeds;
         * otherwise, it throws a UsernameNotFoundException.
         */
        System.out.println("step4");
        User user = userRepository.getUserByName(username);
        if (user == null) {
            throw new UsernameNotFoundException("USER_NOT_FOUND");
        }



        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        return customUserDetails;
    }
}