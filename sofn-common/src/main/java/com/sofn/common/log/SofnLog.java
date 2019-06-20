package com.sofn.common.log;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SofnLog {

    /**
     * 日志描述<br/>
     * 例如：新增用户
     */
    String value() default "";

}

