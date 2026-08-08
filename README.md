# Nikaas (نکاس) — Flood Response. Before it's too late.

An AI-powered emergency coordination system that bridges the gap between scattered environmental telemetry and active rescue responses. Built during the **GDG Islamabad AISeekho Builders Day Hackathon 2026**.

---

## 🌊 The Problem
During heavy monsoon rainfall in Pakistan, streets can turn into rivers within minutes. Critical information is scattered across different isolated channels:
* Citizens post localized hazard reports (*"pani bhar gaya hai"*, *"sadak band hai"*) on social media, but they never reach dispatchers.
* Weather departments predict heavy downpours, but the warning doesn't trigger proactive drain team dispatches.
* Traffic loop data shows congestion building near underpasses, but navigation systems fail to redirect drivers.

Without coordination, response actions are delayed until a disaster occurs.

---

## ⚡ The Solution: Nikaas (نکاس)
**Nikaas** fuses these isolated signals into one unified, actionable response plan:
1. **Citizen Reports**: Real-time localized incident logging with GPS coordinate locking and live camera image capture.
2. **AI Signal Fusion**: Aggregates citizen inputs, live weather advisory warnings, and traffic speed drops. Fuses them using **Gemini 1.5 Pro** to gauge confidence and severity.
3. **Coordinated Responses**: Automatically drafts rerouting paths, WASA drain team dispatches, and resident alert zones.
4. **Human-in-the-Loop Approval**: Requires explicit administrator validation (**Approve & Execute**) or manual adjustment (**Administrative Override**) before triggering execution.

---

## 📱 Project Modules

The repository contains two development attempts:

### 1. Native Android Kotlin Application (`/app`)
Our primary, production-grade native application built for Android devices:
* **Architecture**: MVVM (Model-View-ViewModel), Repository pattern, ViewBinding, and Jetpack Navigation.
* **Core Libraries**: Google Generative AI Client SDK, Google Play Services Maps, and Firebase Cloud Messaging (FCM).
* **Live Features**:
  * **Real GPS Integration**: Requests runtime location permissions to lock down accurate latitude and longitude.
  * **Camera Capture**: Integrates system intent camera capture to take photos and preview them inside the list cards.
  * **Interactive Tactical Map**: Uses Google Maps to draw flood zones (red polygons) and overlay real-time solutions (dashed reroute polylines, amber drain trucks, alert circles).
  * **Administrative Override**: Standard Material Dialog confirmation workflow to manually bypass AI decisions.

### 2. React Native (Expo) Prototype (`/app-expo`)
A cross-platform React Native and TypeScript prototype styled for dark-mode environments:
* **Color Palette**: `#0B1F3A` (background), `#1E3248` (surfaces), `#00C2B2` (teal accent), `#2A4060` (borders), `#8899AA` (muted text).
* **Map Mocking**: Contains a custom vector simulated map for instant testing on web browsers/Expo Go without Google Play Services.

---

## 🚀 Getting Started

### Running the Native Android App (Android Studio)
1. Open **Android Studio**.
2. Select **File ➡️ Open** and choose the root folder of this project (which contains `settings.gradle.kts` and the `app/` folder).
3. The project is pre-configured with the required API keys (Gemini, OpenWeather, Google Maps) in `app/src/main/java/com/nikaas/app/utils/Constants.kt` and `google-services.json` in `app/`.
4. Connect an Android device with USB Debugging enabled, or boot up an Emulator.
5. Click **Run** (`Shift + F10`) to compile and launch.
6. To build the standalone APK, go to **Build ➡️ Build Bundle(s) / APK(s) ➡️ Build APK(s)**.

### Running the React Native Prototype
1. Open a terminal in the `/app-expo` folder.
2. Install dependencies:
   ```bash
   npm install
   ```
3. Launch the Expo developer server:
   ```bash
   npx expo start
   ```
4. Scan the QR code using the **Expo Go** app on your phone (Android/iOS) or press **`w`** to open it instantly in your web browser.

---

## 🛠️ Color Guidelines & Brand System
* **Background**: `#0B1F3A` (Dark Blue)
* **Card Surface**: `#1E3248` (Slate Blue)
* **Primary Teal**: `#00C2B2` (High-contrast Accent)
* **Warning Amber**: `#F5A623` (Medium Severity)
* **Critical Red**: `#E84040` (High Severity)
* **Muted Text**: `#8899AA` (Subtle Details)
* **Border Color**: `#2A4060` (Clean structural outlines)
