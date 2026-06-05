# MSP Overlay Android 문서 라우터

## 1. 문서 목적

이 문서는 `msp-android` 구현 시 어떤 명세서를 먼저 보고, 어떤 순서로 작업해야 하는지 안내하는 상위 라우팅 문서다.

Android 클라이언트는 MSP Overlay 전체 시스템 중 **모바일 환경에서 오버레이를 불러와 전체 화면 위에 적용하는 실행 클라이언트** 역할을 담당한다.

기본 방향은 다음과 같다.

```text
서버 또는 라이브러리에서 overlay.json 조회
→ Android 앱에서 JSON 파싱/검증
→ 미리보기 또는 전체 화면 오버레이 렌더링
→ Notification에서 ON/OFF 제어
```

---

## 2. 전체 문서 목록

| 파일명 | 역할 |
|---|---|
| `AGENTS.md` | 전체 문서 라우팅 및 구현 순서 안내 |
| `01_android_overview_spec.md` | Android 클라이언트 전체 역할과 MVP 범위 |
| `02_android_environment_spec.md` | 개발 환경, SDK, 권한, 빌드 변형 기준 |
| `03_android_project_structure_spec.md` | 패키지 구조, 모듈 책임, 파일 구성 기준 |
| `04_android_auth_spec.md` | Google OAuth, JWT, 토큰 저장/갱신 명세 |
| `05_android_api_contract_spec.md` | 서버 API 연동 계약 및 요청/응답 기준 |
| `06_android_overlay_json_spec.md` | Android에서 처리할 overlay.json 구조 명세 |
| `07_android_renderer_spec.md` | rect/circle/line 렌더링, 좌표 스케일링, 색상/투명도 처리 |
| `08_android_overlay_service_spec.md` | SYSTEM_ALERT_WINDOW, Foreground Service, Notification 제어 |
| `09_android_ui_screen_spec.md` | Home, Discover, Library, Code Load, Settings 화면 명세 |
| `10_android_local_storage_spec.md` | 토큰/캐시/최근 적용 오버레이 로컬 저장 기준 |
| `11_android_error_handling_spec.md` | API/권한/JSON/네트워크 오류 처리 기준 |
| `12_android_test_plan_spec.md` | 기능 테스트, 렌더링 테스트, 통합 테스트 계획 |
| `13_android_implementation_order.md` | 실제 구현 단계별 작업 순서 |

---

## 3. 작업 목적별 참조 순서

## 3.1 Android 프로젝트를 처음 만들 때

먼저 아래 문서를 순서대로 확인한다.

1. `01_android_overview_spec.md`
2. `02_android_environment_spec.md`
3. `03_android_project_structure_spec.md`
4. `13_android_implementation_order.md`

확인할 내용:

- Android 클라이언트의 역할
- 사용 기술 스택
- 권한 구조
- 프로젝트 패키지 구조
- 1차 구현 범위

---

## 3.2 서버 API 연동을 할 때

참조 문서:

1. `05_android_api_contract_spec.md`
2. `04_android_auth_spec.md`
3. `10_android_local_storage_spec.md`
4. `11_android_error_handling_spec.md`

확인할 내용:

- API Base URL
- public API와 authenticated API 구분
- Bearer Token 부착 기준
- refresh token 갱신 흐름
- API 실패 시 처리 방식

---

## 3.3 오버레이 JSON을 처리할 때

참조 문서:

1. `06_android_overlay_json_spec.md`
2. `07_android_renderer_spec.md`
3. `11_android_error_handling_spec.md`

확인할 내용:

- 필수 JSON 필드
- Android에서 허용하는 platform 값
- rect/circle/line 구조
- 지원하지 않는 element 처리
- JSON 유효성 검증 실패 처리

---

## 3.4 전체 화면 오버레이를 구현할 때

참조 문서:

1. `08_android_overlay_service_spec.md`
2. `07_android_renderer_spec.md`
3. `02_android_environment_spec.md`

확인할 내용:

- SYSTEM_ALERT_WINDOW 권한
- Foreground Service 구조
- WindowManager.LayoutParams 기준
- Notification ON/OFF 액션
- 터치 통과 정책

---

## 3.5 화면 UI를 구현할 때

참조 문서:

1. `09_android_ui_screen_spec.md`
2. `05_android_api_contract_spec.md`
3. `04_android_auth_spec.md`

확인할 내용:

- Home 화면 구성
- Discover 목록/상세
- Code Load 흐름
- Library 조회/적용
- Settings 로그인/권한 상태

---

## 3.6 테스트할 때

참조 문서:

1. `12_android_test_plan_spec.md`
2. `11_android_error_handling_spec.md`
3. `07_android_renderer_spec.md`

확인할 내용:

- API 통신 테스트
- JSON 파싱 테스트
- 렌더링 좌표/투명도 테스트
- 권한 거부/네트워크 실패 테스트

---

## 4. 현재 기준 확정 범위

Android MVP에 포함하는 항목:

```text
- Android 프로젝트 기본 구성
- 서버 API 클라이언트 구성
- 코드 기반 오버레이 조회
- Discover 목록/상세 조회
- 로그인 사용자 라이브러리 조회
- overlay.json 파싱/검증
- rect/circle/line 미리보기 렌더링
- SYSTEM_ALERT_WINDOW 권한 처리
- Foreground overlay service 구성
- 전체 화면 오버레이 렌더링
- Notification 기반 ON/OFF 제어
```

Android MVP에서 제외하거나 보류하는 항목:

```text
- Android 내장 오버레이 편집기
- Android에서 overlay 업로드/수정
- 특정 게임 앱 자동 감지
- 접근성 서비스 기반 앱 추적
- image element 렌더링
- text element 렌더링
- iOS 지원
```

---

## 5. 구현 우선순위 요약

```text
1. 프로젝트 skeleton 생성
2. API client / 환경설정 구성
3. public API 연동
4. overlay.json model/parser/validator 구현
5. 앱 내부 preview renderer 구현
6. overlay permission flow 구현
7. foreground overlay service 구현
8. notification start/stop 제어 구현
9. auth/token/library 연동
10. 통합 테스트 및 운영 URL 적용
```

---

## 6. 핵심 원칙

- Android는 Windows처럼 특정 게임 창을 추적하지 않는다.
- Android MVP는 전체 화면 기준 오버레이 실행에 집중한다.
- 서버 API 형태는 현재 구현된 계약을 우선 유지한다.
- 공통 데이터 기준은 `overlay.json`이다.
- Android에서 적용 가능한 오버레이는 기본적으로 `platform == "android"`인 오버레이로 제한한다.
- public API 요청에는 stale token을 붙이지 않는다.
- 로그인 필요한 API에만 Bearer token을 붙인다.
