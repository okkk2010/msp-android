# MSP Overlay Android Project Structure Specification

## 1. 문서 목적

이 문서는 Android 클라이언트의 패키지 구조, 계층 분리, 주요 파일 역할을 정의한다.

---

## 2. 구조 설계 원칙

Android 앱은 다음 원칙을 따른다.

```text
- UI와 데이터 통신을 분리한다.
- overlay rendering 로직은 화면 UI와 분리한다.
- 서버 API 모델과 앱 내부 도메인 모델을 구분한다.
- overlay service는 앱 화면과 독립적으로 실행 가능해야 한다.
- parser/validator/renderer는 테스트 가능하게 분리한다.
```

---

## 3. 권장 패키지 구조

```text
com.mspoverlay.android
 ┣ app
 ┃ ┣ MainActivity.kt
 ┃ ┣ MspOverlayApplication.kt
 ┃ ┗ AppNavHost.kt
 ┣ core
 ┃ ┣ config
 ┃ ┣ result
 ┃ ┣ error
 ┃ ┗ util
 ┣ data
 ┃ ┣ api
 ┃ ┣ dto
 ┃ ┣ local
 ┃ ┣ mapper
 ┃ ┗ repository
 ┣ domain
 ┃ ┣ auth
 ┃ ┣ overlay
 ┃ ┣ library
 ┃ ┗ platform
 ┣ overlay
 ┃ ┣ model
 ┃ ┣ parser
 ┃ ┣ validator
 ┃ ┣ renderer
 ┃ ┣ service
 ┃ ┗ notification
 ┣ ui
 ┃ ┣ home
 ┃ ┣ discover
 ┃ ┣ detail
 ┃ ┣ codeload
 ┃ ┣ library
 ┃ ┣ settings
 ┃ ┗ common
 ┗ di
```

---

## 4. 계층별 책임

## 4.1 app

앱 시작점과 네비게이션을 담당한다.

주요 파일:

```text
MainActivity.kt
MspOverlayApplication.kt
AppNavHost.kt
```

책임:

```text
- Activity 시작
- 앱 전역 초기화
- Compose Navigation 구성
- 권한 상태 확인 진입점 제공
```

---

## 4.2 core

공통 유틸리티와 공통 결과 타입을 관리한다.

예시:

```text
ApiResult
UiState
AppError
DateTimeParser
ColorParser
```

---

## 4.3 data

서버 API, 로컬 저장소, DTO, Repository 구현을 담당한다.

구조:

```text
data/api
 - AuthApi.kt
 - OverlayApi.kt
 - LibraryApi.kt
 - PlatformApi.kt

data/dto
 - OverlaySummaryDto.kt
 - OverlayDetailDto.kt
 - LibraryItemDto.kt
 - AuthTokenDto.kt

data/local
 - TokenDataSource.kt
 - OverlayCacheDataSource.kt
 - AppPreferencesDataSource.kt

data/repository
 - AuthRepositoryImpl.kt
 - OverlayRepositoryImpl.kt
 - LibraryRepositoryImpl.kt
```

---

## 4.4 domain

앱 내부에서 사용하는 핵심 모델과 use case를 관리한다.

예시:

```text
domain/overlay
 - Overlay.kt
 - OverlayElement.kt
 - GetOverlayByCodeUseCase.kt
 - ValidateOverlayUseCase.kt

domain/auth
 - AuthSession.kt
 - LoginUseCase.kt
 - RefreshTokenUseCase.kt
```

---

## 4.5 overlay

오버레이 실행과 렌더링에 직접 관련된 로직을 담당한다.

구조:

```text
overlay/model
 - OverlayDocument.kt
 - RectElement.kt
 - CircleElement.kt
 - LineElement.kt

overlay/parser
 - OverlayJsonParser.kt

overlay/validator
 - OverlayValidator.kt

overlay/renderer
 - OverlayRendererView.kt
 - OverlayPainter.kt
 - CoordinateScaler.kt
 - PaintFactory.kt

overlay/service
 - OverlayService.kt
 - OverlayWindowController.kt

overlay/notification
 - OverlayNotificationFactory.kt
 - OverlayNotificationActionReceiver.kt
```

---

## 4.6 ui

사용자 화면을 담당한다.

화면 단위:

```text
home
 - HomeScreen.kt
 - HomeViewModel.kt

discover
 - DiscoverScreen.kt
 - DiscoverViewModel.kt

detail
 - OverlayDetailScreen.kt
 - OverlayDetailViewModel.kt

codeload
 - CodeLoadScreen.kt
 - CodeLoadViewModel.kt

library
 - LibraryScreen.kt
 - LibraryViewModel.kt

settings
 - SettingsScreen.kt
 - SettingsViewModel.kt
```

---

## 5. 주요 데이터 흐름

## 5.1 코드 조회 흐름

```text
CodeLoadScreen
→ CodeLoadViewModel
→ GetOverlayByCodeUseCase
→ OverlayRepository
→ OverlayApi.getOverlayByCode()
→ OverlayJsonParser
→ OverlayValidator
→ PreviewRenderer / OverlayService
```

## 5.2 라이브러리 조회 흐름

```text
LibraryScreen
→ LibraryViewModel
→ LibraryRepository
→ LibraryApi.getLibrary()
→ DTO Mapper
→ UI 표시
```

## 5.3 오버레이 서비스 시작 흐름

```text
HomeScreen 또는 DetailScreen
→ StartOverlayUseCase
→ OverlayValidator
→ OverlayService Intent 전달
→ OverlayWindowController
→ OverlayRendererView
```

---

## 6. ViewModel 책임 기준

ViewModel은 다음 역할만 담당한다.

```text
- UI 상태 관리
- UseCase 호출
- Loading/Error 상태 노출
- 화면 이벤트 처리
```

ViewModel에서 직접 하지 않는 것:

```text
- JSON 파싱 세부 로직
- Retrofit 직접 호출
- WindowManager 직접 제어
- Paint/Canvas 렌더링 처리
```

---

## 7. Repository 책임 기준

Repository는 다음 역할을 담당한다.

```text
- API 호출
- 로컬 캐시 조회/저장
- DTO와 Domain 모델 변환
- 인증 토큰 필요한 API 구분
```

---

## 8. 완료 기준

프로젝트 구조 완료 기준:

```text
- 패키지가 data/domain/ui/overlay/core로 분리됨
- API 인터페이스가 data/api에 분리됨
- overlay parser/renderer/service가 UI와 분리됨
- ViewModel이 Repository 또는 UseCase를 통해서만 데이터 접근
- OverlayService가 독립적으로 시작/중지 가능
```
