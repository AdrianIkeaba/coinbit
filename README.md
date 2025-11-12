# CoinBit Crypto Wallet App

This project is a Crypto Wallet App UI that integrates with the CoinGecko API to display cryptocurrency information.

## Features

*   **Coin List:** Displays a list of coins with their names, symbols, and current prices.
*   **Coin Detail Screen:** Shows more detailed information for each coin, including price trends.
*   **Price Charts:** Visualizes price trends with charts.
*   **State Handling:** Gracefully handles loading, error, and empty states.
*   **Offline/Poor Connection Handling:** Manages network issues for a resilient user experience.

## Tech Stack

*   **Language:** Kotlin
*   **UI Toolkit:** Jetpack Compose
*   **Navigation:** Navigation3 (Jetpack Navigation Compose)
*   **Dependency Injection:** Koin
*   **Local Cache:** Datastore (for simple caching), ROOM (for complex operations)
*   **Networking:** Ktor
*   **Architecture:** MVVM

## Screenshots
<img width="269" height="598" alt="image" src="https://github.com/user-attachments/assets/72d1f22f-bac6-4716-ac44-55b5364851a8" /> <img width="269" height="598" alt="image" src="https://github.com/user-attachments/assets/74fd050e-45e7-4b14-88c2-256b1abebed4" /> <img width="269" height="598" alt="image" src="https://github.com/user-attachments/assets/18953fb5-a519-4cd5-a5af-ecb89da5606f" /> <img width="269" height="598" alt="image" src="https://github.com/user-attachments/assets/619b0b8d-fd54-4529-9511-47c3eee8b121" />

## Setup

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/AdrianIkeaba/coinbit
    cd CoinBit
    ```

2.  **API Keys:**
    This application uses the CoinGecko API. **Never commit API keys, tokens, or secrets to GitHub.**
    For local development, you can manage your API keys using environment variables or a configuration file excluded from version control (e.g., `local.properties` or a dedicated `secrets.kt` file).

    *   **CoinGecko API:** No API key is required for public endpoints.

3.  **Gradle Sync:**
    Open the project in Android Studio and allow it to perform a Gradle sync.

4.  **Build and Run:**
    You can build and run the application using Android Studio or Gradle commands:
    ```bash
    ./gradlew build
    ./gradlew run
    ```

## Project Structure

```
CoinBit/
│
├── app/
│   ├── build.gradle.kts
│   ├── libs.versions.toml
│   └── src/main/java/com/ghostdev/coinbit/
│       ├── data/       # Data layer (API services, local cache, repository)
│       ├── di/         # Dependency Injection modules
│       ├── domain/     # Domain layer (use cases, models)
│       ├── presentation/
│       │   ├── components/ # Reusable UI components
│       │   ├── navigation/ # Navigation setup
│       │   ├── theme/      # UI theme (colors, typography, etc.)
│       │   └── viewmodel/  # ViewModels
│       └── MainActivity.kt # Main entry point
│
├── gradle/
└── README.md
```

## Links
1. Demo Video
    - 
2. APK Link
    - https://drive.google.com/file/d/1jl4TAJ4bVvO3b1nfTLkcfYxepXjM9Ui0/view?usp=sharing
3. 

## License

This project is licensed under the [MIT License](LICENSE).
