package org.sopt.collaboration.domain.user.dto;

import org.sopt.collaboration.domain.document.entity.DocumentType;
import org.sopt.collaboration.domain.user.entity.User;

public record UserDocumentResponse(
        boolean hasResume,
        boolean hasCoverLetter,
        boolean hasCareer
) {

    // null 은 보유하지 않은 것으로 본다 (User.hasDocument 와 같은 규칙)
    public static UserDocumentResponse from(User user) {
        return new UserDocumentResponse(
                user.hasDocument(DocumentType.RESUME),
                user.hasDocument(DocumentType.COVER_LETTER),
                user.hasDocument(DocumentType.CAREER_DESCRIPTION)
        );
    }
}
