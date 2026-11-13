package org.sopt.collaboration.domain.jobposting.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import org.sopt.collaboration.domain.jobposting.entity.JobPosting;
import org.sopt.collaboration.domain.keyword.entity.Keyword;

public record SearchJobPostingResponse(
        Long id,
        String title,
        String companyName,
        String companyAddress,
        Integer minCareerYears,
        Integer maxCareerYears,
        String requiredEducation,
        String hireType,
        String deadlineType,
        LocalDateTime deadlineAt,
        Integer ranking,
        // 공고 키워드 이름 전체
        List<String> keywords,
        // 공고 키워드 중 검색 키워드와 이름이 같은 것
        List<String> matchedKeywords,
        // 공고 키워드 총 개수 (일치 개수의 분모)
        int totalKeywordCount
) {

    public static SearchJobPostingResponse from(
            JobPosting jobPosting,
            List<Keyword> keywords,
            List<String> matchedKeywords
    ) {
        // 이름이 없는 키워드는 표시할 수 없으므로 목록과 총 개수에서 뺀다
        List<String> keywordNames = keywords.stream()
                .map(Keyword::getName)
                .filter(Objects::nonNull)
                .toList();
        return new SearchJobPostingResponse(
                jobPosting.getId(),
                jobPosting.getTitle(),
                jobPosting.getCompanyName(),
                jobPosting.getCompanyAddress(),
                jobPosting.getMinCareerYears(),
                jobPosting.getMaxCareerYears(),
                jobPosting.getRequiredEducation() != null ? jobPosting.getRequiredEducation().getDescription() : null,
                jobPosting.getHireType() != null ? jobPosting.getHireType().getDescription() : null,
                jobPosting.getDeadlineType().getDescription(),
                jobPosting.getDeadlineAt(),
                jobPosting.getRanking(),
                keywordNames,
                matchedKeywords,
                keywordNames.size()
        );
    }
}
