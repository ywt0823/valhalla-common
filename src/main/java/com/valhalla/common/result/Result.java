package com.valhalla.common.result;

/**
 * @author ywt
 * @Date 2019年2月9日 08:59:01
 **/
public class Result<T> {
    public Result() {

    }

    private ResultEnum status;
    private Integer statusCode;
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

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public ResultPageInfo getResultPageInfo() {
        return resultPageInfo;
    }

    public void setResultPageInfo(ResultPageInfo resultPageInfo) {
        this.resultPageInfo = resultPageInfo;
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

    public T getData() {
        return data;
    }


    @Override
    public String toString() {
        return "Result{" +
                "status=" + status +
                ", status_code=" + statusCode +
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

        return getData() != null ? getData().equals(result.getData()) : result.getData() == null;
    }

    @Override
    public int hashCode() {
        return getData() != null ? getData().hashCode() : 0;
    }
}
