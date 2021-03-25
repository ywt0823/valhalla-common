package com.valhalla.common.result;

import java.io.Serializable;

/**
 * @author ywt
 * @date 2019年2月9日 08:57:48
 **/
public class ResultPageInfo implements Serializable {
    public ResultPageInfo() {
    }

    public ResultPageInfo(String totalRecord, String currentPage, String pageSize,String totalPage) {
        this.totalPage=totalPage;
        this.totalRecord = totalRecord;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }


    public String totalRecord; //总记录数
    public String currentPage;//当前页
    public String pageSize;//页面最大记录数
    /**
     * 总页数
     */
    public String totalPage;

    public String getTotalRecord() {
        return totalRecord;
    }

    public void setTotalRecord(String totalRecord) {
        this.totalRecord = totalRecord;
    }

    public String getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(String currentPage) {
        this.currentPage = currentPage;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public String getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(String totalPage) {
        this.totalPage = totalPage;
    }
}
