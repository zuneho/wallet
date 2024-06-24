package com.zune.wallet.api.common.type;

import com.zune.wallet.api.common.exception.CodeException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Optional;


@Slf4j
@Getter
@RequiredArgsConstructor
public enum ResponseType {
    OK("ok"),
    BAD_REQUEST("올바른 형식의 요청이 아닙니다."),
    UNAUTHORIZED("사용자 정보를 확인할 수 없습니다."),
    FORBIDDEN("접근 권한이 없습니다."),
    NOT_FOUND("올바른 대상 정보를 확인 할 수 없습니다."),
    METHOD_NOT_ALLOWED("허용되지 않는 요청입니다."),
    INTERNAL_SERVER_ERROR("알수 없는 오류가 발생하였습니다.");

    private final String message;

    public static ResponseType findBy(HttpStatus httpStatus) {
        if (null == httpStatus) {
            log.error("ResponseType send httpStatus is null");
            throw new CodeException("올바른 응답값을 찾을 수 없습니다.");
        }

        Optional<ResponseType> foundResponseType = Arrays.stream(ResponseType.values())
                .filter(responseType -> responseType.name().equalsIgnoreCase(httpStatus.name()))
                .findFirst();

        if (foundResponseType.isPresent()) {
            return foundResponseType.get();
        }

        log.warn("is undefined http status code.type name={} type value={}", httpStatus.name(), httpStatus.value());
        if (httpStatus.value() > 200 && httpStatus.value() < 300) {
            return OK;
        }

        return INTERNAL_SERVER_ERROR;
    }
}
