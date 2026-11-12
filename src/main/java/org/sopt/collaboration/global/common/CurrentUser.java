package org.sopt.collaboration.global.common;

/**
 * 요청 사용자 식별자. 로그인·권한(OQ-06) 확정 전까지 프로토타입은 단일 사용자(id=1)로 동작한다.
 */
public final class CurrentUser {

    public static final Long ID = 1L;

    private CurrentUser() {
    }
}
