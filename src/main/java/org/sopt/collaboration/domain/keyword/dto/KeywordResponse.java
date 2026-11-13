package org.sopt.collaboration.domain.keyword.dto;

import org.sopt.collaboration.domain.keyword.entity.Keyword;

public record KeywordResponse(
        Long id,
        String name,
        String type
) {

    public static KeywordResponse from(Keyword keyword) {
        return new KeywordResponse(keyword.getId(), keyword.getName(), keyword.getType());
    }
}
