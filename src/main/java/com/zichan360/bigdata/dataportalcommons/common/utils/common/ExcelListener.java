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
import java.util.Map;
import java.util.Optional;

/**
 * excel监听类
 *
 * @author ywt
 */
public class ExcelListener extends AnalysisEventListener<Map<Integer,Object>> {

    private List<Map<Integer,Object>> dataList = new ArrayList<>();


    @Override
    public void invoke(Map<Integer,Object> data, AnalysisContext context) {
        if (Optional.ofNullable(data).isPresent()){
            dataList.add(data);
        }

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
    }

    public List<Map<Integer,Object>> getDataList() {
        return dataList;
    }

    public void setDataList(List<Map<Integer,Object>> dataList) {
        this.dataList = dataList;
    }
}