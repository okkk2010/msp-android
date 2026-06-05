# Step 03 - API Client

## Commit Message

`step 03: add android api client foundation`

## Scope

- Added Retrofit and OkHttp dependencies.
- Added `AuthTokenProvider`.
- Added `ApiClientFactory`.
- Split public and authenticated OkHttp/Retrofit clients.
- Added tests for Authorization header behavior.

## Changed Files

- `app/build.gradle.kts`
- `app/src/main/java/com/mspoverlay/android/core/network/AuthTokenProvider.kt`
- `app/src/main/java/com/mspoverlay/android/core/network/ApiClientFactory.kt`
- `app/src/test/java/com/mspoverlay/android/core/network/ApiClientFactoryTest.kt`
- `docs/index.md`
- `docs/logs/2026-06-05_step-03_api-client.md`

## Verification

- `testDebugUnitTest` passed.
- `assembleDebug` passed.

## Decisions

- Public API clients never attach Authorization headers.
- Authenticated clients attach `Authorization: Bearer {token}` only when an access token is available.
