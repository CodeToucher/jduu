# Vilnius Transport Android App

A native Android application for real-time Vilnius public transport tracking and offline timetables.

## Features
- **Live Map**: Real-time vehicle positions from `stops.lt` on **OpenStreetMap**.
- **Offline Timetables**: Full route and schedule database stored locally.
- **Search**: Fast route filtering.
- **Material Design**: Modern UI with Jetpack Compose.

## Prerequisites
- Android Studio Hedgehog or newer.
- JDK 17 or newer.
- (No API Key required for OSM).

## Setup

1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd JuduTransport
   ```

2. **Build and Run**:
   - The project uses OpenStreetMap, so no API key configuration is needed.

## Compilation

You can build the project using Gradle Wrapper locally, or use **GitHub Actions** for a cloud build (recommended if you don't have Android Studio).

### Local Build
```bash
./gradlew assembleDebug
```

### Cloud Build (GitHub)
1. Push this repository to GitHub.
2. Go to the **Actions** tab.
3. Select the latest build.
4. Download the **app-debug** artifact.

## Running the App

1. Connect your Android device or start an emulator.
2. Run via command line:
   ```bash
   ./gradlew installDebug
   ```
3. Or simply press **Run** (Green Arrow) in Android Studio.

## Architecture
- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Data**: Room (Offline), Retrofit (Real-time)
- **Maps**: **osmdroid** (OpenStreetMap)
