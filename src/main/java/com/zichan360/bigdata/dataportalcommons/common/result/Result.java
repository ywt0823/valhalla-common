package com.zichan360.bigdata.dataportalcommons.common.result;

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


    public String toString() {
        return "Result{" +
                "status='" + status.name() +"'"+
                ",status_code='" + status_code +"'"+
                ", msg='" + msg +"'"+
                "}";
    }



}
