package org.sopt.collaboration.domain.user.repository;

import org.sopt.collaboration.domain.user.entity.UserKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserKeywordRepository extends JpaRepository<UserKeyword, Long> {
}
