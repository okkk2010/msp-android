# MSP Overlay Android UI Screen Specification

## 1. 문서 목적

이 문서는 MSP Overlay Android 앱의 MVP 화면 구성과 각 화면의 기능을 정의한다.

---

## 2. 화면 목록

Android MVP 화면은 다음으로 구성한다.

```text
Home
Code Load
Discover
Overlay Detail
Library
Settings
Permission Guide
```

---

## 3. Home Screen

## 3.1 역할

앱의 시작 화면이며 현재 오버레이 상태를 보여준다.

## 3.2 표시 정보

```text
- Overlay permission 상태
- Notification permission 상태
- 현재 로그인 상태
- 현재 적용 중인 오버레이 이름
- 현재 overlay service 실행 여부
- 최근 적용 오버레이 요약
```

## 3.3 주요 액션

```text
- Start Overlay
- Stop Overlay
- Load by Code 이동
- Discover 이동
- Library 이동
- Settings 이동
```

## 3.4 상태별 버튼 정책

| 상태 | Start Overlay | Stop Overlay |
|---|---|---|
| 권한 없음 | 비활성 / 권한 안내 | 비활성 |
| 적용 오버레이 없음 | 비활성 | 비활성 |
| 오버레이 준비됨 | 활성 | 비활성 |
| Service 실행 중 | 비활성 | 활성 |

---

## 4. Code Load Screen

## 4.1 역할

6자리 코드를 입력해 오버레이를 바로 조회하고 적용한다.

## 4.2 UI 요소

```text
- 6자리 코드 입력 필드
- 조회 버튼
- 조회 결과 카드
- 미리보기 영역
- Apply 버튼
```

## 4.3 입력 규칙

```text
대문자 영문 + 숫자 6자리
정규식: ^[A-Z0-9]{6}$
```

## 4.4 흐름

```text
코드 입력
→ 입력값 검증
→ GET /api/overlays/code/{code}
→ overlay json 검증
→ platform == android 확인
→ 미리보기 표시
→ Apply
```

---

## 5. Discover Screen

## 5.1 역할

공개 오버레이 목록을 탐색한다.

## 5.2 기본 필터

```text
platform=android
```

## 5.3 UI 요소

```text
- 검색어 입력
- 플랫폼 필터
- 게임 필터
- 정렬 필터
- overlay card list
- loading state
- empty state
- error state
```

## 5.4 Overlay Card 표시 정보

```text
- 썸네일
- 오버레이 이름
- 설명 일부
- platform
- game
- code
- 작성자
```

## 5.5 액션

```text
- 상세 보기
- 라이브러리 저장
- Android 적용 가능 시 Apply
```

로그인이 필요한 액션:

```text
라이브러리 저장
```

---

## 6. Overlay Detail Screen

## 6.1 역할

오버레이 상세 정보와 미리보기를 표시한다.

## 6.2 표시 정보

```text
- 이름
- 설명
- code
- platform
- game
- 썸네일
- canvas size
- opacity
- element count
- createdAt / updatedAt
```

## 6.3 액션

```text
- Apply
- Save to Library
- Back to List
```

## 6.4 적용 제한

```text
platform != android이면 Apply 비활성
```

---

## 7. Library Screen

## 7.1 역할

로그인 사용자가 저장한 오버레이 목록을 조회하고 적용한다.

## 7.2 접근 조건

```text
로그인 필요
```

비로그인 상태:

```text
로그인 안내 화면 표시
```

## 7.3 UI 요소

```text
- 저장된 오버레이 목록
- 검색 필터
- platform/game 필터
- 상세 이동
- Apply 버튼
- Remove 버튼
```

## 7.4 흐름

```text
GET /api/library
→ 목록 표시
→ 오버레이 선택
→ 상세 또는 jsonPath 조회
→ overlay json 검증
→ Apply
```

---

## 8. Settings Screen

## 8.1 역할

앱 설정과 계정/권한 상태를 관리한다.

## 8.2 표시 정보

```text
- 로그인 사용자 정보
- 로그인/로그아웃 버튼
- API Base URL(debug only)
- Overlay permission 상태
- Notification permission 상태
- 현재 token/session 상태
- 앱 버전
```

## 8.3 액션

```text
- Google Login
- Logout
- Overlay permission 설정 이동
- Notification permission 요청
- 캐시 삭제
```

---

## 9. Permission Guide Screen

## 9.1 역할

오버레이 권한이 없는 사용자에게 왜 권한이 필요한지 설명한다.

## 9.2 포함 내용

```text
- 이 앱이 다른 앱 위에 오버레이를 띄우는 이유
- 권한 허용 방법
- 권한 허용 후 앱으로 돌아와야 한다는 안내
- 설정 화면 이동 버튼
```

---

## 10. 공통 UI 상태

모든 API 화면은 다음 상태를 가진다.

```text
Idle
Loading
Success
Empty
Error
```

Error 상태에는 재시도 버튼을 제공한다.

---

## 11. 완료 기준

UI 완료 기준:

```text
- Home에서 현재 오버레이 상태 확인 가능
- Code Load에서 6자리 코드 조회 가능
- Discover에서 Android 오버레이 목록 확인 가능
- Detail에서 적용 가능 여부 표시 가능
- Library에서 로그인 필요 상태와 목록 상태 구분 가능
- Settings에서 로그인/권한/캐시 관리 가능
- 모든 API 화면에 loading/error/empty 상태 존재
```
