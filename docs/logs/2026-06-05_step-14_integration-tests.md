# Step 14 - Integration Tests

## Commit Message

`step 14: add overlay apply integration test`

## Scope

- Added a JVM integration test covering the MVP apply path.
- The test covers code load, stale-token-free public API request, apply request validation, local cache save/load, JSON parser, and render plan creation.

## Changed Files

- `app/src/test/java/com/mspoverlay/android/integration/OverlayApplyIntegrationTest.kt`
- `docs/index.md`
- `docs/logs/2026-06-05_step-14_integration-tests.md`

## Verification

- `testDebugUnitTest` passed.
- `assembleDebug` passed.

## Decisions

- Integration test uses MockWebServer, not the live server, so it is deterministic.
- Public code-load requests must stay Authorization-free even when a stale token exists locally.
