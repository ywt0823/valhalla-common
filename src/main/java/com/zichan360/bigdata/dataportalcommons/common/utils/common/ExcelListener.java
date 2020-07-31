package com.zichan360.bigdata.dataportalcommons.common.utils.common;

/**
 * @author ywt
 * @description
 * @date 2020-07-31 17:20:25
 **/

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * excel监听类
 *
 * @author ywt
 */
public class ExcelListener extends AnalysisEventListener {

    private List<Object> dataList = new ArrayList<>();


    @Override
    public void invoke(Object data, AnalysisContext context) {
        dataList.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
    }

    public List<Object> getDataList() {
        return dataList;
    }

    public void setDataList(List<Object> dataList) {
        this.dataList = dataList;
    }
}