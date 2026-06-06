# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Android application written in Kotlin with Jetpack Compose UI. Single Gradle module (`:app`), package `com.learn.myapplication`. Currently a default Android Studio template (single `MainActivity` rendering a `Greeting` composable inside `MyApplicationTheme`).

## Build / Test Commands

Uses the Gradle wrapper. Always invoke `./gradlew` from the repo root.

- Assemble debug APK: `./gradlew :app:assembleDebug`
- Assemble release APK: `./gradlew :app:assembleRelease`
- Install debug on a connected device/emulator: `./gradlew :app:installDebug`
- Run JVM unit tests (`app/src/test`): `./gradlew :app:testDebugUnitTest`
- Run a single unit test: `./gradlew :app:testDebugUnitTest --tests "com.learn.myapplication.ExampleUnitTest.addition_isCorrect"`
- Run instrumented tests (`app/src/androidTest`, needs device/emulator): `./gradlew :app:connectedDebugAndroidTest`
- Lint: `./gradlew :app:lintDebug` (report under `app/build/reports/lint-results-debug.html`)
- Clean: `./gradlew clean`

## Toolchain Notes

- AGP `9.1.1`, Kotlin `2.2.10`, Compose BOM `2024.09.00`, `minSdk = 30`, `targetSdk = 36`, `compileSdk = 36.1`.
- Java source/target compatibility is set to 11 (`app/build.gradle.kts`). `gradle/gradle-daemon-jvm.properties` may pin the daemon JVM — match it locally if Gradle fails to start.
- Dependency versions and plugin IDs are centralized in `gradle/libs.versions.toml` (version catalog). Add/upgrade libraries there rather than hardcoding in `app/build.gradle.kts`.
- Compose is enabled via the `kotlin-compose` plugin (Kotlin 2.x compose compiler), not the older `composeOptions { kotlinCompilerExtensionVersion = ... }` block — do not add that block.

## Architecture

Single-Activity Compose app. Entry point is `MainActivity` (`app/src/main/java/com/learn/myapplication/MainActivity.kt`), which calls `enableEdgeToEdge()` and sets content inside `MyApplicationTheme`. Theme/typography/colors live under `ui/theme/` (`Theme.kt`, `Color.kt`, `Type.kt`) and follow the standard Material3 template — `MyApplicationTheme` is the wrapper composable to use at the root of any screen.

## Local Config

`local.properties` is checked in (contains `sdk.dir`). It is machine-specific; do not edit it unless intentionally repointing the Android SDK location.
