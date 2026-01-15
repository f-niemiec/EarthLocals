package com.earthlocals.earthlocals.system;

import com.earthlocals.earthlocals.service.gestioneemail.GestioneEmail;
import com.earthlocals.earthlocals.service.gestioneutente.GestioneUtente;
import com.earthlocals.earthlocals.service.gestioneutente.dto.ResetPasswordDTO;
import com.earthlocals.earthlocals.service.gestioneutente.exceptions.ExpiredResetTokenException;
import com.earthlocals.earthlocals.service.gestioneutente.exceptions.PasswordResetTokenNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class LoginController {

    final private GestioneUtente gestioneUtente;
    private final GestioneEmail gestioneEmail;

    @GetMapping("/login")
    public String login(Authentication auth) {
        // If authenticated, redirect to home
        if (auth != null) {
            return "redirect:/";
        }

        return "login";
    }

    @GetMapping("/resetPassword")
    public String resetPassword() {
        return "reset_password";
    }

    @PostMapping("/resetPassword")
    public String resetPassword(@RequestParam(value = "email") String email) {
        var passwordResetToken = gestioneUtente.createPasswordResetToken(email);
        passwordResetToken.ifPresent(s -> gestioneEmail.inviaEmailRecuperoPassword(email, s));

        return "redirect:/login?sentReset=true";
    }

    @GetMapping("/resetPasswordConfirm")
    public String resetPasswordConfirm(Model model, @RequestParam(value = "token") String token) {
        var dto = new ResetPasswordDTO();
        dto.setToken(token);
        model.addAttribute("dto", dto);
        return "reset_password_confirm";
    }

    @PostMapping("/resetPasswordConfirm")
    public String resetPasswordConfirm(@ModelAttribute("dto") @Valid ResetPasswordDTO dto, BindingResult result) {
        if (result.hasErrors()) {
            return "reset_password_confirm";
        }
        try {
            gestioneUtente.resetPassword(dto);
        } catch (ExpiredResetTokenException e) {
            return "redirect:/login?expiredReset=true";
        } catch (PasswordResetTokenNotFoundException e) {
            return "redirect:/login?invalidReset=true";
        }

        return "redirect:/login?reset=true";
    }
}
