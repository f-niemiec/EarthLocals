package com.earthlocals.earthlocals.system;

import com.earthlocals.earthlocals.model.Missione;
import com.earthlocals.earthlocals.model.Paese;
import com.earthlocals.earthlocals.model.Volontario;
import com.earthlocals.earthlocals.service.gestionecandidature.GestioneCandidatura;
import com.earthlocals.earthlocals.service.gestionecandidature.dto.CandidaturaDTO;
import com.earthlocals.earthlocals.service.gestioneemail.GestioneEmail;
import com.earthlocals.earthlocals.service.gestionemissioni.GestioneMissione;
import com.earthlocals.earthlocals.service.gestionemissioni.exceptions.MissioneNotFoundException;
import com.earthlocals.earthlocals.service.gestionepaese.GestionePaese;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/mission")
@RequiredArgsConstructor
public class MissionController {

    final private GestioneMissione gestioneMissione;
    final private GestioneCandidatura gestioneCandidatura;
    final private GestionePaese gestionePaese;
    final private GestioneEmail emailService;

    @GetMapping("/find_mission")
    public String findMission(Model model,
                              @RequestParam(name = "paeseId", required = false) Integer paeseId,
                              @RequestParam(name = "page", defaultValue = "0") int page,
                              @RequestParam(name = "pageSize", defaultValue = "6") int pageSize) {
        List<Paese> listaPaesi = gestionePaese.getPaesiSortedByName(Sort.Direction.ASC);

        Page<Missione> missioniPage = gestioneMissione.getMissioniAperte(paeseId, page, pageSize);

        model.addAttribute("paesi", listaPaesi);
        model.addAttribute("missioniPage", missioniPage);
        model.addAttribute("paeseSelezionato", paeseId);
        return "mission/find_mission";
    }

    @GetMapping("")
    public String missionPage(Model model, @RequestParam(name = "id") Long id, Authentication auth) throws MissioneNotFoundException {
        var mission = gestioneMissione.getMissioneById(id);
        if (mission == null) {
            return "redirect:/";
        }
        model.addAttribute("mission", mission);
        model.addAttribute("giaCandidato", false);
        if (auth != null) {
            if (auth.getPrincipal() instanceof Volontario volontario) {
                var candidaturaDTO = new CandidaturaDTO(volontario.getId(), id);
                model.addAttribute("giaCandidato", gestioneCandidatura.hasVolontarioAlreadyApplied(candidaturaDTO));
            }
        }
        return "mission/mission_page";
    }

    @GetMapping("/image/{path}")
    public ResponseEntity<Resource> getPhoto(@PathVariable String path) throws Exception {

        ResponseEntity<Resource> body = ResponseEntity
                .ok()
                .cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS))
                .body(gestioneMissione.getImmagineMissione(path));
        return body;
    }

    @PostMapping("/candidatura")
    @PreAuthorize("hasRole('VOLUNTEER')")
    public String effettuaCandidatura(@RequestParam(name = "id") Long id, @AuthenticationPrincipal Volontario volontario) {
        var candidaturaDTO = new CandidaturaDTO(volontario.getId(), id);
        var candidatura = gestioneCandidatura.registerCandidatura(candidaturaDTO);
        emailService.inviaEmailCandidatura(candidatura.getId());
        return "redirect:/mission?id=" + id;

    }

    @PostMapping("/candidatura/remove")
    @PreAuthorize("hasRole('VOLUNTEER')")
    public String rimuoviCandidatura(@RequestParam(name = "id") Long id, @AuthenticationPrincipal Volontario volontario) {
        var candidaturaDTO = new CandidaturaDTO(volontario.getId(), id);
        gestioneCandidatura.removeCandidatura(candidaturaDTO);
        return "redirect:/mission?id=" + id;

    }

    @PostMapping("/candidatura/reject")
    @PreAuthorize("hasRole('ORGANIZER')")
    public String rejectCandidatura(@RequestParam(name = "id") Long id, @RequestHeader(value = "referer", required = false) final String referer) {
        if (gestioneCandidatura.rejectCandidatura(id)) {
            emailService.inviaEmailRifiutoCandidatura(id);
        }
        return "redirect:" + referer;
    }

    @PostMapping("/candidatura/accept")
    @PreAuthorize("hasRole('ORGANIZER')")
    public String acceptCandidatura(@RequestParam(name = "id") Long id, @RequestHeader(value = "referer", required = false) final String referer) {
        if (gestioneCandidatura.acceptCandidatura(id)) {
            emailService.inviaEmailAccettazioneCandidatura(id);
        }
        return "redirect:" + referer;
    }

}
