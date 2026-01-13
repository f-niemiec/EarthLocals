package com.earthlocals.earthlocals.events;

import com.earthlocals.earthlocals.service.gestioneemail.GestioneEmail;
import com.earthlocals.earthlocals.service.gestioneutente.GestioneUtente;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    private final GestioneUtente gestioneUtente;
    private final GestioneEmail emailService;


    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);

    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        var utente = event.getUtente();
        var token = gestioneUtente.createVerificationToken(utente);
        emailService.inviaEmailConferma(utente.getId(), token);
    }
}
