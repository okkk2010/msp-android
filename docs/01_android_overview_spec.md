# MSP Overlay Android Overview Specification

## 1. 문서 목적

이 문서는 MSP Overlay Android 클라이언트의 전체 역할, 기능 범위, MVP 기준을 정의한다.

Android 클라이언트는 웹 에디터 또는 Windows 클라이언트에서 만들어진 오버레이 데이터를 서버에서 가져와, Android 디바이스 전체 화면 위에 렌더링하는 실행 클라이언트다.

---

## 2. 시스템 내 역할

MSP Overlay 전체 시스템은 다음 구성으로 동작한다.

```text
Web Frontend
Backend Server
Windows Client
Android Client
Database
File Storage
Overlay JSON Format
```

Android Client는 이 중 다음 역할을 담당한다.

```text
- Android 플랫폼용 오버레이 조회
- 6자리 코드 기반 오버레이 로드
- 로그인 사용자 라이브러리 조회
- overlay.json 파싱/검증
- Android 화면 기준 오버레이 미리보기
- 전체 화면 오버레이 서비스 실행
- Notification 기반 ON/OFF 제어
```

---

## 3. Android 동작 방식

Windows 클라이언트는 특정 게임 프로그램 창을 추적해 오버레이를 맞추는 구조다.

반면 Android는 특정 게임 창을 안정적으로 추적하기 어렵기 때문에 MVP에서는 아래 방식으로 설계한다.

```text
현재 디바이스 화면 전체 크기
→ overlay.json의 canvas 기준 크기와 비교
→ scaleX / scaleY 계산
→ 전체 화면 위에 오버레이 렌더링
```

즉, Android의 기준은 **선택한 앱 창**이 아니라 **디바이스 전체 화면**이다.

---

## 4. Android MVP 핵심 흐름

## 4.1 코드 기반 적용

```text
사용자 6자리 코드 입력
→ GET /api/overlays/code/{code}
→ overlayJson 수신
→ Android 적용 가능 여부 검증
→ 오버레이 미리보기
→ Start Overlay
→ 전체 화면 오버레이 표시
```

## 4.2 라이브러리 기반 적용

```text
사용자 로그인
→ GET /api/library
→ 저장된 오버레이 목록 표시
→ 오버레이 선택
→ 상세 또는 jsonPath 조회
→ overlay.json 파싱
→ Start Overlay
```

## 4.3 Discover 기반 적용

```text
GET /api/overlays?platform=android
→ 목록 표시
→ 상세 조회
→ 저장 또는 적용
```

---

## 5. MVP 포함 범위

Android 1차 MVP에는 다음을 포함한다.

```text
1. Android 프로젝트 기본 구성
2. API Base URL 환경 설정
3. 서버 public API 연동
4. 코드 기반 오버레이 조회
5. Discover 목록/상세 조회
6. Google OAuth/JWT 연동 준비
7. 로그인 사용자 라이브러리 조회
8. overlay.json 파싱
9. overlay.json 유효성 검증
10. rect/circle/line 렌더링
11. 앱 내부 preview renderer
12. SYSTEM_ALERT_WINDOW 권한 요청/확인
13. Foreground Service 기반 overlay service
14. Notification 기반 start/stop 제어
15. 최근 적용 오버레이 캐시
```

---

## 6. MVP 제외 범위

아래 기능은 Android MVP에서 제외한다.

```text
1. Android 내장 오버레이 편집기
2. Android에서 overlay 업로드
3. Android에서 overlay 수정/삭제
4. 특정 게임 앱 자동 감지
5. 접근성 서비스 기반 앱 추적
6. 사용량 접근 권한 기반 게임 감지
7. image element 렌더링
8. text element 렌더링
9. 복잡한 scaleMode
10. 좋아요/댓글/신고/추천
11. 다운로드 통계
12. iOS 지원
```

---

## 7. 주요 설계 제약

## 7.1 platform 제한

Android에서 실제 적용 가능한 오버레이는 기본적으로 다음 조건을 만족해야 한다.

```text
overlayJson.platform == "android"
```

Windows용 오버레이는 목록에서 볼 수 있더라도 Android overlay service에 바로 적용하지 않는다.

## 7.2 지원 element 제한

Android MVP에서 지원하는 요소는 다음 3개다.

```text
rect
circle
line
```

지원하지 않는 요소가 포함된 경우 오버레이 시작 전에 사용자에게 오류를 표시한다.

## 7.3 터치 정책

MVP의 오버레이는 사용자의 게임 조작을 방해하지 않아야 한다.

따라서 기본 상태는 다음 정책을 따른다.

```text
- 터치 입력을 소비하지 않음
- overlay view는 focus를 가져가지 않음
- 게임/앱 조작은 그대로 통과
```

---

## 8. 완료 기준

Android 1차 구현은 아래 조건을 만족하면 완료로 본다.

```text
- Android 앱에서 서버 API Base URL을 설정할 수 있다.
- Android 플랫폼 오버레이 목록을 조회할 수 있다.
- 6자리 코드로 오버레이를 조회할 수 있다.
- overlay.json을 파싱하고 검증할 수 있다.
- rect/circle/line을 앱 화면에서 미리보기 렌더링할 수 있다.
- 오버레이 표시 권한 요청 흐름이 동작한다.
- Foreground Service가 실행 중일 때 Notification이 표시된다.
- Notification 또는 앱 버튼으로 오버레이 ON/OFF가 가능하다.
- 전체 화면 위에 오버레이가 표시된다.
- 로그인 후 라이브러리 목록 조회가 가능하다.
```
