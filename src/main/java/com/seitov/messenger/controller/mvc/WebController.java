package com.seitov.messenger.controller.mvc;

import java.security.Principal;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class WebController implements ErrorController {
    
    @GetMapping("/")
    public String mainPage(){
        return "index";
    }
    
    @RequestMapping("/error")
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleError(Principal principal, Model model) {
        model.addAttribute("message", "Seems you are trying to load non-existing page!");
        model.addAttribute("isLogged", principal!=null);
        return "error";
    }
}
