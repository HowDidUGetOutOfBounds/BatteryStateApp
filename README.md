# Battery State App

An Android app that displays real-time battery information — voltage, temperature, health, charge level, and technology type — both in-app and via a homescreen widget.

## Screenshots

| App | Widget |
|-----|--------|
| ![App screenshot](https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTs37q9cCgGPdepqziy_B7fyb9EFkPIkP90A9c0vrENWvqGnx4uROA92t_3&s=10) | ![Widget screenshot](https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTs37q9cCgGPdepqziy_B7fyb9EFkPIkP90A9c0vrENWvqGnx4uROA92t_3&s=10) |

## Features

- Real-time battery level, voltage, temperature, health, and technology type
- Interactive battery gauge with arc visualization
- Homescreen widget (BatteryAppWidget)
- Light / dark theme support
- MVI architecture with Jetpack Compose
- Navigation 3 for screen navigation

## Build

```bash
# Clone the repository
git clone https://github.com/your-username/BatteryStateApp.git
cd BatteryStateApp

# Build a debug APK
./gradlew assembleDebug

# Install on a connected device
./gradlew installDebug
```

Requires JDK 21 and Android SDK 36.

## Tech Stack

- **Language**: Kotlin 2.0
- **UI**: Jetpack Compose, Material 3
- **Architecture**: MVI (Model-View-Intent), Clean Architecture
- **DI**: Hilt
- **Navigation**: Navigation 3
- **Widget**: Android App Widget (RemoteViews)
- **Build**: Gradle 8.11, AGP 8.9
