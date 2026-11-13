package org.sopt.collaboration.domain.jobposting.service;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.sopt.collaboration.domain.application.entity.Application;
import org.sopt.collaboration.domain.application.repository.ApplicationRepository;
import org.sopt.collaboration.domain.jobposting.dto.JobPostingResponse;
import org.sopt.collaboration.domain.jobposting.entity.DeadlineType;
import org.sopt.collaboration.domain.jobposting.entity.JobKeyword;
import org.sopt.collaboration.domain.jobposting.entity.JobPosting;
import org.sopt.collaboration.domain.jobposting.entity.JobPostingStatus;
import org.sopt.collaboration.domain.jobposting.repository.JobKeywordRepository;
import org.sopt.collaboration.domain.jobposting.repository.JobPostingRepository;
import org.sopt.collaboration.domain.keyword.entity.Keyword;
import org.sopt.collaboration.domain.user.entity.User;
import org.sopt.collaboration.domain.user.repository.UserKeywordRepository;
import org.sopt.collaboration.domain.user.repository.UserRepository;
import org.sopt.collaboration.global.exception.BusinessException;
import org.sopt.collaboration.global.exception.ErrorCode;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobPostingService {

    private final JobPostingRepository jobPostingRepository;
    private final JobKeywordRepository jobKeywordRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final UserKeywordRepository userKeywordRepository;

    public List<JobPostingResponse> getViewedJobPostings(Long userId, int size) {
        User user = findUser(userId);
        List<JobPosting> jobPostings = jobPostingRepository.findAllByIsViewedTrueOrderByIdDesc(Limit.of(size));
        return toResponses(user, jobPostings);
    }

    // 모집 중이고 아직 마감되지 않은 날짜 마감 공고. 채용 시 마감 공고는 마감일이 없어 제외한다
    public List<JobPostingResponse> getDeadlineSoonJobPostings(Long userId, int size) {
        User user = findUser(userId);
        List<JobPosting> jobPostings = jobPostingRepository.findDeadlineSoon(
                JobPostingStatus.OPEN,
                DeadlineType.DATE,
                LocalDateTime.now(),
                Limit.of(size)
        );
        return toResponses(user, jobPostings);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    private List<JobPostingResponse> toResponses(User user, List<JobPosting> jobPostings) {
        if (jobPostings.isEmpty()) {
            return List.of();
        }

        // 공고 수와 관계없이 키워드·지원 내역을 각각 쿼리 1번으로 조회한다
        List<Long> jobPostingIds = jobPostings.stream()
                .map(JobPosting::getId)
                .toList();
        Map<Long, List<Keyword>> keywordsByJobPosting = jobKeywordRepository
                .findAllWithKeywordByJobPostingIdIn(jobPostingIds)
                .stream()
                .collect(groupingBy(
                        jobKeyword -> jobKeyword.getJobPosting().getId(),
                        mapping(JobKeyword::getKeyword, toList())
                ));
        Set<Long> myKeywordIds = new HashSet<>(userKeywordRepository.findKeywordIdsByUserId(user.getId()));
        Map<Long, Application> applicationByJobPosting = applicationRepository
                .findAllByUserIdAndJobPostingIdIn(user.getId(), jobPostingIds)
                .stream()
                .collect(toMap(application -> application.getJobPosting().getId(), Function.identity()));

        return jobPostings.stream()
                .map(jobPosting -> {
                    List<Keyword> keywords = keywordsByJobPosting.getOrDefault(jobPosting.getId(), List.of());
                    return JobPostingResponse.from(
                            jobPosting,
                            user,
                            keywords,
                            countMatchedKeywords(keywords, myKeywordIds),
                            applicationByJobPosting.get(jobPosting.getId())
                    );
                })
                .toList();
    }

    // 공고 키워드와 내 키워드의 교집합 개수 (규칙 기반, 추정 없음)
    private long countMatchedKeywords(List<Keyword> keywords, Set<Long> myKeywordIds) {
        return keywords.stream()
                .filter(keyword -> myKeywordIds.contains(keyword.getId()))
                .count();
    }
}
