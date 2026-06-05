# MSP Overlay Android Environment Specification

## 1. 문서 목적

이 문서는 MSP Overlay Android 클라이언트 개발에 필요한 환경, SDK, 권한, 빌드 설정 기준을 정의한다.

---

## 2. 기술 스택

## 2.1 권장 스택

| 구분 | 권장안 |
|---|---|
| 언어 | Kotlin 권장 |
| UI | Jetpack Compose 권장 |
| Overlay 렌더링 | Custom View 또는 Compose Canvas |
| 네트워크 | Retrofit + OkHttp |
| JSON | Kotlinx Serialization 또는 Moshi |
| 이미지 로딩 | Coil |
| 토큰 저장 | EncryptedSharedPreferences 또는 암호화된 DataStore |
| 장기 실행 | Foreground Service |

## 2.2 제안서 기준 스택과의 차이

기존 제안서에서는 Android Platform을 `Java, Android Studio, Gradle` 기반으로 정의했다.

현재 Android 환경 명세에서는 유지보수성과 최신 Android 개발 흐름을 고려해 Kotlin과 Jetpack Compose를 권장한다.

최종 선택 기준:

```text
- 학교 제안서/기존 작성 내용과 일치 우선: Java
- 구현 편의성과 최신 Android 구조 우선: Kotlin + Compose
```

이 문서에서는 **Kotlin + Compose 권장안**을 기준으로 상세 명세를 작성한다.

---

## 3. SDK 기준

## 3.1 minSdk

권장값:

```text
minSdk 26
```

사유:

```text
TYPE_APPLICATION_OVERLAY가 API 26 이상에서 안정적으로 사용 가능
현대 Android 권한/Notification 정책 대응에 적합
```

## 3.2 targetSdk

권장값:

```text
현재 Android Studio에서 사용 가능한 최신 stable SDK
```

주의:

- targetSdk가 높아질수록 Foreground Service와 Notification 권한 요구가 엄격해진다.
- Android 13 이상에서는 POST_NOTIFICATIONS 권한 처리가 필요하다.

---

## 4. 필수 권한

`AndroidManifest.xml`에 다음 권한을 선언한다.

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
```

Android 13 이상 대응을 위해 아래 권한도 고려한다.

```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

---

## 5. Overlay 권한 정책

Android에서 다른 앱 위에 오버레이를 표시하려면 사용자가 직접 권한을 허용해야 한다.

앱은 다음 흐름을 제공해야 한다.

```text
앱 실행
→ Settings.canDrawOverlays(context) 확인
→ 권한 없음
→ 사용자에게 설명 화면 표시
→ ACTION_MANAGE_OVERLAY_PERMISSION 이동
→ 사용자가 권한 허용
→ 앱 복귀 후 재확인
```

권한이 없는 상태에서는 overlay service를 시작하지 않는다.

---

## 6. Notification 정책

Foreground Service 실행 중에는 지속 Notification을 표시해야 한다.

Notification에는 최소 다음 정보가 포함된다.

```text
- 현재 오버레이 실행 중 여부
- Stop Overlay 액션
- 앱 열기 액션
```

추가 가능 액션:

```text
- Toggle Overlay
- Change Overlay
- Settings
```

---

## 7. 개발 서버 접근 기준

## 7.1 Android Emulator에서 로컬 서버 접근

PC의 localhost 서버에 Android Emulator에서 접근할 때는 다음 주소를 사용한다.

```text
http://10.0.2.2:8080
```

## 7.2 실제 단말에서 로컬 서버 접근

실제 Android 단말은 개발 PC와 같은 네트워크에 있어야 한다.

예시:

```text
http://192.168.x.x:8080
```

이때 서버 방화벽과 네트워크 접근 허용 상태를 확인해야 한다.

## 7.3 운영 서버 접근

운영 API 기본값:

```text
https://api.msp-overlay.store
```

---

## 8. Build Variant 기준

## 8.1 debug

용도:

```text
로컬 개발 및 테스트
```

허용:

```text
- API Base URL 변경 가능
- cleartext HTTP 허용 가능
- 개발용 로그 출력 가능
```

debug manifest 예시:

```xml
<application
    android:usesCleartextTraffic="true" />
```

## 8.2 release

용도:

```text
배포용 빌드
```

정책:

```text
- API Base URL은 운영 서버로 고정
- cleartext HTTP 비허용
- 민감 로그 제거
- debuggable false
```

---

## 9. 환경 설정 값

Android 앱은 아래 값을 환경 설정으로 분리한다.

```text
API_BASE_URL
OAUTH_ANDROID_CALLBACK_SCHEME
OAUTH_ANDROID_CALLBACK_HOST
ACCESS_TOKEN_EXPIRE_MARGIN_SECONDS
```

예시:

```text
API_BASE_URL=https://api.msp-overlay.store
OAUTH_ANDROID_CALLBACK=msp-overlay://auth/callback
```

---

## 10. 완료 기준

환경 세팅 완료 기준은 다음과 같다.

```text
- Android Studio에서 프로젝트 빌드 가능
- debug/release 빌드 변형이 구분됨
- INTERNET 권한 선언 완료
- SYSTEM_ALERT_WINDOW 권한 안내 화면 구현 가능
- Foreground Service 권한 및 Notification 채널 생성 가능
- emulator에서 로컬 서버 접근 가능
- 운영 API URL이 별도 설정으로 분리됨
```
