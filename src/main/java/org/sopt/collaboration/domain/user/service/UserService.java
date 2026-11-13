package org.sopt.collaboration.domain.user.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.collaboration.domain.keyword.dto.KeywordResponse;
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
