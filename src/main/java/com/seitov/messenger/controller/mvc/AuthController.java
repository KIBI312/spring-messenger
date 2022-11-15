package com.seitov.messenger.controller.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.seitov.messenger.dto.RegistrationDto;
import com.seitov.messenger.exception.UserRegistrationFailureException;
import com.seitov.messenger.service.AuthService;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private  AuthService authService;
    
    @Autowired
    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(HttpServletRequest request, Model model) {
        RegistrationDto registrationDto = new RegistrationDto();
        model.addAttribute("user", registrationDto);
        return "register";
    }

    @PostMapping("/register")
    public String registerAccount(@ModelAttribute("user") @Valid RegistrationDto registrationDto, BindingResult result, Model model,HttpServletRequest request){
        if(result.hasErrors()){
            return "register";
        }
        try {
            authService.register(registrationDto);
        } catch (UserRegistrationFailureException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
        return "login";
    }

}
