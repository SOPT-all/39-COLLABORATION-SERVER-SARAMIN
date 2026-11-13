package org.sopt.collaboration.domain.jobposting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.collaboration.domain.jobposting.dto.JobPostingResponse;
import org.sopt.collaboration.domain.jobposting.service.JobPostingService;
import org.sopt.collaboration.global.common.ApiResponse;
import org.sopt.collaboration.global.common.CurrentUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "JobPosting", description = "채용 공고 API")
@RestController
@RequestMapping("/api/v1/job-postings")
@RequiredArgsConstructor
public class JobPostingController {

    private final JobPostingService jobPostingService;

    @Operation(
            summary = "조회한 공고 목록 조회",
            description = "이전에 조회한 공고를 최신 등록순으로 size 개까지 조회한다. "
                    + "내 키워드 일치 개수와 지원 여부를 함께 주며, "
                    + "지원한 공고는 지원 일자를, 미지원 공고는 요구 서류 중 보유 서류 개수와 등록 일자를 준다."
    )
    @GetMapping("/viewed")
    public ApiResponse<List<JobPostingResponse>> getViewedJobPostings(
            @Parameter(description = "조회 개수 (1 이상)")
            @RequestParam(defaultValue = "3") @Min(1) int size
    ) {
        return ApiResponse.success(jobPostingService.getViewedJobPostings(CurrentUser.ID, size));
    }
}
