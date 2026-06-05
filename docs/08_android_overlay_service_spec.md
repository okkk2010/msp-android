# MSP Overlay Android Overlay Service Specification

## 1. 문서 목적

이 문서는 Android에서 전체 화면 오버레이를 표시하기 위한 Foreground Service, WindowManager, Notification 제어 구조를 정의한다.

---

## 2. Overlay Service 역할

Overlay Service는 앱 화면과 독립적으로 오버레이를 화면 위에 띄우는 구성 요소다.

주요 역할:

```text
- Foreground Service 시작/종료
- SYSTEM_ALERT_WINDOW 권한 확인
- WindowManager에 overlay view 추가/제거
- 현재 적용 중인 overlay document 유지
- Notification에서 ON/OFF 제어
```

---

## 3. 권한 전제

Overlay Service는 아래 조건을 만족해야 시작된다.

```text
Settings.canDrawOverlays(context) == true
```

권한이 없으면:

```text
- service 시작 차단
- 사용자에게 권한 설정 화면 이동 안내
```

---

## 4. Service 구조

권장 파일:

```text
OverlayService.kt
OverlayWindowController.kt
OverlayRendererView.kt
OverlayNotificationFactory.kt
OverlayNotificationActionReceiver.kt
```

역할:

| 파일 | 역할 |
|---|---|
| `OverlayService` | Foreground Service lifecycle 관리 |
| `OverlayWindowController` | WindowManager add/remove/update 담당 |
| `OverlayRendererView` | 실제 overlay drawing 담당 |
| `OverlayNotificationFactory` | Notification 생성 |
| `OverlayNotificationActionReceiver` | Notification action 처리 |

---

## 5. Service 시작 흐름

```text
사용자가 Start Overlay 클릭
→ overlay document 검증
→ overlay permission 확인
→ OverlayService startForegroundService 호출
→ Service 내부에서 startForeground 실행
→ WindowManager에 OverlayRendererView 추가
→ Notification 표시
```

---

## 6. Service 종료 흐름

```text
사용자가 Stop Overlay 클릭
또는 Notification Stop 클릭
→ WindowManager에서 overlay view 제거
→ foreground service 중지
→ Notification 제거
```

---

## 7. WindowManager LayoutParams

권장 설정:

```kotlin
WindowManager.LayoutParams(
    MATCH_PARENT,
    MATCH_PARENT,
    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
    PixelFormat.TRANSLUCENT
)
```

## 7.1 FLAG_NOT_FOCUSABLE

오버레이가 포커스를 가져가지 않도록 한다.

## 7.2 FLAG_NOT_TOUCHABLE

MVP의 passive overlay 모드에서는 터치 입력을 소비하지 않는다.

결과:

```text
게임 또는 앱 조작이 오버레이 아래로 통과
```

## 7.3 FLAG_LAYOUT_NO_LIMITS

기본값으로 사용하지 않는다.

필요한 경우에만 테스트 후 적용한다.

---

## 8. Overlay View 크기

MVP 기준:

```text
MATCH_PARENT x MATCH_PARENT
```

즉, 전체 화면 기준으로 렌더링한다.

향후 확장 가능 항목:

```text
- 특정 앱 영역 추정
- safe area/inset 보정
- 가로/세로 모드 별 별도 처리
```

---

## 9. Notification 구성

필수 정보:

```text
- 앱 이름
- 현재 오버레이 실행 중 표시
- Stop Overlay 액션
- 앱 열기 액션
```

Notification Channel:

```text
channelId: overlay_service
channelName: MSP Overlay Service
importance: LOW
```

---

## 10. Notification Action

권장 액션:

```text
ACTION_STOP_OVERLAY
ACTION_OPEN_APP
```

추가 가능:

```text
ACTION_TOGGLE_OVERLAY
ACTION_CHANGE_OVERLAY
```

MVP에서는 Stop과 Open App만 우선 구현한다.

---

## 11. 활성 오버레이 전달 방식

OverlayService에 overlay document를 전달하는 방법은 두 가지다.

## 11.1 Intent Extra 직접 전달

작은 JSON 문자열이면 Intent extra로 전달 가능하다.

```text
EXTRA_OVERLAY_JSON
```

단점:

```text
JSON이 커질 경우 Intent 크기 제한 가능성
```

## 11.2 로컬 캐시에 저장 후 ID 전달

권장 방식:

```text
1. overlay.json을 local cache에 저장
2. service에는 overlayId 또는 cacheKey 전달
3. service가 cache에서 overlay document 로드
```

MVP에서는 2번 방식을 권장한다.

---

## 12. 화면 회전 처리

화면 회전 또는 크기 변경 시:

```text
- OverlayRendererView 크기 재측정
- scaleX/scaleY 재계산
- invalidate 호출
```

현재 적용 중인 overlay document는 유지한다.

---

## 13. 예외 처리

| 상황 | 처리 |
|---|---|
| 권한 없음 | service 시작 차단, 권한 설정 화면 안내 |
| overlay json 없음 | 시작 차단, 사용자 메시지 표시 |
| renderer 오류 | service 중지, 오류 알림 |
| Notification 권한 없음 | 앱 내부 안내, service는 OS 정책에 따라 처리 |

---

## 14. 완료 기준

Overlay Service 완료 기준:

```text
- overlay permission 허용 후 service 시작 가능
- foreground notification 표시
- WindowManager에 전체 화면 overlay view 추가 가능
- overlay view가 터치 입력을 소비하지 않음
- Notification Stop으로 service 종료 가능
- 앱 내부 Stop 버튼으로 service 종료 가능
- 화면 회전 후에도 overlay가 다시 렌더링됨
```
