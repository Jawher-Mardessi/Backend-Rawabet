# Rawabet Backend

Backend Spring Boot de la plateforme **Rawabet**.

Le projet centralise plusieurs domaines fonctionnels:

- authentification JWT, passkeys WebAuthn et login facial
- gestion des utilisateurs, roles et permissions
- cinema: films, salles, seats, seances, reservations
- evenements, salles d'evenement et materiel
- club: adhesion, membres, evenements et participations
- carte de fidelite, abonnements et notifications
- chat temps reel via WebSocket
- integration avec des services ML externes

## Stack technique

- Java 17
- Spring Boot 3.3.5
- Spring Web
- Spring Data JPA
- Spring Security
- Spring Validation
- Spring Mail
- Spring WebSocket
- MySQL
- Maven

## Structure du projet

```text
src/main/java/org/example/rawabet
|- controllers/        API REST principale
|- security/           JWT, user details, filtres
|- config/             security, bootstrap, CORS, WebAuthn
|- services/           logique metier generale
|- repositories/       acces aux donnees
|- entities/           entites JPA
|- dto/                DTOs
|- chat/               module chat temps reel
|- cinema/             module cinema
|- club/               module club
```

## Prerequis

- JDK 17
- Maven 3.9+
- MySQL en local
- services externes optionnels selon les fonctionnalites utilisees:
  - API ML sur `http://localhost:8000`
  - API face auth sur `http://localhost:5000`

## Configuration

Le fichier [`application.properties`](./src/main/resources/application.properties) contient actuellement la configuration locale.

Parametres importants:

- port: `8081`
- context path: `/rawabet`
- base MySQL: `rawabet`
- frontend autorise CORS: `http://localhost:4200`
- uploads statiques: dossier `uploads/`

Variables/proprietes a verifier avant usage:

- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`
- `jwt.secret`
- `spring.mail.username`
- `spring.mail.password`
- `app.super-admin.email`
- `ANTHROPIC_API_KEY`

## Demarrage local

1. Creer une base MySQL accessible localement.
2. Adapter `src/main/resources/application.properties` si necessaire.
3. Lancer l'application:

```bash
./mvnw spring-boot:run
```

Sous Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

L'API sera disponible sur:

```text
http://localhost:8081/rawabet
```

## Donnees initialisees au demarrage

Au lancement, le projet initialise:

- les permissions
- les roles `SUPER_ADMIN`, `ADMIN_CINEMA`, `ADMIN_EVENT`, `ADMIN_CLUB`, `CLIENT`
- les abonnements
- un compte super admin par defaut

Compte cree automatiquement si absent:

- email: `admin@test.com`
- mot de passe: `123456`

## Endpoints et modules principaux

Base URL:

```text
http://localhost:8081/rawabet
```

Groupes de routes presents dans le code:

- `/auth/**` : login, reset password, verification email, face auth, WebAuthn
- `/users/**` : profil, gestion utilisateurs, roles, ban/unban
- `/roles/**` et `/permissions/**` : administration RBAC
- `/api/abonnements/**` : abonnements
- `/api/notifications/**` : notifications
- `/event/**` : gestion des evenements
- `/cinema/**`, `/cinemas/**`, `/films/**`, `/salles-cinema/**`, `/seats/**` : cinema
- `/club/**` : club et reservations club
- `/carte/**` : carte de fidelite
- `/chat/**` et `/ws/**` : chat REST + WebSocket
- `/ml/**` : predictions et services ML
- `/uploads/**` : acces aux fichiers statiques

## Tests

Des tests sont presents dans `src/test/java`, notamment sur:

- chat
- services d'abonnement

Execution:

```bash
./mvnw test
```

Sous Windows PowerShell:

```powershell
.\mvnw.cmd test
```

## Remarques importantes

- `spring.jpa.hibernate.ddl-auto=create` recree le schema a chaque demarrage. Ce mode est utile en dev, pas en production.
- Des secrets et credentials existent actuellement dans `application.properties`. Il faut les externaliser vers des variables d'environnement ou un fichier non versionne avant un usage partage ou prod.
- Le CORS est configure pour `http://localhost:4200`. Si le frontend tourne ailleurs, il faut ajuster la configuration.

## Fichiers utiles

- [`pom.xml`](./pom.xml)
- [`src/main/resources/application.properties`](./src/main/resources/application.properties)
- [`src/main/java/org/example/rawabet/config/SecurityConfig.java`](./src/main/java/org/example/rawabet/config/SecurityConfig.java)
- [`src/main/java/org/example/rawabet/config/DataInitializer.java`](./src/main/java/org/example/rawabet/config/DataInitializer.java)
