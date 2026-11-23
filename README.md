 # PhishWisely

**PhishWisely** is an Android-based mobile application designed to deliver phishing awareness training on-the-go modeled after cybersecurity Capture-The-Flag (CTF) events. This proof-of-concept prototype provides users with email phishing scenarios, immediate feedback, performance tracking, and a leaderboard to compare results. Currently, all data is stored locally on your device; there is no network connectivity required.

## Features

### User Authentication
- User registration with two hardcoded organization-specific signup codes
- Email and password authentication
- Password complexity validation
- Salted password hashing
- Persistent user accounts using Room/SQLite

### Email Phishing Challenges
- Multiple-choice and red-flag identification questions
- Challenge difficulty levels: Easy, Medium, and Hard
- Immediate feedback and a brief explanation
- Persistent challenge attempt history

### Scoring and Progress
- Points awarded for challenge completion
- Best attempt per challenge used for total points
- Overall accuracy, including retries
- Dashboard statistics for points, accuracy, and number of completed challenges
- Leaderboard based on user point total
- Score reset

### User Profiles
- View account and organization information
- Edit profile details
- Change email and password
- Password confirmation required before saving account changes

## Technologies Used

| Technology | Purpose |
|---|---|
| Java | Android application development |
| Android Studio | Development environment |
| Android SDK | Android application framework |
| Room | SQLite persistence library |
| SQLite | Local relational database |
| Gradle | Build automation and dependency management |
| JUnit | Automated unit testing |

## Installation and Setup

### Prerequisites
- Android Studio
- Android SDK
- Android emulator or physical Android device 
- Git (for cloning the repository)

### Tested Environments
- PhishWisely has been developed and tested using a **Pixel 7 running API 37**. Compatability with other devices and versions has not yet been verified.

### Instructions

**1. Clone the repository**

```bash
git clone https://github.com/kadenkidwiler1/phishwisely
```

**2. Open the project**

Open Android Studio and select the cloned project directory containing `settings.gradle.kts`.

**3. Configure the SDK**

Allow Android Studio to synchronize Gradle and verify that the Android SDK is configured correctly.

**4. Build the application**

Wait for Android Studio to build the application, or execute:
```powershell
.\gradlew.bat assembleDebug
```

**5. Run PhishWisely**

Select an Android emulator or physical device and run the `app` configuration. NOTE: Refer to the Tested Environments to verify if your emulator or device has been tested.

## Database Implementation

PhishWisely uses Android Room, built on SQLite, for persistent local storage.

The database contains records for:

- Users
- Organizations
- Email-based challenges and questions
- Challenge attempts
- User answer submission history

Important database classes include:

- `AppDatabase.java` — Room database configuration
- `SeedData.java` — Initial organization, user, and challenge records
- `AccountRepository.java` — Account-related database operations
- `UserDao.java` — User record operations
- `ChallengeDao.java` — Challenge retrieval
- `ScoreCalculator.java` — User performance calculations

Upon initial build, the database will be seeded with the following organization codes and accounts:
- Organization Codes
    - CKI
    - COSC412
- Demo accounts
    - demo@phishwisely.com - Demo123!
    - student@cosc412.towson.edu - Tigers2026!
Alternatively, you may create your own account but you **must** register using one of the above Organization Codes during regitration.

## Testing

**PasswordPolicyTest.java**

Verifies password complexity requirements, including minimum length, uppercase characters, lowercase characters, numbers, and special characters.

**ScoreCalculatorTest.java**

Verifies that:
- Only the highest-scoring attempt per challenge contributes to total points.
- Challenges are counted once toward completion.
- Retries contribute to attempt counts and accuracy calculations.

Run all local unit tests using:

```powershell
.\gradlew.bat testDebugUnitTest
```

## Limitations

PhishWisely currently uses a local SQLite database.

As a result:
- User accounts are stored separately on each device.
- Challenge progress is not synchronized across devices.
- Leaderboards reflect users stored in the same local database.
- No centralized authentication server is used.

