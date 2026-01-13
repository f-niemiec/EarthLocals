package com.earthlocals.earthlocals.system.account;

import com.earthlocals.earthlocals.service.gestionemissioni.GestioneMissione;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/account/moderator")
@PreAuthorize("hasRole('MODERATOR')")
public class AccountModeratorController {

    final private GestioneMissione gestioneMissione;

    @GetMapping("/missions")
    public String missions(Model model, @RequestParam(value = "page", required = false, defaultValue = "0") Integer page, @RequestParam(value = "pageSize", required = false, defaultValue = "6") Integer pageSize) {
        model.addAttribute("missioni", gestioneMissione.getMissioniPending(page, pageSize));
        model.addAttribute("currentPage", "moderator-missions");

        return "account/moderator/missions";
    }

    @PostMapping("/missions/accept")
    public String acceptMission(@RequestParam(value = "id") Long id, @RequestHeader(value = "referer") String referer) {
        gestioneMissione.acceptMissione(id);
        return "redirect:" + referer;
    }

    @PostMapping("/missions/reject")
    public String rejectMission(@RequestParam(value = "id") Long id, @RequestHeader(value = "referer") String referer) {
        gestioneMissione.rejectMissione(id);
        return "redirect:" + referer;
    }
}
