package com.earthlocals.earthlocals.system.account;

import com.earthlocals.earthlocals.model.Utente;
import com.earthlocals.earthlocals.service.gestionecandidature.GestioneCandidatura;
import com.earthlocals.earthlocals.service.gestionemissioni.GestioneMissione;
import com.earthlocals.earthlocals.service.gestionemissioni.dto.MissioneDTO;
import com.earthlocals.earthlocals.service.gestionepaese.GestionePaese;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/account/organizer")
@PreAuthorize("hasRole('ORGANIZER')")
public class AccountOrganizerController {


    final private GestioneMissione gestioneMissione;
    final private GestionePaese gestionePaese;
    final private GestioneCandidatura gestioneCandidatura;

    @GetMapping("/missions")
    public String missions(Model model, @RequestParam(value = "page", required = false, defaultValue = "0") Integer page, @RequestParam(value = "pageSize", required = false, defaultValue = "6") Integer pageSize) {
        model.addAttribute("missioni", gestioneMissione.getMissioniOrganizzatore(page, pageSize));
        model.addAttribute("currentPage", "organizer-missions");

        return "account/organizer/missions";
    }

    @GetMapping("/mission/new")
    public String newMission(Model model) {
        var missioneDTO = new MissioneDTO();
        model.addAttribute("currentPage", "organizer-mission-new");
        model.addAttribute("missioneDTO", missioneDTO);
        var paeseList = gestionePaese.getPaesiSortedByName(Sort.Direction.ASC);
        model.addAttribute("paesi", paeseList);
        return "account/organizer/mission/new";
    }

    @PostMapping("/mission/new")
    public String newMission(Model model, @ModelAttribute("missioneDTO") @Valid MissioneDTO missioneDTO, BindingResult result, @AuthenticationPrincipal Utente utente) throws Exception {
        model.addAttribute("currentPage", "organizer-mission-new");
        model.addAttribute("missioneDTO", missioneDTO);
        var paeseList = gestionePaese.getPaesiSortedByName(Sort.Direction.ASC);
        model.addAttribute("paesi", paeseList);
        if (result.hasErrors()) {
            return "account/organizer/mission/new";
        }
       
        missioneDTO.setCreatore(utente);
        gestioneMissione.registerMissione(missioneDTO);

        return "redirect:/account/organizer/mission/new?success=true";
    }

    @GetMapping("/candidature")
    public String candidature(Model model, @RequestParam(value = "page", required = false, defaultValue = "0") Integer page, @RequestParam(value = "pageSize", required = false, defaultValue = "6") Integer pageSize) {
        model.addAttribute("currentPage", "organizer-candidature");
        var candidature = gestioneCandidatura.getRichiesteCandidatura(page, pageSize);
        model.addAttribute("candidature", candidature);
        return "account/organizer/candidature";
    }

}
