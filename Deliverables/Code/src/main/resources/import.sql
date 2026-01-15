--
-- PostgreSQL database dump
--

\restrict 3qtGzdbDiPLVRn7MWDcfOJitv8XRTa3JvqZRZyDm6SRa7XybCkH76WAGfFe4lwK

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

--
-- Name: candidatura; Type: TABLE; Schema: public; Owner: avnadmin
--

CREATE TABLE public.candidatura (
    stato character varying(255),
    candidato_id bigint NOT NULL,
    data_candidatura date,
    id bigint NOT NULL,
    missione_id bigint NOT NULL
);


ALTER TABLE public.candidatura OWNER TO avnadmin;

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

CREATE TABLE public.missione (
    internal_stato smallint NOT NULL,
    creatore_id bigint NOT NULL,
    data_fine date NOT NULL,
    data_inizio date NOT NULL,
    id bigint NOT NULL,
    citta character varying(255) NOT NULL,
    competenze_richieste text NOT NULL,
    descrizione text NOT NULL,
    immagine character varying(255) NOT NULL,
    nome character varying(255) NOT NULL,
    requisiti_extra text,
    paese_id integer NOT NULL,
    CONSTRAINT missione_stato_check CHECK (((internal_stato >= 0) AND (internal_stato <= 3)))
);


ALTER TABLE public.missione OWNER TO avnadmin;

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

CREATE TABLE public.paese (
    id integer NOT NULL,
    nome character varying(255) NOT NULL
);


ALTER TABLE public.paese OWNER TO avnadmin;

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

CREATE TABLE public.password_reset_token (
    id bigint NOT NULL,
    token character varying(255),
    utente_id bigint NOT NULL,
    expiry_date timestamp(6) without time zone
);


ALTER TABLE public.password_reset_token OWNER TO avnadmin;

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

CREATE TABLE public.recensione (
    voto integer NOT NULL,
    autore_id bigint NOT NULL,
    data_recensione date NOT NULL,
    destinatario_id bigint NOT NULL,
    id bigint NOT NULL,
    testo_recensione character varying(255)
);


ALTER TABLE public.recensione OWNER TO avnadmin;

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

CREATE TABLE public.ruoli_utenti (
    ruolo_id bigint NOT NULL,
    utente_id bigint NOT NULL
);


ALTER TABLE public.ruoli_utenti OWNER TO avnadmin;

--
-- Name: ruolo; Type: TABLE; Schema: public; Owner: avnadmin
--

CREATE TABLE public.ruolo (
    id bigint NOT NULL,
    nome character varying(255) NOT NULL
);


ALTER TABLE public.ruolo OWNER TO avnadmin;

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

CREATE TABLE public.utente (
    pending boolean NOT NULL,
    sesso character(1) NOT NULL,
    data_emissione_passaporto date,
    data_nascita date NOT NULL,
    data_scadenza_passaporto date,
    id bigint NOT NULL,
    temp_pwd_scadenza timestamp(6) without time zone,
    dtype character varying(31) NOT NULL,
    bio character varying(255),
    cognome character varying(255) NOT NULL,
    email character varying(255) NOT NULL,
    foto_profilo character varying(255),
    nome character varying(255) NOT NULL,
    numero_passaporto character varying(255),
    password character varying(255) NOT NULL,
    path_passaporto character varying(255),
    temp_password character varying(255),
    nazionalita_id integer,
    CONSTRAINT utente_dtype_check CHECK (((dtype)::text = ANY ((ARRAY['Utente'::character varying, 'Volontario'::character varying])::text[])))
);


ALTER TABLE public.utente OWNER TO avnadmin;

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

CREATE TABLE public.verification_token (
    id bigint NOT NULL,
    token character varying(255),
    utente_id bigint,
    expiry_date timestamp(6) without time zone
);


ALTER TABLE public.verification_token OWNER TO avnadmin;

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

COPY public.candidatura (stato, candidato_id, data_candidatura, id, missione_id) FROM stdin;
IN_CORSO	152	2026-01-15	252	202
\.


--
-- Data for Name: missione; Type: TABLE DATA; Schema: public; Owner: avnadmin
--

