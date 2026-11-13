package org.sopt.collaboration.domain.jobposting.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.sopt.collaboration.domain.application.entity.Application;
import org.sopt.collaboration.domain.application.repository.ApplicationRepository;
import org.sopt.collaboration.domain.jobposting.dto.JobPostingResponse;
import org.sopt.collaboration.domain.jobposting.dto.RecommendedJobPostingResponse;
import org.sopt.collaboration.domain.jobposting.entity.DeadlineType;
import org.sopt.collaboration.domain.jobposting.entity.JobKeyword;
import org.sopt.collaboration.domain.jobposting.entity.JobPosting;
import org.sopt.collaboration.domain.jobposting.entity.JobPostingStatus;
import org.sopt.collaboration.domain.jobposting.repository.JobKeywordRepository;
import org.sopt.collaboration.domain.jobposting.repository.JobPostingRepository;
import org.sopt.collaboration.domain.keyword.dto.KeywordResponse;
import org.sopt.collaboration.domain.keyword.entity.Keyword;
import org.sopt.collaboration.domain.keyword.repository.KeywordRepository;
import org.sopt.collaboration.domain.user.entity.User;
import org.sopt.collaboration.domain.user.entity.UserKeyword;
import org.sopt.collaboration.domain.user.repository.UserKeywordRepository;
import org.sopt.collaboration.domain.user.repository.UserRepository;
import org.sopt.collaboration.global.exception.BusinessException;
import org.sopt.collaboration.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class JobPostingServiceTest {

    @Autowired
    private JobPostingService jobPostingService;

    @Autowired
    private JobPostingRepository jobPostingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KeywordRepository keywordRepository;

    @Autowired
    private JobKeywordRepository jobKeywordRepository;

    @Autowired
    private UserKeywordRepository userKeywordRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = userRepository.save(User.create(true, false, false));
    }

    private JobPosting saveJobPosting(String title, DeadlineType deadlineType, LocalDateTime deadlineAt,
                                      JobPostingStatus status) {
        return jobPostingRepository.save(JobPosting.create(
                title, "회사", null, null, 0, null,
                deadlineType, deadlineAt, status,
                null, null, null, false,
                true, true, false
        ));
    }

    @Nested
    @DisplayName("마감 임박 공고 조회")
    class GetDeadlineSoonJobPostings {

        @Test
        @DisplayName("모집 중이고 마감되지 않은 날짜 마감 공고만 마감일이 가까운 순으로 조회한다")
        void 마감_임박_공고만_마감일_오름차순으로_조회한다() {
            // given
            LocalDateTime now = LocalDateTime.now();
            saveJobPosting("3일 뒤 마감", DeadlineType.DATE, now.plusDays(3), JobPostingStatus.OPEN);
            saveJobPosting("1일 뒤 마감", DeadlineType.DATE, now.plusDays(1), JobPostingStatus.OPEN);
            saveJobPosting("이미 마감", DeadlineType.DATE, now.minusDays(1), JobPostingStatus.OPEN);
            saveJobPosting("모집 종료", DeadlineType.DATE, now.plusHours(1), JobPostingStatus.CLOSED);
            saveJobPosting("채용 시 마감", DeadlineType.UNTIL_FILLED, null, JobPostingStatus.OPEN);

            // when
            List<JobPostingResponse> result = jobPostingService.getDeadlineSoonJobPostings(user.getId(), 10);

            // then
            assertThat(result)
                    .extracting(JobPostingResponse::title)
                    .containsExactly("1일 뒤 마감", "3일 뒤 마감");
        }

        @Test
        @DisplayName("size 개수만큼만 조회한다")
        void size_개수만큼만_조회한다() {
            // given
            LocalDateTime now = LocalDateTime.now();
            for (int day = 1; day <= 5; day++) {
                saveJobPosting(day + "일 뒤 마감", DeadlineType.DATE, now.plusDays(day), JobPostingStatus.OPEN);
            }

            // when
            List<JobPostingResponse> result = jobPostingService.getDeadlineSoonJobPostings(user.getId(), 3);

            // then
            assertThat(result)
                    .extracting(JobPostingResponse::title)
                    .containsExactly("1일 뒤 마감", "2일 뒤 마감", "3일 뒤 마감");
        }

        @Test
        @DisplayName("마감 임박 공고가 없으면 빈 목록을 반환한다")
        void 마감_임박_공고가_없으면_빈_목록을_반환한다() {
            // given
            saveJobPosting("채용 시 마감", DeadlineType.UNTIL_FILLED, null, JobPostingStatus.OPEN);

            // when
            List<JobPostingResponse> result = jobPostingService.getDeadlineSoonJobPostings(user.getId(), 3);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("미지원 공고는 요구 서류 중 부족한 서류를 함께 반환한다")
        void 미지원_공고는_부족한_서류를_반환한다() {
            // given: 사용자는 이력서만 보유, 공고는 이력서·자기소개서 요구
            saveJobPosting("마감 임박", DeadlineType.DATE, LocalDateTime.now().plusDays(1), JobPostingStatus.OPEN);

            // when
            JobPostingResponse result = jobPostingService.getDeadlineSoonJobPostings(user.getId(), 3).get(0);

            // then
            assertThat(result.applied()).isFalse();
            assertThat(result.requiredDocumentCount()).isEqualTo(2);
            assertThat(result.matchedDocumentCount()).isEqualTo(1);
            assertThat(result.missingDocuments()).containsExactly("자기소개서");
        }

        @Test
        @DisplayName("사용자가 없으면 USER_NOT_FOUND 예외를 던진다")
        void 사용자가_없으면_예외를_던진다() {
            // given
            Long notExistUserId = user.getId() + 1000;

            // when & then
            assertThatThrownBy(() -> jobPostingService.getDeadlineSoonJobPostings(notExistUserId, 3))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.USER_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("추천 공고 조회")
    class GetRecommendedJobPostings {

        private Keyword java;
        private Keyword spring;
        private Keyword figma;

        @BeforeEach
        void setUpKeywords() {
            java = keywordRepository.save(Keyword.create("Java", "툴"));
            spring = keywordRepository.save(Keyword.create("Spring", "툴"));
            figma = keywordRepository.save(Keyword.create("Figma", "툴"));
            userKeywordRepository.save(UserKeyword.create(user, java));
            userKeywordRepository.save(UserKeyword.create(user, spring));
        }

        private JobPosting saveJobPosting(String title, LocalDateTime deadlineAt, JobPostingStatus status,
                                          Keyword... keywords) {
            DeadlineType deadlineType = deadlineAt != null ? DeadlineType.DATE : DeadlineType.UNTIL_FILLED;
            JobPosting jobPosting = JobPostingServiceTest.this.saveJobPosting(title, deadlineType, deadlineAt, status);
            for (Keyword keyword : keywords) {
                jobKeywordRepository.save(JobKeyword.create(jobPosting, keyword));
            }
            return jobPosting;
        }

        @Test
        @DisplayName("일치 키워드 수 내림차순, 동률이면 마감 임박순으로 정렬하고 채용 시 마감 공고는 뒤로 보낸다")
        void 일치_키워드_수와_마감일_순으로_정렬한다() {
            // given
            LocalDateTime now = LocalDateTime.now();
            saveJobPosting("1개 일치 3일 뒤", now.plusDays(3), JobPostingStatus.OPEN, java);
            saveJobPosting("1개 일치 채용 시 마감", null, JobPostingStatus.OPEN, spring);
            saveJobPosting("2개 일치 5일 뒤", now.plusDays(5), JobPostingStatus.OPEN, java, spring, figma);
            saveJobPosting("1개 일치 1일 뒤", now.plusDays(1), JobPostingStatus.OPEN, spring, figma);

            // when
            RecommendedJobPostingResponse result = jobPostingService.getRecommendedJobPostings(user.getId(), 10);

            // then
            assertThat(result.jobPostings())
                    .extracting(JobPostingResponse::title)
                    .containsExactly("2개 일치 5일 뒤", "1개 일치 1일 뒤", "1개 일치 3일 뒤", "1개 일치 채용 시 마감");
            assertThat(result.jobPostings())
                    .extracting(JobPostingResponse::matchedKeywordCount)
                    .containsExactly(2L, 1L, 1L, 1L);
        }

        @Test
        @DisplayName("키워드가 하나도 일치하지 않거나 모집 종료·마감 지난 공고는 제외한다")
        void 일치_없음_모집_종료_마감_지난_공고는_제외한다() {
            // given
            LocalDateTime now = LocalDateTime.now();
            saveJobPosting("추천 대상", now.plusDays(1), JobPostingStatus.OPEN, java);
            saveJobPosting("일치 없음", now.plusDays(1), JobPostingStatus.OPEN, figma);
            saveJobPosting("키워드 없음", now.plusDays(1), JobPostingStatus.OPEN);
            saveJobPosting("모집 종료", now.plusDays(1), JobPostingStatus.CLOSED, java, spring);
            saveJobPosting("마감 지남", now.minusDays(1), JobPostingStatus.OPEN, java, spring);

            // when
            RecommendedJobPostingResponse result = jobPostingService.getRecommendedJobPostings(user.getId(), 10);

            // then
            assertThat(result.jobPostings())
                    .extracting(JobPostingResponse::title)
                    .containsExactly("추천 대상");
        }

        @Test
        @DisplayName("size 개수만큼만 조회한다")
        void size_개수만큼만_조회한다() {
            // given
            LocalDateTime now = LocalDateTime.now();
            for (int day = 1; day <= 5; day++) {
                saveJobPosting(day + "일 뒤 마감", now.plusDays(day), JobPostingStatus.OPEN, java);
            }

            // when
            RecommendedJobPostingResponse result = jobPostingService.getRecommendedJobPostings(user.getId(), 3);

            // then
            assertThat(result.jobPostings())
                    .extracting(JobPostingResponse::title)
                    .containsExactly("1일 뒤 마감", "2일 뒤 마감", "3일 뒤 마감");
        }

        @Test
        @DisplayName("내 MY FIT 키워드를 등록순으로 함께 반환한다")
        void 내_키워드를_함께_반환한다() {
            // when
            RecommendedJobPostingResponse result = jobPostingService.getRecommendedJobPostings(user.getId(), 3);

            // then
            assertThat(result.myFitKeywords()).containsExactly(
                    new KeywordResponse(java.getId(), "Java"),
                    new KeywordResponse(spring.getId(), "Spring")
            );
        }

        @Test
        @DisplayName("지원한 공고는 지원 여부와 지원 일자를 함께 반환한다")
        void 지원한_공고는_지원_정보를_반환한다() {
            // given
            JobPosting applied = saveJobPosting("지원한 공고", LocalDateTime.now().plusDays(1),
                    JobPostingStatus.OPEN, java);
            applicationRepository.save(Application.create(user, applied));

            // when
            JobPostingResponse result = jobPostingService.getRecommendedJobPostings(user.getId(), 3)
                    .jobPostings().get(0);

            // then
            assertThat(result.applied()).isTrue();
            assertThat(result.appliedAt()).isNotNull();
            assertThat(result.missingDocuments()).isNull();
        }

        @Test
        @DisplayName("내 MY FIT 키워드가 없으면 추천 공고를 빈 목록으로 반환한다")
        void 내_키워드가_없으면_빈_목록을_반환한다() {
            // given
            User noKeywordUser = userRepository.save(User.create(true, true, true));
            saveJobPosting("공고", LocalDateTime.now().plusDays(1), JobPostingStatus.OPEN, java);

            // when
            RecommendedJobPostingResponse result =
                    jobPostingService.getRecommendedJobPostings(noKeywordUser.getId(), 3);

            // then
            assertThat(result.myFitKeywords()).isEmpty();
            assertThat(result.jobPostings()).isEmpty();
        }

        @Test
        @DisplayName("사용자가 없으면 USER_NOT_FOUND 예외를 던진다")
        void 사용자가_없으면_예외를_던진다() {
            // given
            Long notExistUserId = user.getId() + 1000;

            // when & then
            assertThatThrownBy(() -> jobPostingService.getRecommendedJobPostings(notExistUserId, 3))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.USER_NOT_FOUND);
        }
    }
}
