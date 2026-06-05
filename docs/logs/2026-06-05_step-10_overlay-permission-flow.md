# Step 10 - Overlay Permission Flow

## Commit Message

`step 10: add overlay permission flow`

## Scope

- Added overlay permission state model.
- Added start decision policy for overlay and notification permissions.
- Added Android permission controller for `SYSTEM_ALERT_WINDOW` and notification permission checks.
- Added overlay settings intent creation.
- Added unit tests for permission-based start blocking.

## Changed Files

- `app/src/main/java/com/mspoverlay/android/overlay/permission/OverlayPermissionState.kt`
- `app/src/main/java/com/mspoverlay/android/overlay/permission/OverlayPermissionController.kt`
- `app/src/test/java/com/mspoverlay/android/overlay/permission/OverlayPermissionPolicyTest.kt`
- `docs/index.md`
- `docs/logs/2026-06-05_step-10_overlay-permission-flow.md`

## Verification

- `testDebugUnitTest` passed.
- `assembleDebug` passed.

## Decisions

- Overlay service start is blocked when `SYSTEM_ALERT_WINDOW` is missing.
- On Android 13 and later, notification permission is also required before starting the foreground overlay service.