COPY public.missione (internal_stato, creatore_id, data_fine, data_inizio, id, citta, competenze_richieste, descrizione, immagine, nome, requisiti_extra, paese_id) FROM stdin;
2	452	2026-06-30	2026-02-01	352	Loksa linn	Fluent English; between 18 and 80 years old; welcomes solo volunteers, couples, and partners of volunteers	\r\n\r\nHey there! 😊 We're an expat couple who chose to live close to nature, grow a garden, and raise a child. It's a beautiful adventure, but sometimes it can get a bit overwhelming! So, we're looking for a little extra help with a bit of everything. If you enjoy a mix of tasks and good company, we'd love to hear from you! 😉🌿🌸	e6378923-5bb2-4a6d-a0ee-1660647abe15-Immagine_WhatsApp_2025-03-23_ore_12.12.51_bf1a518b.jpg	Experience Family Life in Estonia: Garden & House Help Needed! ✨		73
2	552	2026-07-23	2026-06-23	253	Taipei	善意。\r\n强大的力量。\r\n胆识。	大规模攻占台北，收复我们的伟大土地，统一我们的国家。	b443f62f-2216-4d7e-8e69-49f726416779-0227-0524_taipei-xlarge.jpg	我们去夺回台北吧	不用担心	220
2	502	2026-10-22	2026-09-28	302	Bordeaux	Buona forza fisica, dinamismo e manualità di base.	Missione Bordeaux: Nel Cuore della Vendemmia\r\nPartecipa a un’esperienza autentica tra i filari dei vigneti più famosi al mondo. Cerchiamo persone volenterose per la raccolta dell'uva a Bordeaux, dove la tradizione incontra la passione.\r\n\r\nPerché unirti a noi?\r\n\r\nVivi il Territorio: Immergiti nei paesaggi mozzafiato dei castelli bordolesi.\r\n\r\nSpirito di Squadra: Condividi la fatica e il sorriso con un team internazionale.\r\n\r\nSaper Fare Francese: Scopri i segreti della vinificazione direttamente alla fonte.\r\n\r\nRicompensa: Vitto, alloggio in stile rustico e la soddisfazione di contribuire a un’annata d'eccellenza.	05e81d68-bd38-459c-bd10-fdcd7d7183c3-vite.jpeg	Aiuta nella raccolta di vino in Francia a Bordeaux		2
2	552	2027-01-10	2026-12-22	252	Beijing	Italiano – madrelingua\r\nInglese – livello avanzato (C1)\r\nCinese mandarino – livello intermedio (B1/B2), con esperienza di comunicazione quotidiana e scolastica	Svolgimento di attività di supporto all’insegnamento presso una scuola internazionale di Pechino, collaborando con docenti madrelingua e staff educativo. Il ruolo prevedeva il sostegno quotidiano alle lezioni, la preparazione di materiali didattici e il supporto personalizzato agli studenti, con particolare attenzione allo sviluppo delle competenze linguistiche in inglese e italiano.	84707af4-a588-4b62-8510-3758d7b19459-nbts-viaggi-cina-pechino-citta-proibita.jpg	Help teaching a Pechino 	Buona volontà :)	49
2	502	2026-02-16	2026-01-16	303	Napoli	Padronanza Tecnologica: Conoscenza base delle principali app e funzioni dello smartphone.\r\n\r\nEmpatia e Ascolto: Capacità di trasmettere concetti complessi con un linguaggio semplice e calmo.\r\n\r\nProblem Solving: Risoluzione di piccoli intoppi tecnici comuni (configurazioni, recupero password).	Aiuta gli anziani del quartiere a superare le barriere tecnologiche. La missione consiste nel guidare i partecipanti nell'uso dello smartphone e dei servizi digitali essenziali (SPID, email, videochiamate), trasformando la tecnologia in uno strumento di compagnia e indipendenza.	dd1e0624-5bbc-4aa8-91d6-535765750918-istockphoto-1317496472-612x612.jpg	Connessioni Digitali: Tutor per l'Inclusione	Conoscenza delle procedure burocratiche online (portali sanitari o comunali).\r\n\r\nDisponibilità a domicilio per chi ha difficoltà motorie.\r\n\r\nEsperienza pregressa nell'insegnamento o nella formazione.	1
0	452	2026-09-30	2026-06-01	402	Kolkata	Accoglie volontari singoli, coppie e partner di volontari	I volontari supporteranno la fase di pianificazione di un'azienda agricola in permacultura di 2-3 acri, sviluppando un dettagliato foglio Excel che elenchi tutti gli elementi e le attrezzature necessari per l'avvio dell'azienda agricola, tra cui preparazione del terreno, sistemi idrici, rigenerazione del suolo, energia, alloggi, attrezzi e manutenzione. Per ogni elemento, i volontari condurranno un'analisi costi-benefici, confrontando le alternative in base a costi, durabilità, impatto ambientale, disponibilità locale e sostenibilità a lungo termine.	fd21fa75-b1aa-49b4-bc1e-7716a70f5bbb-122942329_3711764192168227_7945410133559973583_n.jpg	Programma di volontariato per la pianificazione agricola e la mappatura delle risorse in permacultura		105
1	552	3033-12-18	3033-12-17	452	Gerusalemme	没有任何	我们很清楚他们在做什么，该死的。我们会来接你。	b30521ad-732d-4da5-ae14-b07eec7b5067-images.jpg	我们攻击非国家		110
1	202	2026-01-08	2026-01-21	153	Zurigo	Fl Studio 25 e Serum	Leggi il titolo e agisci di consequenza	1380bf01-f437-4613-87c8-2f7ca121cc03-Screenshot 2024-10-17 003639.png	Aiuta Papa V a fare un pezzo decente		3
0	452	2027-01-12	2026-01-12	403	Jinja	Welcomes solo volunteers, couples, and partners of volunteers	Supporting social media, volunteers at HGADC Organization help to market and promote foundation activities 	93ebf570-c355-4dc8-8981-78ff16ffef5f-1000179712.jpg	Help Us with Our social media and Market our foundation		236
2	202	2026-01-05	2026-01-22	152	Fisciano	Reti più di 25	Siamo dei poveri cristiani di resto 0 x favore aiutaci	1b16a3ba-191b-4abf-9c58-c9ad3b2d3d02-Screenshot 2025-03-30 011505.png	Offri ripetizioni di reti ad un povero cristiano		1
2	202	2026-03-06	2026-01-01	202	Bruxelles	Nessuna	Il mare è pieno di plastica, aiutaci a pulirlo!	76b735b2-fd75-45d4-8e47-eb3bc5419f2c-tartarughineLast.jpg	Aiuta a pulire il mare per salvare le tartarughe marine	Nessuno	4
0	602	2026-02-22	2026-02-22	453	Salerno	Programmazione in almeno uno dei principali linguaggi: Python, Java, JavaScript, C++ o simili\r\nSviluppo web (HTML, CSS, JavaScript, framework come React, Angular o Vue)\r\nSviluppo backend con framework come Spring, Django, Node.js\r\nDatabase: conoscenza di SQL (MySQL, PostgreSQL) e NoSQL (MongoDB)\r\nVersionamento del codice con Git e GitHub/GitLab\r\nPrincipi di ingegneria del software: design patterns, architetture, testing\r\nMetodologie Agile/Scrum per la gestione dei progetti\r\nFondamenti di sistemi operativi e reti\r\nConoscenza delle API REST e dei microservizi\r\nCapacità di debugging e ottimizzazione del codice	Sviluppo di un percorso formativo e pratico orientato alla professione di ingegnere del software, svolto nell’area di Salerno. L’attività comprende lo studio strutturato di linguaggi di programmazione, metodologie di sviluppo moderne e strumenti utilizzati nell’industria, con l’obiettivo di acquisire competenze tecniche solide e capacità di progettazione software.\r\nStudio approfondito di linguaggi come Python, Java, JavaScript e dei relativi framework.\r\nProgettazione e sviluppo di applicazioni web e software seguendo principi di ingegneria del software.\r\nUtilizzo di strumenti professionali: Git/GitHub, ambienti di sviluppo, sistemi di versionamento e testing.\r\nIntroduzione alle metodologie Agile/Scrum per la gestione dei progetti.\r\nRealizzazione di progetti individuali e di gruppo per simulare contesti lavorativi reali.\r\nApprofondimento di basi di dati relazionali e non relazionali (SQL, NoSQL).\r\nPartecipazione a workshop, eventi tech e iniziative locali nell’ecosistema digitale salernitano.	cc073734-2ae3-4063-9f5b-25bfab00ee45-sesa.png	Diventa un Software Engineer!	Familiarità con Docker e concetti di containerizzazione\r\nConoscenza base di cloud computing (AWS, Azure, Google Cloud)\r\nEsperienza con test automatizzati e CI/CD\r\nPartecipazione a progetti personali, hackathon o community tech locali	1
2	2	2026-07-31	2026-04-17	502	Upper Moutere	- Livello di inglese intermedio; \r\n- Più di 20 anni.	Unisciti a noi nella nostra splendida proprietà per aiutarti con: rimozione delle erbacce dagli alberi autoctoni, manutenzione della proprietà, ritocchi alla vernice, pulizie delle stanze… Avrai la tua deliziosa roulotte e cucina/bagno con viste mozzafiato.	f39310ac-9dd7-4f1a-8a36-feee909e9cfa-nuovazelanda.jpeg	Vieni ad aiutarci con il giardinaggio e la pittura nel nostro agriturismo!	Mezzo di trasporto privato: l'area è poco collegata a supermercati e altri negozi.	159
\.


