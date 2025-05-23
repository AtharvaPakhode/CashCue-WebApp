package com.codelab.expensetracker.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

@Entity
@Table(name="ET_user_table")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int userId;

    @NotBlank
    @Size(min=2 , max=20 )
    private String userName;

    @Column(unique = true)
    @Email
    @NotBlank
    private String userEmail;

    @NotBlank
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&]).{8,128}$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, one special character, and be at least 8 characters long.")
    private String userPassword;

    @Transient
    private String confirmPassword;

    private String userImageURL;

    private boolean userStatus;

    private String userRole;
    
    private boolean twoFactorAuthentication ;

    @Column(length=500)
    private String userDescription;

    @OneToMany(cascade=CascadeType.ALL,fetch=FetchType.LAZY,mappedBy = "user")
    private List<Expense> expenseList;

    @OneToMany(cascade=CascadeType.ALL,fetch=FetchType.LAZY,mappedBy = "user")
    private List<Category>categoryList;


    


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getUserImageURL() {
        return userImageURL;
    }

    public void setUserImageURL(String userImageURL) {
        this.userImageURL = userImageURL;
    }

    public boolean getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(boolean userStatus) {
        this.userStatus = userStatus;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getUserDescription() {
        return userDescription;
    }

    public void setUserDescription(String userDescription) {
        this.userDescription = userDescription;
    }

    public List<Expense> getExpenseList() {
        return expenseList;
    }

    public void setExpenseList(List<Expense> expenseList) {
        this.expenseList = expenseList;
    }

    public List<Category> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    public boolean isTwoFactorAuthentication() {
        return twoFactorAuthentication;
    }

    public void setTwoFactorAuthentication(boolean twoFactorAuthentication) {
        this.twoFactorAuthentication = twoFactorAuthentication;
    }

    public User() {

    }

    //

    public User(int userId, String userName, String userEmail, String userPassword) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
    }

    //

    public User(int userId, String userName, String userEmail, String userPassword, String confirmPassword, String userImageURL, boolean userStatus, String userRole, boolean twoFactorAuthentication, String userDescription, List<Expense> expenseList, List<Category> categoryList) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.confirmPassword = confirmPassword;
        this.userImageURL = userImageURL;
        this.userStatus = userStatus;
        this.userRole = userRole;
        this.twoFactorAuthentication = twoFactorAuthentication;
        this.userDescription = userDescription;
        this.expenseList = expenseList;
        this.categoryList = categoryList;
    }


}
