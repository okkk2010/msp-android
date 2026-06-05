# MSP Overlay Android API Contract Specification

## 1. 문서 목적

이 문서는 Android 클라이언트가 서버와 통신할 때 사용할 API 계약을 정의한다.

Android는 현재 서버 API 형태를 우선 유지하며, 서버 API 구조를 임의로 변경하지 않는다.

---

## 2. API Base URL

## 2.1 운영 서버

```text
https://api.msp-overlay.store
```

## 2.2 Android Emulator 로컬 서버

```text
http://10.0.2.2:8080
```

## 2.3 실제 단말 로컬 서버

```text
http://{개발PC_LOCAL_IP}:8080
```

---

## 3. API 호출 정책

## 3.1 Public API

토큰 없이 호출한다.

```text
GET /api/platforms
GET /api/games?platform=android
GET /api/overlays?page=0&size=20&platform=android
GET /api/overlays/{overlayId}
GET /api/overlays/code/{code}
```

## 3.2 Authenticated API

Bearer token을 붙여 호출한다.

```text
GET /api/auth/me
POST /api/auth/refresh
POST /api/auth/logout
GET /api/library
POST /api/library
DELETE /api/library/{overlayDatabaseId}
```

---

## 4. Platform API

## 4.1 플랫폼 목록 조회

```http
GET /api/platforms
```

용도:

```text
- Settings 또는 Discover 필터 구성
- Android platform 존재 여부 확인
```

Android는 응답 중 `slug == "android"`를 찾아 사용한다.

---

## 5. Game API

## 5.1 Android 게임/카테고리 조회

```http
GET /api/games?platform=android
```

용도:

```text
- Discover 필터
- Library 필터
- Android 오버레이 분류 표시
```

주의:

```text
서버에 Android game seed가 없다면 빈 목록이 반환될 수 있음
```

---

## 6. Overlay API

## 6.1 오버레이 목록 조회

```http
GET /api/overlays?page=0&size=20&platform=android
```

지원 쿼리:

```text
page
size
keyword
platform
game
code
sort
```

Android 기본 정책:

```text
Discover 기본 필터는 platform=android
```

## 6.2 오버레이 상세 조회

```http
GET /api/overlays/{overlayId}
```

현재 서버 기준:

```text
- 메타데이터 반환
- thumbnail 경로 반환
- overlay.json 파일 경로 또는 jsonPath 반환 가능
- overlayJson inline이 없을 수 있음
```

Android 처리:

```text
- overlayJson inline이 있으면 바로 파싱
- jsonPath가 있으면 API base URL과 결합해 static file로 가져옴
```

## 6.3 코드 기반 오버레이 조회

```http
GET /api/overlays/code/{code}
```

용도:

```text
6자리 코드로 오버레이를 바로 적용할 때 사용
```

현재 Android 적용에서는 이 API를 우선 사용한다.

기대 동작:

```text
- code 조회 성공
- overlayJson inline 또는 jsonPath 제공
- Android app에서 파싱 후 적용
```

---

## 7. Library API

## 7.1 내 라이브러리 조회

```http
GET /api/library
Authorization: Bearer {accessToken}
```

용도:

```text
로그인 사용자가 저장한 오버레이 목록 표시
```

## 7.2 라이브러리 저장

```http
POST /api/library
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "overlayId": 25
}
```

주의:

```text
여기서 overlayId는 ovl_xxx 같은 문자열 ID가 아니라 숫자 DB ID
```

## 7.3 라이브러리 삭제

```http
DELETE /api/library/{overlayDatabaseId}
Authorization: Bearer {accessToken}
```

---

## 8. Auth API

## 8.1 내 정보 조회

```http
GET /api/auth/me
Authorization: Bearer {accessToken}
```

용도:

```text
앱 시작 시 로그인 상태 복구
Settings 화면 사용자 정보 표시
```

## 8.2 토큰 갱신

```http
POST /api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "{refreshToken}"
}
```

## 8.3 로그아웃

```http
POST /api/auth/logout
Authorization: Bearer {accessToken}
```

---

## 9. Static File URL 처리

서버가 다음과 같은 상대 경로를 반환할 수 있다.

```text
/storage/overlays/{overlayId}/overlay.json
/storage/overlays/{overlayId}/thumbnail.png
```

Android는 API Base URL과 결합한다.

```text
https://api.msp-overlay.store/storage/overlays/{overlayId}/overlay.json
```

이미 절대 URL이면 그대로 사용한다.

---

## 10. 업로드/수정 API 처리 기준

현재 Android MVP에서는 업로드와 수정을 제외한다.

서버에는 아래 API가 존재한다.

```text
POST /api/overlays
PATCH /api/overlays/{overlayId}
DELETE /api/overlays/{overlayId}
```

Android에서 editor를 추가할 경우 별도 명세를 작성한다.

---

## 11. API Client 구성 기준

권장 구성:

```text
Retrofit
OkHttp
AuthInterceptor
TokenRefreshAuthenticator
PublicApiClient
AuthenticatedApiClient
```

단순화 구현도 가능하다.

필수 기준:

```text
- public API에는 Authorization 미부착
- authenticated API에는 Bearer token 부착
- TOKEN_EXPIRED 시 refresh 후 1회 재시도
- refresh 실패 시 세션 초기화
```

---

## 12. 완료 기준

API 연동 완료 기준:

```text
- GET /api/platforms 호출 가능
- GET /api/games?platform=android 호출 가능
- GET /api/overlays?platform=android 호출 가능
- GET /api/overlays/code/{code} 호출 가능
- jsonPath/static overlay.json 다운로드 가능
- 로그인 상태에서 GET /api/library 호출 가능
- TOKEN_EXPIRED 시 refresh 처리 가능
```
