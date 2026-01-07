package com.earthlocals.earthlocals.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "recensioniBuilder")
public class Recensione implements Serializable {
    @Id
    @GeneratedValue
    @Getter
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "autore_id")
    private Utente autore;

    @ManyToOne(optional = false)
    @JoinColumn(name = "destinatario_id")
    private Utente destinatario;

    @Column(nullable = false)
    private Integer voto;

    @Column(nullable = false)
    private Date dataRecensione;

    private String testoRecensione;
}
