package org.sopt.collaboration.domain.jobposting.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.sopt.collaboration.domain.jobposting.dto.JobPostingResponse;
import org.sopt.collaboration.domain.jobposting.entity.DeadlineType;
import org.sopt.collaboration.domain.jobposting.entity.JobPosting;
import org.sopt.collaboration.domain.jobposting.entity.JobPostingStatus;
import org.sopt.collaboration.domain.jobposting.repository.JobPostingRepository;
import org.sopt.collaboration.domain.user.entity.User;
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
}
