package org.sopt.collaboration.global.common;

import org.sopt.collaboration.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public record ApiResponse<T>(boolean success, int code, String message, T data) {

    private static final String SUCCESS_MESSAGE = "요청에 성공했습니다.";

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, HttpStatus.OK.value(), SUCCESS_MESSAGE, data);
    }

    public static <T> ApiResponse<T> fail(ErrorCode errorCode) {
        return new ApiResponse<>(false, errorCode.getStatus().value(), errorCode.getMessage(), null);
    }
}
