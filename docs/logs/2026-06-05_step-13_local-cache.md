# Step 13 - Local Cache

## Commit Message

`step 13: add overlay local cache`

## Scope

- Added local cache contract for the last applied overlay.
- Added file-backed cache implementation.
- Added tests for save/load/clear behavior.

## Changed Files

- `app/src/main/java/com/mspoverlay/android/overlay/cache/OverlayCache.kt`
- `app/src/test/java/com/mspoverlay/android/overlay/cache/FileOverlayCacheTest.kt`
- `docs/index.md`
- `docs/logs/2026-06-05_step-13_local-cache.md`

## Verification

- `testDebugUnitTest` passed.
- `assembleDebug` passed.

## Decisions

- Cache stores `overlayJson` separately from simple metadata.
- MVP cache tracks only the last applied overlay.
