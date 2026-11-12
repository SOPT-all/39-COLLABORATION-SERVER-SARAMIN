package org.sopt.collaboration.domain.keyword.repository;

import org.sopt.collaboration.domain.keyword.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
}
