# MSP Overlay Android Renderer Specification

## 1. 문서 목적

이 문서는 Android에서 `overlay.json`의 elements를 실제 화면에 렌더링하는 방식과 계산 기준을 정의한다.

---

## 2. Renderer 역할

Renderer는 다음 역할을 담당한다.

```text
- overlay document 입력 수신
- 현재 overlay view 크기 측정
- canvas 기준 좌표를 화면 좌표로 변환
- zIndex 순서대로 요소 렌더링
- opacity, 색상, strokeWidth, rotation 반영
```

---

## 3. 렌더링 대상

Android MVP에서 렌더링할 요소:

```text
rect
circle
line
```

렌더링하지 않는 요소:

```text
text
image
```

지원하지 않는 요소가 있으면 렌더링 단계가 아니라 validation 단계에서 차단한다.

---

## 4. Coordinate Scaling

Windows renderer와 동일한 계산 방식을 사용한다.

```text
scaleX = overlayViewWidth / canvas.baseWidth
scaleY = overlayViewHeight / canvas.baseHeight
```

## 4.1 rect/circle 좌표 변환

```text
screenX = x * scaleX
screenY = y * scaleY
screenWidth = width * scaleX
screenHeight = height * scaleY
```

## 4.2 line 좌표 변환

```text
screenX1 = x1 * scaleX
screenY1 = y1 * scaleY
screenX2 = x2 * scaleX
screenY2 = y2 * scaleY
```

---

## 5. Draw Order

렌더링 순서:

```text
1. visible == false 요소 제거
2. zIndex 오름차순 정렬
3. 정렬 순서대로 그리기
```

동일 zIndex일 경우 JSON 배열 순서를 유지한다.

---

## 6. Opacity 계산

최종 투명도:

```text
finalOpacity = clamp01(overlaySettings.opacity) * clamp01(element.opacity)
```

Android Paint alpha 계산:

```text
alpha = (finalOpacity * 255).roundToInt()
```

---

## 7. Stroke Width Scaling

Windows와 일관성을 위해 평균 scale을 사용한다.

```text
scaleAvg = (scaleX + scaleY) / 2
scaledStrokeWidth = strokeWidth * scaleAvg
```

strokeWidth가 0이면 stroke를 그리지 않는다.

---

## 8. Color 처리

지원 포맷:

```text
#RRGGBB
#AARRGGBB
rgb(r,g,b)
rgba(r,g,b,a)
```

색상 처리 순서:

```text
1. 문자열 포맷 확인
2. RGB/ARGB 값 파싱
3. finalOpacity와 결합
4. Paint에 alpha 적용
```

---

## 9. Rect 렌더링

처리 방식:

```text
1. 좌표/크기 scale 적용
2. 중심점 계산
3. rotation이 있으면 canvas.rotate 적용
4. cornerRadius scale 적용
5. fillColor로 내부 채우기
6. strokeWidth > 0이면 strokeColor로 테두리 그리기
7. canvas 상태 복원
```

cornerRadius scaling:

```text
scaledCornerRadius = cornerRadius * ((scaleX + scaleY) / 2)
```

---

## 10. Circle 렌더링

처리 방식:

```text
1. x, y, width, height scale 적용
2. width/height가 같으면 circle
3. width/height가 다르면 oval
4. rotation이 있으면 중심점 기준 회전
5. fillColor 채우기
6. strokeWidth > 0이면 strokeColor 테두리
```

---

## 11. Line 렌더링

처리 방식:

```text
1. x1, y1, x2, y2 scale 적용
2. strokeWidth scale 적용
3. dashStyle 적용
4. strokeColor와 finalOpacity 적용
5. drawLine 실행
```

Dash 처리 예시:

```text
solid: PathEffect 없음
dash: DashPathEffect(floatArrayOf(16f, 8f), 0f)
dot: DashPathEffect(floatArrayOf(2f, 8f), 0f)
```

dash 간격도 scaleAvg를 곱해서 조정한다.

---

## 12. Preview Renderer와 Overlay Renderer

Renderer는 두 곳에서 재사용 가능해야 한다.

```text
1. 앱 내부 Preview 화면
2. Foreground overlay service의 전체 화면 View
```

공통화할 부분:

```text
CoordinateScaler
OverlayPainter
ColorParser
ElementSorter
```

분리할 부분:

```text
- Preview 화면 크기
- 실제 overlay view 크기
- 터치 정책
- service lifecycle
```

---

## 13. 성능 기준

MVP에서는 static overlay 렌더링을 기준으로 한다.

```text
- 매 프레임 애니메이션 없음
- overlay document 변경 시 invalidate
- 화면 크기 변경 시 invalidate
- 필요 이상으로 반복 invalidate 하지 않음
```

---

## 14. 완료 기준

Renderer 완료 기준:

```text
- rect/circle/line이 정상 렌더링됨
- canvas baseWidth/baseHeight 기준으로 화면 크기에 맞게 scale됨
- zIndex 순서가 반영됨
- visible false 요소가 제외됨
- 전체 opacity와 element opacity가 곱해짐
- strokeWidth가 화면 비율에 맞게 조정됨
- rotation이 rect/circle 중심 기준으로 반영됨
- dash/dot line이 표시됨
```
