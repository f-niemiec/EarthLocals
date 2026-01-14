package com.earthlocals.earthlocals.system;

import com.earthlocals.earthlocals.events.OnRegistrationCompleteEvent;
import com.earthlocals.earthlocals.model.Paese;
import com.earthlocals.earthlocals.service.gestioneemail.GestioneEmail;
import com.earthlocals.earthlocals.service.gestionepaese.GestionePaese;
import com.earthlocals.earthlocals.service.gestioneutente.GestioneUtente;
import com.earthlocals.earthlocals.service.gestioneutente.dto.UtenteDTO;
import com.earthlocals.earthlocals.service.gestioneutente.dto.VolontarioDTO;
import com.earthlocals.earthlocals.service.gestioneutente.exceptions.ExpiredVerificationTokenException;
import com.earthlocals.earthlocals.service.gestioneutente.exceptions.UserAlreadyExistsException;
import com.earthlocals.earthlocals.service.gestioneutente.exceptions.VerificationTokenNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/registration")
public class RegistrationController {

    final private GestioneUtente gestioneUtente;
    final private GestionePaese gestionePaese;
    final private GestioneEmail gestioneEmail;
    final private ApplicationEventPublisher eventPublisher;

    @GetMapping("/volunteer")
    public String volunteerRegistration(Model model, Authentication auth) {
        // If authenticated, redirect to home
        if (auth != null) {
            return "redirect:/";
        }
        var paeseList = gestionePaese.getPaesiSortedByName(Sort.Direction.ASC);
        var user = new VolontarioDTO();
        model.addAttribute("user", user);
        model.addAttribute("paesi", paeseList);
        return "registration/volunteer";
    }

    @PostMapping("/volunteer")
    public String volunteerRegistration(@ModelAttribute("user") @Valid VolontarioDTO userDTO, BindingResult result, Model model, HttpServletRequest request) {
        List<Paese> paeseList = gestionePaese.getPaesiSortedByName(Sort.Direction.ASC);
        model.addAttribute("paesi", paeseList);
        if (result.hasErrors()) {
            return "registration/volunteer";
        }
        try {
            var user = gestioneUtente.registerVolunteer(userDTO);
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user));

        } catch (UserAlreadyExistsException e) {
            // TODO: Questa andrebbe tolto perché si potrebbe fare user enumeration
            result.rejectValue("email", "error.user.email.alreadyExists", "L'email è già registrata");
            return "registration/volunteer";
        }

        return "redirect:/login?registered=true";
    }

    @GetMapping("/organizer")
    public String organizerRegistration(Model model, Authentication auth) {
        // If authenticated, redirect to home
        if (auth != null) {
            return "redirect:/";
        }
        var user = new UtenteDTO();
        model.addAttribute("user", user);
        List<Paese> paeseList = gestionePaese.getPaesiSortedByName(Sort.Direction.ASC);
        model.addAttribute("paesi", paeseList);
        return "registration/organizer";
    }

    @PostMapping("/organizer")
    public String organizerRegistration(@ModelAttribute("user") @Valid UtenteDTO userDTO, BindingResult result, Model model, HttpServletRequest request) {
        List<Paese> paeseList = gestionePaese.getPaesiSortedByName(Sort.Direction.ASC);
        model.addAttribute("paesi", paeseList);
        if (result.hasErrors()) {
            return "registration/organizer";
        }
        try {
            var user = gestioneUtente.registerOrganizer(userDTO);
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user));
        } catch (UserAlreadyExistsException e) {
            // TODO: Questa andrebbe tolto perché si potrebbe fare user enumeration
            result.rejectValue("email", "error.user.email.alreadyExists", "L'email è gia registrata");
            return "registration/organizer";
        }
        return "redirect:/login?registered=true";
    }

    @GetMapping("/registrationConfirm")
    public String confirmRegistration(Model model, @RequestParam(value = "token") String token, WebRequest request) throws VerificationTokenNotFoundException, ExpiredVerificationTokenException {
        gestioneUtente.activateAccount(token);
        return "redirect:/login?confirmed=true";
    }


}
