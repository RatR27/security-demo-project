package ru.rrost27.securitydemoproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.rrost27.securitydemoproject.entity.MyUser;
import ru.rrost27.securitydemoproject.service.UserService;

import java.security.Principal;

@RestController
public class MainController {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String homePage(){
        return "Welcome to home page";
    }

    @GetMapping("/auth")
    public String onlyForAuthUsers(Principal principal){
        //можно получить информацию о польз-ле и из СекьюритиКонтекста, но слишком сложно (в методичке схема)
//        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        //на выходе объект a (Authentication) тоже самое что и principal (Principal)
        
        MyUser myUser = userService.findUserByUsername(principal.getName());
        return "secured page by auth " + myUser.getUsername() + " with phone: " + myUser.getPhone();
    }

    @GetMapping("/admin")
    public String adminPage(){
        return "Admin dashboard";
    }

    @GetMapping("/profile")
    public String pageProfile(){
        return "You can read this profile";
    }

}
