package com.earthlocals.earthlocals.system;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(Authentication auth) {
        // If authenticated, redirect to home
        if (auth != null) {
            return "redirect:/";
        }

        return "login";
    }
}
