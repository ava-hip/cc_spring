# CC Spring MongoDB — API REST de gestion de consultations médicales

API REST développée avec **Spring Boot** et **MongoDB** dans le cadre du projet évalué « Projet JEE, Spring, MongoDB ».
Elle gère les patients, les médecins, les consultations et les prescriptions de médicaments, avec une authentification par jeton **JWT**.

---

## Stack technique

| Domaine | Technologie |
|---|---|
| Langage | Java 21 |
| Framework | Spring Boot 4.1.1 (Spring Framework 7) |
| Base de données | MongoDB 8 (Spring Data MongoDB) |
| Sécurité | Spring Security 7 — OAuth2 Resource Server, JWT signé HS256 |
| Documentation API | springdoc-openapi 3.1.1 (Swagger UI) |
| Mapping DTO | ModelMapper 3.2.7 |
| Tests | JUnit 5, Mockito, AssertJ, MockMvc (`@WebMvcTest`) |
| Qualité | JaCoCo 0.8.15 (couverture), SonarQube Community |
| Conteneurs | Docker Compose (MongoDB + SonarQube) |

---

## Prérequis

- **JDK 21**, avec la variable `JAVA_HOME` pointant dessus (vérifier avec `mvn -v`)
- **Maven 3.9+** (ou le wrapper fourni : `.\mvnw.cmd` sous Windows, `./mvnw` sous Linux/macOS)
- **Docker Desktop** (pour MongoDB et SonarQube)

---

## Configuration

Les secrets ne sont pas versionnés : ils sont lus depuis un fichier `.env` à la racine du projet (ignoré par Git).

```powershell
Copy-Item .env.example .env
```

Puis renseigner les valeurs :

| Variable | Rôle | Obligatoire |
|---|---|---|
| `JWT_SECRET` | Clé de signature des jetons JWT (HS256) — **au moins 32 caractères** | Oui |
| `SONAR_TOKEN` | Token d'analyse SonarQube (voir plus bas) | Pour l'analyse uniquement |
| `MONGO_URI` | URI MongoDB. Par défaut : `mongodb://localhost:27017/cc_spring` | Non |

Spring Boot charge ce fichier automatiquement grâce à la ligne suivante de `application.properties` :

```properties
spring.config.import=optional:file:.env[.properties]
```

La connexion MongoDB se change **uniquement par configuration** (variable `MONGO_URI`), sans recompiler.

---

## Lancer le projet

### 1. Démarrer MongoDB

```powershell
docker compose up -d mongodb
```

### 2. Lancer l'application

```powershell
mvn spring-boot:run
```

L'API démarre sur **http://localhost:8080**.

Au premier démarrage, la classe `DataInitializer` insère un jeu de données de démonstration (uniquement si les collections sont vides) :

| Type | Données |
|---|---|
| Patients | `185057512345678` Dupont, `290036912345678` Martin |
| Médecins | `M001` Durand, `M002` Bernard |
| Médicaments | `DOLI500` Doliprane 500mg, `AMOX1G` Amoxicilline 1g |
| Consultation | une consultation future de Dupont avec M001, prescrivant DOLI500 |

### Comptes de démonstration

| Identifiant | Mot de passe | Rôle |
|---|---|---|
| `admin` | `admin123` | `ADMIN` |
| `medecin` | `medecin123` | `MEDECIN` |

> Comptes destinés uniquement à la démonstration locale.

---

## Utiliser l'API avec Swagger

1. Ouvrir **http://localhost:8080/swagger-ui.html** (accessible sans authentification).
2. Appeler `POST /api/auth/login` avec :
   ```json
   { "username": "admin", "password": "admin123" }
   ```
3. Copier la valeur `token` de la réponse.
4. Cliquer sur **Authorize** et coller le jeton.
5. Tous les autres endpoints sont alors utilisables.

La spécification OpenAPI brute est disponible sur **http://localhost:8080/v3/api-docs**.

---

## Endpoints

| Méthode | Route | Description | Accès |
|---|---|---|---|
| `POST` | `/api/auth/login` | Authentification, renvoie un jeton JWT (1 h) | Public |
| `GET` | `/api/patients?search=&page=&size=` | Patients paginés, recherche optionnelle sur le nom ou le n° de sécurité sociale | Authentifié |
| `GET` | `/api/medecins?search=&page=&size=` | Médecins paginés, recherche optionnelle sur le nom ou le matricule | Authentifié |
| `GET` | `/api/patients/{numSS}/consultations?page=&size=` | Consultations paginées d'un patient | Authentifié |
| `GET` | `/api/consultations/{numero}/prescriptions` | Détail des médicaments prescrits (avec libellé) | Authentifié |
| `POST` | `/api/consultations` | Programme une consultation (date future obligatoire) | Authentifié |
| `PUT` | `/api/consultations/{numero}` | Modifie la date et le médecin d'une consultation programmée | Authentifié |
| `DELETE` | `/api/consultations/{numero}` | Supprime une consultation programmée | **ADMIN** |
| `PUT` | `/api/consultations/{numero}/prescriptions` | Remplace la prescription d'une consultation programmée | Authentifié |

