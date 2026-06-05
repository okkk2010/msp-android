# MSP Overlay Android Error Handling Specification

## 1. 문서 목적

이 문서는 Android 클라이언트에서 발생할 수 있는 API, 인증, 권한, JSON, 렌더링, 네트워크 오류의 처리 기준을 정의한다.

---

## 2. 오류 분류

Android 앱의 오류는 다음으로 구분한다.

```text
- Auth Error
- API Error
- Network Error
- Overlay Permission Error
- Overlay JSON Error
- Renderer Error
- Storage Error
```

---

## 3. Auth Error

## 3.1 TOKEN_EXPIRED

처리:

```text
1. refresh token으로 access token 갱신
2. 원래 요청 1회 재시도
3. 재시도 실패 시 세션 초기화
```

## 3.2 INVALID_TOKEN

처리:

```text
- 로컬 token 삭제
- 로그인 상태 false 처리
- 사용자에게 재로그인 요청
```

## 3.3 REFRESH_TOKEN_EXPIRED

처리:

```text
- 전체 세션 삭제
- 로그인 화면 또는 Settings 로그인 안내
```

---

## 4. API Error

| 오류 | 처리 |
|---|---|
| `OVERLAY_NOT_FOUND` | 존재하지 않는 오버레이 메시지 표시 |
| `INVALID_OVERLAY_CODE` | 코드 확인 안내 |
| `INVALID_OVERLAY_JSON` | 적용 차단, JSON 오류 메시지 표시 |
| `LIBRARY_ALREADY_SAVED` | 이미 저장됨으로 처리 |
| `UNAUTHORIZED` | 로그인 필요 메시지 표시 |
| `FORBIDDEN` | 권한 없음 메시지 표시 |

---

## 5. Network Error

대상:

```text
- timeout
- no internet
- DNS failure
- SSL error
- server unreachable
```

처리:

```text
- 재시도 버튼 제공
- 최근 적용 overlay cache가 있으면 로컬 적용 옵션 제공
- SSL 오류는 release에서 상세 노출하지 않음
```

---

## 6. Overlay Permission Error

상황:

```text
SYSTEM_ALERT_WINDOW 권한 없음
```

처리:

```text
- Start Overlay 차단
- Permission Guide 화면 표시
- 설정 이동 버튼 제공
```

권한 설정 후 앱 복귀 시 다시 `Settings.canDrawOverlays(context)`를 확인한다.

---

## 7. Notification Permission Error

Android 13 이상에서 Notification 권한이 없을 수 있다.

처리:

```text
- 권한 요청 UI 표시
- 권한 거부 시 안내 메시지 표시
- Foreground Service 동작 가능 여부는 OS 정책에 따라 확인
```

---

## 8. Overlay JSON Error

검증 오류 예시:

```text
INVALID_PLATFORM
UNSUPPORTED_SCHEMA_VERSION
INVALID_CANVAS_SIZE
INVALID_GLOBAL_OPACITY
UNSUPPORTED_ELEMENT_TYPE
INVALID_ELEMENT_GEOMETRY
INVALID_COLOR_FORMAT
```

사용자 표시 방식:

```text
이 오버레이는 Android에서 적용할 수 없습니다.
사유: 지원하지 않는 요소가 포함되어 있습니다.
```

개발 로그에는 상세 필드 경로를 남긴다.

---

## 9. Renderer Error

상황:

```text
- Paint 생성 실패
- 색상 파싱 실패
- 좌표 계산 중 NaN/Infinity
- view size 0
```

처리:

```text
- 해당 요소 skip 가능 여부 판단
- 전체 렌더링 불가능하면 overlay service 중지
- 사용자에게 적용 실패 메시지 표시
```

MVP에서는 렌더링 안정성을 위해 검증 단계에서 최대한 차단한다.

---

## 10. Storage Error

상황:

```text
- overlay cache 저장 실패
- overlay cache 읽기 실패
- token 저장소 접근 실패
```

처리:

```text
- cache 저장 실패 시 사용자에게 경고
- token 저장 실패 시 로그인 실패 처리
- cache 읽기 실패 시 서버 재조회 유도
```

---

## 11. 사용자 메시지 원칙

사용자에게는 기술적인 예외명을 그대로 노출하지 않는다.

좋은 예:

```text
오버레이를 불러오지 못했습니다. 네트워크 상태를 확인한 뒤 다시 시도해주세요.
```

피할 예:

```text
JsonDataException at elements[2].strokeColor
```

단, debug build에서는 상세 정보를 로그로 남긴다.

---

## 12. 완료 기준

오류 처리 완료 기준:

```text
- token 만료 시 refresh 후 재시도 가능
- refresh 실패 시 세션 삭제 가능
- 네트워크 실패 시 재시도 UI 표시
- overlay 권한 없을 때 service 시작 차단
- invalid JSON 적용 차단
- unsupported platform 적용 차단
- renderer 오류 발생 시 service가 비정상 종료되지 않음
```
