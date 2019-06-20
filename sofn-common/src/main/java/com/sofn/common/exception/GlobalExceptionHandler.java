package com.sofn.common.exception;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.sofn.common.model.Result;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseBody
    public Result unauthorizedHandler() {
        return Result.error(ExceptionType.NO_PERMISSION);
    }

    @ExceptionHandler(UnauthenticatedException.class)
    @ResponseBody
    public Result unauthenticatedHandler() {
        return Result.error(ExceptionType.NOT_LOGIN);
    }

    @ExceptionHandler(TokenExpiredException.class)
    @ResponseBody
    public Result tokenExpiredHandler() {
        return Result.error(ExceptionType.TOKEN_EXPIRED);
    }

    @ExceptionHandler()
    @ResponseBody
    public Result sofnExceptionHandler(SofnException e) {
        return Result.error(e.getCode(),e.getMsg());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public Result paramsExceptionHandler() {
        return Result.error(ExceptionType.PARAMS_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public Result runtimeExceptionHandler() {
        return Result.error(ExceptionType.UNKNOWN_ERROR);
    }

}