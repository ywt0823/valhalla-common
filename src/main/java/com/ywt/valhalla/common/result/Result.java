package com.ywt.valhalla.common.result;

import java.util.Objects;

/**
* @author ywt
* @Date 2019年2月9日 08:59:01
**/
public class Result<T> {
    public Result(){

    }
    private ResultEnum status;
    private Integer status_code;
    private String msg;
    private T data;

    private ResultPageInfo resultPageInfo;

    public ResultEnum getStatus() {
        return status;
    }

    public void setStatus(ResultEnum code) {
        this.status = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setStatus_code(Integer status_code) {
        this.status_code = status_code;
    }

    public Integer getStatus_code() {
        return status_code;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ResultPageInfo getPageInfo() {
        return resultPageInfo;
    }

    public void setPageInfo(ResultPageInfo pageInfo) {
        this.resultPageInfo = pageInfo;
    }

    public  T getData() {
        return data;
    }


    @Override
    public String toString() {
        return "Result{" +
                "status=" + status +
                ", status_code=" + status_code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                ", resultPageInfo=" + resultPageInfo +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Result)) {
            return false;
        }

        Result<?> result = (Result<?>) o;

        if (getStatus() != result.getStatus()) {
            return false;
        }
        if (getStatus_code() != null ? !getStatus_code().equals(result.getStatus_code()) : result.getStatus_code() != null) {
            return false;
        }
        if (getMsg() != null ? !getMsg().equals(result.getMsg()) : result.getMsg() != null) {
            return false;
        }
        if (getData() != null ? !getData().equals(result.getData()) : result.getData() != null) {
            return false;
        }
        return Objects.equals(resultPageInfo, result.resultPageInfo);
    }

    @Override
    public int hashCode() {
        int result = getStatus() != null ? getStatus().hashCode() : 0;
        result = 31 * result + (getStatus_code() != null ? getStatus_code().hashCode() : 0);
        result = 31 * result + (getMsg() != null ? getMsg().hashCode() : 0);
        result = 31 * result + (getData() != null ? getData().hashCode() : 0);
        result = 31 * result + (resultPageInfo != null ? resultPageInfo.hashCode() : 0);
        return result;
    }
}
