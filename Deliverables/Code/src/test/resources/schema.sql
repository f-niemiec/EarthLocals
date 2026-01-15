--
-- PostgreSQL database dump
--


-- Dumped from database version 17.7
-- Dumped by pg_dump version 18.1

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;

--
-- Name: candidatura; Type: TABLE; Schema: public; Owner: avnadmin
--

CREATE TABLE public.candidatura
(
    stato            character varying(255),
    candidato_id     bigint NOT NULL,
    data_candidatura date,
    id               bigint NOT NULL,
    missione_id      bigint NOT NULL
);


ALTER TABLE public.candidatura
    OWNER TO avnadmin;

--
-- Name: candidatura_seq; Type: SEQUENCE; Schema: public; Owner: avnadmin
--

CREATE SEQUENCE public.candidatura_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.candidatura_seq OWNER TO avnadmin;

--
-- Name: missione; Type: TABLE; Schema: public; Owner: avnadmin
--

CREATE TABLE public.missione
(
    internal_stato       smallint               NOT NULL,
    creatore_id          bigint                 NOT NULL,
    data_fine            date                   NOT NULL,
    data_inizio          date                   NOT NULL,
    id                   bigint                 NOT NULL,
    citta                character varying(255) NOT NULL,
    competenze_richieste text                   NOT NULL,
    descrizione          text                   NOT NULL,
    immagine             character varying(255) NOT NULL,
    nome                 character varying(255) NOT NULL,
    requisiti_extra      text,
    paese_id             integer                NOT NULL,
    CONSTRAINT missione_stato_check CHECK (((internal_stato >= 0) AND (internal_stato <= 3)))
);


ALTER TABLE public.missione
    OWNER TO avnadmin;

--
-- Name: missione_seq; Type: SEQUENCE; Schema: public; Owner: avnadmin
--

CREATE SEQUENCE public.missione_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.missione_seq OWNER TO avnadmin;

--
-- Name: paese; Type: TABLE; Schema: public; Owner: avnadmin
--

CREATE TABLE public.paese
(
    id   integer                NOT NULL,
    nome character varying(255) NOT NULL
);


ALTER TABLE public.paese
    OWNER TO avnadmin;

--
-- Name: paese_seq; Type: SEQUENCE; Schema: public; Owner: avnadmin
--

CREATE SEQUENCE public.paese_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.paese_seq OWNER TO avnadmin;

--
-- Name: password_reset_token; Type: TABLE; Schema: public; Owner: avnadmin
--

CREATE TABLE public.password_reset_token
(
    id          bigint NOT NULL,
    token       character varying(255),
    utente_id   bigint NOT NULL,
    expiry_date timestamp(6) without time zone
);


ALTER TABLE public.password_reset_token
    OWNER TO avnadmin;

--
-- Name: password_reset_token_seq; Type: SEQUENCE; Schema: public; Owner: avnadmin
--

CREATE SEQUENCE public.password_reset_token_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.password_reset_token_seq OWNER TO avnadmin;

--
-- Name: recensione; Type: TABLE; Schema: public; Owner: avnadmin
--

CREATE TABLE public.recensione
(
    voto             integer NOT NULL,
    autore_id        bigint  NOT NULL,
    data_recensione  date    NOT NULL,
    destinatario_id  bigint  NOT NULL,
    id               bigint  NOT NULL,
    testo_recensione character varying(255)
);


ALTER TABLE public.recensione
    OWNER TO avnadmin;

--
-- Name: recensione_seq; Type: SEQUENCE; Schema: public; Owner: avnadmin
--

CREATE SEQUENCE public.recensione_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.recensione_seq OWNER TO avnadmin;

--
-- Name: ruoli_utenti; Type: TABLE; Schema: public; Owner: avnadmin
--

CREATE TABLE public.ruoli_utenti
(
    ruolo_id  bigint NOT NULL,
    utente_id bigint NOT NULL
);


ALTER TABLE public.ruoli_utenti
    OWNER TO avnadmin;

--
-- Name: ruolo; Type: TABLE; Schema: public; Owner: avnadmin
--

CREATE TABLE public.ruolo
(
    id   bigint                 NOT NULL,
    nome character varying(255) NOT NULL
);


ALTER TABLE public.ruolo
    OWNER TO avnadmin;

--
-- Name: ruolo_seq; Type: SEQUENCE; Schema: public; Owner: avnadmin
--

CREATE SEQUENCE public.ruolo_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.ruolo_seq OWNER TO avnadmin;

--
-- Name: utente; Type: TABLE; Schema: public; Owner: avnadmin
--

CREATE TABLE public.utente
(
    pending                   boolean                NOT NULL,
    sesso                     character(1)           NOT NULL,
    data_emissione_passaporto date,
    data_nascita              date                   NOT NULL,
    data_scadenza_passaporto  date,
    id                        bigint                 NOT NULL,
    temp_pwd_scadenza         timestamp(6) without time zone,
    dtype                     character varying(31)  NOT NULL,
    bio                       character varying(255),
    cognome                   character varying(255) NOT NULL,
    email                     character varying(255) NOT NULL,
    foto_profilo              character varying(255),
    nome                      character varying(255) NOT NULL,
    numero_passaporto         character varying(255),
    password                  character varying(255) NOT NULL,
    path_passaporto           character varying(255),
    temp_password             character varying(255),
    nazionalita_id            integer,
    CONSTRAINT utente_dtype_check CHECK (((dtype)::text = ANY
                                          (ARRAY [('Utente'::character varying)::text, ('Volontario'::character varying)::text])))
);


ALTER TABLE public.utente
    OWNER TO avnadmin;

--
-- Name: utente_seq; Type: SEQUENCE; Schema: public; Owner: avnadmin
--

CREATE SEQUENCE public.utente_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.utente_seq OWNER TO avnadmin;

--
-- Name: verification_token; Type: TABLE; Schema: public; Owner: avnadmin
--

CREATE TABLE public.verification_token
(
    id          bigint NOT NULL,
    token       character varying(255),
    utente_id   bigint,
    expiry_date timestamp(6) without time zone
);


ALTER TABLE public.verification_token
    OWNER TO avnadmin;

--
-- Name: verification_token_seq; Type: SEQUENCE; Schema: public; Owner: avnadmin
--

CREATE SEQUENCE public.verification_token_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.verification_token_seq OWNER TO avnadmin;

--
-- Data for Name: candidatura; Type: TABLE DATA; Schema: public; Owner: avnadmin
--

INSERT INTO public.candidatura
VALUES ('IN_CORSO', 152, '2026-01-15', 252, 202);


--
-- Data for Name: missione; Type: TABLE DATA; Schema: public; Owner: avnadmin
--

INSERT INTO public.missione
VALUES (2, 452, '2026-06-30', '2026-02-01', 352, 'Loksa linn',
        'Fluent English; between 18 and 80 years old; welcomes solo volunteers, couples, and partners of volunteers', '

Hey there! 😊 We''re an expat couple who chose to live close to nature, grow a garden, and raise a child. It''s a beautiful adventure, but sometimes it can get a bit overwhelming! So, we''re looking for a little extra help with a bit of everything. If you enjoy a mix of tasks and good company, we''d love to hear from you! 😉🌿🌸',
        'e6378923-5bb2-4a6d-a0ee-1660647abe15-Immagine_WhatsApp_2025-03-23_ore_12.12.51_bf1a518b.jpg',
        'Experience Family Life in Estonia: Garden & House Help Needed! ✨', '', 73);
