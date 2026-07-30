# Architecture Backend — RDV

## Constat sur l'architecture actuelle

| Problème | Détail |
|---|---|
| Exposition des entités JPA en API | `AppointmentController`, `UserController`, `ServiceRDVController` retournent les entités directement → couplage API / persistance |
| Services sans interface | Tous les services sont des classes concrètes → difficile à mocker, pas de contrat explicite |
| Exceptions génériques | `RuntimeException` lancée partout → pas de sémantique métier |
| Responsabilités mélangées | `AppointmentService.createPublicBooking()` crée un utilisateur ET un service ET un RDV |
| Mapping manuel et partiel | Seul `UserMapper` existe, pas de MapStruct, pas de pattern uniforme |
| Package-by-layer uniquement | `controller/`, `service/`, `repository/`, `entity/`, `dto/` → navigation difficile, pas de cohésion fonctionnelle |
| Pas d'enums | `role` et `status` sont des `String` → pas de type-safety |
| `@CrossOrigin` dupliqué | Dans les controllers ET dans `WebConfig` → confusion |
| Logique de validation dans les controllers | `AuthController` fait des `if (request == null)` à la main |
| DTOs mélangés | `request/` et `response/` dans le même dossier, records et classes mélangés |

---

## Architecture cible : Monolithe modulaire (package-by-feature)

Une approche pragmatique qui combine le meilleur du **package-by-feature** et du **layered architecture** :

```
backend/src/main/java/com/rdv/
├── RdvApplication.java
│
├── appointment/          ← Module RDV
│   ├── Appointment.java          (entité JPA)
│   ├── AppointmentStatus.java    (enum)
│   ├── AppointmentRepository.java
│   ├── AppointmentService.java   (interface)
│   ├── AppointmentServiceImpl.java
│   ├── AppointmentController.java
│   ├── AppointmentRequest.java   (DTO entrée)
│   ├── AppointmentResponse.java  (DTO sortie)
│   ├── AppointmentMapper.java    (entity ↔ DTO)
│   └── AppointmentNotFoundException.java
│
├── user/
│   ├── User.java
│   ├── UserRole.java
│   ├── UserRepository.java
│   ├── UserService.java
│   ├── UserServiceImpl.java
│   ├── UserController.java
│   ├── UserRequest.java
│   ├── UserResponse.java
│   ├── UserMapper.java
│   └── UserNotFoundException.java
│
├── service/               ← Module "prestations"
│   ├── ServiceRDV.java
│   ├── ServiceStatus.java
│   ├── ServiceRDVRepository.java
│   ├── ServiceRDVService.java
│   ├── ServiceRDVServiceImpl.java
│   ├── ServiceRDVController.java
│   ├── ServiceRDVRequest.java
│   ├── ServiceRDVResponse.java
│   ├── ServiceRDVMapper.java
│   └── ServiceRDVNotFoundException.java
│
├── auth/
│   ├── Admin.java
│   ├── AdminPasswordReset.java
│   ├── AdminRepository.java
│   ├── AdminPasswordResetRepository.java
│   ├── AdminAuthService.java
│   ├── AdminAuthServiceImpl.java
│   ├── AuthController.java
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   ├── AdminResetRequest.java
│   ├── AdminResetConfirm.java
│   └── AuthenticationException.java
│
├── email/
│   ├── EmailService.java         (interface)
│   └── SmtpEmailService.java     (impl)
│
└── common/
    ├── exception/
    │   ├── BusinessException.java      (base)
    │   ├── ResourceNotFoundException.java
    │   └── GlobalExceptionHandler.java  (@ControllerAdvice)
    └── config/
        ├── WebConfig.java          (CORS centralisé)
        ├── PasswordConfig.java     (BCrypt)
        └── DataSeeder.java         (admin bootstrap)
```

---

## Principes directeurs

### 1. Package-by-feature
Chaque module métier est autonome : entité, repository, service (interface + impl), controller, DTOs (request/response), mapper, exceptions.
- **Avantage** : cohésion forte, navigation rapide, suppression facile d'un module.
- **Règle** : un module ne dépend pas d'un autre module directement — il passe par les services.

### 2. Contrat explicite via interfaces
```
UserService (interface) ← UserServiceImpl
AppointmentService (interface) ← AppointmentServiceImpl
```
- Permet le mock facile en test
- Découple l'API publique de l'implémentation
- L'interface définit le **contrat métier**

### 3. Exposition API via DTOs uniquement
- Les controllers ne retournent **jamais** d'entités JPA
- `AppointmentRequest` / `AppointmentResponse` sont les DTOs d'API
- Le `AppointmentMapper` fait la conversion (manuel ou MapStruct)
- **Principe** : l'API REST est indépendante du modèle de persistance

### 4. Exceptions métier explicites
```
BusinessException (abstract)
├── ResourceNotFoundException
├── DuplicateResourceException
├── AuthenticationException
└── InvalidStateException
```
- Pas de `RuntimeException` lancée depuis les services
- Le `GlobalExceptionHandler` les catch et retourne des réponses HTTP appropriées

### 5. Configuration centralisée
- CORS dans `WebConfig` uniquement — pas de `@CrossOrigin` dans les controllers
- `PasswordConfig` reste inchangé
- `DataSeeder` remplace `AdminBootstrap` (même rôle)

### 6. Enums pour les status/roles
```java
public enum AppointmentStatus {
    PENDING, CONFIRMED, CANCELLED, COMPLETED
}
public enum UserRole {
    USER, ADMIN
}
```
- Type-safety, pas de `String` magiques
- Validation automatique via Jackson

---

## Diagramme de dépendances (simplifié)

```
[Controller] → [Service (interface)]
                    ↓
             [ServiceImpl] → [Repository] → [Entity]
                    ↓
              [Mapper] → [DTO]
```

Les dépendances vont **toujours** dans le même sens : `Controller → Service → Repository`.

---

## Règles de codage

| Règle | Explication |
|---|---|
| Un fichier = une responsabilité | Pas de classes fourre-tout |
| Services sans `@Autowired` direct | Utiliser `@RequiredArgsConstructor` + `final` |
| `@Transactional` sur les services | Pas sur les controllers |
| Validation via `jakarta.validation` | `@Valid`, `@NotBlank`, etc. — pas de `if` dans les controllers |
| Records pour les DTOs immutables | `LoginRequest`, `LoginResponse`, etc. |
| Mapper dans le module | Pas de mapper global / partagé |
| Pas de logique métier dans les controllers | Un controller = orchestrateur, pas de `if` métier |

---

## Évolution vers microservices

Cette architecture modulaire est conçue pour une future extraction en microservices :
- Chaque module peut devenir un service indépendant
- Les interfaces de service définissent les points de découpage
- Les DTOs deviennent les contrats inter-services
- L'infrastructure (email, etc.) est déjà isolée

---

## Migration depuis l'architecture actuelle

1. Créer les enums (`AppointmentStatus`, `UserRole`, `ServiceStatus`)
2. Créer les DTOs request/response pour chaque module
3. Créer les interfaces de service
4. Déplacer la logique métier des services dans les implémentations
5. Créer les exceptions métier (`AppointmentNotFoundException`, etc.)
6. Supprimer les `@CrossOrigin` des controllers (garder uniquement `WebConfig`)
7. Remplacer `AdminBootstrap` par `DataSeeder`
8. Nettoyer les anciens packages plats
