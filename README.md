<h1 align="center"> 💱 ARK Rate</h1>

**ARK Rate** is the **open-source Android app** that redefines currency conversion and portfolio management! Convert over **200 currencies** instantly, manage **cryptocurrencies**, and monitor your assets—all in one blazing-fast, ad-free app. Whether you’re traveling offline in Tokyo or trading BTC in New York, ARK Rate delivers offline-first accuracy with zero hassle. Ready to master your money? Let’s dive into the future of finance!

## 📱 Screenshots
<p align="center">
<img src="https://github.com/user-attachments/assets/7050cc40-74b5-4ff2-99d9-0f734471616d"/>
</p>

[<img src="https://upload.wikimedia.org/wikipedia/commons/7/78/Google_Play_Store_badge_EN.svg" alt="Download on the Play Store" width="240" height="52"/>](https://play.google.com/store/apps/details?id=dev.arkbuilders.rate)
[<img src="https://api.producthunt.com/widgets/embed-image/v1/featured.svg?post_id=955026" alt="Download on the Play Store" width="240"/>](https://www.producthunt.com/posts/ark-rate)

[![Build the app](https://github.com/ARK-Builders/ARK-Rate/actions/workflows/build.yml/badge.svg)](https://github.com/ARK-Builders/ARK-Rate/actions/workflows/build.yml)

## 🌟 Why ARK Rate Rules the Money Game
- **Instant Conversions**: Swap USD to JPY, EUR to NGN, or BTC to ETH in a flash! ⚡
- **200+ Currencies**: From GBP to CZK, we’ve got the world’s money covered. 🌎
- **Crypto Power**: Track and convert BTC, ETH, SOL, and more with live crypto rates.
- **Portfolio Tracking**: Manage fiat and crypto assets. 📊
- **Offline Mode**: Convert currencies anywhere, no Wi-Fi needed! 📴
- **Ad-Free & No Login**: Pure, distraction-free finance—no sign-ups, no nonsense. 🚀

**[Discover ARK Rate](https://www.ark-builders.dev/apps/rate)**

## 🛠 Tech Stack:
Our stack is a love letter to modern development, blending cutting-edge libraries with rock-solid architecture. Here’s the magic behind the scenes! 💻

- **Kotlin 2.0+**: The latest Kotlin powers our code with sleek syntax, bulletproof null safety, and next-gen features. It’s fast, expressive, and keeps ARK Rate future-proof! 🚀
- **Jetpack Compose**: Say hello to a jaw-dropping UI! Compose crafts ARK Rate’s slick, responsive interface for seamless currency swaps and portfolio views. 🎨
- **Compose Destinations**: KSP library that processes annotations and generates code that uses Official Jetpack Compose Navigation under the hood, without boilerplate code 🎯
- **Jetpack Glance**: Home screen widgets? Nailed it! Glance brings instant rate updates to your fingertips, no app launch needed. 📱
- **Dagger**: Precision dependency injection keeps our code modular and testable, injecting everything from databases to network clients like a pro. 🔪
- **MVVM+**: Our Model-View-ViewModel architecture ensures clean, scalable code, powering real-time rate updates with zero fuss. Empowered by [orbit-mvi](https://github.com/orbit-mvi/orbit-mvi), Redux/MVI-like library - but without the baggage. It's so simple we think of it as MVVM+  📊
- **Clean Architecture**: Layered, modular, and maintainable—our codebase is built to scale, making it easy to add epic new features. 🏗️
- **Room Database**: Offline conversions? No sweat! Room stores currency data locally for instant access, even on a remote safari. 💾
- **WorkManager**: Background tasks like rate syncing run smoothly, ensuring your portfolio stays fresh without draining your battery. ⏰

## 📐 ARK Rate Architecture
Below, our Mermaid-powered tree diagram showcases the repository’s structure with vibrant, color-coded nodes and straight-line connections. Ready to explore this financial beast? Fork it and join the revolution! 🚀

```mermaid
graph TD
    %% Presentation Layer
    A[Presentation Layer] --> B[Domain Layer]
    A --> C[UI]
    A --> D[ViewModel]

    %% Domain Layer
    B --> E[Use Cases]
    B --> F[Entities]
    B --> G[Repository Interfaces]

    %% Data Layer
    H[Data Layer] --> B
    H --> I[Repository Impl]
    I --> J[Data Sources]
    J --> K[Local Data Source]
    J --> L[Remote Data Source]

    %% Styling
    classDef presentation fill:#f9f,stroke:#333,stroke-width:2px
    classDef domain fill:#bbf,stroke:#333,stroke-width:2px
    classDef data fill:#bfb,stroke:#333,stroke-width:2px
    class A,C,D presentation
    class B,E,F,G domain
    class H,I,J,K,L data
```

## 🤝 Any contribution is welcomed!
**Coders, traders, and finance geeks—unite!** ARK Rate is open-source and craving your brilliance. Here’s how to make your mark:

1. **Fork the Repo**: Grab it at [ARK-Builders/ARK-Rate](https://github.com/ARK-Builders/ARK-Rate).
2. **Hunt Issues**: Find bugs or features in [issues](https://github.com/ARK-Builders/ARK-Rate/issues).
3. **Chat with Us**: Join the party on [Discord](https://discord.com/invite/uRWJyYBr) or [Telegram](https://t.me/ark_builders).

**Hit that ⭐ Star button** if ARK Rate sparks your financial fire! Your support drives us forward!

## 🔧 Building
To build the project, you need to provide a personal access token to download dependencies hosted on GitHub Packages.

### 📦 GitHub Packages Authentication

1. Generate a **fine-grained personal access token**:
   - URL: [Create token](https://github.com/settings/personal-access-tokens/new)
   - Required access: **Public repositories**

2. Add the token to your `local.properties` file at the root of the project: `gpr.token=$your_github_token`


## 🌐 Connect with ARK-Builders
[![ARK Builders](https://img.shields.io/badge/ARK%20Builders-5865F2?logo=discord&logoColor=white)](https://discord.com/invite/uRWJyYBr)
[![ark_builders](https://img.shields.io/badge/ark__builders-0088CC?logo=telegram&logoColor=white)](https://t.me/ark_builders)
[![ARK-Builders](https://img.shields.io/badge/ARK--Builders-181717?logo=github&logoColor=white)](https://github.com/ARK-Builders)
[![ARKBuilders_DEV](https://img.shields.io/badge/ARKBuilders__DEV-000000?logo=x&logoColor=white)](https://x.com/ARKBuilders_DEV)
[![in ARK Builders Dev](https://img.shields.io/badge/in%20ARK%20Builders%20Dev-0A66C2?logoColor=white)](https://www.linkedin.com/company/ark-builders-dev/)
[![ARK-Builders-Dev](https://img.shields.io/badge/ARK--Builders--Dev-FF0000?logo=youtube&logoColor=white)](https://www.youtube.com/@ARK-Builders-Dev)
[![ARKBuilders](https://img.shields.io/badge/ARKBuilders-12100E?logo=medium&logoColor=white)](https://ark-builders.medium.com/rate-2-0-full-revamp-b9ca3246fad2)


<a href="https://www.buymeacoffee.com/arkbuilders" target="_blank"><img src="https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png" alt="Buy Me A Coffee" style="height: 60px !important;width: 217px !important;" ></a>
