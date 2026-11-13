package org.sopt.collaboration.domain.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.collaboration.domain.document.entity.DocumentType;
import org.sopt.collaboration.global.common.BaseTimeEntity;

@Entity
@Table(name = "users")
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

    // 서류 보유 체크 상태를 한 번에 갱신한다 (세 값 모두 현재 상태 그대로 받는다)
    public void updateDocuments(boolean hasResume, boolean hasCoverLetter, boolean hasCareer) {
        this.hasResume = hasResume;
        this.hasCoverLetter = hasCoverLetter;
        this.hasCareer = hasCareer;
    }

    // 서류 보유 여부. null 은 보유하지 않은 것으로 본다
    public boolean hasDocument(DocumentType documentType) {
        Boolean has = switch (documentType) {
            case RESUME -> hasResume;
            case COVER_LETTER -> hasCoverLetter;
            case CAREER_DESCRIPTION -> hasCareer;
        };
        return Boolean.TRUE.equals(has);
    }
}
