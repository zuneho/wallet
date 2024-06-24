package com.zune.wallet.api.common.exception.handler;

import com.zune.wallet.api.common.exception.AuthException;
import com.zune.wallet.api.common.exception.BusinessException;
import com.zune.wallet.api.common.exception.CodeException;
import com.zune.wallet.api.common.model.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@ControllerAdvice
public class DefaultControllerAdvice {
    //known exceptions
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<?> businessException(BusinessException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<?> authException(AuthException e) {
        return buildResponse(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(CodeException.class)
    public ResponseEntity<?> codeException(CodeException e) {
        log.error("wrong code exception", e);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<?> authorizationDeniedException(AuthorizationDeniedException e) {
        return buildResponse(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> illegalArgumentException(IllegalArgumentException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<?> noResourceFoundException(NoResourceFoundException e) {
        log.warn("access not defined resource. {}", e.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, "올바른 URL 이 아닙니다.");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> httpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn(e.toString());
        return buildResponse(HttpStatus.NOT_ACCEPTABLE, "형식에 맞는 데이터가 아닙니다.");
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<?> bindException(BindException e) {
        log.warn(e.getMessage(), e);
        return buildResponse(HttpStatus.NOT_ACCEPTABLE, "형식에 맞는 데이터가 아닙니다.");
    }


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.warn(e.getMessage(), e);
        return buildResponse(HttpStatus.BAD_REQUEST, "올바른 형태의 입력값이 아닙니다.");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error(e.getMessage(), e);
        return buildResponse(HttpStatus.BAD_REQUEST, "지원하지 않는 요청입니다.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<?> objectOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e) {
        log.warn("optimisticLock Failure Exception.", e);
        return buildResponse(HttpStatus.BAD_REQUEST, "동시에 너무 많은 요청이 발생하여 일부 요청만 처리되었습니다.");
    }



    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> exception(Exception e) {
        log.error("INTERNAL SERVER ERROR", e);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "알수없는 오류가 발생하였습니다.");
    }

    private ResponseEntity<?> buildResponse(HttpStatus httpStatus, String message) {
        return ResponseEntity
                .status(httpStatus)
                .body(BaseResponse.fail(httpStatus, message));
    }
}
