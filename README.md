# 온길 (ONGIL)

> 오늘도 안전하게, 집으로

학생의 귀가 상황에 따라 **학부모 픽업 합류** 또는 **혼자 귀가 안전 경로**를 제안하는 Android 프로토타입입니다.

## 현재 구현된 뼈대

- 로그인 및 학생/학부모 역할 선택
- 현재 위치와 픽업존을 표현한 지도형 홈 목업
- 학부모의 위치 공유 요청과 학생의 수락/거절 흐름
- 소요 시간과 위험도를 비교하는 안전 경로 플랜 A/B
- 학부모 모드의 `TMAP으로 안내받기` 연결 자리
- 상황에 맞춰 먼저 말을 거는 온길 도우미 카드
- AI 도우미의 `픽업 합류 / 혼자 귀가 / 위치 공유` 의도 분기 목업
- A/B/C 픽업존 혼잡도 비교와 추천 화면

현재 화면의 지도, 혼잡도와 위치는 발표용 목업입니다. 실제 API 연동 시 화면 코드를 유지하고 데이터 공급 부분만 교체할 수 있도록 기능별 Activity로 분리했습니다.

## 프로젝트 구조

```text
app/src/main/java/com/project/ongil/
├─ LoginActivity.java          # 로그인 및 역할 선택
├─ MainActivity.java           # 지도형 홈과 역할별 기능
├─ AssistantActivity.java      # 귀가 상황을 묻는 AI 대화 목업
├─ PickupZoneActivity.java     # 픽업존 혼잡도 비교와 합류 선택
├─ LocationShareActivity.java  # 위치 공유 요청/수락 흐름
├─ RoutePlansActivity.java     # 경로별 시간/위험도 비교
└─ ai/                         # 실제 API로 교체 가능한 AI 클라이언트
```

## 위험도 기준 초안

위험도는 `0`에 가까울수록 안전하고 `100`에 가까울수록 위험합니다.

- 공사 차량 진출입 구간
- 보행 신호등 유무
- 골목길 및 보도 분리 여부
- 가로등·안전시설 접근성
- 시간대별 차량 혼잡도

PPT에서는 "범죄 예측"보다 **보행 환경 데이터 기반 위험도**라고 표현합니다.

## API 키 설정

TMAP 지도 SDK 연동 전 `local.properties.example`을 참고해 개인 PC의 `local.properties`에 키를 추가합니다.

```properties
TMAP_APP_KEY=replace_with_your_tmap_key
```

`local.properties`는 `.gitignore`에 포함되어 GitHub에 올라가지 않습니다. 지도 SDK 키는 빌드된 앱에서 추출될 수 있으므로 TMAP 콘솔에서 패키지 제한과 호출량 제한을 설정하고, 교통·경로 REST API는 서버 프록시 사용을 권장합니다.

## 2인 협업 권장 방식

1. `main`에는 실행 가능한 상태만 합칩니다.
2. 각자 `feature/기능명` 브랜치에서 작업합니다.
3. 한 명은 지도/TMAP·데이터 연동, 다른 한 명은 로그인·위치 공유·UI를 맡습니다.
4. Pull Request에서 화면 캡처와 테스트 방법을 함께 남깁니다.

예시 브랜치:

```text
feature/map-and-tmap
feature/location-sharing
feature/safe-route-score
```

## 다음 구현 순서

1. 지도 SDK 표시 및 현재 위치 권한 처리
2. Firebase 또는 Supabase 기반 가족 연결과 실시간 위치 공유
3. TMAP 차량 경로 및 픽업존 연결
4. 공개 데이터 기반 위험도 계산
5. 발표용 시나리오 녹화

## AI 연동 방향

현재 `MockAiAssistantClient`가 발표용 응답과 앱 화면 전환을 담당합니다. 실제 AI 연동 시 무료 티어가 제공되는 Gemini Developer API의 `gemini-3.1-flash-lite`를 사용합니다. Android 앱에서 API 키를 직접 보관하지 않고, 별도 백엔드가 Gemini API를 호출하도록 구성합니다.

- 모델: `gemini-3.1-flash-lite`
- 선택 이유: 무료 입력·출력 티어, 빠른 단순 대화 및 의도 분류
- 공식 가격표: https://ai.google.dev/gemini-api/docs/pricing

AI는 경로 위험도를 계산하지 않습니다. 사용자 대화를 아래 앱 동작 중 하나로 분류하고, 계산된 데이터 결과를 친근하게 설명합니다.

```text
OPEN_PICKUP_ZONES
OPEN_SAFE_ROUTES
REQUEST_LOCATION_SHARE
```
