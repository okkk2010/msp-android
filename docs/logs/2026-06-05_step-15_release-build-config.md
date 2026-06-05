# Step 15 - Release Build Configuration

## Commit Message

`step 15: finalize android release build config`

## Scope

- Finalized build-type specific environment flags for debug and release.
- Release builds use the production API URL, disable cleartext traffic, and keep HTTP logging disabled.
- Added ProGuard keep rules for Retrofit/Gson DTO contracts used by the API and overlay parser.

## Changed Files

- `app/build.gradle.kts`
- `app/proguard-rules.pro`
- `app/src/main/java/com/mspoverlay/android/core/config/AppEnvironment.kt`
- `app/src/test/java/com/mspoverlay/android/core/config/AppEnvironmentTest.kt`
- `docs/index.md`
- `docs/logs/2026-06-05_step-15_release-build-config.md`

## Verification

- `testDebugUnitTest` passed.
- `assembleDebug` passed.
- `assembleRelease` passed.

## Decisions

- Signing credentials are not committed. Release build verification uses the default unsigned release artifact until a secure signing setup is provided.
- Minification remains disabled for the MVP release build, but ProGuard rules are prepared so shrink/minify can be enabled later with less risk.

## Issues

- Production Google OAuth redirect registration still depends on server/provider configuration.

## Next Steps

- Add CI secrets and release signing when a distribution channel is selected.
