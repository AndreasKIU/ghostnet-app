# GhostNet (Aufgabe 3) – Spring Boot Starter

## Voraussetzungen
- **Java 17** (z. B. Temurin/OpenJDK)
- **Maven** (oder VS Code mit Java & Spring Boot Extensions)
- Optional: **VS Code** + "Extension Pack for Java" + "Lombok Annotations Support"

## Start
```bash
mvn spring-boot:run
# danach: http://localhost:8080/  (leitet auf /nets)
# Seiten:
# - /nets     -> Liste offener/geplanter Netze
# - /nets/new -> Netz melden
# H2-Konsole: /h2-console  (JDBC URL: jdbc:h2:mem:ghostnet)
```

## Was ist drin?
- Spring Boot (Web, Thymeleaf, Validation, Data JPA)
- H2 In-Memory DB (Dev)
- Seed-Daten (2 Recoverer, 1 Reporter, 2 Netze)
- Einfache MVC-Views mit Bootstrap (via WebJars)

## Nächste Schritte
- Tests erweitern
- (Optional) „Verschollen melden“ & Kartenansicht (Leaflet)
- Bericht gemäß Leitfaden erstellen (UML/ER/Screenshots)
