package org.sopt.collaboration.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.collaboration.domain.user.dto.UpdateUserDocumentRequest;
import org.sopt.collaboration.domain.user.dto.UserDocumentResponse;
import org.sopt.collaboration.domain.user.service.UserService;
import org.sopt.collaboration.global.common.ApiResponse;
import org.sopt.collaboration.global.common.CurrentUser;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "사용자 API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "보유 서류 체크 변경",
            description = "이력서·자기소개서·경력기술서 보유 체크 상태를 변경한다. "
                    + "세 값 모두 현재 체크 상태를 담아 보내야 하며, 변경된 체크 상태를 그대로 돌려준다."
    )
    @PatchMapping("/me/documents")
    public ApiResponse<UserDocumentResponse> updateDocuments(
            @Valid @RequestBody UpdateUserDocumentRequest request
    ) {
        return ApiResponse.success(userService.updateDocuments(CurrentUser.ID, request));
    }
}
