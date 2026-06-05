# MSP Overlay Android Auth Specification

## 1. 문서 목적

이 문서는 Android 클라이언트의 Google OAuth 로그인, JWT 저장, 토큰 갱신, 로그아웃 흐름을 정의한다.

---

## 2. 현재 서버 인증 구조

서버는 다음 인증 구조를 가진다.

```text
Google OAuth 로그인
→ 서버에서 access token / refresh token 발급
→ API 요청 시 Authorization: Bearer {accessToken}
→ refresh token으로 access token 재발급
```

현재 서버 기준:

```text
access token 만료: 30분
refresh token 만료: 14일
```

---

## 3. Android OAuth 기본 방향

Android는 WebView 내부 로그인 방식을 사용하지 않는다.

권장 방식:

```text
System Browser 또는 Chrome Custom Tabs
```

사유:

```text
- Google 로그인 정책과 보안 흐름에 적합
- 사용자의 기존 브라우저 세션 활용 가능
- WebView 내 계정 입력보다 안전함
```

---

## 4. Android 전용 OAuth Bridge 필요

현재 서버에는 웹 로그인과 Windows 전용 로그인 흐름이 존재한다.

Android는 Windows의 localhost callback 방식을 그대로 사용하지 않는다.

권장 서버 추가 API:

```http
GET /api/auth/android/google/start?callbackUrl={appDeepLink}&state={randomState}
```

Android callback 예시:

```text
msp-overlay://auth/callback
```

성공 redirect 예시:

```text
msp-overlay://auth/callback?accessToken={token}&refreshToken={token}&state={state}
```

실패 redirect 예시:

```text
msp-overlay://auth/callback?error=oauth_login_failed&state={state}
```

---

## 5. Deep Link 설정

`AndroidManifest.xml`의 Activity에 deep link intent-filter를 등록한다.

```xml
<intent-filter>
    <action android:name="android.intent.action.VIEW" />
    <category android:name="android.intent.category.DEFAULT" />
    <category android:name="android.intent.category.BROWSABLE" />
    <data
        android:scheme="msp-overlay"
        android:host="auth"
        android:path="/callback" />
</intent-filter>
```

---

## 6. state 검증

OAuth 시작 전 Android 앱은 난수 state를 생성한다.

```text
1. cryptographically random state 생성
2. pending state를 로컬 임시 저장
3. OAuth 시작 URL에 state 포함
4. callback 수신
5. callback state와 pending state 비교
6. 불일치 시 로그인 실패 처리
```

state 검증 실패 시 토큰을 저장하지 않는다.

---

## 7. 토큰 저장

토큰은 일반 SharedPreferences에 평문 저장하지 않는다.

권장 저장소:

```text
EncryptedSharedPreferences
또는 암호화된 Jetpack DataStore
```

저장 값:

```text
accessToken
refreshToken
tokenType
accessTokenExpiresAt
refreshTokenExpiresAt
```

만료 시각을 서버가 내려주지 않는 경우 앱 수신 시간을 기준으로 계산한다.

---

## 8. API 요청 토큰 부착 기준

## 8.1 Public API

아래 public API에는 Authorization 헤더를 붙이지 않는다.

```text
GET /api/platforms
GET /api/games
GET /api/overlays
GET /api/overlays/{overlayId}
GET /api/overlays/code/{code}
```

사유:

```text
로컬에 오래된 access token이 남아 있어도 public 조회가 실패하지 않도록 하기 위함
```

## 8.2 Authenticated API

아래 API에는 Bearer token을 붙인다.

```text
GET /api/auth/me
POST /api/auth/logout
GET /api/library
POST /api/library
DELETE /api/library/{overlayDatabaseId}
```

헤더:

```http
Authorization: Bearer {accessToken}
```

---

## 9. Token Refresh 흐름

refresh API:

```http
POST /api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "{refreshToken}"
}
```

갱신 조건:

```text
- access token이 만료되었거나 곧 만료될 때
- authenticated API가 TOKEN_EXPIRED를 반환할 때
- 앱 시작 시 세션 복구가 필요할 때
```

갱신 실패 처리:

```text
- 저장된 토큰 삭제
- 로그인 상태 false 처리
- 사용자에게 재로그인 안내
```

---

## 10. 로그아웃

로그아웃 흐름:

```text
1. POST /api/auth/logout 호출
2. 성공 또는 실패와 무관하게 로컬 토큰 삭제
3. 사용자 정보 캐시 삭제
4. Library 화면 접근 제한
5. Home/Settings 화면 로그인 상태 갱신
```

서버 통신 실패 시에도 로컬 로그아웃은 진행한다.

---

## 11. 로그인 상태별 UI 정책

## 11.1 비로그인 상태 허용

```text
- Discover 조회
- 오버레이 상세 조회
- 코드로 오버레이 조회
- Android 적용 가능한 오버레이 실행
```

## 11.2 로그인 필요

```text
- 내 라이브러리 조회
- 라이브러리 저장
- 라이브러리 삭제
- 내 정보 조회
```

---

## 12. 완료 기준

인증 구현 완료 기준:

```text
- Android deep link callback 수신 가능
- state 검증 적용
- access/refresh token 보안 저장
- Authorization 헤더 자동 부착 구조 구현
- public API에는 Authorization 헤더 미부착
- access token 만료 시 refresh 후 1회 재시도
- refresh 실패 시 세션 초기화
- 로그아웃 시 로컬 토큰 삭제
```
