package org.sopt.collaboration.domain.keyword.dto;

import org.sopt.collaboration.domain.keyword.entity.Keyword;

/**
 * 키워드 칩. 다른 화면에서도 키워드 id·이름을 내려줄 때 재사용한다.
 */
public record KeywordResponse(
        Long keywordId,
        String name
) {

    public static KeywordResponse from(Keyword keyword) {
        return new KeywordResponse(keyword.getId(), keyword.getName());
    }
}
