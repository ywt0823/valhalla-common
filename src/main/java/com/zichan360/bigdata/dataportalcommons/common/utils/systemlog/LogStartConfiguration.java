package com.zichan360.bigdata.dataportalcommons.common.utils.systemlog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author ywt
 * @description 自动扫描被日志注解的类下的所有包 并注册容器
 * @date 2020-09-28 16:47:11
 **/
public class LogStartConfiguration implements ImportBeanDefinitionRegistrar {
    private static final Logger log = LoggerFactory.getLogger(LogStartConfiguration.class);

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        log.info("packages value:" + importingClassMetadata.getAnnotationAttributes(EnableAutoConfiguration.class.getName()));
        String[] packageArray = (String[]) Objects.requireNonNull(importingClassMetadata.getAnnotationAttributes(EnableAutoConfiguration.class.getName())).get("packages");
        List<String> packages = Arrays.asList(packageArray);
        BeanDefinitionBuilder bdb = BeanDefinitionBuilder.rootBeanDefinition(LogInfo.class);
        bdb.addPropertyValue("packages", packages);
        registry.registerBeanDefinition(EnableAutoConfiguration.class.getName(), bdb.getBeanDefinition());
    }
}
