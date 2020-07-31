package com.zichan360.bigdata.dataportalcommons.common.utils.common;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author ywt
 * @description Excel工具类
 * @date 2020-07-28 10:39:31
 **/
public class ExcelUtil {
    private static final Logger log = LoggerFactory.getLogger(ExcelUtil.class);

    /**
     * 读取Excel（一个sheet）
     *
     * @param excel   文件
     * @param clazz   实体类
     * @param sheetNo sheet序号
     * @return 返回实体列表(需转换)
     */
    public static <T> List<T> readExcel(MultipartFile excel, Class<T> clazz, int sheetNo) {

        ExcelListener excelListener = new ExcelListener();

        ExcelReader excelReader = getReader(excel, clazz, excelListener);
        if (excelReader == null) {
            return new ArrayList<>();
        }
        ReadSheet readSheet = EasyExcel.readSheet(sheetNo).build();
        excelReader.read(readSheet);
        excelReader.finish();
        return BeanConvert.objectConvertBean(excelListener.getDataList(), clazz);
    }


    /**
     * 读取Excel（多个sheet可以用同一个实体类解析）
     *
     * @param excel 文件
     * @param clazz 实体类
     * @return 返回实体列表(需转换)
     */
    public static <T> List<T> readExcel(MultipartFile excel, Class<T> clazz) {

        ExcelListener excelListener = new ExcelListener();
        ExcelReader excelReader = getReader(excel, clazz, excelListener);

        if (excelReader == null) {
            return new ArrayList<>();
        }

        List<ReadSheet> readSheetList = excelReader.excelExecutor().sheetList();

        for (ReadSheet readSheet : readSheetList) {
            excelReader.read(readSheet);
        }
        excelReader.finish();

        return BeanConvert.objectConvertBean(excelListener.getDataList(), clazz);
    }


    /**
     * 导出Excel(一个sheet)
     *
     * @param response  HttpServletResponse
     * @param list      数据list
     * @param fileName  导出的文件名
     * @param sheetName 导入文件的sheet名
     * @param clazz     实体类
     */
    public static <T> void writeExcel(HttpServletResponse response, List<T> list, String fileName, String sheetName, Class<T> clazz) {

        OutputStream outputStream = getOutputStream(response, fileName);

        ExcelWriter excelWriter = EasyExcel.write(outputStream, clazz).build();
        WriteSheet writeSheet = EasyExcel.writerSheet(sheetName).build();

        excelWriter.write(list, writeSheet);

        excelWriter.finish();
    }


    /**
     * 导出Excel(带样式)
     *
     * @return
     */
    public static <T> void writeStyleExcel(HttpServletResponse response, List<T> list, String fileName, String sheetName, Class<T> clazz) {
        //表头策略
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        //背景浅灰
        headWriteCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontHeightInPoints((short) 20);
        headWriteCellStyle.setWriteFont(headWriteFont);

        //内容策略
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        //这里需要指定 FillPatternType 为FillPatternType.SOLID_FOREGROUND 否则无法显示背景颜色；头默认了FillPatternType
        contentWriteCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        //背景浅绿
        contentWriteCellStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        WriteFont contentWriteFont = new WriteFont();
        //字体大小
        contentWriteFont.setFontHeightInPoints((short) 15);
        contentWriteCellStyle.setWriteFont(contentWriteFont);

        HorizontalCellStyleStrategy horizontalCellStyleStrategy = new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);

        OutputStream outputStream = getOutputStream(response, fileName);
        EasyExcel.write(outputStream, clazz).registerWriteHandler(horizontalCellStyleStrategy).sheet(sheetName).doWrite(list);

    }


    /**
     * 导出Excel(动态表头)
     * write时不传入class,table时传入并设置needHead为false
     *
     * @return
     */
    public static <T> void writeDynamicHeadExcel(HttpServletResponse response, List<T> list, String fileName, String sheetName, Class<T> clazz, List<List<String>> headList) {

        OutputStream outputStream = getOutputStream(response, fileName);

        EasyExcel.write(outputStream)
                .head(headList)
                .sheet(sheetName)
                .table().head(clazz).needHead(Boolean.FALSE)
                .doWrite(list);
    }


    /**
     * 导出时生成OutputStream
     */
    private static OutputStream getOutputStream(HttpServletResponse response, String fileName) {
        //创建本地文件
        String filePath = fileName + ".xlsx";
        File file = new File(filePath);
        try {
            if (!file.exists() || file.isDirectory()) {
                file.createNewFile();
            }
            fileName = new String(filePath.getBytes(), "utf-8");
            response.addHeader("Content-Disposition", "filename=" + fileName);
            return response.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 返回ExcelReader
     *
     * @param excel         文件
     * @param clazz         实体类
     * @param excelListener
     */
    private static <T> ExcelReader getReader(MultipartFile excel, Class<T> clazz, ExcelListener excelListener) {
        String filename = excel.getOriginalFilename();

        try {
            if (filename == null || (!filename.toLowerCase().endsWith(".xls") && !filename.toLowerCase().endsWith(".xlsx"))) {
                return null;
            }

            InputStream inputStream = new BufferedInputStream(excel.getInputStream());

            ExcelReader excelReader = EasyExcel.read(inputStream, clazz, excelListener).build();

            inputStream.close();

            return excelReader;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }




    /**
     * 强转实体类
     */
    private static class BeanConvert {
        /**
         * 将List<Object> 转换为List<Bean>
         *
         * @param sources     源对象
         * @param targetClass 目标类
         * @param <T>
         * @return
         */
        private static <T> List<T> objectConvertBean(List<?> sources, Class<T> targetClass) {
            List<?> sourcesObj = sources;
            if (sourcesObj == null) {
                sourcesObj = Collections.emptyList();
            }
            List<T> targets = new ArrayList<>(sourcesObj.size());
            convert(sourcesObj, targets, targetClass);
            return targets;
        }

        /**
         * 复制源对象到目的对象
         * 注意：
         * org.springframework.beans.BeanUtils.copyProperties 是一个Spring提供的名称相同的工具类
         * 但它不支持类型自动转换，如果某个类型属性不同，则不予转换那个属性
         * org.apache.commons.beanutils.BeanUtils 是一个Apache提供的名称相同的工具类
         * 支持类型自动转换，如Date类型会自动转换为字符串
         *
         * @param sources     源对象
         * @param targets     目的对象
         * @param targetClass 目标类
         * @param <T>
         */
        private static <T> void convert(List<?> sources, List<T> targets, Class<T> targetClass) {
            if (sources == null) {
                return;
            }
            if (targets == null) {
                return;
            }
            targets.clear();
            for (Object obj : sources) {
                try {
                    T target = targetClass.newInstance();
                    targets.add(target);
                    BeanUtils.copyProperties(obj, target);
                } catch (Exception e) {
                    return;
                }
            }
        }

    }

}
