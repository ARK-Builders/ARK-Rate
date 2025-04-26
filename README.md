<h1 align="center"> 💱 ARK Rate</h1>

**ARK Rate** is the **open-source Android app** that redefines currency conversion and portfolio management! Convert over **200 currencies** instantly, manage **cryptocurrencies**, and monitor your assets—all in one blazing-fast, ad-free app. Whether you’re traveling offline in Tokyo or trading BTC in New York, ARK Rate delivers offline-first accuracy with zero hassle. Ready to master your money? Let’s dive into the future of finance!



## 📱 Screenshots
<p align="center">
<img src="https://github.com/user-attachments/assets/7050cc40-74b5-4ff2-99d9-0f734471616d"/>
</p>

[![Build the app](https://github.com/ARK-Builders/ARK-Rate/actions/workflows/build.yml/badge.svg)](https://github.com/ARK-Builders/ARK-Rate/actions/workflows/build.yml)
[![Get it on Google Play](https://img.shields.io/badge/Get%20it%20on%20Google%20Play-3DDC84?logo=google-play&logoColor=white&style=flat-square)](https://play.google.com/store/apps/details?id=dev.arkbuilders.rate)

<a href="https://www.producthunt.com/posts/ark-rate?embed=true&utm_source=badge-featured&utm_medium=badge&utm_souce=badge-ark&#0045;rate" target="_blank"><img src="https://api.producthunt.com/widgets/embed-image/v1/featured.svg?post_id=955026&theme=light&t=1745634671467" alt="ARK&#0032;Rate - Offline&#0032;money&#0032;exchange&#0032;calculator&#0032;for&#0032;travelers&#0032;and&#0032;nomads | Product Hunt" style="width: 250px; height: 54px;" width="250" height="54" /></a>



## 🌟 Why ARK Rate Rules the Money Game
- **Instant Conversions**: Swap USD to JPY, EUR to NGN, or BTC to ETH in a flash! ⚡
- **200+ Currencies**: From GBP to CZK, we’ve got the world’s money covered. 🌎
- **Crypto Power**: Track and convert BTC, ETH, SOL, and more with live crypto rates. ₿
- **Portfolio Tracking**: Manage fiat and crypto assets. 📊
- **Offline Mode**: Convert currencies anywhere, no Wi-Fi needed! 📴
- **Ad-Free & No Login**: Pure, distraction-free finance—no sign-ups, no nonsense. 🚀

**[Discover ARK Rate](https://www.ark-builders.dev/apps/rate)** | **[Download on Google Play](https://play.google.com/store/apps/details?id=dev.arkbuilders.rate)**

## 🛠 Tech Stack:
Our stack is a love letter to modern development, blending cutting-edge libraries with rock-solid architecture. Here’s the magic behind the scenes! 💻

- **Kotlin 2.0+**: The latest Kotlin powers our code with sleek syntax, bulletproof null safety, and next-gen features. It’s fast, expressive, and keeps ARK Rate future-proof! 🚀
- **Gradle 8.11.1+**: Our build system screams speed with blazing-fast compiles and slick dependency management, making development a breeze for contributors. ⚡
- **Jetpack Compose**: Say hello to a jaw-dropping UI! Compose crafts ARK Rate’s slick, responsive interface for seamless currency swaps and portfolio views. 🎨
- **Jetpack Glance**: Home screen widgets? Nailed it! Glance brings instant rate updates to your fingertips, no app launch needed. 📱
- **Dagger**: Precision dependency injection keeps our code modular and testable, injecting everything from databases to network clients like a pro. 🔪
- **MVVM**: Our Model-View-ViewModel architecture ensures clean, scalable code, powering real-time rate updates with zero fuss. 📊
- **Clean Architecture**: Layered, modular, and maintainable—our codebase is built to scale, making it easy to add epic new features. 🏗️
- **Room Database**: Offline conversions? No sweat! Room stores currency data locally for instant access, even on a remote safari. 💾
- **WorkManager**: Background tasks like rate syncing run smoothly, ensuring your portfolio stays fresh without draining your battery. ⏰

## 📐 ARK Rate Modules Diagram
Below, our Mermaid-powered tree diagram showcases the repository’s structure with vibrant, color-coded nodes and straight-line connections. Ready to explore this financial beast? Fork it and join the revolution! 🚀

```mermaid
graph TD
    A[ARK Rate] --- B[app]
    A --- C[core]
    A --- D[feature]
    A --- E[fiaticons]
    A --- F[cryptoicons]

    C --- C1[data]
    C --- C2[db]
    C --- C3[di]
    C --- C4[domain]
    C --- C5[presentation]

    D --- D1[pairalert]
    D --- D2[portfolio]
    D --- D3[quick]
    D --- D4[quickwidget]
    D --- D5[search]
    D --- D6[settings]

    %% Styling for visual appeal
    classDef root fill:#FFD700,stroke:#333,stroke-width:2px,border-radius:10px;
    classDef core fill:#00C4B4,stroke:#333,stroke-width:2px,border-radius:5px;
    classDef feature fill:#FF6F61,stroke:#333,stroke-width:2px,border-radius:5px;
    classDef assets fill:#9370DB,stroke:#333,stroke-width:2px,border-radius:5px;

    class A root;
    class C,C1,C2,C3,C4,C5 core;
    class D,D1,D2,D3,D4,D5,D6 feature;
    class E,F assets;

    %% Node labels with context
    A[ARK Rate: Root Module]
    B[app: Main Application]
    C[core: Shared Infrastructure]
    C1[data: Data Sources & APIs]
    C2[db: Room Database]
    C3[di: Dagger Injection]
    C4[domain: Use Cases & Entities]
    C5[presentation: UI & ViewModels]
    D[feature: App Features]
    D1[pairalert: Rate Alerts]
    D2[portfolio: Asset Tracking]
    D3[quick: Quick Converter]
    D4[quickwidget: Home Widget]
    D5[search: Currency Search]
    D6[settings: User Settings]
    E[fiaticons: Fiat Currency Icons]
    F[cryptoicons: Crypto Icons]
```

## 🤝 Any contribution is welcomed!
**Coders, traders, and finance geeks—unite!** ARK Rate is open-source and craving your brilliance. Here’s how to make your mark:

1. **Fork the Repo**: Grab it at [ARK-Builders/ARK-Rate](https://github.com/ARK-Builders/ARK-Rate).
2. **Hunt Issues**: Find bugs or features in [issues](https://github.com/ARK-Builders/ARK-Rate/issues).
3. **Chat with Us**: Join the party on [Discord](https://discord.com/invite/uRWJyYBr) or [Telegram](https://t.me/ark_builders).

**Hit that ⭐ Star button** if ARK Rate sparks your financial fire! Your support drives us forward!

## 🌐 Connect with ARK-Builders
[![ARK Builders](https://img.shields.io/badge/ARK%20Builders-5865F2?logo=discord&logoColor=white)](https://discord.com/invite/uRWJyYBr)
[![ark_builders](https://img.shields.io/badge/ark__builders-0088CC?logo=telegram&logoColor=white)](https://t.me/ark_builders)
[![ARK-Builders](https://img.shields.io/badge/ARK--Builders-181717?logo=github&logoColor=white)](https://github.com/ARK-Builders)
[![ARKBuilders_DEV](https://img.shields.io/badge/ARKBuilders__DEV-000000?logo=x&logoColor=white)](https://x.com/ARKBuilders_DEV)
[![in ARK Builders Dev](https://img.shields.io/badge/in%20ARK%20Builders%20Dev-0A66C2?logoColor=white)](https://www.linkedin.com/company/ark-builders-dev/)
[![ARK-Builders-Dev](https://img.shields.io/badge/ARK--Builders--Dev-FF0000?logo=youtube&logoColor=white)](https://www.youtube.com/@ARK-Builders-Dev)
[![ARKBuilders](https://img.shields.io/badge/ARKBuilders-12100E?logo=medium&logoColor=white)](https://ark-builders.medium.com/rate-2-0-full-revamp-b9ca3246fad2)

<a href="https://www.ark-builders.dev/apps/rate" target="_blank"><img src="https://github-production-user-asset-6210df.s3.amazonaws.com/581023/255703247-978df9c0-3de4-44ed-97f1-ae46631af49d.png" alt="Download APK" style="height: 60px !important;width: 217px !important;" ></a>
<a href="https://www.buymeacoffee.com/arkbuilders" target="_blank"><img src="https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png" alt="Buy Me A Coffee" style="height: 60px !important;width: 217px !important;" ></a>
