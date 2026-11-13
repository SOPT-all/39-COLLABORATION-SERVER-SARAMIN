package org.sopt.collaboration.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KeywordRepository keywordRepository;

    @Autowired
    private UserKeywordRepository userKeywordRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = userRepository.save(User.create(true, true, false));
    }

    @Nested
    @DisplayName("내 MY FIT 키워드 조회")
    class GetMyKeywords {

        @Test
        @DisplayName("내 키워드만 등록한 순서대로 조회한다")
        void 내_키워드만_등록한_순서대로_조회한다() {
            // given
            Keyword spring = keywordRepository.save(Keyword.create("Spring", "툴"));
            Keyword backend = keywordRepository.save(Keyword.create("백엔드", "직무"));
            Keyword figma = keywordRepository.save(Keyword.create("Figma", "툴"));
            userKeywordRepository.save(UserKeyword.create(user, spring));
            userKeywordRepository.save(UserKeyword.create(user, backend));

            User otherUser = userRepository.save(User.create(false, false, false));
            userKeywordRepository.save(UserKeyword.create(otherUser, figma));

            // when
            List<KeywordResponse> result = userService.getMyKeywords(user.getId());

            // then
            assertThat(result).containsExactly(
                    new KeywordResponse(spring.getId(), "Spring", "툴"),
                    new KeywordResponse(backend.getId(), "백엔드", "직무")
            );
        }

        @Test
        @DisplayName("키워드가 없으면 빈 목록을 반환한다")
        void 키워드가_없으면_빈_목록을_반환한다() {
            // when
            List<KeywordResponse> result = userService.getMyKeywords(user.getId());

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 사용자면 예외가 발생한다")
        void 존재하지_않는_사용자면_예외가_발생한다() {
            // given
            Long unknownUserId = user.getId() + 1000;

            // when & then
            assertThatThrownBy(() -> userService.getMyKeywords(unknownUserId))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.USER_NOT_FOUND);
        }
    }
}
