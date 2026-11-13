package org.sopt.collaboration.domain.jobposting.repository;

import java.util.Collection;
import java.util.List;
import org.sopt.collaboration.domain.jobposting.entity.JobPosting;
import org.sopt.collaboration.domain.jobposting.entity.JobPostingStatus;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {

    List<JobPosting> findAllByIsViewedTrueOrderByIdDesc(Limit limit);

    /**
     * 검색어로만 검색한다. query 가 null 이면 해당 상태의 공고 전체.
     * 정렬: 마감 임박순(마감일 없는 채용 시 마감은 뒤로) → 최신 등록순
     */
    @Query(value = """
            select jp from JobPosting jp
            where jp.status = :status
              and (:query is null
                   or jp.title like concat('%', :query, '%')
                   or jp.companyName like concat('%', :query, '%'))
            order by case when jp.deadlineAt is null then 1 else 0 end,
                     jp.deadlineAt,
                     jp.id desc
            """,
            countQuery = """
            select count(jp) from JobPosting jp
            where jp.status = :status
              and (:query is null
                   or jp.title like concat('%', :query, '%')
                   or jp.companyName like concat('%', :query, '%'))
            """)
    Page<JobPosting> searchByQuery(
            @Param("status") JobPostingStatus status,
            @Param("query") String query,
            Pageable pageable
    );

    /**
     * 검색어와 키워드로 검색한다. 공고 키워드 중 하나 이상 이름이 같은 공고만 (대소문자 무시).
     * lowerKeywordNames 는 소문자로 바꾼 값이며 비어 있으면 안 된다.
     * 정렬: 일치 키워드 수 내림차순 → 마감 임박순(채용 시 마감은 뒤로) → 최신 등록순
     */
    @Query(value = """
            select jp from JobPosting jp
            where jp.status = :status
              and (:query is null
                   or jp.title like concat('%', :query, '%')
                   or jp.companyName like concat('%', :query, '%'))
              and exists (
                  select jk.id from JobKeyword jk
                  where jk.jobPosting = jp and lower(jk.keyword.name) in :lowerKeywordNames
              )
            order by (
                         select count(matched) from JobKeyword matched
                         where matched.jobPosting = jp and lower(matched.keyword.name) in :lowerKeywordNames
                     ) desc,
                     case when jp.deadlineAt is null then 1 else 0 end,
                     jp.deadlineAt,
                     jp.id desc
            """,
            countQuery = """
            select count(jp) from JobPosting jp
            where jp.status = :status
              and (:query is null
                   or jp.title like concat('%', :query, '%')
                   or jp.companyName like concat('%', :query, '%'))
              and exists (
                  select jk.id from JobKeyword jk
                  where jk.jobPosting = jp and lower(jk.keyword.name) in :lowerKeywordNames
              )
            """)
    Page<JobPosting> searchByQueryAndKeywordNames(
            @Param("status") JobPostingStatus status,
            @Param("query") String query,
            @Param("lowerKeywordNames") Collection<String> lowerKeywordNames,
            Pageable pageable
    );
}
