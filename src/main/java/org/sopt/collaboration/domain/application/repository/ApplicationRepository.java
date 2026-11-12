package org.sopt.collaboration.domain.application.repository;

import org.sopt.collaboration.domain.application.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
}
