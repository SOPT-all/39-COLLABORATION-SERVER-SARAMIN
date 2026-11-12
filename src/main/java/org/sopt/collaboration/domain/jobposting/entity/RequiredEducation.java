package org.sopt.collaboration.domain.jobposting.entity;

import lombok.Getter;

/**
 * 요구 학력(ERD job_posting.required_education).
 */
@Getter
public enum RequiredEducation {

    UNIVERSITY_GRADUATE("대졸"),
    NOT_REQUIRED("학력무관");

    private final String description;

    RequiredEducation(String description) {
        this.description = description;
    }
}
