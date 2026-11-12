package org.sopt.collaboration.domain.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.collaboration.global.common.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 이력서 보유 여부
    private Boolean hasResume;

    // 자기소개서 보유 여부
    private Boolean hasCoverLetter;

    // 경력기술서 보유 여부
    private Boolean hasCareer;

    private User(Boolean hasResume, Boolean hasCoverLetter, Boolean hasCareer) {
        this.hasResume = hasResume;
        this.hasCoverLetter = hasCoverLetter;
        this.hasCareer = hasCareer;
    }

    public static User create(Boolean hasResume, Boolean hasCoverLetter, Boolean hasCareer) {
        return new User(hasResume, hasCoverLetter, hasCareer);
    }
}
