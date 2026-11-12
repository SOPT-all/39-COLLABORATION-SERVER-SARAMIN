package org.sopt.collaboration.domain.user.repository;

import org.sopt.collaboration.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
