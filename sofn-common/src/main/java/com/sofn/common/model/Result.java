package com.sofn.common.model;

import com.sofn.common.exception.ExceptionType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * api接口返回结果封装对象
 */
@SuppressWarnings("ALL")
@ApiModel("返回类")
@Data
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final Integer CODE = 0;

    @ApiModelProperty(value = "返回码", notes = "返回码")
    private Integer code;

    @ApiModelProperty(value = "描述消息")
    private String msg = "";

    @ApiModelProperty(value = "返回对象")
    private T data;

    public Result() {

    }

    public Result(Integer code) {
        this.code = code;
    }

    public Result(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Result(Integer code, T result) {
        this.code = code;
        this.data = result;
    }

    public Result(T result) {
        this.data = result;
    }

    public static Result error() {
        return error(500, "未知异常");
    }

    public static Result error(String msg) {
        return error(500, msg);
    }

    public static Result error(Integer code, String msg) {
        return new Result(code, msg);
    }

    public static Result error(ExceptionType exceptionType) {
        return new Result(exceptionType.getCode(), exceptionType.getMsg());
    }

    public static Result ok(String msg) {
        return new Result(CODE, msg);
    }

    public static Result ok(Object result) {
        return new Result(CODE, result);
    }

    public static Result ok() {
        return new Result(CODE);
    }

}
