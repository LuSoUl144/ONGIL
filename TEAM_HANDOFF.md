# ONGIL 팀 작업 전달 문서

## 서비스 한 줄

학생의 귀가 상황에 따라 **안전한 픽업 합류** 또는 **혼자 귀가 안전 경로**를 안내하는 서비스입니다.

## 역할 분담

### 앱 담당

- 학생/학부모 역할 선택 UI
- 홈 지도, 픽업존 비교, 안전 경로 플랜, 위치 공유 화면
- ONGIL 주간/야간 로고와 브랜드 디자인
- 백엔드 API 응답을 화면에 연결

### 백엔드 + AI 챗봇 담당

- 가족 코드 생성 및 연결
- 학생·학부모 관계 관리
- 위치 공유 요청/수락 상태 관리
- AI 챗봇 API
- 앱에서 사용할 API 문서와 테스트용 서버 주소 제공

---

## 가족 코드 기능 제안

가족 코드는 꼭 넣는 것을 추천합니다. 로그인만으로는 어떤 학부모와 학생을 연결해야 하는지 알 수 없기 때문입니다.

### 해커톤 데모 흐름

1. 학부모가 `가족 연결 코드 만들기`를 누른다.
2. 서버가 6자리 코드(예: `ONGIL7`)를 만든다.
3. 학생이 `가족 코드 입력`에 해당 코드를 입력한다.
4. 서버가 학생과 학부모를 한 가족으로 연결한다.
5. 그 뒤부터 학부모만 위치 공유를 요청하고, 학생은 수락/거절할 수 있다.

### 최소 데이터 모델

```text
User: id, name, role(student|parent)
Family: id, inviteCode, parentId, studentId
LocationShare: familyId, requestedBy, status(pending|accepted|rejected), expiresAt
```

### 최소 API 예시

```http
POST /api/family/code
Authorization: Bearer <parent-token>

POST /api/family/join
{ "code": "ONGIL7" }

GET /api/family/me

POST /api/location-share/request
{ "studentId": "student-1" }

POST /api/location-share/respond
{ "requestId": "share-1", "accepted": true }
```

> 데모에서는 코드의 만료 시간, 실제 GPS 저장은 생략해도 됩니다. 대신 `연결됨`, `요청 중`, `공유 중` 상태가 화면에 보이면 충분합니다.

---

## AI 챗봇 범위

챗봇은 위험도를 직접 계산하거나 실제 길을 결정하지 않습니다. 사용자의 귀가 상황을 이해하고, 알맞은 화면으로 보내는 역할입니다.

### 챗봇이 할 일

- “오늘 픽업이야, 혼자 가?”처럼 먼저 질문
- 사용자 문장을 아래 행동 중 하나로 분류
  - `OPEN_PICKUP_ZONES`: 픽업존 보기
  - `OPEN_SAFE_ROUTES`: 혼자 귀가 안전 경로 보기
  - `REQUEST_LOCATION_SHARE`: 위치 공유 요청 화면
  - `NONE`: 일반 안내
- 짧고 친절한 한국어 답변 반환

### 앱이 보낼 요청 예시

```json
{
  "message": "오늘 혼자 걸어서 갈 것 같아",
  "role": "student",
  "timeBucket": "night"
}
```

### 서버가 반환할 응답 예시

```json
{
  "reply": "그럼 밝은 길과 안전 거점을 우선한 경로를 보여줄게.",
  "action": "OPEN_SAFE_ROUTES"
}
```

### 중요한 기준

- Gemini 무료 모델을 서버에서만 호출하고, 앱에는 API 키를 넣지 않습니다.
- 이름, 정확한 GPS, 가족 코드 등 개인 정보는 AI 모델에 보내지 않습니다.
- 경로 안전 점수는 AI가 만들지 않습니다. 앱/서버의 고정 규칙(가로등, 안전 거점, 시간대, 공사·돌발 정보 등)으로 계산합니다.
- 범죄 다발지 회피라고 단정하지 말고 `야간 안전 우선 경로`라고 표현합니다.

---

## 팀원에게 필요한 전달물

1. 실행 방법이 담긴 `README.md`
2. `requirements.txt` 또는 `package.json`
3. 비밀 키 없이 실행 가능한 `.env.example`
4. 테스트 서버 주소 또는 로컬 실행 주소
5. 위 API의 요청·응답 JSON 예시

`.venv`, `__pycache__`, 실제 API 키는 GitHub에 올리지 않습니다.
