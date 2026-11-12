package org.sopt.collaboration.domain.application.entity;

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
import org.sopt.collaboration.domain.jobposting.entity.JobPosting;
import org.sopt.collaboration.domain.user.entity.User;
import org.sopt.collaboration.global.common.BaseTimeEntity;

// 내 지원 공고
@Entity
@Table(uniqueConstraints = @UniqueConstraint(
        name = "uk_application_user_job_posting",
        columnNames = {"user_id", "job_posting_id"}
))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Application extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_posting_id", nullable = false)
    private JobPosting jobPosting;

    private Application(User user, JobPosting jobPosting) {
        this.user = user;
        this.jobPosting = jobPosting;
    }

    public static Application create(User user, JobPosting jobPosting) {
        return new Application(user, jobPosting);
    }
}