--
-- Data for Name: paese; Type: TABLE DATA; Schema: public; Owner: avnadmin
--

COPY public.paese (id, nome) FROM stdin;
1	Italia
2	Francia
3	Svizzera
4	Belgio
5	Afghanistan
6	Isole Aland
7	Albania
8	Algeria
9	Samoa Americane
10	Andorra
11	Angola
12	Anguilla
13	Antartide
14	Antigua e Barbuda
15	Argentina
16	Armenia
17	Aruba
18	Australia
19	Austria
20	Azerbaijan
21	Bahrein
22	Bangladesh
23	Barbados
24	Bielorussia
26	Belize
27	Benin
28	Bermuda
29	Bhutan
30	Bolivia
31	Bonaire, Saint-Eustache e Saba
32	Bosnia ed Erzegovina
33	Botswana
34	Isola Bouvet
35	Brasile
36	Territorio britannico dell'oceano indiano
37	Brunei
38	Bulgaria
39	Burkina Faso
40	Burundi
41	Cambogia
42	Camerun
43	Canada
44	Capo Verde
45	Isole Cayman
46	Repubblica Centrafricana
47	Ciad
48	Cile
49	Cina
50	Isola di Natale
51	Isole Cocos e Keeling
52	Colombia
53	Comore
54	Congo
55	Isole Cook
56	Costa Rica
57	Costa D'Avorio
58	Croazia
59	Cuba
60	Curaçao
61	Cipro
62	Repubblica Ceca
63	Congo (Rep. Dem.)
64	Danimarca
65	Gibuti
66	Dominica
67	Repubblica Dominicana
68	Ecuador
69	Egitto
70	El Salvador
71	Guinea Equatoriale
72	Eritrea
73	Estonia
74	Swaziland
75	Etiopia
76	Isole Falkland o Isole Malvine
77	Isole Far Oer
78	Figi
79	Finlandia
81	Guyana francese
82	Polinesia Francese
83	Territori Francesi del Sud
84	Gabon
85	Georgia
86	Germania
87	Ghana
88	Gibilterra
89	Grecia
90	Groenlandia
91	Grenada
92	Guadeloupa
93	Guam
94	Guatemala
95	Guernsey
96	Guinea
97	Guinea-Bissau
98	Guyana
99	Haiti
100	Isole Heard e McDonald
101	Honduras
102	Hong Kong
103	Ungheria
104	Islanda
105	India
106	Indonesia
107	Iran
108	Iraq
109	Irlanda
110	Israele
112	Giamaica
113	Giappone
114	Isola di Jersey
115	Giordania
116	Kazakistan
117	Kenya
118	Kiribati
119	Kosovo
120	Kuwait
121	Kirghizistan
122	Laos
123	Lettonia
124	Libano
125	Lesotho
126	Liberia
127	Libia
128	Liechtenstein
129	Lituania
130	Lussemburgo
131	Macao
132	Madagascar
133	Malawi
134	Malesia
135	Maldive
136	Mali
137	Malta
138	Isola di Man
139	Isole Marshall
140	Martinica
141	Mauritania
142	Mauritius
143	Mayotte
144	Messico
145	Micronesia
146	Moldavia
147	Principato di Monaco
148	Mongolia
149	Montenegro
150	Montserrat
151	Morocco
152	Mozambico
153	Birmania
154	Namibia
155	Nauru
156	Nepal
157	Paesi Bassi
158	Nuova Caledonia
159	Nuova Zelanda
160	Nicaragua
161	Niger
162	Nigeria
163	Niue
164	Isola Norfolk
165	Corea del Nord
166	Macedonia del Nord
167	Isole Marianne Settentrionali
168	Norvegia
169	Oman
170	Pakistan
171	Palau
172	Palestina
173	Panama
174	Papua Nuova Guinea
175	Paraguay
176	Perù
177	Filippine
178	Isole Pitcairn
179	Polonia
180	Portogallo
181	Puerto Rico
182	Qatar
183	Riunione
184	Romania
185	Russia
186	Ruanda
187	Sant'Elena
188	Saint Kitts e Nevis
189	Santa Lucia
190	Saint-Pierre e Miquelon
191	Saint Vincent e Grenadine
192	Antille Francesi
193	Saint Martin
194	Samoa
195	San Marino
196	São Tomé e Príncipe
197	Arabia Saudita
198	Senegal
199	Serbia
200	Seychelles
201	Sierra Leone
202	Singapore
203	Saint Martin (parte olandese)
204	Slovacchia
205	Slovenia
206	Isole Salomone
207	Somalia
208	Sud Africa
209	Georgia del Sud e Isole Sandwich Meridionali
210	Corea del Sud
211	Sudan del sud
212	Spagna
213	Sri Lanka
214	Sudan
215	Suriname
216	Svalbard e Jan Mayen
217	Svezia
219	Siria
220	Taiwan
221	Tagikistan
222	Tanzania
223	Tailandia
224	Bahamas
225	Gambia
226	Timor Est
227	Togo
228	Isole Tokelau
229	Tonga
230	Trinidad e Tobago
231	Tunisia
232	Turchia
233	Turkmenistan
234	Isole Turks e Caicos
235	Tuvalu
236	Uganda
237	Ucraina
238	Emirati Arabi Uniti
239	Regno Unito
240	Stati Uniti D'America
241	Isole minori esterne degli Stati Uniti d'America
242	Uruguay
243	Uzbekistan
244	Vanuatu
245	Santa Sede
246	Venezuela
247	Vietnam
248	Isole Vergini Britanniche
249	Isole Vergini americane
250	Wallis e Futuna
251	Sahara Occidentale
252	Yemen
253	Zambia
254	Zimbabwe
\.