Paramètres de pagination : `page` (à partir de 0), `size` (20 par défaut), `sort` (ex. `sort=nom,asc`).

### Règles métier

- Une consultation ne peut être **créée** qu'à une date future (`@Future`).
- Une consultation déjà passée ne peut être **ni modifiée, ni supprimée, ni voir sa prescription changer** → `409 Conflict`.
- Le médecin, le patient et les médicaments référencés doivent exister → sinon `404 Not Found`.

### Format des erreurs

Toutes les erreurs sont renvoyées au format standard **ProblemDetail** (RFC 9457, `application/problem+json`) par le gestionnaire global `GlobalExceptionHandler` :

| Statut | Cas |
|---|---|
| `400` | Données invalides (validation, JSON mal formé) — détail champ par champ dans `erreurs` |
| `401` | Jeton absent/invalide ou identifiants incorrects |
| `403` | Rôle insuffisant |
| `404` | Ressource introuvable |
| `409` | Consultation passée, non modifiable |
| `500` | Erreur inattendue (journalisée, message générique côté client) |

---

## Tests

> MongoDB doit être démarré : le test `CcSpringApplicationTests` charge le contexte complet de l'application.

```powershell
mvn test
```

| Type | Classes | Ce qui est vérifié |
|---|---|---|
| Tests unitaires de services (Mockito) | `PatientServiceTest`, `ConsultationServiceTest` | Logique métier, règles de modification, mapping DTO. Une `Clock` figée rend les tests indépendants de la date du jour. |
| Tests de la couche web (`@WebMvcTest` + vraie `SecurityConfig`) | `PatientControllerTest`, `ConsultationControllerTest` | JSON renvoyé, codes HTTP (200, 201, 204, 400, 401, 403, 404), autorisations par rôle |
| Test de démarrage | `CcSpringApplicationTests` | Chargement complet du contexte Spring |

Rapport de couverture JaCoCo (généré par `mvn verify`) : `target/site/jacoco/index.html`.

---

## Analyse de la qualité avec SonarQube

### 1. Démarrer le serveur SonarQube

```powershell
docker compose up -d sonarqube
```

Patienter 1 à 2 minutes, puis ouvrir **http://localhost:9000**.

### 2. Première configuration (une seule fois)

1. Se connecter avec `admin` / `admin`, puis définir un nouveau mot de passe.
2. **Create a local project** → clé du projet : `cc_spring`, branche : `main`.
3. Choisir **Use the global setting**.
4. Méthode d'analyse : **Locally** → **Generate a token**.
5. Copier le token dans `.env` : `SONAR_TOKEN=sqp_...`

La clé du projet et l'URL du serveur sont déjà déclarées dans le `pom.xml` (`sonar.projectKey`, `sonar.host.url`).

### 3. Lancer une analyse

Charger les variables du `.env` dans la session PowerShell, puis lancer Maven :

```powershell
Get-Content .env | Where-Object { $_ -match '^[^#].*=' } | ForEach-Object {
    $k, $v = $_ -split '=', 2; Set-Item "env:$($k.Trim())" $v.Trim()
}
mvn clean verify sonar:sonar
```

Le plugin SonarQube lit automatiquement la variable d'environnement `SONAR_TOKEN`.
La commande exécute les tests, génère le rapport JaCoCo (`target/site/jacoco/jacoco.xml`, importé automatiquement), puis envoie l'analyse.

Résultats : **http://localhost:9000/dashboard?id=cc_spring**

## Architecture

Organisation en couches : **contrôleur → service → repository**, avec des DTO pour isoler l'API du modèle de données.

```
src/main/java/fr/dawan/cc_spring
├── config/       Données de démo, horloge (Clock), configuration OpenAPI
├── controller/   Contrôleurs REST (Auth, Patient, Medecin, Consultation)
├── dto/          Objets échangés avec l'API + PageResponse<T> générique
├── entity/       Documents MongoDB
├── exception/    Exceptions métier + GlobalExceptionHandler (ProblemDetail)
├── mapper/       DtoMapper générique (ModelMapper)
├── repository/   Interfaces Spring Data MongoDB
├── security/     SecurityConfig (JWT, CORS, rôles) + UtilisateurDetailsService
└── service/      Logique métier
```

### Modélisation MongoDB (depuis le MCD)

| MCD | Collection / choix |
|---|---|
| MEDECIN (Matricule, NomMED) | `medecin`, `_id` = matricule |
| PATIENT (N°SS, NomPAT) | `patient`, `_id` = n° de sécurité sociale |
| MEDICAMENT (Code, Libellé) | `medicament`, `_id` = code |
| CONSULTATION (Numéro, Date) + associations *Donne* et *Assiste* (1,1) | `consultation`, `_id` = ObjectId généré ; références simples `medecinMatricule` et `patientNumSS` |
| Association *Prescrit* (NB prises) | liste **embarquée** `prescriptions` dans la consultation : une prescription n'existe pas sans sa consultation |
| Utilisateurs de l'API | `utilisateur` (mot de passe haché en BCrypt, rôles) |

Les références sont stockées par identifiant plutôt qu'avec `@DBRef`, plus simple et plus performant ; l'intégrité est vérifiée dans la couche service (MongoDB ne gère pas de clés étrangères).