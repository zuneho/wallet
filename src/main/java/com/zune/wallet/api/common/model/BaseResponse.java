package com.zune.wallet.api.common.model;

import com.zune.wallet.api.common.type.ResponseType;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@Getter
public class BaseResponse implements Serializable {
    private static final String DEFAULT_SUCCESS_MESSAGE = "ok";
    private static final String DEFAULT_FAIL_MESSAGE = "fail";

    private final ResponseType type;
    private final String message;
    private final Object data;

    private BaseResponse(ResponseType type, String message, Object data) {
        this.type = type;
        this.message = message;
        this.data = data;
    }

    public static BaseResponse ok(String message) {
        return new BaseResponse(
                ResponseType.OK,
                StringUtils.isNoneEmpty(message) ? message : DEFAULT_SUCCESS_MESSAGE,
                null);
    }

    public static BaseResponse ok(Object data) {
        return new BaseResponse(
                ResponseType.OK,
                DEFAULT_SUCCESS_MESSAGE,
                data);
    }


    public static BaseResponse fail(String message) {
        return new BaseResponse(
                ResponseType.BAD_REQUEST,
                StringUtils.isNoneEmpty(message) ? message : DEFAULT_FAIL_MESSAGE,
                null);
    }

    public static BaseResponse fail(HttpStatus httpStatus, String message) {
        ResponseType responseType = ResponseType.findBy(httpStatus);
        return new BaseResponse(
                responseType,
                StringUtils.isNoneEmpty(message) ? message : responseType.getMessage(),
                null);
    }
}
