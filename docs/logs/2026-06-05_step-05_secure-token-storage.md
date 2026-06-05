# Step 05 - Secure Token Storage

## Commit Message

`step 05: add secure token storage contract`

## Scope

- Added token model and token store contract.
- Added AndroidX Security Crypto based encrypted token store implementation.
- Added stored token provider for authenticated API clients.
- Added unit tests for token save/read/clear behavior.

## Changed Files

- `app/build.gradle.kts`
- `app/src/main/java/com/mspoverlay/android/core/auth/AuthTokens.kt`
- `app/src/main/java/com/mspoverlay/android/core/auth/TokenStore.kt`
- `app/src/main/java/com/mspoverlay/android/core/auth/StoredAuthTokenProvider.kt`
- `app/src/main/java/com/mspoverlay/android/core/auth/EncryptedPreferencesTokenStore.kt`
- `app/src/test/java/com/mspoverlay/android/core/auth/TokenStoreTest.kt`
- `docs/index.md`
- `docs/logs/2026-06-05_step-05_secure-token-storage.md`

## Verification

- `testDebugUnitTest` passed.
- `assembleDebug` passed.

## Decisions

- Runtime token persistence uses `EncryptedSharedPreferences`.
- Unit tests validate the storage contract with an in-memory store to keep JVM tests device-independent.
