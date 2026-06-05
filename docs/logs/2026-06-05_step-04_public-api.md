# Step 04 - Public API Integration

## Commit Message

`step 04: add public msp api contracts`

## Scope

- Added common API response DTOs.
- Added public MSP API DTOs for platform, game, overlay list, detail, and code load.
- Added Retrofit service contract for public APIs.
- Added Gson converter and coroutine dependencies.
- Added MockWebServer tests for public overlay list and code load calls.

## Changed Files

- `app/build.gradle.kts`
- `app/src/main/java/com/mspoverlay/android/core/network/ApiClientFactory.kt`
- `app/src/main/java/com/mspoverlay/android/core/network/dto/ApiResponse.kt`
- `app/src/main/java/com/mspoverlay/android/core/network/dto/PageResponse.kt`
- `app/src/main/java/com/mspoverlay/android/feature/discover/data/PublicApiDtos.kt`
- `app/src/main/java/com/mspoverlay/android/feature/discover/data/PublicMspApi.kt`
- `app/src/test/java/com/mspoverlay/android/feature/discover/data/PublicMspApiTest.kt`
- `docs/index.md`
- `docs/logs/2026-06-05_step-04_public-api.md`

## Verification

- `testDebugUnitTest` passed.
- `assembleDebug` passed.

## Decisions

- Public API calls remain Authorization-free.
- `GET /api/overlays/code/{code}` is treated as the direct apply-by-code API because it returns inline `overlayJson`.
