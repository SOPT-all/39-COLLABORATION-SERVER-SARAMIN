package org.sopt.collaboration.domain.jobposting.entity;

import lombok.Getter;

/**
 * 공고 상태(ERD job_posting.status).
 */
@Getter
public enum JobPostingStatus {

    OPEN("모집 중"),
    CLOSED("모집 마감");

    private final String description;

    JobPostingStatus(String description) {
        this.description = description;
    }
}
