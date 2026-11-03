---
name: conventions
description: 이 프로젝트(사람인 리디자인 · 계층형 · Spring Boot 4 · Java 21)의 코드 컨벤션을 적용하는 스킬. Controller·Service·Repository·Entity·DTO·예외·테스트 코드를 작성하거나 리뷰할 때 /conventions로 호출하면 필요한 문서를 읽고 규칙에 맞게 작성한 뒤 체크리스트로 자가 검증한다.
---

# 컨벤션 적용 워크플로우

코드 작성·리뷰 전에 이 순서를 따른다.

## Step 1 — 규칙 확인

`CLAUDE.md`의 "코드 컨벤션"과 "하지 말 것"을 기준으로 삼는다.

## Step 2 — 작업에 필요한 문서만 읽기

모든 문서를 한 번에 읽지 않는다.

| 작업 유형 | 읽을 문서 |
|---|---|
| 새 도메인 기능 추가, 계층 배치, 패키지 위치 | `docs/ARCHITECTURE.md` (패키지 구조·계층별 책임) |
| 공고·키워드·저장·서류·경험 일치 관련 로직 | `docs/DOMAIN.md` (용어 정의·미결 사항) |
| 목록 조회, 연관관계, 인덱스 | `docs/ARCHITECTURE.md` (성능 고려사항) |
| 응답 포맷, 에러 코드 추가 | `docs/ARCHITECTURE.md` (공통 규약) + `global/exception/ErrorCode.java` |
| 미결 사항에 걸리는 결정 | `docs/DOMAIN.md` 미결 사항 + `docs/adr/` |

미결 사항(OQ)에 해당하는 동작은 임의로 정하지 않고 사용자에게 확인한다.

## Step 3 — 자가 검증

코드 작성 후 아래를 확인한다.

**계층·구조**
- [ ] Controller에 비즈니스 로직이 없는가 (요청 수신·DTO 변환·응답 반환만)
- [ ] Entity가 Service 밖으로 나가지 않고, Controller가 `{Domain}Response`를 반환하는가
- [ ] 새 클래스가 해당 도메인의 `controller/service/repository/entity/dto` 패키지에 있는가

**API·DTO**
- [ ] URL이 `/api/v1`로 시작하고 복수형 명사·케밥케이스인가
- [ ] 응답을 `ApiResponse<T>`로 감쌌는가
- [ ] DTO가 `record`이고 변환은 `from()` 정적 팩토리인가
- [ ] 요청 DTO에 Bean Validation을 선언하고 Controller에서 `@Valid`로 트리거하는가

**Entity**
- [ ] `@Setter`가 없고 `BaseTimeEntity`를 상속하는가
- [ ] 기본 생성자가 `@NoArgsConstructor(access = PROTECTED)`인가
- [ ] 연관관계가 `LAZY`이고, 목록 조회에 `fetch join` 또는 `@BatchSize`를 적용했는가

**예외·DI**
- [ ] 비즈니스 예외를 `BusinessException(ErrorCode)`로 던지고, 에러 응답을 직접 만들지 않는가
- [ ] 생성자 주입 + `private final`인가 (필드 주입 금지)

**도메인**
- [ ] 체크(클라이언트 상태)와 저장(서버 영속)을 섞지 않았는가
- [ ] 경험 일치가 스킬 교집합 규칙 기반이고 LLM 호출·추정이 없는가
- [ ] PRD 범위 밖 기능을 추가하지 않았는가

**테스트·빌드**
- [ ] 테스트명이 한국어 행위 서술이고 given-when-then 구조인가
- [ ] 스프링 컨텍스트를 띄우는 테스트에 `@ActiveProfiles("test")`가 있는가
- [ ] `./gradlew clean build`가 통과하는가
