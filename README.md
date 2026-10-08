# Learning Dashboard (Android – Kotlin, Jetpack Compose, MVVM)

Login → Course Dashboard → Course Details, with offline cache and lesson completion.
Login: any valid email + password of 6+ chars (`wrong@test.com` simulates a failed login).
Mock API: `FakeCourseApi` (`mode = NORMAL | EMPTY | FAIL` to demo empty/error states). It also
throws when the device is offline, so airplane mode exercises the real offline path.
Tests: `./gradlew test`.

## 1. Architecture
MVVM with a repository layer: `Compose UI → ViewModel (StateFlow) → Repository (interface in domain) → API + Room`.
UI is stateless and renders one immutable state per screen, so loading/success/empty/error
can't be mixed up. Repositories are interfaces, so ViewModels are testable with fakes and the mock
API can be swapped for Retrofit without touching UI. Full multi-module Clean Architecture with
use-case classes would be over-engineering for 3 screens; the domain package holds pure logic
(`ProgressCalculator`, `InputValidator`) and can grow into that.

## 2. Offline Support
Room is the single source of truth; the UI only observes DB `Flow`s. On launch, `refresh()` fetches
from the API and upserts into Room in one transaction. If it fails, cached data stays on screen
with a "showing saved courses" banner. Lesson completion is written to Room, and course progress is
*derived* from the completed-lesson count in the SQL query, so it can't drift. Refresh never
overwrites local lesson state.

## 3. Security
Access token in memory; refresh token in Android Keystore-backed storage (EncryptedSharedPreferences,
or DataStore encrypted with a Keystore AES key), never plain SharedPreferences. Short-lived access
tokens, OkHttp Authenticator for refresh, `allowBackup=false`, certificate pinning, and wipe
tokens + DB on logout.

## 4. Scale (1M users, hundreds of courses)
1. Paginated, delta-synced APIs (Paging 3 + RemoteMediator, ETags / `updatedSince`) instead of full-list fetches.
2. Fetch lessons lazily per course; CDN + HTTP caching for static catalog data.
3. Sync lesson completions to the server via WorkManager (queued, retried, idempotent) with conflict rules.
4. Hilt for DI, feature modules for build times and team ownership, Room indices/migrations with tests.
5. Observability: Crashlytics, performance tracing, analytics; staged rollouts and feature flags.

