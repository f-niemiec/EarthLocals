package com.earthlocals.earthlocals.system.account;

import com.earthlocals.earthlocals.model.Utente;
import com.earthlocals.earthlocals.service.gestionepaese.GestionePaese;
import com.earthlocals.earthlocals.service.gestioneutente.GestioneUtente;
import com.earthlocals.earthlocals.service.gestioneutente.dto.EditPasswordDTO;
import com.earthlocals.earthlocals.service.gestioneutente.dto.EditUtenteDTO;
import com.earthlocals.earthlocals.service.gestioneutente.exceptions.WrongPasswordException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class AccountController {

    final private GestioneUtente gestioneUtente;
    final private GestionePaese gestionePaese;

    @GetMapping("/edit")
    public String edit(Model model, @AuthenticationPrincipal Utente utente) {
        var user = new EditUtenteDTO(utente);
        model.addAttribute("user", user);
        model.addAttribute("currentPage", "edit");
        var paeseList = gestionePaese.getPaesiSortedByName(Sort.Direction.ASC);
        model.addAttribute("paesi", paeseList);
        return "account/edit";
    }

    @PostMapping("/edit")
    public String edit(Model model, @ModelAttribute("user") @Valid EditUtenteDTO editUtenteDTO, BindingResult result) {
        model.addAttribute("currentPage", "edit");
        var paeseList = gestionePaese.getPaesiSortedByName(Sort.Direction.ASC);
        model.addAttribute("paesi", paeseList);
        if (result.hasErrors()) {
            return "account/edit";
        }
        gestioneUtente.editUser(editUtenteDTO);

        return "redirect:/account/edit?success=true";
    }

    @GetMapping("/edit-password")
    public String editPassword(Model model) {
        var passwordDTO = new EditPasswordDTO();
        model.addAttribute("passwordDTO", passwordDTO);
        model.addAttribute("currentPage", "edit-password");
        return "account/edit-password";
    }

    @PostMapping("/edit-password")
    public String editPassword(Model model, @ModelAttribute("passwordDTO") @Valid EditPasswordDTO editPasswordDTO, BindingResult result) {
        model.addAttribute("currentPage", "edit-password");
        if (result.hasErrors()) {
            return "account/edit-password";
        }
        try {
            gestioneUtente.editPassword(editPasswordDTO);
        } catch (WrongPasswordException e) {
            result.rejectValue("currentPassword", "error.passwordDTO.currentPassword", "Password errata.");
            return "account/edit-password";
        }

        return "redirect:/account/edit-password?success=true";
    }
}
