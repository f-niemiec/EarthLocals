package com.earthlocals.earthlocals.system;

import com.earthlocals.earthlocals.service.gestioneutente.dto.UtenteDTO;
import com.earthlocals.earthlocals.model.Paese;
import com.earthlocals.earthlocals.model.PaeseRepository;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/registration")
public class RegistrationController {


    private final PaeseRepository paeseRepository;

    public RegistrationController(PaeseRepository paeseRepository) {
        this.paeseRepository = paeseRepository;
    }

    @GetMapping("/organizer")
    public String organizerRegistration(Model model, Authentication auth) {
        // If authenticated, redirect to home
        if (auth != null) {
            return "redirect:/";
        }
        var user = new UtenteDTO();
        model.addAttribute("user", user);
        List<Paese> paeseList = paeseRepository.findAll(Sort.by(Sort.Direction.ASC, "nome"));
        model.addAttribute("paesi", paeseList);
        return "registration/organizer";
    }


}
