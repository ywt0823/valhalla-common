package com.zichan360.bigdata.dataportalcommons.common.utils.systemlog;

import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ywt
 * @description
 * @date 2020-10-14 15:55:28
 **/
public class LogUtil {

    public static Map<Object, Object> getProjectLogInfo(final ApplicationContext applicationContext) {
        return applicationContext.getBeansWithAnnotation(RequestMapping.class).entrySet().stream()
                .filter(stringObjectEntry -> !"basicErrorController".equals(stringObjectEntry.getKey()))
                .collect(Collectors.toMap(stringObjectEntry -> {
                    Class<?> aClass = stringObjectEntry.getValue().getClass();
                    return aClass.getAnnotation(RequestMapping.class).value()[0].replaceFirst("/","");
                }, stringObjectEntry -> {
                    Method[] methodList = stringObjectEntry.getValue().getClass().getDeclaredMethods();
                    return Arrays.stream(methodList)
                            .filter(method -> method.isAnnotationPresent(GetMapping.class) ||
                                    method.isAnnotationPresent(PostMapping.class) ||
                                    method.isAnnotationPresent(RequestMapping.class))
                            .filter(method -> method.isAnnotationPresent(LogInfo.class))
                            .collect(Collectors.toMap(method -> {
                                if (method.isAnnotationPresent(PostMapping.class)) {
                                    String[] value = method.getAnnotation(PostMapping.class).value();
                                    return value[0].replaceFirst("/","");
                                } else if (method.isAnnotationPresent(GetMapping.class)) {
                                    String[] value = method.getAnnotation(GetMapping.class).value();
                                    return value[0].replaceFirst("/","");
                                } else {
                                    String[] value = method.getAnnotation(RequestMapping.class).value();
                                    return value[0].replaceFirst("/","");
                                }
                            }, method -> method.getAnnotation(LogInfo.class).value()));
                }));
    }
}
