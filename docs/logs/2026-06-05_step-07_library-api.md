# Step 07 - Library API Integration

## Commit Message

`step 07: add authenticated library api contracts`

## Scope

- Added authenticated API DTOs for current user, token refresh, and library items.
- Added Retrofit service contract for auth and library APIs.
- Added MockWebServer tests for library list, save, and delete calls.

## Changed Files

- `app/src/main/java/com/mspoverlay/android/feature/library/data/AuthenticatedApiDtos.kt`
- `app/src/main/java/com/mspoverlay/android/feature/library/data/AuthenticatedMspApi.kt`
- `app/src/test/java/com/mspoverlay/android/feature/library/data/AuthenticatedMspApiTest.kt`
- `docs/index.md`
- `docs/logs/2026-06-05_step-07_library-api.md`

## Verification

- `testDebugUnitTest` passed.
- `assembleDebug` passed.

## Decisions

- Library save uses the numeric overlay database ID.
- `DELETE /api/library/{overlayId}` uses the numeric overlay database ID path value.
- Authenticated calls use the authenticated Retrofit client so Bearer tokens are attached.
