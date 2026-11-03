# CLAUDE.md

## 프로젝트 개요

사람인 UX/UI 리디자인 프로젝트의 백엔드 서버.
취업준비생이 채용공고를 탐색하고 여러 공고를 비교해 지원 대상을
스스로 고르는 경험을 제공한다.

**중요: 이 프로젝트는 프로토타입이다.**
- 샘플 공고 데이터 기반으로 동작한다
- 실제 파일 업로드·입사지원 제출은 범위 밖이다
- 외부 채용 API 연동은 하지 않는다
- PRD 범위 밖의 기능을 임의로 추가하지 않는다

## 기술 스택

- Java 21 / Spring Boot 4.1.2
- Spring Data JPA
- DB: 미정 (MySQL 또는 PostgreSQL)
- springdoc-openapi (Swagger)
- 패키지 루트: `org.sopt.collaboration`

## 아키텍처

계층형 아키텍처 (Controller - Service - Repository).
패키지는 도메인별로 묶는다.

상세 구조는 @docs/ARCHITECTURE.md 참조.

## 도메인 용어

작업 전 @docs/DOMAIN.md 를 반드시 읽는다.
특히 아래 두 가지를 혼동하지 않는다.

- **체크 ≠ 저장**
  체크는 비교 대상 선택(클라이언트 상태), 저장은 나중에 볼 공고 보관(서버 저장)
- **경험 일치는 AI 추정이 아니다**
  유저 스킬과 공고 요구 스킬의 교집합을 계산하는 규칙 기반 로직이다.
  LLM을 호출하지 않으며 없는 값을 추정하지 않는다.

---

## 깃 컨벤션

### 브랜치 전략

Git Flow 기반.

| 브랜치 | 역할 | 분기 | 머지 |
|---|---|---|---|
| `main` | 운영 배포 | - | - |
| `develop` | 개발 통합 | `main` | `main` |
| `feat/*` | 기능 개발 | `develop` | `develop` |
| `release/*` | 배포 준비 | `develop` | `main`, `develop` |
| `hotfix/*` | 긴급 수정 | `main` | `main`, `develop` |

**`feat` 브랜치를 `main`에 직접 머지하지 않는다.**

### 브랜치 네이밍

feat/#{이슈번호}/{기능명}
hotfix/#{이슈번호}/{버그명}
release/{버전}

예시
- `feat/#12/kakao-login`
- `feat/#34/product-search`
- `hotfix/#56/payment-timeout`
- `release/1.0.0`

금지
- `feature/login` (접두사 틀림, 이슈 번호 없음)
- `Feat/Login` (대문자 사용)

### 커밋 메시지

Angular 컨벤션 기반. **싱글 라인으로 작성한다.**

<type>(<scope>): <subject> (#이슈번호)

**Type**

| Type | 설명 |
|---|---|
| `feat` | 새로운 기능 추가 |
| `fix` | 버그 수정 |
| `docs` | 문서 수정 |
| `refactor` | 코드 리팩토링 |
| `perf` | 성능 개선 |
| `test` | 테스트 코드 추가·수정 |
| `build` | 빌드 시스템·외부 의존성 변경 |
| `ci` | CI 설정 변경 |
| `chore` | 기타 변경사항 (소스·테스트 변경 없음) |
| `revert` | 이전 커밋 되돌리기 |

**Scope**

| Scope | 설명 |
|---|---|
| `user` | 사용자 |
| `jobposting` | 채용공고 |
| `keyword` | 마이핏 키워드 |
| `bookmark` | 공고 저장 |
| `document` | 서류 패키지 |
| `common` | 공통 모듈 |
| `config` | 설정 |
| `deps` | 의존성 |

**작성 규칙**

- subject는 50자 이내
- 마침표를 붙이지 않는다
- 명령문 형식을 사용한다 ("추가함" ❌ → "추가" ⭕)
- 상세 설명이 필요하면 PR 본문에 작성한다

**좋은 예시**

feat(jobposting): 공고 목록 조회 API 구현 (#12)
fix(keyword): 키워드 중복 추가 오류 수정 (#34)
refactor(user): UserService에서 매칭 로직 분리 (#56)
chore: .gitignore 업데이트 (#78)

**나쁜 예시**

수정함
Update code
feat: 작업
버그수정.
FIX: BUG FIX!!!
feat(jobposting): 공고 목록 조회 API 구현 (페이징, 정렬, 필터링 지원) (#12)

마지막 예시는 50자를 초과하므로 상세 내용을 PR 본문으로 옮긴다.

---

## 코드 컨벤션

### 네이밍

- Controller: `{Domain}Controller`
- Service: `{Domain}Service`
- Repository: `{Domain}Repository`
- Request DTO: `{Action}{Domain}Request`
- Response DTO: `{Domain}Response`

### API

- URL은 복수형 명사 + 케밥케이스 (`/api/v1/job-postings`)
- 응답은 반드시 `ApiResponse<T>` 로 감싼다
- 예외는 `BusinessException` 을 던지고 `ErrorCode` 를 사용한다

### DTO

- `record` 를 사용한다
- Entity를 Controller 밖으로 노출하지 않는다
- 변환은 DTO 내부 `from()` 정적 팩토리 메서드로 처리한다

### Entity

- `@Setter` 를 사용하지 않는다
- 모든 Entity는 `BaseTimeEntity` 를 상속한다
- 기본 생성자는 `@NoArgsConstructor(access = PROTECTED)` 로 제한한다

---

## 작업 규칙

- 한 브랜치에서는 하나의 기능만 개발한다
- 한 PR은 하나의 기능만 다룬다
- 작업 시작 전 `git pull` 을 실행한다
- 머지된 브랜치는 즉시 삭제한다

## 하지 말 것

- Controller에 비즈니스 로직 작성
- `application.yml` 에 비밀값 하드코딩 (환경변수 사용)
- PRD 범위 밖 기능의 임의 추가
- 테스트 없이 대규모 리팩토링
- `feat` 브랜치에서 `main` 으로 직접 머지