INSERT INTO public.missione
VALUES (2, 552, '2026-07-23', '2026-06-23', 253, 'Taipei', '善意。
强大的力量。
胆识。', '大规模攻占台北，收复我们的伟大土地，统一我们的国家。',
        'b443f62f-2216-4d7e-8e69-49f726416779-0227-0524_taipei-xlarge.jpg', '我们去夺回台北吧', '不用担心', 220);
INSERT INTO public.missione
VALUES (2, 502, '2026-10-22', '2026-09-28', 302, 'Bordeaux', 'Buona forza fisica, dinamismo e manualità di base.', 'Missione Bordeaux: Nel Cuore della Vendemmia
Partecipa a un’esperienza autentica tra i filari dei vigneti più famosi al mondo. Cerchiamo persone volenterose per la raccolta dell''uva a Bordeaux, dove la tradizione incontra la passione.

Perché unirti a noi?

Vivi il Territorio: Immergiti nei paesaggi mozzafiato dei castelli bordolesi.

Spirito di Squadra: Condividi la fatica e il sorriso con un team internazionale.

Saper Fare Francese: Scopri i segreti della vinificazione direttamente alla fonte.

Ricompensa: Vitto, alloggio in stile rustico e la soddisfazione di contribuire a un’annata d''eccellenza.',
        '05e81d68-bd38-459c-bd10-fdcd7d7183c3-vite.jpeg', 'Aiuta nella raccolta di vino in Francia a Bordeaux', '', 2);
INSERT INTO public.missione
VALUES (2, 552, '2027-01-10', '2026-12-22', 252, 'Beijing', 'Italiano – madrelingua
Inglese – livello avanzato (C1)
Cinese mandarino – livello intermedio (B1/B2), con esperienza di comunicazione quotidiana e scolastica',
        'Svolgimento di attività di supporto all’insegnamento presso una scuola internazionale di Pechino, collaborando con docenti madrelingua e staff educativo. Il ruolo prevedeva il sostegno quotidiano alle lezioni, la preparazione di materiali didattici e il supporto personalizzato agli studenti, con particolare attenzione allo sviluppo delle competenze linguistiche in inglese e italiano.',
        '84707af4-a588-4b62-8510-3758d7b19459-nbts-viaggi-cina-pechino-citta-proibita.jpg', 'Help teaching a Pechino ',
        'Buona volontà :)', 49);
