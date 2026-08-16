# TwoFold — Task Management Application

> A native Android task-management application built with Kotlin, Jetpack Compose, Room, Retrofit, Firebase services, coroutines, and modern Android tooling.

[![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)](https://developer.android.com/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4)](https://developer.android.com/compose)
[![Room](https://img.shields.io/badge/Database-Room-6C63FF)](https://developer.android.com/training/data-storage/room)

## Overview

TwoFold is a task-management application built as a native Android project. The project focuses on modern Android development, local persistence, asynchronous programming, API integration, and testable UI architecture.

## Engineering highlights

- **Jetpack Compose UI** for declarative Android interfaces.
- **Kotlin Coroutines** for asynchronous work.
- **Room** for local relational persistence.
- **Retrofit + OkHttp** for network communication.
- **Moshi** for JSON serialization/code generation.
- **Firebase AI / App Check integration** for cloud capabilities and application protection.
- **ViewModel + lifecycle-aware Compose integration** for state management.
- **Robolectric + Roborazzi + Compose testing** infrastructure for automated and screenshot-based UI validation.
- **KSP** for generated Room/Moshi code.
- **Environment-based secrets** through the Gradle Secrets plugin.

## Architecture

```text
Compose UI
    │
    ▼
ViewModel / State
    │
 ┌──┴───────────┐
 ▼              ▼
Room          Retrofit
 │              │
 ▼              ▼
Local data   Remote API
        \      /
         \    /
       Domain / App logic
```

## Technology stack

| Layer | Technologies |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose, Material 3 |
| Architecture | ViewModel + lifecycle-aware state |
| Local database | Room |
| Networking | Retrofit, OkHttp |
| Serialization | Moshi |
| Async | Kotlin Coroutines |
| Cloud | Firebase services / Firebase AI |
| Testing | JUnit, Robolectric, Compose UI tests, Roborazzi |
| Build | Gradle Kotlin DSL, KSP |

## Project configuration

The Android module targets modern Android SDK versions and uses Compose. Release signing values are read from environment variables rather than hard-coded credentials. The repository also provides `.env.example` to document required configuration.

## Running locally

```bash
git clone https://github.com/Rrachet/TwoFold--App-for-Task-management.git
cd TwoFold--App-for-Task-management
```

Open the project in Android Studio, configure the required environment values and Firebase configuration, then build/run the `app` module on an emulator or Android device.

## Testing

The project includes infrastructure for:

- JVM unit tests
- Android/Compose UI tests
- Robolectric tests
- screenshot-based UI validation with Roborazzi

Run the appropriate Gradle test tasks from Android Studio or the Gradle wrapper.

## Engineering decisions

### Why Room?

Task-management data benefits from a structured local relational model and reliable offline persistence. Room provides an abstraction over SQLite while integrating cleanly with Kotlin and Android architecture components.

### Why Compose?

Compose reduces XML/UI boilerplate and makes state-driven interfaces easier to build and iterate on.

### Why Retrofit + OkHttp?

The combination provides a clean boundary for remote API calls while keeping networking concerns separate from UI code.

## What I learned

- Building native Android applications with Kotlin.
- Managing UI state with Compose and ViewModels.
- Designing local persistence with Room.
- Integrating REST APIs and asynchronous network calls.
- Setting up Android testing and screenshot validation.
- Managing build-time configuration and secrets safely.

## Roadmap

- [ ] Add a complete architecture diagram with data-flow details
- [ ] Add offline-first conflict handling
- [ ] Expand automated test coverage
- [ ] Add CI for Android build/test/lint
- [ ] Add release screenshots and demo video

## Author

**Amarnath Mishra** — Computer Science Engineer focused on full-stack development, Android, AI/ML, and production-oriented software engineering.

[GitHub](https://github.com/Rrachet)