# MSP Overlay Android Implementation Order

## 1. 문서 목적

이 문서는 Android 클라이언트 구현을 실제 작업 단위로 나눈 개발 순서를 정의한다.

---

## 2. 전체 구현 순서

```text
1. Android 프로젝트 생성
2. 환경 설정/빌드 변형 구성
3. 패키지 구조 생성
4. API client 구성
5. public API 연동
6. overlay.json model/parser/validator 구현
7. preview renderer 구현
8. permission guide 구현
9. overlay foreground service 구현
10. notification start/stop 구현
11. auth/token 저장 구현
12. library API 연동
13. local cache 구현
14. 통합 테스트
15. 운영 빌드 설정
```

---

## 3. 1단계: 프로젝트 생성

작업:

```text
- Android Studio 프로젝트 생성
- package name 확정
- minSdk 26 설정
- targetSdk 최신 stable 설정
- Kotlin/Compose 또는 Java/XML 선택 확정
```

산출물:

```text
app/build.gradle
settings.gradle
MainActivity
```

완료 기준:

```text
앱이 emulator 또는 실제 단말에서 실행됨
```

---

## 4. 2단계: 환경 설정 구성

작업:

```text
- debug/release build variant 구성
- API_BASE_URL 분리
- debug cleartext 허용
- release cleartext 차단
```

완료 기준:

```text
emulator에서 http://10.0.2.2:8080 접근 가능
release에서 https://api.msp-overlay.store 사용
```

---

## 5. 3단계: 패키지 구조 생성

작업:

```text
core/data/domain/ui/overlay 패키지 생성
API 인터페이스 위치 정의
overlay parser/renderer/service 위치 정의
```

완료 기준:

```text
기본 파일 구조가 명세와 일치
```

---

## 6. 4단계: API Client 구성

작업:

```text
- Retrofit/OkHttp 구성
- 공통 ApiResult 정의
- public API client 구성
- authenticated API client 구성 준비
```

완료 기준:

```text
GET /api/platforms 호출 성공
```

---

## 7. 5단계: Public API 연동

작업:

```text
- platforms 조회
- games 조회
- overlays 목록 조회
- overlay 상세 조회
- code 조회
```

완료 기준:

```text
Discover와 Code Load 화면에서 서버 데이터 확인 가능
```

---

## 8. 6단계: overlay.json model/parser/validator

작업:

```text
- OverlayDocument 모델 생성
- Rect/Circle/Line 모델 생성
- JSON parser 구현
- Android 적용 가능 여부 검증
```

완료 기준:

```text
overlay.json을 내부 모델로 변환하고 invalid JSON을 차단 가능
```

---

## 9. 7단계: Preview Renderer

작업:

```text
- 앱 내부 preview canvas 구현
- scaleX/scaleY 계산
- rect/circle/line 렌더링
- opacity/zIndex/visible 반영
```

완료 기준:

```text
Code Load 또는 Detail 화면에서 오버레이 미리보기 가능
```

---

## 10. 8단계: Permission Guide

작업:

```text
- SYSTEM_ALERT_WINDOW 권한 상태 확인
- 권한 안내 화면 구현
- 설정 화면 이동 Intent 구현
- 앱 복귀 후 권한 재확인
```

완료 기준:

```text
권한 없을 때 overlay service 시작이 차단되고 안내 화면 표시
```

---

## 11. 9단계: Overlay Foreground Service

작업:

```text
- OverlayService 구현
- OverlayWindowController 구현
- WindowManager에 full-screen view 추가
- OverlayRendererView 연결
```

완료 기준:

```text
Start Overlay 클릭 시 전체 화면 위에 오버레이 표시
```

---

## 12. 10단계: Notification 제어

작업:

```text
- Notification Channel 생성
- Foreground notification 표시
- Stop action 구현
- 앱 열기 action 구현
```

완료 기준:

```text
Notification Stop 클릭 시 overlay service 종료
```

---

## 13. 11단계: Auth/Token 구현

작업:

```text
- Android OAuth bridge 준비
- deep link callback 처리
- state 검증
- token 암호화 저장
- auth/me 호출
- refresh token 처리
```

완료 기준:

```text
로그인 후 사용자 정보 조회 가능
```

---

## 14. 12단계: Library 연동

작업:

```text
- GET /api/library
- POST /api/library
- DELETE /api/library/{overlayDatabaseId}
- Library 화면 표시
- 저장 오버레이 Apply
```

완료 기준:

```text
로그인 사용자의 저장 오버레이를 Android에서 적용 가능
```

---

## 15. 13단계: Local Cache 구현

작업:

```text
- 최근 적용 overlay 저장
- service에 cacheKey 전달
- 앱 재실행 후 최근 오버레이 표시
- Settings에서 cache 삭제
```

완료 기준:

```text
네트워크 없이도 최근 적용 오버레이를 다시 불러올 수 있음
```

---

## 16. 14단계: 통합 테스트

작업:

```text
- API 통합 테스트
- 권한 테스트
- service start/stop 테스트
- renderer 테스트
- invalid JSON 테스트
- token 만료 테스트
```

완료 기준:

```text
MVP 주요 흐름이 crash 없이 동작
```

---

## 17. 15단계: 운영 빌드 설정

작업:

```text
- release API URL 확인
- cleartext traffic 비활성화
- debug log 제거
- signing 설정
- OAuth redirect URI 운영 등록 확인
```

완료 기준:

```text
운영 서버 기준 Android 앱 실행 가능
```
