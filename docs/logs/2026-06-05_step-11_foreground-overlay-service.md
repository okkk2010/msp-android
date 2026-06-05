# Step 11 - Foreground Overlay Service

## Commit Message

`step 11: add foreground overlay service`

## Scope

- Added foreground `OverlayService`.
- Added notification channel and persistent notification with Stop action.
- Added `OverlayWindowController` to attach/remove the full-screen overlay view.
- Added `WindowManager.LayoutParams` factory for passive full-screen overlays.
- Added manifest service declaration and foreground service special-use permission.
- Added tests for the passive full-screen window spec.

## Changed Files

- `app/src/main/AndroidManifest.xml`
- `app/src/main/res/values/strings.xml`
- `app/src/main/java/com/mspoverlay/android/overlay/service/OverlayWindowSpec.kt`
- `app/src/main/java/com/mspoverlay/android/overlay/service/OverlayWindowLayoutParamsFactory.kt`
- `app/src/main/java/com/mspoverlay/android/overlay/service/OverlayWindowController.kt`
- `app/src/main/java/com/mspoverlay/android/overlay/service/OverlayService.kt`
- `app/src/test/java/com/mspoverlay/android/overlay/service/OverlayWindowSpecFactoryTest.kt`
- `docs/index.md`
- `docs/logs/2026-06-05_step-11_foreground-overlay-service.md`

## Verification

- `testDebugUnitTest` passed.
- `assembleDebug` passed.

## Decisions

- MVP overlay window is full-screen and passive.
- Passive mode uses non-focusable and non-touchable window flags so it does not steal game/app input.
- The service starts foreground before attaching the overlay view.
