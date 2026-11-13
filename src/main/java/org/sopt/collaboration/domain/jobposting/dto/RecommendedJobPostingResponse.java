package org.sopt.collaboration.domain.jobposting.dto;

import java.util.List;
import org.sopt.collaboration.domain.keyword.dto.KeywordResponse;

/**
 * 추천 공고 조회 응답. 공고 카드는 메인 화면 공고 조회 API 공용 형식(JobPostingResponse)을 쓴다.
 *
 * @param myFitKeywords 내 MY FIT 키워드 (검색 화면 초기 칩 목록)
 * @param jobPostings   추천 공고 카드 목록
 */
public record RecommendedJobPostingResponse(
        List<KeywordResponse> myFitKeywords,
        List<JobPostingResponse> jobPostings
) {

    public static RecommendedJobPostingResponse from(
            List<KeywordResponse> myFitKeywords,
            List<JobPostingResponse> jobPostings
    ) {
        return new RecommendedJobPostingResponse(myFitKeywords, jobPostings);
    }
}
