package org.sopt.collaboration.domain.jobposting.dto;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

/**
 * 공고 검색 조건. 키워드는 클라이언트가 id 를 모르므로 이름으로 받는다.
 */
public record SearchJobPostingRequest(
        @Parameter(description = "선택한 키워드 이름 (반복 파라미터, 예: keywords=Figma&keywords=프로토타입)")
        @Size(max = 20)
        List<String> keywords,

        @Parameter(description = "검색어 (공고 제목·회사명 부분 일치)")
        @Size(max = 100)
        String query,

        @Parameter(description = "페이지 번호 (0부터, 기본 0)")
        @Min(0)
        Integer page,

        @Parameter(description = "페이지 크기 (1~50, 기본 10)")
        @Min(1)
        @Max(50)
        Integer size
) {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 10;

    public SearchJobPostingRequest {
        page = page != null ? page : DEFAULT_PAGE;
        size = size != null ? size : DEFAULT_SIZE;
    }

    // 앞뒤 공백을 지우고 빈 값·중복을 뺀 키워드 이름 (요청 순서 유지)
    public List<String> keywordNames() {
        if (keywords == null) {
            return List.of();
        }
        return List.copyOf(keywords.stream()
                .filter(Objects::nonNull)
                .map(String::strip)
                .filter(name -> !name.isEmpty())
                .collect(LinkedHashSet::new, LinkedHashSet::add, LinkedHashSet::addAll));
    }

    // 앞뒤 공백을 지운 검색어. 비어 있으면 null (검색어 조건 없음)
    public String searchQuery() {
        if (query == null || query.isBlank()) {
            return null;
        }
        return query.strip();
    }
}
