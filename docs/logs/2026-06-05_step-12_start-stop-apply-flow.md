# Step 12 - Start Stop Apply Flow and Final Verification

## Commit Message

`step 12: wire overlay apply flow`

## Scope

- Added `OverlayApplyRequestFactory` for validating code-load overlay responses before service apply.
- Added service start/stop intent factory methods.
- Wired `OverlayService` to parse `overlayJson` extras and pass the parsed document to the full-screen overlay view.
- Added tests for valid apply requests, missing JSON, and overlay ID mismatch.
- Ran final test, debug build, and release build.

## Changed Files

- `app/src/main/java/com/mspoverlay/android/overlay/apply/OverlayApplyRequest.kt`
- `app/src/main/java/com/mspoverlay/android/overlay/service/OverlayService.kt`
- `app/src/test/java/com/mspoverlay/android/overlay/apply/OverlayApplyRequestFactoryTest.kt`
- `docs/index.md`
- `docs/logs/2026-06-05_step-12_start-stop-apply-flow.md`

## Verification

- `testDebugUnitTest` passed.
- `assembleDebug` passed.
- `assembleRelease` passed.

## Notes

- Build emitted deprecation warnings for AndroidX Security Crypto and `Notification.Builder.addAction`; these do not fail the build.
- Real end-to-end Google login still requires the server-side Android OAuth bridge endpoint.
- Real overlay display still requires testing on an Android device or emulator with `SYSTEM_ALERT_WINDOW` permission granted.
