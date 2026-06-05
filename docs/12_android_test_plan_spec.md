# MSP Overlay Android Test Plan Specification

## 1. 문서 목적

이 문서는 Android 클라이언트의 기능 테스트, 통합 테스트, 렌더링 테스트 기준을 정의한다.

---

## 2. 테스트 범위

Android MVP 테스트 범위:

```text
- API 연동 테스트
- 인증/토큰 테스트
- overlay.json 파싱/검증 테스트
- renderer 좌표/투명도 테스트
- overlay permission 테스트
- foreground service 테스트
- notification action 테스트
- library 조회 테스트
- 오류 처리 테스트
```

---

## 3. API 테스트

## 3.1 Public API

테스트 항목:

```text
GET /api/platforms
GET /api/games?platform=android
GET /api/overlays?platform=android
GET /api/overlays/{overlayId}
GET /api/overlays/code/{code}
```

검증 기준:

```text
- Authorization 헤더 없이 호출됨
- 응답 파싱 성공
- 실패 시 Error UI 표시
```

## 3.2 Authenticated API

테스트 항목:

```text
GET /api/auth/me
GET /api/library
POST /api/library
DELETE /api/library/{overlayDatabaseId}
```

검증 기준:

```text
- Bearer token이 포함됨
- token 만료 시 refresh 후 재시도됨
- refresh 실패 시 로그아웃 처리됨
```

---

## 4. Overlay JSON 테스트

## 4.1 정상 케이스

테스트 JSON:

```text
- rect 1개
- circle 1개
- line 1개
- platform android
- opacity 0.0 / 0.5 / 1.0
```

검증:

```text
- 파싱 성공
- validation 성공
- renderer 입력 모델 생성 성공
```

## 4.2 실패 케이스

```text
- platform windows
- canvas baseWidth 0
- opacity 1.5
- elements type image
- elements type text
- 잘못된 색상 문자열
- elements 배열 누락
```

검증:

```text
- 적용 전에 차단
- 사용자 메시지 표시
- 앱 crash 없음
```

---

## 5. Renderer 테스트

테스트 항목:

```text
- scaleX/scaleY 계산
- rect 좌표 변환
- circle 좌표 변환
- line 좌표 변환
- zIndex 정렬
- visible false 제외
- finalOpacity 계산
- strokeWidth scaling
- dashStyle 적용
- rotation 적용
```

검증 기준:

```text
- 기준 canvas와 화면 크기에 맞춰 위치가 조정됨
- zIndex가 낮은 요소가 먼저 그려짐
- 투명도가 전체 opacity와 요소 opacity의 곱으로 적용됨
```

---

## 6. Permission 테스트

## 6.1 Overlay 권한 없음

```text
권한 제거
→ Start Overlay 클릭
→ service 시작 차단
→ Permission Guide 표시
```

## 6.2 Overlay 권한 있음

```text
권한 허용
→ Start Overlay 클릭
→ service 시작
→ overlay 표시
```

---

## 7. Foreground Service 테스트

테스트 항목:

```text
- service 시작
- Notification 표시
- overlay view 추가
- Stop 버튼으로 service 종료
- 앱 task 제거 후 service 상태
- 화면 회전 후 overlay 유지
```

검증 기준:

```text
- service 실행 중 notification이 존재함
- Stop 후 overlay view가 제거됨
- crash 없이 상태가 정리됨
```

---

## 8. UI 테스트

## 8.1 Home

```text
- 권한 상태 표시
- 현재 오버레이 상태 표시
- Start/Stop 버튼 상태 변경
```

## 8.2 Code Load

```text
- 6자리 코드 validation
- 조회 성공 시 preview 표시
- invalid code 오류 표시
```

## 8.3 Discover

```text
- android platform 목록 조회
- 필터 변경
- empty state
- error state
```

## 8.4 Library

```text
- 비로그인 상태 로그인 안내
- 로그인 상태 목록 조회
- 저장된 오버레이 적용
```

---

## 9. 운영 전 점검

운영 빌드 전 확인:

```text
- release API base URL이 https://api.msp-overlay.store인지 확인
- cleartext traffic 비활성화
- debug 로그 제거
- OAuth callback deep link 정상 등록
- token 저장소 암호화 확인
- Android platform seed 데이터 존재 확인
- Android 테스트 overlay 1개 이상 존재 확인
```

---

## 10. 완료 기준

테스트 완료 기준:

```text
- public API 조회 정상
- 인증 API 조회 정상
- invalid token 처리 정상
- overlay json 정상/비정상 케이스 처리 완료
- preview renderer 정상
- overlay service start/stop 정상
- notification action 정상
- 권한 거부 상태 처리 정상
- 앱 crash 없이 오류 상태 표시
```
