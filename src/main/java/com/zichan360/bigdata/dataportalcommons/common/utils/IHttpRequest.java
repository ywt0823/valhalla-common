package com.zichan360.bigdata.dataportalcommons.common.utils;

@FunctionalInterface
public interface IHttpRequest<T,R> {
    R apply(T apiUrl, T body, T contentType);
}
