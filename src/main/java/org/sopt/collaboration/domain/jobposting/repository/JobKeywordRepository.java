package org.sopt.collaboration.domain.jobposting.repository;

import org.sopt.collaboration.domain.jobposting.entity.JobKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobKeywordRepository extends JpaRepository<JobKeyword, Long> {
}