--
-- Data for Name: password_reset_token; Type: TABLE DATA; Schema: public; Owner: avnadmin
--

COPY public.password_reset_token (id, token, utente_id, expiry_date) FROM stdin;
52	c5ae5c9b-052e-4da5-9921-4032fa18ad9a	802	2026-01-13 18:46:48.485093
\.


--
-- Data for Name: recensione; Type: TABLE DATA; Schema: public; Owner: avnadmin
--

COPY public.recensione (voto, autore_id, data_recensione, destinatario_id, id, testo_recensione) FROM stdin;
\.


--
-- Data for Name: ruoli_utenti; Type: TABLE DATA; Schema: public; Owner: avnadmin
--

COPY public.ruoli_utenti (ruolo_id, utente_id) FROM stdin;
1	1
2	2
3	3
4	4
1	152
2	202
1	252
1	352
2	353
2	402
2	452
2	502
2	552
2	602
2	702
1	752
1	802
1	803
1	954
\.


--
-- Data for Name: ruolo; Type: TABLE DATA; Schema: public; Owner: avnadmin
--

COPY public.ruolo (id, nome) FROM stdin;
1	ROLE_VOLUNTEER
2	ROLE_ORGANIZER
3	ROLE_MODERATOR
4	ROLE_ACCOUNT_MANAGER
\.


--
-- Data for Name: utente; Type: TABLE DATA; Schema: public; Owner: avnadmin
--

