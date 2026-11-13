package org.sopt.collaboration.domain.jobposting.repository;

import java.util.Collection;
import java.util.List;
import org.sopt.collaboration.domain.jobposting.entity.JobKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JobKeywordRepository extends JpaRepository<JobKeyword, Long> {

    // 여러 공고의 키워드를 한 번에 조회한다 (공고별 조회 시 N+1 방지)
    @Query("""
            select jk from JobKeyword jk
            join fetch jk.keyword
            where jk.jobPosting.id in :jobPostingIds
            order by jk.id
            """)
    List<JobKeyword> findAllWithKeywordByJobPostingIdIn(@Param("jobPostingIds") Collection<Long> jobPostingIds);
}
