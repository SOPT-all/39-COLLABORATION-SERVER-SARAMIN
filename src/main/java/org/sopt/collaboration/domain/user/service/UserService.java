package org.sopt.collaboration.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.sopt.collaboration.domain.user.dto.UpdateUserDocumentRequest;
import org.sopt.collaboration.domain.user.dto.UserDocumentResponse;
import org.sopt.collaboration.domain.user.entity.User;
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

    // 보유 서류 체크 상태만 갱신한다. 공고별 지원 가능 여부는 공고 목록 API가 계산한다
    @Transactional
    public UserDocumentResponse updateDocuments(Long userId, UpdateUserDocumentRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.updateDocuments(request.hasResume(), request.hasCoverLetter(), request.hasCareer());
        return UserDocumentResponse.from(user);
    }
}
