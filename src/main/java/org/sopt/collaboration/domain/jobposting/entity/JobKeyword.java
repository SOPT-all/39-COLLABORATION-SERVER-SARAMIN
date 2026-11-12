package org.sopt.collaboration.domain.jobposting.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.collaboration.domain.keyword.entity.Keyword;
import org.sopt.collaboration.global.common.BaseTimeEntity;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(
        name = "uk_job_keyword_job_posting_keyword",
        columnNames = {"job_posting_id", "keyword_id"}
))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JobKeyword extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_posting_id", nullable = false)
    private JobPosting jobPosting;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "keyword_id", nullable = false)
    private Keyword keyword;

    private JobKeyword(JobPosting jobPosting, Keyword keyword) {
        this.jobPosting = jobPosting;
        this.keyword = keyword;
    }

    public static JobKeyword create(JobPosting jobPosting, Keyword keyword) {
        return new JobKeyword(jobPosting, keyword);
    }
}
