package org.sopt.collaboration.domain.user.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.collaboration.domain.keyword.dto.KeywordResponse;
import org.sopt.collaboration.domain.user.dto.UpdateUserDocumentRequest;
import org.sopt.collaboration.domain.user.dto.UserDocumentResponse;
import org.sopt.collaboration.domain.user.entity.User;
import org.sopt.collaboration.domain.user.entity.UserKeyword;
import org.sopt.collaboration.domain.user.repository.UserKeywordRepository;
import org.sopt.collaboration.domain.user.repository.UserRepository;
import org.sopt.collaboration.global.exception.BusinessException;
import org.sopt.collaboration.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserKeywordRepository userKeywordRepository;

    // 보유 서류 체크 상태만 갱신한다. 공고별 지원 가능 여부는 공고 목록 API가 계산한다
    @Transactional
    public UserDocumentResponse updateDocuments(Long userId, UpdateUserDocumentRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.updateDocuments(request.hasResume(), request.hasCoverLetter(), request.hasCareer());
        return UserDocumentResponse.from(user);
    }

    public List<KeywordResponse> getMyKeywords(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return userKeywordRepository.findAllWithKeywordByUserId(userId).stream()
                .map(UserKeyword::getKeyword)
                .map(KeywordResponse::from)
                .toList();
    }
}
