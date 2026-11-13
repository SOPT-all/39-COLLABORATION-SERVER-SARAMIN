package org.sopt.collaboration.domain.jobposting.service;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.sopt.collaboration.domain.application.entity.Application;
import org.sopt.collaboration.domain.application.repository.ApplicationRepository;
import org.sopt.collaboration.domain.jobposting.dto.JobPostingResponse;
import org.sopt.collaboration.domain.jobposting.dto.SearchJobPostingRequest;
import org.sopt.collaboration.domain.jobposting.dto.SearchJobPostingResponse;
import org.sopt.collaboration.domain.jobposting.entity.JobKeyword;
import org.sopt.collaboration.domain.jobposting.entity.JobPosting;
import org.sopt.collaboration.domain.jobposting.entity.JobPostingStatus;
import org.sopt.collaboration.domain.jobposting.repository.JobKeywordRepository;
import org.sopt.collaboration.domain.jobposting.repository.JobPostingRepository;
import org.sopt.collaboration.domain.keyword.entity.Keyword;
import org.sopt.collaboration.domain.user.entity.User;
import org.sopt.collaboration.domain.user.repository.UserKeywordRepository;
import org.sopt.collaboration.domain.user.repository.UserRepository;
import org.sopt.collaboration.global.common.PageResponse;
import org.sopt.collaboration.global.exception.BusinessException;
import org.sopt.collaboration.global.exception.ErrorCode;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        List<JobPosting> jobPostings = jobPostingRepository.findAllByIsViewedTrueOrderByIdDesc(Limit.of(size));
        if (jobPostings.isEmpty()) {
            return List.of();
        }

        // 공고 수와 관계없이 키워드·지원 내역을 각각 쿼리 1번으로 조회한다
        List<Long> jobPostingIds = toIds(jobPostings);
        Map<Long, List<Keyword>> keywordsByJobPosting = findKeywordsByJobPosting(jobPostingIds);
        Set<Long> myKeywordIds = new HashSet<>(userKeywordRepository.findKeywordIdsByUserId(userId));
        Map<Long, Application> applicationByJobPosting = applicationRepository
                .findAllByUserIdAndJobPostingIdIn(userId, jobPostingIds)
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

    /**
     * 모집 중인 공고를 검색어와 키워드로 검색해 offset 페이지로 반환한다.
     * 키워드를 보내면 공고 키워드 중 하나 이상 이름이 같은 공고만 반환한다 (OR, OQ-01 확정 전 임시).
     * 키워드 이름은 대소문자를 무시하고 비교한다 (DB 필터·정렬과 일치 키워드 계산이 같은 규칙을 쓰도록).
     */
    public PageResponse<SearchJobPostingResponse> searchJobPostings(SearchJobPostingRequest request) {
        Set<String> lowerKeywordNames = request.keywordNames().stream()
                .map(name -> name.toLowerCase(Locale.ROOT))
                .collect(toCollection(LinkedHashSet::new));
        Pageable pageable = PageRequest.of(request.page(), request.size());

        Page<JobPosting> jobPostings = lowerKeywordNames.isEmpty()
                ? jobPostingRepository.searchByQuery(JobPostingStatus.OPEN, request.searchQuery(), pageable)
                : jobPostingRepository.searchByQueryAndKeywordNames(
                        JobPostingStatus.OPEN, request.searchQuery(), lowerKeywordNames, pageable);
        if (jobPostings.isEmpty()) {
            return PageResponse.from(jobPostings, List.of());
        }

        // 현재 페이지 공고의 키워드만 쿼리 1번으로 조회한다
        Map<Long, List<Keyword>> keywordsByJobPosting = findKeywordsByJobPosting(toIds(jobPostings.getContent()));
        List<SearchJobPostingResponse> content = jobPostings.getContent().stream()
                .map(jobPosting -> {
                    List<Keyword> keywords = keywordsByJobPosting.getOrDefault(jobPosting.getId(), List.of());
                    return SearchJobPostingResponse.from(
                            jobPosting,
                            keywords,
                            findMatchedKeywordNames(keywords, lowerKeywordNames)
                    );
                })
                .toList();
        return PageResponse.from(jobPostings, content);
    }

    private List<Long> toIds(List<JobPosting> jobPostings) {
        return jobPostings.stream()
                .map(JobPosting::getId)
                .toList();
    }

    // 여러 공고의 키워드를 쿼리 1번으로 조회해 공고 id 별로 묶는다 (N+1 방지)
    private Map<Long, List<Keyword>> findKeywordsByJobPosting(List<Long> jobPostingIds) {
        return jobKeywordRepository.findAllWithKeywordByJobPostingIdIn(jobPostingIds)
                .stream()
                .collect(groupingBy(
                        jobKeyword -> jobKeyword.getJobPosting().getId(),
                        mapping(JobKeyword::getKeyword, toList())
                ));
    }

    // 공고 키워드와 내 키워드의 교집합 개수 (규칙 기반, 추정 없음)
    private long countMatchedKeywords(List<Keyword> keywords, Set<Long> myKeywordIds) {
        return keywords.stream()
                .filter(keyword -> myKeywordIds.contains(keyword.getId()))
                .count();
    }

    // 공고 키워드 중 검색 키워드와 이름이 같은 것, 대소문자 무시 (규칙 기반, 추정 없음)
    private List<String> findMatchedKeywordNames(List<Keyword> keywords, Set<String> lowerKeywordNames) {
        return keywords.stream()
                .map(Keyword::getName)
                .filter(Objects::nonNull)
                .filter(name -> lowerKeywordNames.contains(name.toLowerCase(Locale.ROOT)))
                .distinct()
                .toList();
    }
}
