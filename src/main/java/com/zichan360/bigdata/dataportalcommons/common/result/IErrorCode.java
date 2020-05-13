package com.zichan360.bigdata.dataportalcommons.common.result;

/**
 * 封装API的错误码
 * Created by ywt on 2019/2/19.
 */
public interface IErrorCode {
    long getCode();

    String getMessage();
}
