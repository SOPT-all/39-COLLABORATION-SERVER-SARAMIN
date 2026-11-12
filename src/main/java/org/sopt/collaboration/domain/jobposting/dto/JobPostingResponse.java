package org.sopt.collaboration.domain.jobposting.dto;

import java.time.LocalDateTime;
import java.util.List;
import org.sopt.collaboration.domain.application.entity.Application;
import org.sopt.collaboration.domain.document.entity.DocumentType;
import org.sopt.collaboration.domain.jobposting.entity.JobPosting;
import org.sopt.collaboration.domain.keyword.entity.Keyword;
import org.sopt.collaboration.domain.user.entity.User;

public record JobPostingResponse(
        Long id,
        String title,
        String companyName,
        String companyAddress,
        Boolean companyCertified,
        String hireType,
        String requiredEducation,
        Integer minCareerYears,
        Integer maxCareerYears,
        String deadlineType,
        LocalDateTime deadlineAt,
        String status,
        List<String> keywords,
        long matchedKeywordCount,
        boolean applied,
        // 미지원 공고만 채운다
        Integer requiredDocumentCount,
        Integer matchedDocumentCount,
        List<String> missingDocuments,
        LocalDateTime postedAt,
        // 지원한 공고만 채운다
        LocalDateTime appliedAt
) {

    /**
     * @param application 사용자의 지원 내역. 지원하지 않은 공고면 null
     */
    public static JobPostingResponse from(
            JobPosting jobPosting,
            User user,
            List<Keyword> keywords,
            long matchedKeywordCount,
            Application application
    ) {
        boolean applied = application != null;
        List<DocumentType> requiredDocuments = jobPosting.getRequiredDocuments();
        List<DocumentType> missingDocuments = jobPosting.getMissingDocuments(user);
        return new JobPostingResponse(
                jobPosting.getId(),
                jobPosting.getTitle(),
                jobPosting.getCompanyName(),
                jobPosting.getCompanyAddress(),
                jobPosting.getCompanyCertified(),
                jobPosting.getHireType() != null ? jobPosting.getHireType().getDescription() : null,
                jobPosting.getRequiredEducation() != null ? jobPosting.getRequiredEducation().getDescription() : null,
                jobPosting.getMinCareerYears(),
                jobPosting.getMaxCareerYears(),
                jobPosting.getDeadlineType().getDescription(),
                jobPosting.getDeadlineAt(),
                jobPosting.getStatus().getDescription(),
                keywords.stream().map(Keyword::getName).toList(),
                matchedKeywordCount,
                applied,
                applied ? null : requiredDocuments.size(),
                applied ? null : requiredDocuments.size() - missingDocuments.size(),
                applied ? null : missingDocuments.stream().map(DocumentType::getDescription).toList(),
                applied ? null : jobPosting.getCreatedAt(),
                applied ? application.getCreatedAt() : null
        );
    }
}
