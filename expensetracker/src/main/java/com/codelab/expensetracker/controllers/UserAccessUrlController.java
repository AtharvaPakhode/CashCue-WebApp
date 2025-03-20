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
    
    @PostMapping("/change-profile-image")
    public String changeProfileImage(Model model, Principal principal,
                                     @RequestParam("profileImage") MultipartFile profileImage,
                                     HttpSession session){
        
        try{
            String name = principal.getName() ;
            User user = this.userRepository.getUserByName(name);

            
            
            if(profileImage.isEmpty()){
                user.setUserImageURL("userDefault.png");
            }
            else{
                String originalFileName = profileImage.getOriginalFilename();
                
                String userID= String.valueOf(user.getUserId());
                
                String fileExtension =originalFileName.substring(originalFileName.lastIndexOf("."));
                
                String imageName = userID + "_" + "PROFILE_IMAGE" + fileExtension;
                
                user.setUserImageURL(imageName);


                String folder = "src/main/resources/static/userprofileimages";
                File directory = new File(folder);
                
                
                if(!directory.exists()){
                    directory.mkdir();
                }
                
                Path targetLocation = Paths.get(directory.getAbsolutePath() + File.separator + imageName);
                Files.copy( profileImage.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
                
            }
            
            this.userRepository.save(user);
            session.setAttribute("customMessage", new CustomDisplayMessage("Profile Image Changed Successfully","alert-success"));
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return "redirect:/user/settings";
    }

}
