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

## Setup

1.  **Clone the repository:**
    ```bash
    git clone [repository_url]
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

## Contributing

Contributions are welcome! Please follow the established coding conventions and pull request process.

## License

This project is licensed under the [MIT License](LICENSE).
