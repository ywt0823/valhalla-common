package com.zichan360.bigdata.dataportalcommons.common.utils.systemlog;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author ywt
 * @date 2020年9月28日 16:47:41
 * 自定义日志开启类
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(LogStartConfiguration.class)
public @interface EnableLogStartConfiguration {


}
