package org.sopt.collaboration.domain.jobposting.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.sopt.collaboration.global.common.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JobPosting extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 100)
    private String companyName;

    @Column(length = 255)
    private String companyAddress;

    // 기업 인증 여부
    private Boolean companyCertified;

    // 최소 경력(년)
    @JdbcTypeCode(SqlTypes.SMALLINT)
    @Column(nullable = false)
    private Integer minCareerYears;

    // 최대 경력(년), null 이면 상한 없음
    @JdbcTypeCode(SqlTypes.SMALLINT)
    private Integer maxCareerYears;

    // enum 칼럼은 MySQL 네이티브 enum 대신 VARCHAR(30) 으로 저장한다.
    // ddl-auto: update 는 기존 칼럼 타입·길이를 바꾸지 않으므로 값 추가에 대비해 여유를 둔다
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 30)
    private DeadlineType deadlineType;

    private LocalDateTime deadlineAt;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 30)
    private JobPostingStatus status;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(length = 30)
    private RequiredEducation requiredEducation;

    private Integer ranking;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(length = 30)
    private HireType hireType;

    private Boolean isViewed;

    // 이력서 요구 여부
    private Boolean resumeRequired;

    // 자기소개서 요구 여부
    @Column(name = "coverletter_required")
    private Boolean coverLetterRequired;

    // 경력기술서 요구 여부
    private Boolean careerRequired;

    private JobPosting(
            String title,
            String companyName,
            String companyAddress,
            Boolean companyCertified,
            Integer minCareerYears,
            Integer maxCareerYears,
            DeadlineType deadlineType,
            LocalDateTime deadlineAt,
            JobPostingStatus status,
            RequiredEducation requiredEducation,
            Integer ranking,
            HireType hireType,
            Boolean isViewed,
            Boolean resumeRequired,
            Boolean coverLetterRequired,
            Boolean careerRequired
    ) {
        this.title = title;
        this.companyName = companyName;
        this.companyAddress = companyAddress;
        this.companyCertified = companyCertified;
        this.minCareerYears = minCareerYears != null ? minCareerYears : 0;
        this.maxCareerYears = maxCareerYears;
        this.deadlineType = deadlineType != null ? deadlineType : DeadlineType.DATE;
        this.deadlineAt = deadlineAt;
        this.status = status != null ? status : JobPostingStatus.OPEN;
        this.requiredEducation = requiredEducation;
        this.ranking = ranking;
        this.hireType = hireType;
        this.isViewed = isViewed;
        this.resumeRequired = resumeRequired;
        this.coverLetterRequired = coverLetterRequired;
        this.careerRequired = careerRequired;
    }

    public static JobPosting create(
            String title,
            String companyName,
            String companyAddress,
            Boolean companyCertified,
            Integer minCareerYears,
            Integer maxCareerYears,
            DeadlineType deadlineType,
            LocalDateTime deadlineAt,
            JobPostingStatus status,
            RequiredEducation requiredEducation,
            Integer ranking,
            HireType hireType,
            Boolean isViewed,
            Boolean resumeRequired,
            Boolean coverLetterRequired,
            Boolean careerRequired
    ) {
        return new JobPosting(
                title,
                companyName,
                companyAddress,
                companyCertified,
                minCareerYears,
                maxCareerYears,
                deadlineType,
                deadlineAt,
                status,
                requiredEducation,
                ranking,
                hireType,
                isViewed,
                resumeRequired,
                coverLetterRequired,
                careerRequired
        );
    }
}