COPY public.utente (pending, sesso, data_emissione_passaporto, data_nascita, data_scadenza_passaporto, id, temp_pwd_scadenza, dtype, bio, cognome, email, foto_profilo, nome, numero_passaporto, password, path_passaporto, temp_password, nazionalita_id) FROM stdin;
f	M	2004-04-01	2026-01-06	2027-04-01	1	\N	Volontario	\N	test	volunteer@earthlocals.com	\N	test	AAA	$argon2id$v=19$m=16384,t=2,p=1$E/NCSlGFntqHasNftBFEuA$3dDcSF67KBuuncTrUZ70jX9OavG32gIBjlCcbPlUhqk	a240fe66-f323-4c34-a5d8-527c8d6b732f-basic-text.pdf	\N	1
f	M	2004-03-23	2004-03-23	3223-03-23	352	\N	Volontario	\N	Niemiec	francesconiemiec23@gmail.com	\N	Francesco	FN2323232	$argon2id$v=19$m=16384,t=2,p=1$7f64SB2ngs0ox4hmE77+Eg$E4gjipQhkIOK/EGBR2oIonl6RmrbsPPa3qYeUxJ8I20	52ab3eac-66f2-4ee7-815b-c68e820f0927-03.Introduzione ad UML.pdf	\N	1
f	M	\N	2004-01-27	\N	353	\N	Utente	\N	Pha Lo Mbhs	ghastman999@gmail.com	\N	Fhan Bi Yoh	\N	$argon2id$v=19$m=16384,t=2,p=1$+CSIHoovZECv1k8M1rlz9w$LjscYiCGjChF1KJCuzoLexcbDa7jQhoEt8D7JivMUyA	\N	\N	1
f	M	\N	2004-04-01	\N	202	\N	Utente	\N	Rossi	mario.rossi@example.com	\N	Bruno	\N	$argon2id$v=19$m=16384,t=2,p=1$3+NjYX5/DX7mS4vcl4em9w$Vx2Kd487ZxTN6+eI0MPVuoIxf1s2Fa9RdV3uxftzkGI	\N	\N	1
f	M	\N	2005-12-23	\N	402	\N	Utente	\N	test	pixetid138@gopicta.com	\N	Test	\N	$argon2id$v=19$m=16384,t=2,p=1$CLATRTFBuuyP2MMvfoZjvA$UzTH5NtpBWxs1VWZSroCidAc6RfOXU+ETt6RS0YhvE4	\N	\N	1
f	M	\N	2004-04-01	\N	452	\N	Utente	\N	Squitieri	andeser@protonmail.com	\N	Andrea	\N	$argon2id$v=19$m=16384,t=2,p=1$T0kmSL0UNfzDJ3I2fKxXew$LGVrHROmIz9DbKoz951GTnGyiS422SLksOj9HjBizQk	\N	\N	1
f	M	\N	2003-06-09	\N	502	\N	Utente	\N	Abbatiello	onlyforadobe69@gmail.com	\N	Simone	\N	$argon2id$v=19$m=16384,t=2,p=1$X/v/azTNH9Mj887Plr5PIw$zYt6PgyreYleCK8Kazlx+gincH9I3tsQGIrHFsDw+54	\N	\N	1
f	M	\N	1995-02-19	\N	552	\N	Utente	\N	De Rox	maoderox@test.com	\N	Mao	\N	$argon2id$v=19$m=16384,t=2,p=1$pdgFY2fdqKXu5wWmY+TfGA$WBfInw4i/o0vmIj+LLaMPdbmnVN53aAViKcSEcZnn+o	\N	\N	49
f	M	\N	2026-01-04	\N	3	\N	Utente	\N	test	moderator@earthlocals.com	\N	test	\N	$argon2id$v=19$m=16384,t=2,p=1$dWMlIfMZv7OJCpJIKyzhfg$08m1tcYgaMWqZfTii5i84NTvM7U3H3v41MZ7X0wOynU	\N	\N	1
f	M	\N	2026-01-04	\N	2	\N	Utente	\N	test	organizer@earthlocals.com	\N	test	\N	$argon2id$v=19$m=16384,t=2,p=1$OTGnCC3SRJdVdu2QyEw1ww$gKo/hRge6qOI/fdOaEgM2BiB6pdSYsigStL/jEHpJAw	\N	\N	1
f	M	\N	2026-01-04	\N	4	\N	Utente	\N	test	accountmanager@earthlocals.com	\N	test	\N	$argon2id$v=19$m=16384,t=2,p=1$5xUDl9mQkptkimRTIfHWmQ$Jxz1oQnv1y76taPC663lmgqoySuFXo5PU/xKS9y1llk	\N	\N	1
f	M	\N	0001-01-01	\N	602	\N	Utente	\N	Lab	sesalab@earthlocalstest.com	\N	Sesa	\N	$argon2id$v=19$m=16384,t=2,p=1$tiH4cPJmu+bQ6RZk0WHemg$jIk6H1I+n4m72fOVy1861I6n3StFRS6GXUnnVaKNZaA	\N	\N	1
f	F	2004-04-01	2004-04-01	2027-04-01	152	\N	Volontario	\N	Squitieri	andrea.squitieri@disroot.org	\N	Andrea	AAA	$argon2id$v=19$m=16384,t=2,p=1$VE79SUVDbDsX/V9ZEj2BMQ$PsmDa2zBu/VaWEWxruRRSc3ZHr+TRhr2CtoP0F7reuM	fdd12856-0919-4ccc-a6cd-00fd8b130fa9-basic-text.pdf	\N	3
t	M	2000-04-21	2003-02-23	2040-04-29	954	\N	Volontario	\N	Rossi	frankrossi@mail.com	\N	Francesco	AC000000	$argon2id$v=19$m=16384,t=2,p=1$Pd7z+yjbkYqMZuS/8GqjFw$TFThVHCV4WA67/cZbcJVqJRMTcdQ/7BDLWjLP9xfBhg	c6bc7134-fb55-403d-a10b-cc3e5774e289-Checklist.pdf	\N	14
f	M	\N	2004-04-01	\N	702	\N	Utente	\N	Squitieri	andeser44@gmail.com	\N	Andrea	\N	$argon2id$v=19$m=16384,t=2,p=1$rH3G+SJXF5JqpUjjd9D+wA$ClsD/rZJLlU+ffJLhpigD5CCggckAVX/GcKDhW1PAVA	\N	\N	1
f	M	2004-04-01	2004-04-01	2028-04-01	752	\N	Volontario	\N	Squitieri	meros17640@eubonus.com	\N	Andrea	AAA	$argon2id$v=19$m=16384,t=2,p=1$YvR34IHEYAW3nFCY7Ju+Xg$zOeCJj1r2StClVnQ6Of/wl6lGMGNV8cnYHkW7DWmrR4	aa7cba0b-888e-4bf3-b46f-6f3b55ba88a1-basic-text.pdf	\N	5
t	F	2021-01-12	1986-04-23	2032-03-18	802	\N	Volontario	\N	Niemiec 	asia.86jn@gmail.com	\N	Joanna Magdalena 	AA0000000	$argon2id$v=19$m=16384,t=2,p=1$5a5G5XY2kxpFvtERKj6TbA$XUcxgv4IaRRN5cCBqqjPxwuJJ0gpuQ5EA2txp03MRFk	56e1975f-2dec-4e52-b32b-a30f43740e31-Attestazione_Patrimoniale_PosteItaliane_NMCFNC04C23E791J_2026.pdf	\N	179
t	M	2004-04-01	2004-04-01	2027-04-01	803	\N	Volontario	\N	Eubonus	kaboy73656@feanzier.com	\N	Meros	AAA	$argon2id$v=19$m=16384,t=2,p=1$18jR9GFy67U41OjngpeAKg$5pLUhvI8YYa2DEpM3gOVOT+4Uh3A9uhopirH6OlaLfY	e1e881f9-62aa-439e-a8d1-8a1f440bb3a9-basic-text.pdf	\N	1
f	M	2025-12-24	2004-03-02	2222-01-20	252	\N	Volontario	\N	Abbatiello	s.abbatiello@studenti.unisa.it	\N	Simone	AA0000001	$argon2id$v=19$m=16384,t=2,p=1$kzlSzRA9viKzI2B2CKAvkw$vN0rMg+aGWzMtIQYoy16p6m2frEIiJNFINOt3MeByhs	a4022efb-bc7e-4686-81e2-a8612595a116-03.Maven.pdf	\N	1
\.


