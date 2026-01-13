package com.earthlocals.earthlocals.events;

import com.earthlocals.earthlocals.model.Utente;
import lombok.Data;
import org.springframework.context.ApplicationEvent;

@Data
public class OnRegistrationCompleteEvent extends ApplicationEvent {
    private final Utente utente;

    public OnRegistrationCompleteEvent(Utente utente) {
        super(utente);
        this.utente = utente;
    }
}
