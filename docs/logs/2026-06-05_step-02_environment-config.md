# Step 02 - Environment Configuration

## Commit Message

`step 02: configure android environment urls`

## Scope

- Added build-type-specific API base URLs.
- Enabled `BuildConfig` generation.
- Added a central `AppEnvironment` object.
- Configured cleartext traffic by build type.
- Added tests for the debug API URL contract.

## Changed Files

- `app/build.gradle.kts`
- `app/src/main/AndroidManifest.xml`
- `app/src/main/java/com/mspoverlay/android/core/config/AppEnvironment.kt`
- `app/src/test/java/com/mspoverlay/android/core/config/AppEnvironmentTest.kt`
- `docs/index.md`
- `docs/logs/2026-06-05_step-02_environment-config.md`

## Verification

- `testDebugUnitTest` passed.
- `assembleDebug` passed.
- `assembleRelease` passed.
- Confirmed `app/build/outputs/apk/release/app-release-unsigned.apk` was generated.

## Decisions

- Debug API base URL is `http://10.0.2.2:8080` for Android emulator access to the host server.
- Release API base URL is `https://api.msp-overlay.store`.
- Debug allows cleartext traffic; release blocks cleartext traffic.
