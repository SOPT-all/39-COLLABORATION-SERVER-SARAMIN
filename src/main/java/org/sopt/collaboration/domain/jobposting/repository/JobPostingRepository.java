package org.sopt.collaboration.domain.jobposting.repository;

import java.util.List;
import org.sopt.collaboration.domain.jobposting.entity.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {

    List<JobPosting> findAllByIsViewedTrueOrderByIdDesc();
}
