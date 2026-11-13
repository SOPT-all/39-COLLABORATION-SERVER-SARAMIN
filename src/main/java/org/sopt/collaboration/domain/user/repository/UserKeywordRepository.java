package org.sopt.collaboration.domain.user.repository;

import java.util.List;
import org.sopt.collaboration.domain.user.entity.UserKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserKeywordRepository extends JpaRepository<UserKeyword, Long> {

    @Query("select uk.keyword.id from UserKeyword uk where uk.user.id = :userId")
    List<Long> findKeywordIdsByUserId(@Param("userId") Long userId);

    @Query("""
            select uk from UserKeyword uk
            join fetch uk.keyword
            where uk.user.id = :userId
            order by uk.id
            """)
    List<UserKeyword> findAllWithKeywordByUserId(@Param("userId") Long userId);
}
