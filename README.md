
# 🛡️ Ktor JWT Auth Practice Server

간단한 **JWT 인증 서버**로, 클라이언트(JWT 사용 앱)의 로그인/회원가입/토큰 재발급 등 **토큰 기반 인증 플로우를 연습**할 수 있도록 만들어졌습니다.

accessToken의 유효기간 =  30초
refreshToken의 유효기간 =  2분

- 🧪 **Database 사용 없음** — 메모리에 유저를 올려 관리합니다.
- 🔐 JWT 기반 AccessToken / RefreshToken 발급 및 검증
- 🧵 Ktor + Kotlin + JWT 사용
- 📦 BaseResponse 구조로 통일된 응답 제공
- 토큰의 유효시간을 변경하고 싶다면 src/main/kotlin/service/JwtService 파일에서 상수 값을 변경하시면 됩니다(refreshToken 요청 시 routine/AuthRouting 파일에서 조정 가능)
---

## 🧰 기술 스택

- **Kotlin + Ktor**
- **JWT**

---

## 📦 BaseResponse 구조

```kotlin
@Serializable
data class BaseResponse<T>(
    val status: Int,
    val message: String,
    val data: T?
)
```

모든 API 응답은 위와 같은 **공통 응답 구조**로 내려갑니다.

토큰이 만료되었거나 유효하지 않은 토큰이라면 BaseResponse 대신 **401 에러를 반환합니다**. <br>
(Android Okhttp3의 Authenticator을 연습하기 위함)

---

## 🔐 주요 기능 및 API 명세
### 기본 BASEURL 은 [http://localhost](http://0.0.0.0:8080) 입니다. (http://0.0.0.0:8080)
### 📌 회원가입

- **[POST] /api/user**
- 요청:

```json
{
  "username": "tester",
  "email": "test@email.com",
  "password": "1234"
}
```

- 응답:
```json
{
  "status": 201,
  "message": "회원가입 성공",
  "data": true
}
```
회원가입이 귀찮으시다면
email: 옥지@naver.com   /   pw: 1234
email: 빵빵이@naver.com   /   pw: 1234
email: 지방이@naver.com   /   pw: 1234
email: dong@naver.com   /   pw: 1234

미리 저장되어 있는 4개의 계정을 사용하셔도 토큰이 발급됩니다.
---

### 📌 로그인

- **[POST] /api/auth**
- 요청:
```json
{
  "email": "test@email.com",
  "password": "1234"
}
```

- 응답:
```json
{
  "status": 200,
  "message": "로그인 성공",
  "data": {
    "accessToken": "...",
    "refreshToken": "..."
  }
}
```

---

### 📌 토큰 재발급

- **[POST] /api/auth/refresh**
- 요청:
```json
{
  "refreshToken": "..."
}
```

- 응답:
```json
{
  "status": 200,
  "message": "AccessToken 재발급 성공",
  "data": {
    "accessToken": "...",
    "refreshToken": "..."
  }
}
```

---

### 📌 유저 전체 조회

- **[GET] /api/user**
- 인증 필요: ✅ (accessToken)
- 응답:
```json
{
  "status": 200,
  "message": "success",
  "data": [
    {
      "id": "uuid",
      "username": "tester",
      "email": "test@email.com"
    }
  ]
}
```

---

### 📌 내 정보 가져오기

- **[GET] /api/user/me**
- 인증 필요: ✅ (accessToken)

---

### 📌 특정 유저 조회

- **[GET] /api/user/{id}**
- 인증 필요: ✅
- 현재 로그인된 유저와 `id`가 일치할 때만 허용됨

---

## 🧪 테스트 방법

1. 서버 실행
2. Postman 혹은 안드로이드 앱에서 API 호출
3. accessToken은 `"Authorization": "Bearer <token>"` 형식으로 헤더에 포함

---


## 👨‍💻 만든 이유

- JWT 구조를 백엔드 레벨에서 이해하고, 클라이언트 인증 흐름을 테스트해보기 위함
- 프론트 or 앱 클라이언트 개발자들이 직접 붙어볼 수 있도록 간단하고 직관적으로 구성

---

> 이 프로젝트는 로컬 연습 및 학습 목적에 최적화되어 있습니다.
