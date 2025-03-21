package com.codelab.expensetracker.controllers;


import com.codelab.expensetracker.helper.CustomDisplayMessage;
import com.codelab.expensetracker.models.User;
import com.codelab.expensetracker.repositories.CategoryRepository;
import com.codelab.expensetracker.repositories.ExpenseRepository;
import com.codelab.expensetracker.repositories.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

@Controller
@RequestMapping("/user")
public class UserAccessUrlController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private CategoryRepository categoryRepository;




    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        model.addAttribute("title", "Dashboard");
        

        String name = principal.getName();
        User user = this.userRepository.getUserByName(name);

        System.out.println(user.getUserName());
        
        model.addAttribute("user", user);



        // Adding attributes to the model to pass to Thymeleaf template
//        model.addAttribute("savings", savings);
//        model.addAttribute("expenses", monthlySpending);
//        model.addAttribute("savingsPercentage", savingsPercentage);
//        model.addAttribute("expensesPercentage", expensesPercentage);


        


        return "user-access-url/dashboard";
    }

    @GetMapping("/settings")
    public String settings(Model model, Principal principal) {
        model.addAttribute("title", "Dashboard");


        String name = principal.getName();
        User user = this.userRepository.getUserByName(name);

        

        model.addAttribute("user", user);



        // Adding attributes to the model to pass to Thymeleaf template
//        model.addAttribute("savings", savings);
//        model.addAttribute("expenses", monthlySpending);
//        model.addAttribute("savingsPercentage", savingsPercentage);
//        model.addAttribute("expensesPercentage", expensesPercentage);

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
                                     HttpSession session){
        try {
            // Retrieve the username of the currently authenticated user
            String name = principal.getName();
            // Fetch the user details from the database based on the username
            User user = this.userRepository.getUserByName(name);

            // Get the current user's profile image URL
            String oldImage = user.getUserImageURL();
            

            // Prepare the file name for the image to be deleted (if any)
            String fileNameToDelete = oldImage;

            // Define the folder path where the profile images are stored
            String folderPath = "static/userprofileimages";
            File directory = new File(folderPath);

            // Construct the full path to the old profile image that needs to be deleted
            Path targetLocation = Paths.get(directory.getAbsolutePath() + File.separator + fileNameToDelete);
            

            // Create a File object for the image to be deleted
            File fileToDelete = targetLocation.toFile();

            // Check if the file exists and is a valid file, then delete it
            if (fileToDelete.exists() && fileToDelete.isFile()) {
                fileToDelete.delete();
               
            } 

            // If the uploaded image file is empty, set the default profile image
            if (profileImage.isEmpty()) {
                user.setUserImageURL("userDefault.png");
            } else {
                // If a new image is uploaded, process it
                String originalFileName = profileImage.getOriginalFilename();

                // Generate a unique image name based on the user's ID
                String userID = String.valueOf(user.getUserId());
                String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                String imageName = userID + "_PROFILE_IMAGE" + fileExtension;

                // Set the new profile image URL for the user
                user.setUserImageURL(imageName);

                // Define the folder path where the new image will be stored
                String folder = "static/userprofileimages";
                directory = new File(folder);

                // Create the folder if it does not exist
                if (!directory.exists()) {
                    directory.mkdir();
                }

                // Construct the target path for the new image file
                targetLocation = Paths.get(directory.getAbsolutePath() + File.separator + imageName);

                // Copy the uploaded file to the target location, replacing any existing file with the same name
                Files.copy(profileImage.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
                

                // Save the updated user data in the repository
                this.userRepository.save(user);
                model.addAttribute("user", user);

                // Set a success message in the session to be displayed on the frontend
                session.setAttribute("customMessage", new CustomDisplayMessage("Profile image updated successfully", "alert-success"));
            }

        } catch (Exception e) {
            // Log the exception if an error occurs during the file upload process
            e.printStackTrace();
        }

        // Redirect to the user settings page after the update process is complete
        return "redirect:/user/settings";
    }

}
