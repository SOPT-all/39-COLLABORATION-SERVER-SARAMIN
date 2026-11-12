package org.sopt.collaboration.domain.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.collaboration.domain.keyword.entity.Keyword;
import org.sopt.collaboration.global.common.BaseTimeEntity;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(
        name = "uk_user_keyword_user_keyword",
        columnNames = {"user_id", "keyword_id"}
))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserKeyword extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "keyword_id", nullable = false)
    private Keyword keyword;

    private UserKeyword(User user, Keyword keyword) {
        this.user = user;
        this.keyword = keyword;
    }

    public static UserKeyword create(User user, Keyword keyword) {
        return new UserKeyword(user, keyword);
    }
}
