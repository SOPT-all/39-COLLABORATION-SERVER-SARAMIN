# ADR (Architecture Decision Record)

되돌리기 어렵거나 팀 전체에 영향을 주는 결정을 기록한다.
특히 `docs/DOMAIN.md` 의 미결 사항(OQ)이 확정되면 여기에 남기고 `DOMAIN.md` 를 갱신한다.

## 작성 규칙

- 파일명: `NNNN-{kebab-case-제목}.md` (예: `0001-keyword-and-or-combination.md`)
- 번호는 0001부터 순번으로 매긴다
- 한 번 머지된 ADR은 내용을 바꾸지 않는다. 결정이 바뀌면 새 ADR을 쓰고 기존 ADR의 상태를 `대체됨`으로 바꾼다

## 템플릿

```markdown
# {제목} (#{이슈번호})

- 상태: 제안 | 채택 | 대체됨 (→ NNNN)
- 날짜: YYYY-MM-DD
- 관련: OQ-NN

## 배경

어떤 문제 때문에 결정이 필요한가.

## 결정

무엇을 하기로 했는가.

## 대안

검토했지만 고르지 않은 선택지와 그 이유.

## 영향

이 결정으로 바뀌는 코드·API·문서.
```
