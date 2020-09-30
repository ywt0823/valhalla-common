package com.zichan360.bigdata.dataportalcommons.common.utils.systemlog;


import java.lang.annotation.*;

/**
 * @author zhk
 * 日志注解
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogInfo {


    /**
     * 值
     * @return
     */
    String value() default "";

    /**
     * 是否开启
     * @return
     */
    boolean enable() default true;
}