--
-- Data for Name: verification_token; Type: TABLE DATA; Schema: public; Owner: avnadmin
--

COPY public.verification_token (id, token, utente_id, expiry_date) FROM stdin;
152	54ad6c07-f2d1-4c27-8d4f-c4e0c0b7adf9	802	2026-01-13 18:43:50.57597
153	ccee272e-eb67-4b4a-a3c5-30226be6f4a2	803	2026-01-13 18:54:33.3793
303	edf7756d-8d05-4161-859a-6a04acddb1ee	954	2026-01-16 12:17:56.370133
\.


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

SELECT pg_catalog.lo_create('21616');
SELECT pg_catalog.lo_create('21617');
SELECT pg_catalog.lo_create('21618');
SELECT pg_catalog.lo_create('21637');
SELECT pg_catalog.lo_create('21638');
SELECT pg_catalog.lo_create('21639');
SELECT pg_catalog.lo_create('21692');
SELECT pg_catalog.lo_create('21693');
SELECT pg_catalog.lo_create('21694');
SELECT pg_catalog.lo_create('21700');
SELECT pg_catalog.lo_create('21701');
SELECT pg_catalog.lo_create('21702');
SELECT pg_catalog.lo_create('21703');
SELECT pg_catalog.lo_create('21704');
SELECT pg_catalog.lo_create('21705');
SELECT pg_catalog.lo_create('21710');
SELECT pg_catalog.lo_create('21711');
SELECT pg_catalog.lo_create('21712');

ALTER LARGE OBJECT 21616 OWNER TO avnadmin;
ALTER LARGE OBJECT 21617 OWNER TO avnadmin;
ALTER LARGE OBJECT 21618 OWNER TO avnadmin;
ALTER LARGE OBJECT 21637 OWNER TO avnadmin;
ALTER LARGE OBJECT 21638 OWNER TO avnadmin;
ALTER LARGE OBJECT 21639 OWNER TO avnadmin;
ALTER LARGE OBJECT 21692 OWNER TO avnadmin;
ALTER LARGE OBJECT 21693 OWNER TO avnadmin;
ALTER LARGE OBJECT 21694 OWNER TO avnadmin;
ALTER LARGE OBJECT 21700 OWNER TO avnadmin;
ALTER LARGE OBJECT 21701 OWNER TO avnadmin;
ALTER LARGE OBJECT 21702 OWNER TO avnadmin;
ALTER LARGE OBJECT 21703 OWNER TO avnadmin;
ALTER LARGE OBJECT 21704 OWNER TO avnadmin;
ALTER LARGE OBJECT 21705 OWNER TO avnadmin;
ALTER LARGE OBJECT 21710 OWNER TO avnadmin;
ALTER LARGE OBJECT 21711 OWNER TO avnadmin;
ALTER LARGE OBJECT 21712 OWNER TO avnadmin;

--
-- Data for Name: 21616..21712; Type: BLOBS; Schema: -; Owner: avnadmin
--

BEGIN;

