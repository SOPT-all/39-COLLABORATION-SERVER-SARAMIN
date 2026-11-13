package org.sopt.collaboration.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.collaboration.domain.keyword.dto.KeywordResponse;
import org.sopt.collaboration.domain.user.service.UserService;
import org.sopt.collaboration.global.common.ApiResponse;
import org.sopt.collaboration.global.common.CurrentUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "사용자 API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "내 MY FIT 키워드 조회",
            description = "사용자의 MY FIT 키워드를 등록 순서대로 조회한다. "
                    + "홈 화면 MY FIT 칩과 검색 화면 칩의 초기값으로 사용한다."
    )
    @GetMapping("/me/keywords")
    public ApiResponse<List<KeywordResponse>> getMyKeywords() {
        return ApiResponse.success(userService.getMyKeywords(CurrentUser.ID));
    }
}
