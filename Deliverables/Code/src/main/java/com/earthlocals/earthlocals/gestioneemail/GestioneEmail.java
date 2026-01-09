package com.earthlocals.earthlocals.gestioneemail;

import com.earthlocals.earthlocals.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GestioneEmail {

    final private static String warning = "\nEmail generata automaticamente, si prega di non rispondere." +
            "\nEarthLocals";
    final private static String linkBase = "http://localhost:8080/";

    final private JavaMailSender mailSender;
    final private CandidaturaRepository candidaturaRepository;
    final private MissioneRepository missioneRepository;
    final private UtenteRepository utenteRepository;

    public void inviaEmailCandidatura(Long id) {
        Candidatura candidatura = candidaturaRepository.findById(id).orElseThrow();
        SimpleMailMessage mail = new SimpleMailMessage();
        String nomeMissione = candidatura.getMissione().getNome();
        mail.setTo(candidatura.getMissione().getCreatore().getEmail());
        mail.setSubject("[Nuova candidatura] "
                + nomeMissione);
        mail.setText("Gentile " + candidatura.getMissione().getCreatore().getNome()
                + ", la sua missione " + nomeMissione +
                " ha ricevuto una nuova candidatura!\n " + warning);
        mailSender.send(mail);
    }

    public void inviaEmailAnnullamentoCandidatura(Long id) {
        Candidatura candidatura = candidaturaRepository.findById(id).orElseThrow();
        SimpleMailMessage mail = new SimpleMailMessage();
        String nomeMissione = candidatura.getMissione().getNome();
        mail.setTo(candidatura.getMissione().getCreatore().getEmail());
        mail.setSubject("[Annullamento candidatura] "
                + nomeMissione);
        mail.setText("Gentile " + candidatura.getMissione().getCreatore().getNome()
                + ", una candidatura alla sua missione " + nomeMissione +
                " è stata annullata!\n " + warning);
        mailSender.send(mail);
    }

    public void inviaEmailAccettazioneCandidatura(Long id) {
        Candidatura candidatura = candidaturaRepository.findById(id).orElseThrow();
        SimpleMailMessage mail = new SimpleMailMessage();
        String nomeMissione = candidatura.getMissione().getNome();
        mail.setTo(candidatura.getCandidato().getEmail());
        mail.setSubject("[Accettazione candidatura] "
                + nomeMissione);
        mail.setText("Gentile " + candidatura.getCandidato().getNome()
                + ",\n la sua candidatura alla missione " + nomeMissione +
                " è stata approvata!\n " + warning);
        mailSender.send(mail);
    }

    public void inviaEmailRifiutoCandidatura(Long id) {
        Candidatura candidatura = candidaturaRepository.findById(id).orElseThrow();
        SimpleMailMessage mail = new SimpleMailMessage();
        String nomeMissione = candidatura.getMissione().getNome();
        mail.setTo(candidatura.getCandidato().getEmail());
        mail.setSubject("[Accettazione candidatura] "
                + nomeMissione);
        mail.setText("Gentile " + candidatura.getCandidato().getNome()
                + ",\n ci dispiace informarla che la sua candidatura alla missione " + nomeMissione +
                " è stata rifiutata.\n " + warning);
        mailSender.send(mail);
    }

    public void inviaEmailApprovazioneMissione(Long id) {
        Missione missione = missioneRepository.findById(id).orElseThrow();
        SimpleMailMessage mail = new SimpleMailMessage();
        String nomeMissione = missione.getNome();
        mail.setTo(missione.getCreatore().getEmail());
        mail.setSubject("[Approvazione missione] "
                + nomeMissione);
        mail.setText("Gentile " + missione.getCreatore().getNome()
                + ", la sua missione " + nomeMissione +
                " è stata approvata!\n " + warning);
        mailSender.send(mail);
    }

    public void inviaEmailRifiutoMissione(Long id) {
        Missione missione = missioneRepository.findById(id).orElseThrow();
        SimpleMailMessage mail = new SimpleMailMessage();
        String nomeMissione = missione.getNome();
        mail.setTo(missione.getCreatore().getEmail());
        mail.setSubject("[Rifiuto missione] "
                + nomeMissione);
        mail.setText("Gentile " + missione.getCreatore().getNome()
                + ", ci dispiace informarla che " +
                "la sua missione " + nomeMissione +
                " è stata rifiutata.\n " + warning);
        mailSender.send(mail);
    }

    public void inviaEmailConferma(Long id) {
        Utente utente = utenteRepository.findById(id).orElseThrow();
        SimpleMailMessage mail = new SimpleMailMessage();

        mail.setTo(utente.getEmail());
        mail.setSubject("[Conferma registrazione] Account EarthLocals");
        mail.setText("Gentile, " + utente.getNome() + ", di seguito le inviamo" +
                " un link per confermare la propria registrazione: " + linkBase +
                "id=" + id + "password=" + utente.getTempPassword() + "\n" + warning);
        mailSender.send(mail);
    }

}
