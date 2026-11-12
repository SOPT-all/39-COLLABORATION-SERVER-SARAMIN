package org.sopt.collaboration.domain.jobposting.entity;

import lombok.Getter;

/**
 * 고용 형태(ERD job_posting.hire_type).
 */
@Getter
public enum HireType {

    REGULAR("정규직"),
    CONTRACT("계약직"),
    INTERN("인턴");

    private final String description;

    HireType(String description) {
        this.description = description;
    }
}
