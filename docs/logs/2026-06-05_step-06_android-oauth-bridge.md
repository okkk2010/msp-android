# Step 06 - Android OAuth Bridge Readiness

## Commit Message

`step 06: prepare android oauth bridge flow`

## Scope

- Added Android deep link intent filter for `msp-overlay://auth/callback`.
- Added Android OAuth start URL builder for the future server endpoint.
- Added secure random OAuth state generator.
- Added OAuth callback parser with state validation and error handling.
- Added unit tests for URL generation, state generation, and callback parsing.

## Changed Files

- `app/src/main/AndroidManifest.xml`
- `app/src/main/java/com/mspoverlay/android/core/auth/oauth/OAuthConfig.kt`
- `app/src/main/java/com/mspoverlay/android/core/auth/oauth/OAuthStateGenerator.kt`
- `app/src/main/java/com/mspoverlay/android/core/auth/oauth/AndroidOAuthUrlBuilder.kt`
- `app/src/main/java/com/mspoverlay/android/core/auth/oauth/OAuthCallbackParser.kt`
- `app/src/test/java/com/mspoverlay/android/core/auth/oauth/AndroidOAuthUrlBuilderTest.kt`
- `app/src/test/java/com/mspoverlay/android/core/auth/oauth/OAuthCallbackParserTest.kt`
- `docs/index.md`
- `docs/logs/2026-06-05_step-06_android-oauth-bridge.md`

## Verification

- `testDebugUnitTest` passed.
- `assembleDebug` passed.

## Decisions

- Android will use `msp-overlay://auth/callback`.
- The app expects a future server endpoint at `/api/auth/android/google/start`.
- The app rejects callbacks with mismatched state before accepting tokens.

## Server Follow-up

- Add `/api/auth/android/google/start` on `msp-server` before real Google login can work end-to-end on Android.