SELECT pg_catalog.lo_open('21616', 131072);
SELECT pg_catalog.lowrite(0, '\x4c6f72656d20697073756d20646f6c6f722073697420616d65742c20636f6e73656374657475722061646970697363696e6720656c69742e204e616d206c61637573206c696265726f2c206d6178696d75732069642066657567696174206e65632c206d616c65737561646120706f7375657265206c65637475732e204d61757269732070756c76696e61722066696e69627573206d6f6c6c69732e2050726f696e2069642067726176696461206475692e20446f6e656320696e2076756c70757461746520656c69742e204475697320616363756d73616e206c656f206e6563206d6574757320696d706572646965742076756c7075746174652e2050686173656c6c757320696420736167697474697320646f6c6f722c206e6f6e20696d70657264696574206f64696f2e2053757370656e646973736520656765742073757363697069742061756775652e204675736365206174206e6973692061756775652e20496e74657264756d206574206d616c6573756164612066616d657320616320616e746520697073756d207072696d697320696e2066617563696275732e20536564207669746165206e697369206a7573746f2e204e616d207072657469756d206d6178696d7573206c65637475732c2076697461652068656e64726572697420746f72746f7220756c6c616d636f727065722061632e20');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('21617', 131072);
SELECT pg_catalog.lowrite(0, '\x4c6f72656d20697073756d20646f6c6f722073697420616d65742c20636f6e73656374657475722061646970697363696e6720656c69742e204e616d206c61637573206c696265726f2c206d6178696d75732069642066657567696174206e65632c206d616c65737561646120706f7375657265206c65637475732e204d61757269732070756c76696e61722066696e69627573206d6f6c6c69732e2050726f696e2069642067726176696461206475692e20446f6e656320696e2076756c70757461746520656c69742e204475697320616363756d73616e206c656f206e6563206d6574757320696d706572646965742076756c7075746174652e2050686173656c6c757320696420736167697474697320646f6c6f722c206e6f6e20696d70657264696574206f64696f2e2053757370656e646973736520656765742073757363697069742061756775652e204675736365206174206e6973692061756775652e20496e74657264756d206574206d616c6573756164612066616d657320616320616e746520697073756d207072696d697320696e2066617563696275732e20536564207669746165206e697369206a7573746f2e204e616d207072657469756d206d6178696d7573206c65637475732c2076697461652068656e64726572697420746f72746f7220756c6c616d636f727065722061632e200d0a');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('21618', 131072);
SELECT pg_catalog.lowrite(0, '\x4c6f72656d20697073756d20646f6c6f722073697420616d65742c20636f6e73656374657475722061646970697363696e6720656c69742e204e616d206c61637573206c696265726f2c206d6178696d75732069642066657567696174206e65632c206d616c65737561646120706f7375657265206c65637475732e204d61757269732070756c76696e61722066696e69627573206d6f6c6c69732e2050726f696e2069642067726176696461206475692e20446f6e656320696e2076756c70757461746520656c69742e204475697320616363756d73616e206c656f206e6563206d6574757320696d706572646965742076756c7075746174652e2050686173656c6c757320696420736167697474697320646f6c6f722c206e6f6e20696d70657264696574206f64696f2e2053757370656e646973736520656765742073757363697069742061756775652e204675736365206174206e6973692061756775652e20496e74657264756d206574206d616c6573756164612066616d657320616320616e746520697073756d207072696d697320696e2066617563696275732e20536564207669746165206e697369206a7573746f2e204e616d207072657469756d206d6178696d7573206c65637475732c2076697461652068656e64726572697420746f72746f7220756c6c616d636f727065722061632e20');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('21637', 131072);
SELECT pg_catalog.lowrite(0, '\x4c6f72656d20697073756d20646f6c6f722073697420616d65742c20636f6e73656374657475722061646970697363696e6720656c69742e204e616d206c61637573206c696265726f2c206d6178696d75732069642066657567696174206e65632c206d616c65737561646120706f7375657265206c65637475732e204d61757269732070756c76696e61722066696e69627573206d6f6c6c69732e2050726f696e2069642067726176696461206475692e20446f6e656320696e2076756c70757461746520656c69742e204475697320616363756d73616e206c656f206e6563206d6574757320696d706572646965742076756c7075746174652e2050686173656c6c757320696420736167697474697320646f6c6f722c206e6f6e20696d70657264696574206f64696f2e2053757370656e646973736520656765742073757363697069742061756775652e204675736365206174206e6973692061756775652e20496e74657264756d206574206d616c6573756164612066616d657320616320616e746520697073756d207072696d697320696e2066617563696275732e20536564207669746165206e697369206a7573746f2e204e616d207072657469756d206d6178696d7573206c65637475732c2076697461652068656e64726572697420746f72746f7220756c6c616d636f727065722061632e20');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('21638', 131072);
SELECT pg_catalog.lowrite(0, '\x4c6f72656d20697073756d20646f6c6f722073697420616d65742c20636f6e73656374657475722061646970697363696e6720656c69742e204e616d206c61637573206c696265726f2c206d6178696d75732069642066657567696174206e65632c206d616c65737561646120706f7375657265206c65637475732e204d61757269732070756c76696e61722066696e69627573206d6f6c6c69732e2050726f696e2069642067726176696461206475692e20446f6e656320696e2076756c70757461746520656c69742e204475697320616363756d73616e206c656f206e6563206d6574757320696d706572646965742076756c7075746174652e2050686173656c6c757320696420736167697474697320646f6c6f722c206e6f6e20696d70657264696574206f64696f2e2053757370656e646973736520656765742073757363697069742061756775652e204675736365206174206e6973692061756775652e20496e74657264756d206574206d616c6573756164612066616d657320616320616e746520697073756d207072696d697320696e2066617563696275732e20536564207669746165206e697369206a7573746f2e204e616d207072657469756d206d6178696d7573206c65637475732c2076697461652068656e64726572697420746f72746f7220756c6c616d636f727065722061632e20');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('21639', 131072);
SELECT pg_catalog.lowrite(0, '\x4c6f72656d20697073756d20646f6c6f722073697420616d65742c20636f6e73656374657475722061646970697363696e6720656c69742e204e616d206c61637573206c696265726f2c206d6178696d75732069642066657567696174206e65632c206d616c65737561646120706f7375657265206c65637475732e204d61757269732070756c76696e61722066696e69627573206d6f6c6c69732e2050726f696e2069642067726176696461206475692e20446f6e656320696e2076756c70757461746520656c69742e204475697320616363756d73616e206c656f206e6563206d6574757320696d706572646965742076756c7075746174652e2050686173656c6c757320696420736167697474697320646f6c6f722c206e6f6e20696d70657264696574206f64696f2e2053757370656e646973736520656765742073757363697069742061756775652e204675736365206174206e6973692061756775652e20496e74657264756d206574206d616c6573756164612066616d657320616320616e746520697073756d207072696d697320696e2066617563696275732e20536564207669746165206e697369206a7573746f2e204e616d207072657469756d206d6178696d7573206c65637475732c2076697461652068656e64726572697420746f72746f7220756c6c616d636f727065722061632e20');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('21692', 131072);
SELECT pg_catalog.lowrite(0, '\x4e657373756e61');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('21693', 131072);
SELECT pg_catalog.lowrite(0, '\x416975746120692062616d62696e692061206c65676765726520676c69206f726f6c6f6769');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('21694', 131072);
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('21700', 131072);
SELECT pg_catalog.lowrite(0, '\x52657469207069c3b9206469203235');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('21701', 131072);
SELECT pg_catalog.lowrite(0, '\x5369616d6f2064656920706f7665726920637269737469616e6920646920726573746f20302078206661766f72652061697574616369');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('21702', 131072);
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('21703', 131072);
SELECT pg_catalog.lowrite(0, '\x466c2053747564696f203235206520536572756d');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('21704', 131072);
SELECT pg_catalog.lowrite(0, '\x4c6567676920696c207469746f6c6f20652061676973636920646920636f6e73657175656e7a61');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('21705', 131072);
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('21710', 131072);
SELECT pg_catalog.lowrite(0, '\x4e657373756e61');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('21711', 131072);
SELECT pg_catalog.lowrite(0, '\x496c206d61726520c3a8207069656e6f20646920706c6173746963612c206169757461636920612070756c69726c6f21');
SELECT pg_catalog.lo_close(0);

