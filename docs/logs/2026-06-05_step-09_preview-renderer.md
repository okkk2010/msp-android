# Step 09 - Preview Renderer

## Commit Message

`step 09: add overlay preview renderer`

## Scope

- Added pure Kotlin render planning for overlay documents.
- Added color parsing for `#RRGGBB`, `#AARRGGBB`, `rgb()`, and `rgba()`.
- Added Android `OverlayPreviewView` for Canvas drawing.
- Added tests for scaling, z-index ordering, visible filtering, stroke scaling, and color parsing.

## Changed Files

- `app/src/main/java/com/mspoverlay/android/overlay/render/OverlayColor.kt`
- `app/src/main/java/com/mspoverlay/android/overlay/render/OverlayRenderPlan.kt`
- `app/src/main/java/com/mspoverlay/android/overlay/render/OverlayRenderPlanner.kt`
- `app/src/main/java/com/mspoverlay/android/overlay/render/OverlayPreviewView.kt`
- `app/src/test/java/com/mspoverlay/android/overlay/render/OverlayColorTest.kt`
- `app/src/test/java/com/mspoverlay/android/overlay/render/OverlayRenderPlannerTest.kt`
- `docs/index.md`
- `docs/logs/2026-06-05_step-09_preview-renderer.md`

## Verification

- Initial test run found an incorrect test expectation for stroke scaling.
- Corrected the expected value to match the Windows-compatible rule `strokeWidth * ((scaleX + scaleY) / 2)`.
- `testDebugUnitTest` passed.
- `assembleDebug` passed.

## Decisions

- Rendering math is separated into `OverlayRenderPlanner` so it can be tested without Android framework dependencies.
- `OverlayPreviewView` consumes the render plan for Android Canvas drawing.
