package org.sopt.collaboration.domain.application.repository;

import java.util.Collection;
import java.util.List;
import org.sopt.collaboration.domain.application.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findAllByUserIdAndJobPostingIdIn(Long userId, Collection<Long> jobPostingIds);
}
