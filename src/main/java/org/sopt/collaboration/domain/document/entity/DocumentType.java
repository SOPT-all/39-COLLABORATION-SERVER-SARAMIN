package org.sopt.collaboration.domain.document.entity;

import lombok.Getter;

/**
 * 서류 종류(ERD users.has_* 보유 여부, job_posting.*_required 요구 여부 칼럼에 대응).
 */
@Getter
public enum DocumentType {

    RESUME("이력서"),
    COVER_LETTER("자기소개서"),
    CAREER_DESCRIPTION("경력기술서");

    private final String description;

    DocumentType(String description) {
        this.description = description;
    }
}
