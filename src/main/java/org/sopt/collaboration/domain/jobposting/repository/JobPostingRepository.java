package org.sopt.collaboration.domain.jobposting.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.sopt.collaboration.domain.jobposting.entity.DeadlineType;
import org.sopt.collaboration.domain.jobposting.entity.JobPosting;
import org.sopt.collaboration.domain.jobposting.entity.JobPostingStatus;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {

    List<JobPosting> findAllByIsViewedTrueOrderByIdDesc(Limit limit);

    // 지정한 상태·마감 유형 중 기준 시각 이후 마감인 공고를 마감일이 가까운 순으로 조회한다
    @Query("""
            select jp from JobPosting jp
            where jp.status = :status
              and jp.deadlineType = :deadlineType
              and jp.deadlineAt >= :from
            order by jp.deadlineAt asc, jp.id asc
            """)
    List<JobPosting> findDeadlineSoon(
            @Param("status") JobPostingStatus status,
            @Param("deadlineType") DeadlineType deadlineType,
            @Param("from") LocalDateTime from,
            Limit limit
    );
}
