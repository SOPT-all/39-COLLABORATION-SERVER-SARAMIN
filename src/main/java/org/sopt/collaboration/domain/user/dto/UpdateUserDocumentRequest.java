package org.sopt.collaboration.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * 보유 서류 체크 상태 변경 요청. 세 필드 모두 현재 체크 상태를 담아 보낸다.
 */
public record UpdateUserDocumentRequest(
        @Schema(description = "이력서 보유 여부", example = "true")
        @NotNull
        Boolean hasResume,

        @Schema(description = "자기소개서 보유 여부", example = "true")
        @NotNull
        Boolean hasCoverLetter,

        @Schema(description = "경력기술서 보유 여부", example = "false")
        @NotNull
        Boolean hasCareer
) {
}
