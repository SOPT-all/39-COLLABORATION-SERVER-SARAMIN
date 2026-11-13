package org.sopt.collaboration.domain.jobposting.entity;

import lombok.Getter;

/**
 * 요구 학력(ERD job_posting.required_education).
 */
@Getter
public enum RequiredEducation {

    HIGH_SCHOOL_GRADUATE("고졸"),
    COLLEGE_GRADUATE("초대졸"),
    UNIVERSITY_GRADUATE("대졸"),
    MASTER("석사"),
    NOT_REQUIRED("학력무관");

    private final String description;

    RequiredEducation(String description) {
        this.description = description;
    }
}
