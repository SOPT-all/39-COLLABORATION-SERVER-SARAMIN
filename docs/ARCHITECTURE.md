# 아키텍처

## 선정 배경

계층형 아키텍처 (Controller - Service - Repository)를 채택한다.

**근거**

1. 도메인 로직이 얇다
   조회·필터링이 대부분이고 복잡한 비즈니스 규칙이 없어
   포트·어댑터 같은 추가 추상화의 이득이 없다.

2. 팀의 공통 기반이다
   세미나 커리큘럼이 계층형 기반이라 전원이 같은 구조를 이해하고 있다.

3. 일정 제약
   8주 프로젝트에서 구조 학습 비용을 최소화한다.

패키지는 **도메인별로 묶는다.** 계층별로 묶으면 작업 분배 시
같은 폴더에 전원이 파일을 추가하게 되어 머지 충돌이 잦아진다.

## 패키지 구조

org.sopt.collaboration
├── CollaborationApplication.java
├── global
│   ├── config
│   │   ├── SwaggerConfig.java
│   │   ├── JpaConfig.java
│   │   └── CorsConfig.java
│   ├── common
│   │   ├── ApiResponse.java
│   │   └── BaseTimeEntity.java
│   └── exception
│       ├── BusinessException.java
│       ├── ErrorCode.java
│       └── GlobalExceptionHandler.java
└── domain
    ├── user
    │   ├── controller
    │   ├── service
    │   ├── repository
    │   ├── entity
    │   └── dto
    ├── jobposting
    ├── keyword
    ├── bookmark
    └── document

각 도메인은 `controller / service / repository / entity / dto` 5계층을 가진다.

## 계층별 책임

| 계층 | 책임 | 금지 |
|---|---|---|
| Controller | 요청 수신, DTO 변환, 응답 반환 | 비즈니스 로직 |
| Service | 비즈니스 로직, 트랜잭션 경계 | HTTP 관련 처리 |
| Repository | 데이터 접근 | 비즈니스 로직 |

## 데이터 흐름

Request
  → Controller (Request DTO 수신)
  → Service (비즈니스 로직, Entity 조작)
  → Repository (DB 접근)
  → Service (Response DTO 변환)
  → Controller (ApiResponse 래핑)
Response

Entity는 Service 계층을 벗어나지 않는다.

## 공통 규약

### 응답 포맷

모든 응답은 `ApiResponse<T>` 로 감싼다.

{
  "success": true,
  "code": 200,
  "message": "요청에 성공했습니다.",
  "data": { }
}

### 예외 처리

- 비즈니스 예외는 `BusinessException` 을 던진다
- 에러 코드는 `ErrorCode` enum에 정의한다
- `GlobalExceptionHandler` 에서 일괄 처리한다

### 프로파일

| 프로파일 | 용도 | DB |
|---|---|---|
| `local` | 로컬 개발 | 환경변수로 주입 |
| `test` | CI 테스트 | H2 인메모리 |

DB가 확정되면 드라이버 의존성과 `DB_URL` 만 교체한다.

## 성능 고려사항

### N+1 주의

공고 목록 조회 시 각 공고의 요구 스킬을 불러오고
유저 스킬과 매칭하는 구조라 N+1이 발생하기 쉽다.

- 연관관계는 기본 `LAZY`
- 목록 조회는 `fetch join` 또는 `@BatchSize` 적용
- 쿼리 수를 확인하며 개발한다

### 검색엔진 미사용

샘플 공고 수십~수백 건 규모에서는 DB 인덱스로 충분하다.
Elasticsearch 등 별도 검색엔진은 도입하지 않는다.

### 인덱스 설계

- 선택도가 낮은 칼럼(직무, 경력구분 등)에 단독 인덱스를 걸지 않는다
- 복합 인덱스는 등호 조건 칼럼을 앞에, 범위 조건을 뒤에 배치한다
