package com.jym.authentication.controllers;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.jym.authentication.models.User;
import com.jym.authentication.services.UserService;
import com.jym.authentication.validator.UserValidator;

@Controller
public class UserController {
    private final UserService userService;
    private final UserValidator userValidator;
    
    public UserController(UserService userService, UserValidator userValidator) {
        this.userService = userService;
        this.userValidator = userValidator;
    }
    
    @RequestMapping("/")
    public String registerForm(@ModelAttribute("user") User user) {
        System.out.println("this is starting");
    	return "index.jsp";
    }
    @RequestMapping("/login")
    public String login() {
        return "redirect:/events";
    }
    
    @RequestMapping(value="/registration", method=RequestMethod.POST)
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, HttpSession session) {
    	userValidator.validate(user, result);
        if(result.hasErrors()) {
        	return "registrationPage.jsp";
        }
        User u = userService.registerUser(user);
        session.setAttribute("userId", u.getId());
        return"redirect:/home";
    }
    
    @RequestMapping(value="/login", method=RequestMethod.POST)
    public String loginUser(@RequestParam("email") String email, @RequestParam("password") String password, Model model, HttpSession session) {
        boolean isAuthenticated = userService.authenticateUser(email, password);
        if(isAuthenticated) {
        	User u = userService.findByEmail(email);
        	session.setAttribute("userId", u.getId());
        	return "redirect:/home";
        } else {
        	model.addAttribute("error", "Invalid Credentials. Please try again.");
        	return "loginPage.jsp";
        }
        
    }
    
    @RequestMapping("/home")
    public String home(HttpSession session, Model model) {
       Long userId = (Long) session.getAttribute("userId");
       User u = userService.findUserById(userId);
       model.addAttribute("user", u);
       return "homePage.jsp";
    }
    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

}
