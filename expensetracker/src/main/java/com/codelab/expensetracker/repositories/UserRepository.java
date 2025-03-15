package com.codelab.expensetracker.repositories;

import com.codelab.expensetracker.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository interface for performing CRUD operations on the User entity.
 * It extends JpaRepository to provide standard database operations for User.
 */
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     *  Custom method to find a user by their email address.
     * This method searches for a User entity based on the provided email address.
     *
     * @param email The email address of the user to be fetched.
     * @return A User object that matches the provided email. If no user exists with the given email, it returns null.
     */
    @Query("SELECT u FROM User u WHERE u.userEmail = :email")
    User getUserByName(@Param("email") String email);

}
