package org.sopt.collaboration.domain.jobposting.entity;

import lombok.Getter;

/**
 * 공고 마감 유형(ERD job_posting.deadline_type).
 */
@Getter
public enum DeadlineType {

    DATE("마감 일자 마감"),
    UNTIL_FILLED("채용 시 마감");

    private final String description;

    DeadlineType(String description) {
        this.description = description;
    }
}
