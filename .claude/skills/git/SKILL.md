---
name: git
description: 이 프로젝트의 Git 컨벤션을 적용하는 스킬. 커밋 메시지 작성, 브랜치 생성·네이밍, 이슈·PR 작성 시 /git으로 호출하면 CLAUDE.md의 깃 컨벤션을 읽고 규칙에 맞게 작성한 뒤 자가 검증한다.
---

# Git 컨벤션 적용 워크플로우

커밋·브랜치·이슈·PR 작업 전에 이 순서를 따른다.

## Step 1 — 규칙 확인

`CLAUDE.md`의 "깃 컨벤션" 섹션을 기준으로 삼는다.

## Step 2 — 작업 유형별 적용

| 작업 | 규칙 |
|---|---|
| 커밋 | `<type>(<scope>): <subject> (#이슈번호)` 싱글 라인 |
| 브랜치 생성 | `feat/#{이슈번호}/{기능명}`, `hotfix/#{이슈번호}/{버그명}`, `release/{버전}` |
| 이슈 | 제목 `[Type] 작업 내용`, `.github/ISSUE_TEMPLATE` 사용 |
| PR | 제목 `[Type] 작업 내용 (#이슈번호)`, `.github/pull_request_template.md` 모든 항목 작성 |

## Step 3 — 자가 검증

**커밋**
- [ ] type이 목록(feat/fix/docs/refactor/perf/test/build/ci/chore/revert)에 있는가
- [ ] scope가 목록(user/jobposting/keyword/bookmark/document/common/config/deps)에 있는가
- [ ] subject가 50자 이내·마침표 없음·명령문인가
- [ ] 싱글 라인이고 `Co-Authored-By` 등 트레일러가 없는가
- [ ] 한 커밋이 하나의 type으로 설명되는가 (두 type이 필요하면 쪼갠다)
- [ ] 각 커밋 시점에 `./gradlew clean build`가 통과하는가

**브랜치·PR**
- [ ] 작업 전 `git pull`을 했는가
- [ ] 브랜치명이 허용 패턴이고 소문자인가
- [ ] `feat` 브랜치의 PR 대상이 `develop`인가 (`main` 직접 머지 금지)
- [ ] PR 하나가 기능 하나만 다루는가
- [ ] PR 본문에 `closes #이슈번호`가 있는가
