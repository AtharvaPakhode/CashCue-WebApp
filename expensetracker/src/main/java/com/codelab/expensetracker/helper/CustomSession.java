package com.codelab.expensetracker.helper;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * This class provides helper methods to manage session-related operations.
 * Specifically, it handles removing a message from the session.
 */
@Component
public class CustomSession {

    /**
     * Removes the message attribute from the current HTTP session.
     * This is typically used after the message has been displayed to the user
     * to ensure that it is not retained across different requests.
     */
    public void removeMessageFromSession() {

        try {
            // Get the current HTTP session
            HttpSession session = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                    .getRequest()
                    .getSession();

            // Remove the "message" attribute from the session
            session.removeAttribute("customMessage");
        } catch (Exception e) {
            //  Log the exception for debugging purposes
            e.printStackTrace();
        }
    }
}
