# MSP Overlay Android Overlay JSON Specification

## 1. 문서 목적

이 문서는 Android 클라이언트가 처리해야 하는 `overlay.json` 구조, 검증 규칙, 내부 모델 변환 기준을 정의한다.

---

## 2. 기본 구조

Android는 서버/웹/Windows와 동일한 overlay JSON 구조를 사용한다.

최상위 예시:

```json
{
  "schemaVersion": "1.0.0",
  "overlayId": "ovl_example",
  "name": "Example Overlay",
  "platform": "android",
  "game": {
    "id": 1,
    "name": "Game Name"
  },
  "canvas": {
    "baseWidth": 1920,
    "baseHeight": 1080
  },
  "overlaySettings": {
    "opacity": 1.0
  },
  "elements": [],
  "meta": {
    "createdAt": "2026-06-05T00:00:00Z",
    "updatedAt": "2026-06-05T00:00:00Z"
  }
}
```

---

## 3. 최상위 필드 명세

| 필드 | 타입 | 필수 | 설명 |
|---|---:|---:|---|
| `schemaVersion` | string | Y | JSON 구조 버전 |
| `overlayId` | string | Y | 오버레이 고유 식별자 |
| `name` | string | Y | 오버레이 이름 |
| `platform` | string | Y | 적용 대상 플랫폼 |
| `game` | object | N | 게임/카테고리 정보 |
| `canvas` | object | Y | 기준 해상도 |
| `overlaySettings` | object | Y | 전역 오버레이 설정 |
| `elements` | array | Y | 렌더링 요소 목록 |
| `meta` | object | Y | 생성/수정 시각 |

---

## 4. Android 적용 가능 조건

Android overlay service에 적용하려면 아래 조건을 만족해야 한다.

```text
platform == "android"
schemaVersion 지원 버전
canvas.baseWidth > 0
canvas.baseHeight > 0
overlaySettings.opacity between 0.0 and 1.0
elements 배열 존재
모든 elements.type이 rect/circle/line 중 하나
```

Windows 오버레이는 목록에 표시할 수 있지만, Android 적용은 차단한다.

---

## 5. canvas 명세

```json
"canvas": {
  "baseWidth": 1920,
  "baseHeight": 1080
}
```

| 필드 | 타입 | 필수 | 설명 |
|---|---:|---:|---|
| `baseWidth` | number | Y | 편집 기준 너비 |
| `baseHeight` | number | Y | 편집 기준 높이 |

Android는 이 기준으로 현재 화면 크기와 비율을 계산한다.

```text
scaleX = overlayViewWidth / canvas.baseWidth
scaleY = overlayViewHeight / canvas.baseHeight
```

---

## 6. overlaySettings 명세

```json
"overlaySettings": {
  "opacity": 0.85
}
```

| 필드 | 타입 | 필수 | 범위 | 설명 |
|---|---:|---:|---:|---|
| `opacity` | number | Y | 0.0 ~ 1.0 | 전체 오버레이 투명도 |

최종 요소 투명도 계산:

```text
finalOpacity = overlaySettings.opacity * element.opacity
```

---

## 7. element 공통 처리

지원 element:

```text
rect
circle
line
```

지원 제외 element:

```text
text
image
```

공통 필드:

```text
id
type
opacity
zIndex
visible
locked
```

Android 렌더러는 `locked`를 렌더링에는 사용하지 않는다.

---

## 8. rect 명세

```json
{
  "id": "el_001",
  "type": "rect",
  "x": 100,
  "y": 100,
  "width": 300,
  "height": 200,
  "rotation": 0,
  "opacity": 0.6,
  "zIndex": 1,
  "visible": true,
  "locked": false,
  "fillColor": "#000000",
  "strokeColor": "#FFFFFF",
  "strokeWidth": 2,
  "cornerRadius": 12
}
```

필수 처리 필드:

```text
x, y, width, height
rotation
opacity
zIndex
visible
fillColor
strokeColor
strokeWidth
cornerRadius
```

---

## 9. circle 명세

```json
{
  "id": "el_002",
  "type": "circle",
  "x": 100,
  "y": 100,
  "width": 200,
  "height": 200,
  "rotation": 0,
  "opacity": 0.7,
  "zIndex": 2,
  "visible": true,
  "locked": false,
  "fillColor": "#000000",
  "strokeColor": "#FFFFFF",
  "strokeWidth": 2
}
```

Android에서는 width/height가 다르면 ellipse처럼 렌더링될 수 있다.

---

## 10. line 명세

```json
{
  "id": "el_003",
  "type": "line",
  "x1": 500,
  "y1": 500,
  "x2": 700,
  "y2": 500,
  "opacity": 1.0,
  "zIndex": 3,
  "visible": true,
  "locked": false,
  "strokeColor": "#FFFFFF",
  "strokeWidth": 3,
  "dashStyle": "solid"
}
```

지원 dashStyle:

```text
solid
dash
dot
```

Windows 호환을 위해 허용 가능:

```text
dashdot
dashdotdot
```

MVP에서는 `solid`, `dash`, `dot`만 실제 계약으로 본다.

---

## 11. 색상 파싱 규칙

Android renderer는 다음 색상 포맷을 처리한다.

```text
#RRGGBB
#AARRGGBB
rgb(r,g,b)
rgba(r,g,b,a)
```

파싱 실패 시 기본값:

```text
fillColor: transparent 또는 #000000
strokeColor: #FFFFFF
```

단, 파싱 실패가 많은 경우 오버레이 적용 전에 경고를 표시한다.

---

## 12. Validator 오류 기준

검증 실패 코드 예시:

```text
UNSUPPORTED_SCHEMA_VERSION
INVALID_PLATFORM
INVALID_CANVAS_SIZE
INVALID_GLOBAL_OPACITY
UNSUPPORTED_ELEMENT_TYPE
INVALID_ELEMENT_GEOMETRY
INVALID_COLOR_FORMAT
```

---

## 13. 완료 기준

Overlay JSON 처리 완료 기준:

```text
- overlay.json 문자열을 내부 모델로 변환 가능
- platform == android 검증 가능
- canvas 크기 검증 가능
- opacity 범위 검증 가능
- rect/circle/line을 타입별 모델로 변환 가능
- unsupported element를 시작 전에 차단 가능
- zIndex 정렬용 데이터 준비 가능
```
