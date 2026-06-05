# MSP Overlay Android Local Storage Specification

## 1. 문서 목적

이 문서는 Android 클라이언트의 로컬 저장 대상, 저장 위치, 캐시 정책을 정의한다.

---

## 2. 저장 대상

Android 앱은 다음 데이터를 로컬에 저장한다.

```text
- access token
- refresh token
- token 만료 시각
- 사용자 기본 정보 캐시
- 최근 적용 overlay.json
- 최근 적용 overlay metadata
- API base URL debug 설정
- overlay permission 안내 확인 여부
```

---

## 3. 민감 데이터 저장

민감 데이터:

```text
accessToken
refreshToken
```

저장 방식:

```text
EncryptedSharedPreferences
또는 암호화된 DataStore
```

일반 SharedPreferences 평문 저장은 사용하지 않는다.

---

## 4. 사용자 정보 캐시

저장 대상:

```text
userId
name
email
profileImageUrl
```

용도:

```text
앱 시작 시 Settings/Home 화면에 빠르게 표시
```

정책:

```text
GET /api/auth/me 성공 시 갱신
로그아웃 시 삭제
```

---

## 5. Overlay Cache

## 5.1 최근 적용 오버레이 저장

저장 대상:

```text
overlayId
name
code
platform
game
overlayJson
thumbnailUrl
lastAppliedAt
```

용도:

```text
- 앱 재실행 후 마지막 오버레이 복원
- 네트워크 실패 시 최근 오버레이 재사용
- OverlayService에 큰 JSON을 Intent로 넘기지 않고 cacheKey만 전달
```

## 5.2 저장 위치

권장:

```text
internal app storage
```

예시:

```text
/files/overlays/{overlayId}/overlay.json
/files/overlays/{overlayId}/metadata.json
```

---

## 6. 캐시 정책

## 6.1 최근 적용 오버레이

MVP 정책:

```text
최근 적용 오버레이 1개 이상 저장
```

권장 확장:

```text
최근 5개까지 저장
```

## 6.2 캐시 삭제

Settings에서 다음 액션을 제공한다.

```text
Clear Overlay Cache
```

삭제 대상:

```text
저장된 overlay.json
thumbnail cache
최근 적용 metadata
```

토큰은 삭제하지 않는다.

---

## 7. API Base URL 저장

debug build에서만 API Base URL을 변경 가능하게 한다.

저장 대상:

```text
apiBaseUrl
```

release build에서는 운영 URL로 고정한다.

---

## 8. OverlayService와 캐시 연동

권장 흐름:

```text
1. 사용자가 오버레이 Apply 클릭
2. overlay json validation 성공
3. local cache에 overlay 저장
4. OverlayService에는 cacheKey 전달
5. OverlayService가 cacheKey로 overlay json 로드
6. renderer에 document 전달
```

장점:

```text
- Intent 크기 제한 회피
- service 재시작 시 마지막 오버레이 복원 가능
- foreground service와 UI의 데이터 전달 구조 단순화
```

---

## 9. 로그아웃 시 삭제 기준

로그아웃 시 삭제:

```text
accessToken
refreshToken
user profile cache
```

로그아웃 시 유지 가능:

```text
최근 적용 overlay cache
API base URL debug 설정
권한 안내 확인 여부
```

단, 사용자가 명시적으로 캐시 삭제를 누르면 overlay cache도 삭제한다.

---

## 10. 완료 기준

로컬 저장 구현 완료 기준:

```text
- token이 암호화 저장소에 저장됨
- 로그아웃 시 token이 삭제됨
- 최근 적용 overlay.json을 internal storage에 저장 가능
- OverlayService가 cacheKey로 overlay를 로드 가능
- debug API Base URL 저장/변경 가능
- Settings에서 overlay cache 삭제 가능
```
