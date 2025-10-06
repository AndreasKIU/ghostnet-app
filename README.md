# GhostNet — Prototyp (IPWA02-01)

Web-App zum **Melden** und **Bergen** von Geisternetzen (Fallstudie „Ghost Net Fishing“).  
Umgesetzt mit **Spring Boot**, **Spring Data JPA (Hibernate)**, **H2**, **Thymeleaf**, **Bootstrap**.

---

## Ziele & Stories (MoSCoW)

**MUST**
1. Geisternetz melden (auch anonym)
2. Bergung übernehmen
3. Offene/geplante Netze sehen (Filter)
4. Als geborgen melden

**COULD**
- „Verschollen melden“ + „Als wiedergefunden markieren“ (strenge Logik)
- „Wer bergt was?“ – Übersicht der geplanten Bergungen je Person

---

## Tech-Stack

- Java 17, Maven  
- Spring Boot 3 (Web, Validation), Spring Data JPA (Hibernate)  
- H2 (persistente Datei-DB), Thymeleaf, Bootstrap 5

---

## Schnellstart

```bash
mvn spring-boot:run
# App: http://localhost:8080/
# H2-Konsole: http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:file:./data/ghostnetdb  | user: sa | pass: (leer)