SELECT pg_catalog.lo_open('21712', 131072);
SELECT pg_catalog.lowrite(0, '\x4e657373756e6f');
SELECT pg_catalog.lo_close(0);

COMMIT;

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
    ADD CONSTRAINT fk10ta5do2ypgix6pjgpip7i63x FOREIGN KEY (candidato_id) REFERENCES public.utente(id);


--
-- Name: password_reset_token fk5qa6bkql47fb5egc4qc8pw4p0; Type: FK CONSTRAINT; Schema: public; Owner: avnadmin
--

ALTER TABLE ONLY public.password_reset_token
    ADD CONSTRAINT fk5qa6bkql47fb5egc4qc8pw4p0 FOREIGN KEY (utente_id) REFERENCES public.utente(id);


--
-- Name: missione fk9gm0qevu3cy2j2hjokaebtptf; Type: FK CONSTRAINT; Schema: public; Owner: avnadmin
--

ALTER TABLE ONLY public.missione
    ADD CONSTRAINT fk9gm0qevu3cy2j2hjokaebtptf FOREIGN KEY (creatore_id) REFERENCES public.utente(id);


--
-- Name: ruoli_utenti fkhknrwxc2lv744hohxnx91f890; Type: FK CONSTRAINT; Schema: public; Owner: avnadmin
--

ALTER TABLE ONLY public.ruoli_utenti
    ADD CONSTRAINT fkhknrwxc2lv744hohxnx91f890 FOREIGN KEY (ruolo_id) REFERENCES public.ruolo(id) ON DELETE CASCADE;


--
-- Name: ruoli_utenti fkiwmbppl6sfk6qxq5vydx3ixm6; Type: FK CONSTRAINT; Schema: public; Owner: avnadmin
--

ALTER TABLE ONLY public.ruoli_utenti
    ADD CONSTRAINT fkiwmbppl6sfk6qxq5vydx3ixm6 FOREIGN KEY (utente_id) REFERENCES public.utente(id) ON DELETE CASCADE;


--
-- Name: recensione fkli7as4tg5jh41cgf66jgquwy8; Type: FK CONSTRAINT; Schema: public; Owner: avnadmin
--

ALTER TABLE ONLY public.recensione
    ADD CONSTRAINT fkli7as4tg5jh41cgf66jgquwy8 FOREIGN KEY (destinatario_id) REFERENCES public.utente(id);


--
-- Name: missione fklnuyyawt4hh8lh5ny2gjrqoi8; Type: FK CONSTRAINT; Schema: public; Owner: avnadmin
--

ALTER TABLE ONLY public.missione
    ADD CONSTRAINT fklnuyyawt4hh8lh5ny2gjrqoi8 FOREIGN KEY (paese_id) REFERENCES public.paese(id);


--
-- Name: verification_token fklsx3kmjulu5c7lcbthlc2s12p; Type: FK CONSTRAINT; Schema: public; Owner: avnadmin
--

ALTER TABLE ONLY public.verification_token
    ADD CONSTRAINT fklsx3kmjulu5c7lcbthlc2s12p FOREIGN KEY (utente_id) REFERENCES public.utente(id) ON DELETE CASCADE;


--
-- Name: utente fkm7huen18kye93o92sq0hm4lxj; Type: FK CONSTRAINT; Schema: public; Owner: avnadmin
--

ALTER TABLE ONLY public.utente
    ADD CONSTRAINT fkm7huen18kye93o92sq0hm4lxj FOREIGN KEY (nazionalita_id) REFERENCES public.paese(id);


--
-- Name: candidatura fktm5cmk3falt1fl7a377yf2pff; Type: FK CONSTRAINT; Schema: public; Owner: avnadmin
--

ALTER TABLE ONLY public.candidatura
    ADD CONSTRAINT fktm5cmk3falt1fl7a377yf2pff FOREIGN KEY (missione_id) REFERENCES public.missione(id);


--
-- Name: recensione fktrvpb3er67txo72psquoy7dt8; Type: FK CONSTRAINT; Schema: public; Owner: avnadmin
--

ALTER TABLE ONLY public.recensione
    ADD CONSTRAINT fktrvpb3er67txo72psquoy7dt8 FOREIGN KEY (autore_id) REFERENCES public.utente(id);


--
-- PostgreSQL database dump complete
--

\unrestrict 3qtGzdbDiPLVRn7MWDcfOJitv8XRTa3JvqZRZyDm6SRa7XybCkH76WAGfFe4lwK