INSERT INTO public.missione
VALUES (2, 502, '2026-02-16', '2026-01-16', 303, 'Napoli', 'Padronanza Tecnologica: Conoscenza base delle principali app e funzioni dello smartphone.

Empatia e Ascolto: Capacità di trasmettere concetti complessi con un linguaggio semplice e calmo.

Problem Solving: Risoluzione di piccoli intoppi tecnici comuni (configurazioni, recupero password).',
        'Aiuta gli anziani del quartiere a superare le barriere tecnologiche. La missione consiste nel guidare i partecipanti nell''uso dello smartphone e dei servizi digitali essenziali (SPID, email, videochiamate), trasformando la tecnologia in uno strumento di compagnia e indipendenza.',
        'dd1e0624-5bbc-4aa8-91d6-535765750918-istockphoto-1317496472-612x612.jpg',
        'Connessioni Digitali: Tutor per l''Inclusione', 'Conoscenza delle procedure burocratiche online (portali sanitari o comunali).

Disponibilità a domicilio per chi ha difficoltà motorie.

Esperienza pregressa nell''insegnamento o nella formazione.', 1);
INSERT INTO public.missione
VALUES (0, 452, '2026-09-30', '2026-06-01', 402, 'Kolkata', 'Accoglie volontari singoli, coppie e partner di volontari',
        'I volontari supporteranno la fase di pianificazione di un''azienda agricola in permacultura di 2-3 acri, sviluppando un dettagliato foglio Excel che elenchi tutti gli elementi e le attrezzature necessari per l''avvio dell''azienda agricola, tra cui preparazione del terreno, sistemi idrici, rigenerazione del suolo, energia, alloggi, attrezzi e manutenzione. Per ogni elemento, i volontari condurranno un''analisi costi-benefici, confrontando le alternative in base a costi, durabilità, impatto ambientale, disponibilità locale e sostenibilità a lungo termine.',
        'fd21fa75-b1aa-49b4-bc1e-7716a70f5bbb-122942329_3711764192168227_7945410133559973583_n.jpg',
        'Programma di volontariato per la pianificazione agricola e la mappatura delle risorse in permacultura', '',
        105);
INSERT INTO public.missione
VALUES (1, 552, '3033-12-18', '3033-12-17', 452, 'Gerusalemme', '没有任何',
        '我们很清楚他们在做什么，该死的。我们会来接你。', 'b30521ad-732d-4da5-ae14-b07eec7b5067-images.jpg',
        '我们攻击非国家', '', 110);
INSERT INTO public.missione
VALUES (1, 202, '2026-01-08', '2026-01-21', 153, 'Zurigo', 'Fl Studio 25 e Serum',
        'Leggi il titolo e agisci di consequenza',
        '1380bf01-f437-4613-87c8-2f7ca121cc03-Screenshot 2024-10-17 003639.png', 'Aiuta Papa V a fare un pezzo decente',
        '', 3);
INSERT INTO public.missione
VALUES (0, 452, '2027-01-12', '2026-01-12', 403, 'Jinja',
        'Welcomes solo volunteers, couples, and partners of volunteers',
        'Supporting social media, volunteers at HGADC Organization help to market and promote foundation activities ',
        '93ebf570-c355-4dc8-8981-78ff16ffef5f-1000179712.jpg',
        'Help Us with Our social media and Market our foundation', '', 236);
INSERT INTO public.missione
VALUES (2, 202, '2026-01-05', '2026-01-22', 152, 'Fisciano', 'Reti più di 25',
        'Siamo dei poveri cristiani di resto 0 x favore aiutaci',
        '1b16a3ba-191b-4abf-9c58-c9ad3b2d3d02-Screenshot 2025-03-30 011505.png',
        'Offri ripetizioni di reti ad un povero cristiano', '', 1);
INSERT INTO public.missione
VALUES (2, 202, '2026-03-06', '2026-01-01', 202, 'Bruxelles', 'Nessuna',
        'Il mare è pieno di plastica, aiutaci a pulirlo!', '76b735b2-fd75-45d4-8e47-eb3bc5419f2c-tartarughineLast.jpg',
        'Aiuta a pulire il mare per salvare le tartarughe marine', 'Nessuno', 4);
INSERT INTO public.missione
VALUES (0, 602, '2026-02-22', '2026-02-22', 453, 'Salerno', 'Programmazione in almeno uno dei principali linguaggi: Python, Java, JavaScript, C++ o simili
Sviluppo web (HTML, CSS, JavaScript, framework come React, Angular o Vue)
Sviluppo backend con framework come Spring, Django, Node.js
Database: conoscenza di SQL (MySQL, PostgreSQL) e NoSQL (MongoDB)
Versionamento del codice con Git e GitHub/GitLab
Principi di ingegneria del software: design patterns, architetture, testing
Metodologie Agile/Scrum per la gestione dei progetti
Fondamenti di sistemi operativi e reti
Conoscenza delle API REST e dei microservizi
Capacità di debugging e ottimizzazione del codice', 'Sviluppo di un percorso formativo e pratico orientato alla professione di ingegnere del software, svolto nell’area di Salerno. L’attività comprende lo studio strutturato di linguaggi di programmazione, metodologie di sviluppo moderne e strumenti utilizzati nell’industria, con l’obiettivo di acquisire competenze tecniche solide e capacità di progettazione software.
Studio approfondito di linguaggi come Python, Java, JavaScript e dei relativi framework.
Progettazione e sviluppo di applicazioni web e software seguendo principi di ingegneria del software.
Utilizzo di strumenti professionali: Git/GitHub, ambienti di sviluppo, sistemi di versionamento e testing.
Introduzione alle metodologie Agile/Scrum per la gestione dei progetti.
Realizzazione di progetti individuali e di gruppo per simulare contesti lavorativi reali.
Approfondimento di basi di dati relazionali e non relazionali (SQL, NoSQL).
Partecipazione a workshop, eventi tech e iniziative locali nell’ecosistema digitale salernitano.',
        'cc073734-2ae3-4063-9f5b-25bfab00ee45-sesa.png', 'Diventa un Software Engineer!', 'Familiarità con Docker e concetti di containerizzazione
Conoscenza base di cloud computing (AWS, Azure, Google Cloud)
Esperienza con test automatizzati e CI/CD
Partecipazione a progetti personali, hackathon o community tech locali', 1);
INSERT INTO public.missione
VALUES (2, 2, '2026-07-31', '2026-04-17', 502, 'Upper Moutere', '- Livello di inglese intermedio;
- Più di 20 anni.',
        'Unisciti a noi nella nostra splendida proprietà per aiutarti con: rimozione delle erbacce dagli alberi autoctoni, manutenzione della proprietà, ritocchi alla vernice, pulizie delle stanze… Avrai la tua deliziosa roulotte e cucina/bagno con viste mozzafiato.',
        'f39310ac-9dd7-4f1a-8a36-feee909e9cfa-nuovazelanda.jpeg',
        'Vieni ad aiutarci con il giardinaggio e la pittura nel nostro agriturismo!',
        'Mezzo di trasporto privato: l''area è poco collegata a supermercati e altri negozi.', 159);


--
-- Data for Name: paese; Type: TABLE DATA; Schema: public; Owner: avnadmin
--

INSERT INTO public.paese
VALUES (1, 'Italia');
INSERT INTO public.paese
VALUES (2, 'Francia');
INSERT INTO public.paese
VALUES (3, 'Svizzera');
INSERT INTO public.paese
VALUES (4, 'Belgio');
INSERT INTO public.paese
VALUES (5, 'Afghanistan');
INSERT INTO public.paese
VALUES (6, 'Isole Aland');
INSERT INTO public.paese
VALUES (7, 'Albania');
INSERT INTO public.paese
VALUES (8, 'Algeria');
INSERT INTO public.paese
VALUES (9, 'Samoa Americane');
INSERT INTO public.paese
VALUES (10, 'Andorra');
INSERT INTO public.paese
VALUES (11, 'Angola');
INSERT INTO public.paese
VALUES (12, 'Anguilla');
INSERT INTO public.paese
VALUES (13, 'Antartide');
INSERT INTO public.paese
VALUES (14, 'Antigua e Barbuda');
INSERT INTO public.paese
VALUES (15, 'Argentina');
INSERT INTO public.paese
VALUES (16, 'Armenia');
INSERT INTO public.paese
VALUES (17, 'Aruba');
INSERT INTO public.paese
VALUES (18, 'Australia');
INSERT INTO public.paese
VALUES (19, 'Austria');
INSERT INTO public.paese
VALUES (20, 'Azerbaijan');
INSERT INTO public.paese
VALUES (21, 'Bahrein');
INSERT INTO public.paese
VALUES (22, 'Bangladesh');
INSERT INTO public.paese
VALUES (23, 'Barbados');
INSERT INTO public.paese
VALUES (24, 'Bielorussia');
INSERT INTO public.paese
VALUES (26, 'Belize');
INSERT INTO public.paese
VALUES (27, 'Benin');
INSERT INTO public.paese
VALUES (28, 'Bermuda');
INSERT INTO public.paese
VALUES (29, 'Bhutan');
INSERT INTO public.paese
VALUES (30, 'Bolivia');
INSERT INTO public.paese
VALUES (31, 'Bonaire, Saint-Eustache e Saba');
INSERT INTO public.paese
VALUES (32, 'Bosnia ed Erzegovina');
INSERT INTO public.paese
VALUES (33, 'Botswana');
INSERT INTO public.paese
VALUES (34, 'Isola Bouvet');
INSERT INTO public.paese
VALUES (35, 'Brasile');
INSERT INTO public.paese
VALUES (36, 'Territorio britannico dell''oceano indiano');
INSERT INTO public.paese
VALUES (37, 'Brunei');
INSERT INTO public.paese
VALUES (38, 'Bulgaria');
INSERT INTO public.paese
VALUES (39, 'Burkina Faso');
INSERT INTO public.paese
VALUES (40, 'Burundi');
INSERT INTO public.paese
VALUES (41, 'Cambogia');
INSERT INTO public.paese
VALUES (42, 'Camerun');
INSERT INTO public.paese
VALUES (43, 'Canada');
INSERT INTO public.paese
VALUES (44, 'Capo Verde');
INSERT INTO public.paese
VALUES (45, 'Isole Cayman');
INSERT INTO public.paese
VALUES (46, 'Repubblica Centrafricana');
INSERT INTO public.paese
VALUES (47, 'Ciad');
INSERT INTO public.paese
VALUES (48, 'Cile');
INSERT INTO public.paese
VALUES (49, 'Cina');
INSERT INTO public.paese
VALUES (50, 'Isola di Natale');
INSERT INTO public.paese
VALUES (51, 'Isole Cocos e Keeling');
INSERT INTO public.paese
VALUES (52, 'Colombia');
INSERT INTO public.paese
VALUES (53, 'Comore');
INSERT INTO public.paese
VALUES (54, 'Congo');
INSERT INTO public.paese
VALUES (55, 'Isole Cook');
INSERT INTO public.paese
VALUES (56, 'Costa Rica');
INSERT INTO public.paese
VALUES (57, 'Costa D''Avorio');
INSERT INTO public.paese
VALUES (58, 'Croazia');
INSERT INTO public.paese
VALUES (59, 'Cuba');
INSERT INTO public.paese
VALUES (60, 'Curaçao');
INSERT INTO public.paese
VALUES (61, 'Cipro');
INSERT INTO public.paese
VALUES (62, 'Repubblica Ceca');
INSERT INTO public.paese
VALUES (63, 'Congo (Rep. Dem.)');
INSERT INTO public.paese
VALUES (64, 'Danimarca');
INSERT INTO public.paese
VALUES (65, 'Gibuti');
INSERT INTO public.paese
VALUES (66, 'Dominica');
INSERT INTO public.paese
VALUES (67, 'Repubblica Dominicana');
INSERT INTO public.paese
VALUES (68, 'Ecuador');
INSERT INTO public.paese
VALUES (69, 'Egitto');
INSERT INTO public.paese
VALUES (70, 'El Salvador');
INSERT INTO public.paese
VALUES (71, 'Guinea Equatoriale');
INSERT INTO public.paese
VALUES (72, 'Eritrea');
INSERT INTO public.paese
VALUES (73, 'Estonia');
INSERT INTO public.paese
VALUES (74, 'Swaziland');
INSERT INTO public.paese
VALUES (75, 'Etiopia');
INSERT INTO public.paese
VALUES (76, 'Isole Falkland o Isole Malvine');
INSERT INTO public.paese
VALUES (77, 'Isole Far Oer');
INSERT INTO public.paese
VALUES (78, 'Figi');
INSERT INTO public.paese
VALUES (79, 'Finlandia');
INSERT INTO public.paese
VALUES (81, 'Guyana francese');
INSERT INTO public.paese
VALUES (82, 'Polinesia Francese');
INSERT INTO public.paese
VALUES (83, 'Territori Francesi del Sud');
INSERT INTO public.paese
VALUES (84, 'Gabon');
INSERT INTO public.paese
VALUES (85, 'Georgia');
INSERT INTO public.paese
VALUES (86, 'Germania');
INSERT INTO public.paese
VALUES (87, 'Ghana');
INSERT INTO public.paese
VALUES (88, 'Gibilterra');
INSERT INTO public.paese
VALUES (89, 'Grecia');
INSERT INTO public.paese
VALUES (90, 'Groenlandia');
INSERT INTO public.paese
VALUES (91, 'Grenada');
INSERT INTO public.paese
VALUES (92, 'Guadeloupa');
INSERT INTO public.paese
VALUES (93, 'Guam');
INSERT INTO public.paese
VALUES (94, 'Guatemala');
INSERT INTO public.paese
VALUES (95, 'Guernsey');
INSERT INTO public.paese
VALUES (96, 'Guinea');
INSERT INTO public.paese
VALUES (97, 'Guinea-Bissau');
INSERT INTO public.paese
VALUES (98, 'Guyana');
INSERT INTO public.paese
VALUES (99, 'Haiti');
INSERT INTO public.paese
VALUES (100, 'Isole Heard e McDonald');
INSERT INTO public.paese
VALUES (101, 'Honduras');
INSERT INTO public.paese
VALUES (102, 'Hong Kong');
INSERT INTO public.paese
VALUES (103, 'Ungheria');
INSERT INTO public.paese
VALUES (104, 'Islanda');
INSERT INTO public.paese
VALUES (105, 'India');
INSERT INTO public.paese
VALUES (106, 'Indonesia');
INSERT INTO public.paese
VALUES (107, 'Iran');
INSERT INTO public.paese
VALUES (108, 'Iraq');
INSERT INTO public.paese
VALUES (109, 'Irlanda');
INSERT INTO public.paese
VALUES (110, 'Israele');
INSERT INTO public.paese
VALUES (112, 'Giamaica');
INSERT INTO public.paese
VALUES (113, 'Giappone');
INSERT INTO public.paese
VALUES (114, 'Isola di Jersey');
INSERT INTO public.paese
VALUES (115, 'Giordania');
INSERT INTO public.paese
VALUES (116, 'Kazakistan');
INSERT INTO public.paese
VALUES (117, 'Kenya');
INSERT INTO public.paese
VALUES (118, 'Kiribati');
INSERT INTO public.paese
VALUES (119, 'Kosovo');
INSERT INTO public.paese
VALUES (120, 'Kuwait');
INSERT INTO public.paese
VALUES (121, 'Kirghizistan');
INSERT INTO public.paese
VALUES (122, 'Laos');
INSERT INTO public.paese
VALUES (123, 'Lettonia');
INSERT INTO public.paese
VALUES (124, 'Libano');
INSERT INTO public.paese
VALUES (125, 'Lesotho');
INSERT INTO public.paese
VALUES (126, 'Liberia');
INSERT INTO public.paese
VALUES (127, 'Libia');
INSERT INTO public.paese
VALUES (128, 'Liechtenstein');
INSERT INTO public.paese
VALUES (129, 'Lituania');
INSERT INTO public.paese
VALUES (130, 'Lussemburgo');
INSERT INTO public.paese
VALUES (131, 'Macao');
INSERT INTO public.paese
VALUES (132, 'Madagascar');
INSERT INTO public.paese
VALUES (133, 'Malawi');
INSERT INTO public.paese
VALUES (134, 'Malesia');
INSERT INTO public.paese
VALUES (135, 'Maldive');
INSERT INTO public.paese
VALUES (136, 'Mali');
INSERT INTO public.paese
VALUES (137, 'Malta');
INSERT INTO public.paese
VALUES (138, 'Isola di Man');
INSERT INTO public.paese
VALUES (139, 'Isole Marshall');
INSERT INTO public.paese
VALUES (140, 'Martinica');
INSERT INTO public.paese
VALUES (141, 'Mauritania');
INSERT INTO public.paese
VALUES (142, 'Mauritius');
INSERT INTO public.paese
VALUES (143, 'Mayotte');
INSERT INTO public.paese
VALUES (144, 'Messico');
INSERT INTO public.paese
VALUES (145, 'Micronesia');
INSERT INTO public.paese
VALUES (146, 'Moldavia');
INSERT INTO public.paese
VALUES (147, 'Principato di Monaco');
INSERT INTO public.paese
VALUES (148, 'Mongolia');
INSERT INTO public.paese
VALUES (149, 'Montenegro');
INSERT INTO public.paese
VALUES (150, 'Montserrat');
INSERT INTO public.paese
VALUES (151, 'Morocco');
INSERT INTO public.paese
VALUES (152, 'Mozambico');
INSERT INTO public.paese
VALUES (153, 'Birmania');
INSERT INTO public.paese
VALUES (154, 'Namibia');
INSERT INTO public.paese
VALUES (155, 'Nauru');
INSERT INTO public.paese
VALUES (156, 'Nepal');
INSERT INTO public.paese
VALUES (157, 'Paesi Bassi');
INSERT INTO public.paese
VALUES (158, 'Nuova Caledonia');
INSERT INTO public.paese
VALUES (159, 'Nuova Zelanda');
INSERT INTO public.paese
VALUES (160, 'Nicaragua');
INSERT INTO public.paese
VALUES (161, 'Niger');
INSERT INTO public.paese
VALUES (162, 'Nigeria');
INSERT INTO public.paese
VALUES (163, 'Niue');
INSERT INTO public.paese
VALUES (164, 'Isola Norfolk');
INSERT INTO public.paese
VALUES (165, 'Corea del Nord');
INSERT INTO public.paese
VALUES (166, 'Macedonia del Nord');
INSERT INTO public.paese
VALUES (167, 'Isole Marianne Settentrionali');
INSERT INTO public.paese
VALUES (168, 'Norvegia');
INSERT INTO public.paese
VALUES (169, 'Oman');
INSERT INTO public.paese
VALUES (170, 'Pakistan');
INSERT INTO public.paese
VALUES (171, 'Palau');
INSERT INTO public.paese
VALUES (172, 'Palestina');
INSERT INTO public.paese
VALUES (173, 'Panama');
INSERT INTO public.paese
VALUES (174, 'Papua Nuova Guinea');
INSERT INTO public.paese
VALUES (175, 'Paraguay');
INSERT INTO public.paese
VALUES (176, 'Perù');
INSERT INTO public.paese
VALUES (177, 'Filippine');
INSERT INTO public.paese
VALUES (178, 'Isole Pitcairn');
INSERT INTO public.paese
VALUES (179, 'Polonia');
INSERT INTO public.paese
VALUES (180, 'Portogallo');
INSERT INTO public.paese
VALUES (181, 'Puerto Rico');
INSERT INTO public.paese
VALUES (182, 'Qatar');
INSERT INTO public.paese
VALUES (183, 'Riunione');
INSERT INTO public.paese
VALUES (184, 'Romania');
INSERT INTO public.paese
VALUES (185, 'Russia');
INSERT INTO public.paese
VALUES (186, 'Ruanda');
INSERT INTO public.paese
VALUES (187, 'Sant''Elena');
INSERT INTO public.paese
VALUES (188, 'Saint Kitts e Nevis');
INSERT INTO public.paese
VALUES (189, 'Santa Lucia');
INSERT INTO public.paese
VALUES (190, 'Saint-Pierre e Miquelon');
INSERT INTO public.paese
VALUES (191, 'Saint Vincent e Grenadine');
INSERT INTO public.paese
VALUES (192, 'Antille Francesi');
INSERT INTO public.paese
VALUES (193, 'Saint Martin');
INSERT INTO public.paese
VALUES (194, 'Samoa');
INSERT INTO public.paese
VALUES (195, 'San Marino');
INSERT INTO public.paese
VALUES (196, 'São Tomé e Príncipe');
INSERT INTO public.paese
VALUES (197, 'Arabia Saudita');
INSERT INTO public.paese
VALUES (198, 'Senegal');
INSERT INTO public.paese
VALUES (199, 'Serbia');
INSERT INTO public.paese
VALUES (200, 'Seychelles');
INSERT INTO public.paese
VALUES (201, 'Sierra Leone');
INSERT INTO public.paese
VALUES (202, 'Singapore');
INSERT INTO public.paese
VALUES (203, 'Saint Martin (parte olandese)');
INSERT INTO public.paese
VALUES (204, 'Slovacchia');
INSERT INTO public.paese
VALUES (205, 'Slovenia');
INSERT INTO public.paese
VALUES (206, 'Isole Salomone');
INSERT INTO public.paese
VALUES (207, 'Somalia');
INSERT INTO public.paese
VALUES (208, 'Sud Africa');
INSERT INTO public.paese
VALUES (209, 'Georgia del Sud e Isole Sandwich Meridionali');
INSERT INTO public.paese
VALUES (210, 'Corea del Sud');
INSERT INTO public.paese
VALUES (211, 'Sudan del sud');
INSERT INTO public.paese
VALUES (212, 'Spagna');
INSERT INTO public.paese
VALUES (213, 'Sri Lanka');
INSERT INTO public.paese
VALUES (214, 'Sudan');
INSERT INTO public.paese
VALUES (215, 'Suriname');
INSERT INTO public.paese
VALUES (216, 'Svalbard e Jan Mayen');
INSERT INTO public.paese
VALUES (217, 'Svezia');
INSERT INTO public.paese
VALUES (219, 'Siria');
INSERT INTO public.paese
VALUES (220, 'Taiwan');
INSERT INTO public.paese
VALUES (221, 'Tagikistan');
INSERT INTO public.paese
VALUES (222, 'Tanzania');
INSERT INTO public.paese
VALUES (223, 'Tailandia');
INSERT INTO public.paese
VALUES (224, 'Bahamas');
INSERT INTO public.paese
VALUES (225, 'Gambia');
INSERT INTO public.paese
VALUES (226, 'Timor Est');
INSERT INTO public.paese
VALUES (227, 'Togo');
INSERT INTO public.paese
VALUES (228, 'Isole Tokelau');
INSERT INTO public.paese
VALUES (229, 'Tonga');
INSERT INTO public.paese
VALUES (230, 'Trinidad e Tobago');
INSERT INTO public.paese
VALUES (231, 'Tunisia');
INSERT INTO public.paese
VALUES (232, 'Turchia');
INSERT INTO public.paese
VALUES (233, 'Turkmenistan');
INSERT INTO public.paese
VALUES (234, 'Isole Turks e Caicos');
INSERT INTO public.paese
VALUES (235, 'Tuvalu');
INSERT INTO public.paese
VALUES (236, 'Uganda');
INSERT INTO public.paese
VALUES (237, 'Ucraina');
INSERT INTO public.paese
VALUES (238, 'Emirati Arabi Uniti');
INSERT INTO public.paese
VALUES (239, 'Regno Unito');
INSERT INTO public.paese
VALUES (240, 'Stati Uniti D''America');
INSERT INTO public.paese
VALUES (241, 'Isole minori esterne degli Stati Uniti d''America');
INSERT INTO public.paese
VALUES (242, 'Uruguay');
INSERT INTO public.paese
VALUES (243, 'Uzbekistan');
INSERT INTO public.paese
VALUES (244, 'Vanuatu');
INSERT INTO public.paese
VALUES (245, 'Santa Sede');
INSERT INTO public.paese
VALUES (246, 'Venezuela');
INSERT INTO public.paese
VALUES (247, 'Vietnam');
INSERT INTO public.paese
VALUES (248, 'Isole Vergini Britanniche');
INSERT INTO public.paese
VALUES (249, 'Isole Vergini americane');
INSERT INTO public.paese
VALUES (250, 'Wallis e Futuna');
INSERT INTO public.paese
VALUES (251, 'Sahara Occidentale');
INSERT INTO public.paese
VALUES (252, 'Yemen');
INSERT INTO public.paese
VALUES (253, 'Zambia');
INSERT INTO public.paese
VALUES (254, 'Zimbabwe');


--
-- Data for Name: password_reset_token; Type: TABLE DATA; Schema: public; Owner: avnadmin
--

INSERT INTO public.password_reset_token
VALUES (52, 'c5ae5c9b-052e-4da5-9921-4032fa18ad9a', 802, '2026-01-13 18:46:48.485093');


--
-- Data for Name: recensione; Type: TABLE DATA; Schema: public; Owner: avnadmin
--


--
-- Data for Name: ruoli_utenti; Type: TABLE DATA; Schema: public; Owner: avnadmin
--

INSERT INTO public.ruoli_utenti
VALUES (1, 1);
INSERT INTO public.ruoli_utenti
VALUES (2, 2);
INSERT INTO public.ruoli_utenti
VALUES (3, 3);
INSERT INTO public.ruoli_utenti
VALUES (4, 4);
INSERT INTO public.ruoli_utenti
VALUES (1, 152);
INSERT INTO public.ruoli_utenti
VALUES (2, 202);
INSERT INTO public.ruoli_utenti
VALUES (1, 252);
INSERT INTO public.ruoli_utenti
VALUES (1, 352);
INSERT INTO public.ruoli_utenti
VALUES (2, 353);
INSERT INTO public.ruoli_utenti
VALUES (2, 402);
INSERT INTO public.ruoli_utenti
VALUES (2, 452);
INSERT INTO public.ruoli_utenti
VALUES (2, 502);
INSERT INTO public.ruoli_utenti
VALUES (2, 552);
INSERT INTO public.ruoli_utenti
VALUES (2, 602);
INSERT INTO public.ruoli_utenti
VALUES (2, 702);
INSERT INTO public.ruoli_utenti
VALUES (1, 752);
INSERT INTO public.ruoli_utenti
VALUES (1, 802);
INSERT INTO public.ruoli_utenti
VALUES (1, 803);
INSERT INTO public.ruoli_utenti
VALUES (1, 954);


--
-- Data for Name: ruolo; Type: TABLE DATA; Schema: public; Owner: avnadmin
--

INSERT INTO public.ruolo
VALUES (1, 'ROLE_VOLUNTEER');
INSERT INTO public.ruolo
VALUES (2, 'ROLE_ORGANIZER');
INSERT INTO public.ruolo
VALUES (3, 'ROLE_MODERATOR');
INSERT INTO public.ruolo
VALUES (4, 'ROLE_ACCOUNT_MANAGER');


--
-- Data for Name: utente; Type: TABLE DATA; Schema: public; Owner: avnadmin
--

INSERT INTO public.utente
VALUES (false, 'M', '2004-04-01', '2026-01-06', '2027-04-01', 1, NULL, 'Volontario', NULL, 'test',
        'volunteer@earthlocals.com', NULL, 'test', 'AAA',
        '$argon2id$v=19$m=16384,t=2,p=1$E/NCSlGFntqHasNftBFEuA$3dDcSF67KBuuncTrUZ70jX9OavG32gIBjlCcbPlUhqk',
        'a240fe66-f323-4c34-a5d8-527c8d6b732f-basic-text.pdf', NULL, 1);
INSERT INTO public.utente
VALUES (false, 'M', '2004-03-23', '2004-03-23', '3223-03-23', 352, NULL, 'Volontario', NULL, 'Niemiec',
        'francesconiemiec23@gmail.com', NULL, 'Francesco', 'FN2323232',
        '$argon2id$v=19$m=16384,t=2,p=1$7f64SB2ngs0ox4hmE77+Eg$E4gjipQhkIOK/EGBR2oIonl6RmrbsPPa3qYeUxJ8I20',
        '52ab3eac-66f2-4ee7-815b-c68e820f0927-03.Introduzione ad UML.pdf', NULL, 1);
INSERT INTO public.utente
VALUES (false, 'M', NULL, '2004-01-27', NULL, 353, NULL, 'Utente', NULL, 'Pha Lo Mbhs', 'ghastman999@gmail.com', NULL,
        'Fhan Bi Yoh', NULL,
        '$argon2id$v=19$m=16384,t=2,p=1$+CSIHoovZECv1k8M1rlz9w$LjscYiCGjChF1KJCuzoLexcbDa7jQhoEt8D7JivMUyA', NULL, NULL,
        1);
INSERT INTO public.utente
VALUES (false, 'M', NULL, '2004-04-01', NULL, 202, NULL, 'Utente', NULL, 'Rossi', 'mario.rossi@example.com', NULL,
        'Bruno', NULL,
        '$argon2id$v=19$m=16384,t=2,p=1$3+NjYX5/DX7mS4vcl4em9w$Vx2Kd487ZxTN6+eI0MPVuoIxf1s2Fa9RdV3uxftzkGI', NULL, NULL,
        1);
INSERT INTO public.utente
VALUES (false, 'M', NULL, '2005-12-23', NULL, 402, NULL, 'Utente', NULL, 'test', 'pixetid138@gopicta.com', NULL, 'Test',
        NULL, '$argon2id$v=19$m=16384,t=2,p=1$CLATRTFBuuyP2MMvfoZjvA$UzTH5NtpBWxs1VWZSroCidAc6RfOXU+ETt6RS0YhvE4', NULL,
        NULL, 1);
INSERT INTO public.utente
VALUES (false, 'M', NULL, '2004-04-01', NULL, 452, NULL, 'Utente', NULL, 'Squitieri', 'andeser@protonmail.com', NULL,
        'Andrea', NULL,
        '$argon2id$v=19$m=16384,t=2,p=1$T0kmSL0UNfzDJ3I2fKxXew$LGVrHROmIz9DbKoz951GTnGyiS422SLksOj9HjBizQk', NULL, NULL,
        1);
INSERT INTO public.utente
VALUES (false, 'M', NULL, '2003-06-09', NULL, 502, NULL, 'Utente', NULL, 'Abbatiello', 'onlyforadobe69@gmail.com', NULL,
        'Simone', NULL,
        '$argon2id$v=19$m=16384,t=2,p=1$X/v/azTNH9Mj887Plr5PIw$zYt6PgyreYleCK8Kazlx+gincH9I3tsQGIrHFsDw+54', NULL, NULL,
        1);
INSERT INTO public.utente
VALUES (false, 'M', NULL, '1995-02-19', NULL, 552, NULL, 'Utente', NULL, 'De Rox', 'maoderox@test.com', NULL, 'Mao',
        NULL, '$argon2id$v=19$m=16384,t=2,p=1$pdgFY2fdqKXu5wWmY+TfGA$WBfInw4i/o0vmIj+LLaMPdbmnVN53aAViKcSEcZnn+o', NULL,
        NULL, 49);
INSERT INTO public.utente
VALUES (false, 'M', NULL, '2026-01-04', NULL, 3, NULL, 'Utente', NULL, 'test', 'moderator@earthlocals.com', NULL,
        'test', NULL,
        '$argon2id$v=19$m=16384,t=2,p=1$dWMlIfMZv7OJCpJIKyzhfg$08m1tcYgaMWqZfTii5i84NTvM7U3H3v41MZ7X0wOynU', NULL, NULL,
        1);
INSERT INTO public.utente
VALUES (false, 'M', NULL, '2026-01-04', NULL, 2, NULL, 'Utente', NULL, 'test', 'organizer@earthlocals.com', NULL,
        'test', NULL,
        '$argon2id$v=19$m=16384,t=2,p=1$OTGnCC3SRJdVdu2QyEw1ww$gKo/hRge6qOI/fdOaEgM2BiB6pdSYsigStL/jEHpJAw', NULL, NULL,
        1);
INSERT INTO public.utente
VALUES (false, 'M', NULL, '2026-01-04', NULL, 4, NULL, 'Utente', NULL, 'test', 'accountmanager@earthlocals.com', NULL,
        'test', NULL,
        '$argon2id$v=19$m=16384,t=2,p=1$5xUDl9mQkptkimRTIfHWmQ$Jxz1oQnv1y76taPC663lmgqoySuFXo5PU/xKS9y1llk', NULL, NULL,
        1);
INSERT INTO public.utente
VALUES (false, 'M', NULL, '0001-01-01', NULL, 602, NULL, 'Utente', NULL, 'Lab', 'sesalab@earthlocalstest.com', NULL,
        'Sesa', NULL,
        '$argon2id$v=19$m=16384,t=2,p=1$tiH4cPJmu+bQ6RZk0WHemg$jIk6H1I+n4m72fOVy1861I6n3StFRS6GXUnnVaKNZaA', NULL, NULL,
        1);
INSERT INTO public.utente
VALUES (false, 'F', '2004-04-01', '2004-04-01', '2027-04-01', 152, NULL, 'Volontario', NULL, 'Squitieri',
        'andrea.squitieri@disroot.org', NULL, 'Andrea', 'AAA',
        '$argon2id$v=19$m=16384,t=2,p=1$VE79SUVDbDsX/V9ZEj2BMQ$PsmDa2zBu/VaWEWxruRRSc3ZHr+TRhr2CtoP0F7reuM',
        'fdd12856-0919-4ccc-a6cd-00fd8b130fa9-basic-text.pdf', NULL, 3);
INSERT INTO public.utente
VALUES (true, 'M', '2000-04-21', '2003-02-23', '2040-04-29', 954, NULL, 'Volontario', NULL, 'Rossi',
        'frankrossi@mail.com', NULL, 'Francesco', 'AC000000',
        '$argon2id$v=19$m=16384,t=2,p=1$Pd7z+yjbkYqMZuS/8GqjFw$TFThVHCV4WA67/cZbcJVqJRMTcdQ/7BDLWjLP9xfBhg',
        'c6bc7134-fb55-403d-a10b-cc3e5774e289-Checklist.pdf', NULL, 14);
INSERT INTO public.utente
VALUES (false, 'M', NULL, '2004-04-01', NULL, 702, NULL, 'Utente', NULL, 'Squitieri', 'andeser44@gmail.com', NULL,
        'Andrea', NULL,
        '$argon2id$v=19$m=16384,t=2,p=1$rH3G+SJXF5JqpUjjd9D+wA$ClsD/rZJLlU+ffJLhpigD5CCggckAVX/GcKDhW1PAVA', NULL, NULL,
        1);
INSERT INTO public.utente
VALUES (false, 'M', '2004-04-01', '2004-04-01', '2028-04-01', 752, NULL, 'Volontario', NULL, 'Squitieri',
        'meros17640@eubonus.com', NULL, 'Andrea', 'AAA',
        '$argon2id$v=19$m=16384,t=2,p=1$YvR34IHEYAW3nFCY7Ju+Xg$zOeCJj1r2StClVnQ6Of/wl6lGMGNV8cnYHkW7DWmrR4',
        'aa7cba0b-888e-4bf3-b46f-6f3b55ba88a1-basic-text.pdf', NULL, 5);
INSERT INTO public.utente
VALUES (true, 'F', '2021-01-12', '1986-04-23', '2032-03-18', 802, NULL, 'Volontario', NULL, 'Niemiec ',
        'asia.86jn@gmail.com', NULL, 'Joanna Magdalena ', 'AA0000000',
        '$argon2id$v=19$m=16384,t=2,p=1$5a5G5XY2kxpFvtERKj6TbA$XUcxgv4IaRRN5cCBqqjPxwuJJ0gpuQ5EA2txp03MRFk',
        '56e1975f-2dec-4e52-b32b-a30f43740e31-Attestazione_Patrimoniale_PosteItaliane_NMCFNC04C23E791J_2026.pdf', NULL,
        179);
INSERT INTO public.utente
VALUES (true, 'M', '2004-04-01', '2004-04-01', '2027-04-01', 803, NULL, 'Volontario', NULL, 'Eubonus',
        'kaboy73656@feanzier.com', NULL, 'Meros', 'AAA',
        '$argon2id$v=19$m=16384,t=2,p=1$18jR9GFy67U41OjngpeAKg$5pLUhvI8YYa2DEpM3gOVOT+4Uh3A9uhopirH6OlaLfY',
        'e1e881f9-62aa-439e-a8d1-8a1f440bb3a9-basic-text.pdf', NULL, 1);
INSERT INTO public.utente
VALUES (false, 'M', '2025-12-24', '2004-03-02', '2222-01-20', 252, NULL, 'Volontario', NULL, 'Abbatiello',
        's.abbatiello@studenti.unisa.it', NULL, 'Simone', 'AA0000001',
        '$argon2id$v=19$m=16384,t=2,p=1$kzlSzRA9viKzI2B2CKAvkw$vN0rMg+aGWzMtIQYoy16p6m2frEIiJNFINOt3MeByhs',
        'a4022efb-bc7e-4686-81e2-a8612595a116-03.Maven.pdf', NULL, 1);


--
-- Data for Name: verification_token; Type: TABLE DATA; Schema: public; Owner: avnadmin
--

INSERT INTO public.verification_token
VALUES (152, '54ad6c07-f2d1-4c27-8d4f-c4e0c0b7adf9', 802, '2026-01-13 18:43:50.57597');
INSERT INTO public.verification_token
VALUES (153, 'ccee272e-eb67-4b4a-a3c5-30226be6f4a2', 803, '2026-01-13 18:54:33.3793');
INSERT INTO public.verification_token
VALUES (303, 'edf7756d-8d05-4161-859a-6a04acddb1ee', 954, '2026-01-16 12:17:56.370133');


--
-- Name: candidatura_seq; Type: SEQUENCE SET; Schema: public; Owner: avnadmin
--

SELECT pg_catalog.setval('public.candidatura_seq', 301, true);


--
-- Name: missione_seq; Type: SEQUENCE SET; Schema: public; Owner: avnadmin
--

SELECT pg_catalog.setval('public.missione_seq', 551, true);


--
-- Name: paese_seq; Type: SEQUENCE SET; Schema: public; Owner: avnadmin
--

SELECT pg_catalog.setval('public.paese_seq', 151, true);


--
-- Name: password_reset_token_seq; Type: SEQUENCE SET; Schema: public; Owner: avnadmin
--

SELECT pg_catalog.setval('public.password_reset_token_seq', 151, true);


--
-- Name: recensione_seq; Type: SEQUENCE SET; Schema: public; Owner: avnadmin
--

SELECT pg_catalog.setval('public.recensione_seq', 1, false);


--
-- Name: ruolo_seq; Type: SEQUENCE SET; Schema: public; Owner: avnadmin
--

SELECT pg_catalog.setval('public.ruolo_seq', 51, true);


--
-- Name: utente_seq; Type: SEQUENCE SET; Schema: public; Owner: avnadmin
--

SELECT pg_catalog.setval('public.utente_seq', 1251, true);


--
-- Name: verification_token_seq; Type: SEQUENCE SET; Schema: public; Owner: avnadmin
--

SELECT pg_catalog.setval('public.verification_token_seq', 601, true);


--
-- Name: 21616..21712; Type: BLOB METADATA; Schema: -; Owner: avnadmin
--


--
-- Name: candidatura candidatura_pkey; Type: CONSTRAINT; Schema: public; Owner: avnadmin
--

ALTER TABLE ONLY public.candidatura
    ADD CONSTRAINT candidatura_pkey PRIMARY KEY (id);


--
-- Name: missione missione_pkey; Type: CONSTRAINT; Schema: public; Owner: avnadmin
--

ALTER TABLE ONLY public.missione
    ADD CONSTRAINT missione_pkey PRIMARY KEY (id);


--
-- Name: paese paese_pkey; Type: CONSTRAINT; Schema: public; Owner: avnadmin
--

ALTER TABLE ONLY public.paese
    ADD CONSTRAINT paese_pkey PRIMARY KEY (id);


--
-- Name: password_reset_token password_reset_token_pkey; Type: CONSTRAINT; Schema: public; Owner: avnadmin
--

ALTER TABLE ONLY public.password_reset_token
    ADD CONSTRAINT password_reset_token_pkey PRIMARY KEY (id);


--
-- Name: recensione recensione_pkey; Type: CONSTRAINT; Schema: public; Owner: avnadmin
--

ALTER TABLE ONLY public.recensione
    ADD CONSTRAINT recensione_pkey PRIMARY KEY (id);


--
-- Name: ruolo ruolo_pkey; Type: CONSTRAINT; Schema: public; Owner: avnadmin
--

ALTER TABLE ONLY public.ruolo
    ADD CONSTRAINT ruolo_pkey PRIMARY KEY (id);


--
-- Name: password_reset_token uk4h1cy45dlp7ghqc1cg7g65q62; Type: CONSTRAINT; Schema: public; Owner: avnadmin
--

ALTER TABLE ONLY public.password_reset_token
    ADD CONSTRAINT uk4h1cy45dlp7ghqc1cg7g65q62 UNIQUE (utente_id);


--
-- Name: paese ukgqbrf50n96tj752v1nulux4pa; Type: CONSTRAINT; Schema: public; Owner: avnadmin
--

ALTER TABLE ONLY public.paese
    ADD CONSTRAINT ukgqbrf50n96tj752v1nulux4pa UNIQUE (nome);


--
-- Name: verification_token uktr1t4jptl6t2demc9vj1bmuor; Type: CONSTRAINT; Schema: public; Owner: avnadmin
--

ALTER TABLE ONLY public.verification_token
    ADD CONSTRAINT uktr1t4jptl6t2demc9vj1bmuor UNIQUE (utente_id);


--
-- Name: utente utente_email_key; Type: CONSTRAINT; Schema: public; Owner: avnadmin
--

ALTER TABLE ONLY public.utente
    ADD CONSTRAINT utente_email_key UNIQUE (email);


--
-- Name: utente utente_pkey; Type: CONSTRAINT; Schema: public; Owner: avnadmin
--

ALTER TABLE ONLY public.utente
    ADD CONSTRAINT utente_pkey PRIMARY KEY (id);


--
-- Name: verification_token verification_token_pkey; Type: CONSTRAINT; Schema: public; Owner: avnadmin
--

ALTER TABLE ONLY public.verification_token
    ADD CONSTRAINT verification_token_pkey PRIMARY KEY (id);


--
-- Name: candidatura fk10ta5do2ypgix6pjgpip7i63x; Type: FK CONSTRAINT; Schema: public; Owner: avnadmin
--

ALTER TABLE ONLY public.candidatura
    ADD CONSTRAINT fk10ta5do2ypgix6pjgpip7i63x FOREIGN KEY (candidato_id) REFERENCES public.utente (id);


--
-- Name: password_reset_token fk5qa6bkql47fb5egc4qc8pw4p0; Type: FK CONSTRAINT; Schema: public; Owner: avnadmin
--

ALTER TABLE ONLY public.password_reset_token
    ADD CONSTRAINT fk5qa6bkql47fb5egc4qc8pw4p0 FOREIGN KEY (utente_id) REFERENCES public.utente (id);


--
-- Name: missione fk9gm0qevu3cy2j2hjokaebtptf; Type: FK CONSTRAINT; Schema: public; Owner: avnadmin
--

ALTER TABLE ONLY public.missione
    ADD CONSTRAINT fk9gm0qevu3cy2j2hjokaebtptf FOREIGN KEY (creatore_id) REFERENCES public.utente (id);


--
-- Name: ruoli_utenti fkhknrwxc2lv744hohxnx91f890; Type: FK CONSTRAINT; Schema: public; Owner: avnadmin
--

ALTER TABLE ONLY public.ruoli_utenti
    ADD CONSTRAINT fkhknrwxc2lv744hohxnx91f890 FOREIGN KEY (ruolo_id) REFERENCES public.ruolo (id) ON DELETE CASCADE;


--
-- Name: ruoli_utenti fkiwmbppl6sfk6qxq5vydx3ixm6; Type: FK CONSTRAINT; Schema: public; Owner: avnadmin
--

ALTER TABLE ONLY public.ruoli_utenti
    ADD CONSTRAINT fkiwmbppl6sfk6qxq5vydx3ixm6 FOREIGN KEY (utente_id) REFERENCES public.utente (id) ON DELETE CASCADE;


--
-- Name: recensione fkli7as4tg5jh41cgf66jgquwy8; Type: FK CONSTRAINT; Schema: public; Owner: avnadmin
--

ALTER TABLE ONLY public.recensione
    ADD CONSTRAINT fkli7as4tg5jh41cgf66jgquwy8 FOREIGN KEY (destinatario_id) REFERENCES public.utente (id);


--
-- Name: missione fklnuyyawt4hh8lh5ny2gjrqoi8; Type: FK CONSTRAINT; Schema: public; Owner: avnadmin
--

ALTER TABLE ONLY public.missione
    ADD CONSTRAINT fklnuyyawt4hh8lh5ny2gjrqoi8 FOREIGN KEY (paese_id) REFERENCES public.paese (id);


--
-- Name: verification_token fklsx3kmjulu5c7lcbthlc2s12p; Type: FK CONSTRAINT; Schema: public; Owner: avnadmin
--

ALTER TABLE ONLY public.verification_token
    ADD CONSTRAINT fklsx3kmjulu5c7lcbthlc2s12p FOREIGN KEY (utente_id) REFERENCES public.utente (id) ON DELETE CASCADE;


--
-- Name: utente fkm7huen18kye93o92sq0hm4lxj; Type: FK CONSTRAINT; Schema: public; Owner: avnadmin
--

ALTER TABLE ONLY public.utente
    ADD CONSTRAINT fkm7huen18kye93o92sq0hm4lxj FOREIGN KEY (nazionalita_id) REFERENCES public.paese (id);


--
-- Name: candidatura fktm5cmk3falt1fl7a377yf2pff; Type: FK CONSTRAINT; Schema: public; Owner: avnadmin
--

ALTER TABLE ONLY public.candidatura
    ADD CONSTRAINT fktm5cmk3falt1fl7a377yf2pff FOREIGN KEY (missione_id) REFERENCES public.missione (id);


--
-- Name: recensione fktrvpb3er67txo72psquoy7dt8; Type: FK CONSTRAINT; Schema: public; Owner: avnadmin
--

ALTER TABLE ONLY public.recensione
    ADD CONSTRAINT fktrvpb3er67txo72psquoy7dt8 FOREIGN KEY (autore_id) REFERENCES public.utente (id);


--
-- PostgreSQL database dump complete
--


