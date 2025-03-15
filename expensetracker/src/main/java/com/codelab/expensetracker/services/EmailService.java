package com.codelab.expensetracker.services;

import jakarta.mail.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Properties;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    private String from;
    private String to ;
    private String text;
    private String subject;

    public EmailService(String from, String subject, String text, String to) {
        this.from = from;
        this.subject = subject;
        this.text = text;
        this.to = to;
    }

    public EmailService() {
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getText() {
        return text;
    }

    public void setText(String OTP) {
        this.text = text;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }



    // Method to send email
    public void sendEmail(String from, String to, String subject, String text) {
        try {
            // Create a Session object with properties from the configuration
            Properties props = System.getProperties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            // Creating a session with the properties
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("noreply.cswiz@gmail.com","cphu wewo eyhj zhnx");
                }
            });

            // Create a message with the session
            MimeMessage message = new MimeMessage(session);
            message.setFrom(from);
            message.setRecipients(Message.RecipientType.TO, to);
            message.setSubject(subject);
            message.setText(text);

            // Send the email
            Transport.send(message);

            System.out.println("Email Sent Successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error in sending email: " + e.getMessage());
        }
    }


}
