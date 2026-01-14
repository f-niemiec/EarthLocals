package com.earthlocals.earthlocals.system.account;

import com.earthlocals.earthlocals.model.Volontario;
import com.earthlocals.earthlocals.service.gestioneutente.GestioneUtente;
import com.earthlocals.earthlocals.service.gestioneutente.dto.EditPassportDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
@RequiredArgsConstructor
@RequestMapping("/account/volunteer")
@PreAuthorize("hasRole('VOLUNTEER')")

public class AccountVolunteerController {
    final private GestioneUtente gestioneUtente;

    @GetMapping("/edit-passport")
    public String editPassport(Model model, @AuthenticationPrincipal Volontario volontario) {
        var passportDTO = new EditPassportDTO(volontario);
        model.addAttribute("passportDTO", passportDTO);
        model.addAttribute("currentPage", "edit-passport");
        return "account/volunteer/edit-passport";
    }

    @PostMapping("/edit-passport")
    public String editPassport(Model model, @ModelAttribute("passportDTO") @Valid EditPassportDTO editPassportDTO, BindingResult result) {
        model.addAttribute("currentPage", "edit-passport");
        if (result.hasErrors()) {
            return "account/volunteer/edit-passport";
        }
        gestioneUtente.editPassport(editPassportDTO);

        return "redirect:/account/volunteer/edit-passport?success=true";
    }

    @GetMapping(
            value = "/download-passport",
            produces = MediaType.APPLICATION_PDF_VALUE
    )
    public ResponseEntity<Resource> viewPassport() throws Exception {
        return ResponseEntity.ok().body(gestioneUtente.getPassportVolontarioFileResource());

    }
    //TODO: candidature and experience

}
