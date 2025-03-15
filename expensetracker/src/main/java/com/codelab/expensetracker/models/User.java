package com.codelab.expensetracker.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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


    private String userPassword;

    @Transient
    private String confirmPassword;

    private String userImageURL;

    private boolean userStatus;

    private String userRole;

    @Column(length=500)
    private String userDescription;

    @OneToMany(cascade=CascadeType.ALL,fetch=FetchType.LAZY,mappedBy = "user")
    private List<Expense> expenseList;

    @OneToMany(cascade=CascadeType.ALL,fetch=FetchType.LAZY,mappedBy = "user")
    private List<Category>categoryList;


    //


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

    //

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

    public User(int userId, String userName, String userEmail, String userPassword, boolean userStatus, String userRole, String userDescription, List<Expense> expenseList, List<Category> categoryList, String userImageURL) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.userStatus = userStatus;
        this.userRole = userRole;
        this.userDescription = userDescription;
        this.expenseList = expenseList;
        this.categoryList = categoryList;
        this.userImageURL = userImageURL;
    }


    //

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", userPassword='" + userPassword + '\'' +
                ", userImageURL='" + userImageURL + '\'' +
                ", userStatus='" + userStatus + '\'' +
                ", userRole='" + userRole + '\'' +
                ", userDescription='" + userDescription + '\'' +
                '}';
    }
}
