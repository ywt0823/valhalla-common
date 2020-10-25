package com.ywt.valhalla.common.utils.common;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.WriteTable;
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
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.*;

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

    public static List<Map<Integer, Object>> getExcelList(MultipartFile file) throws IOException {
        ExcelListener excelListener = new ExcelListener();
        ExcelReaderBuilder excelReaderBuilder = EasyExcelFactory.read(file.getInputStream(), excelListener);
        excelReaderBuilder.doReadAll();
        return excelListener.getDataList();
    }

    public static List<Map<Integer, Object>> getExcelList(MultipartFile file, ExcelListener excelListener) throws IOException {
        ExcelReaderBuilder excelReaderBuilder = EasyExcelFactory.read(file.getInputStream(), excelListener);
        excelReaderBuilder.doReadAll();
        return excelListener.getDataList();
    }

    /**
     * 生成多表头的excel
     *
     * @param httpServletResponse httpServletResponse
     * @param header              表头列表
     * @param fileName            文件名称
     * @param dataList            数据集
     * @throws IOException
     */
    public static void exportData(HttpServletResponse httpServletResponse, List<List<String>> header, String fileName, List<Map<String, Object>> dataList) throws IOException {
        httpServletResponse.setContentType("application/vnd.ms-excel;charset=utf-8");
        httpServletResponse.setCharacterEncoding("utf-8");
        httpServletResponse.setHeader("Content-disposition", "attachment;filename=\"" + new String((fileName + ExcelTypeEnum.XLSX.getValue()).getBytes("utf-8"),"ISO-8859-1") + "\"");
        httpServletResponse.setHeader("fileName", URLEncoder.encode(fileName+ExcelTypeEnum.XLSX,"UTF-8"));
        ExcelWriterBuilder excelWriterBuilder = EasyExcel.write(httpServletResponse.getOutputStream());
        ExcelWriter excelWriter = excelWriterBuilder.build();
        // 创建一个表格，用于 Sheet 中使用
        WriteTable writeTable = convertHeaderListToTable(header);
        WriteSheet writeSheet = new WriteSheet();
        writeSheet.setSheetName(LocalDate.now().toString());
        excelWriter.write(!Optional.ofNullable(dataList).isPresent() || dataList.isEmpty() ? null : convertDataMapToResultData(dataList), writeSheet, writeTable);
        // 写数据
        excelWriter.finish();
        httpServletResponse.getOutputStream().close();
    }

    /**
     * 生成单表头的excel
     *
     * @param httpServletResponse httpServletResponse
     * @param firstHeader         表头列表
     * @param fileName            文件名称
     * @param dataList            数据集
     * @throws IOException
     */
    public static void exportData(HttpServletResponse httpServletResponse, String fileName, List<String> firstHeader, List<Map<String, Object>> dataList) throws IOException {
        httpServletResponse.setContentType("application/vnd.ms-excel;charset=utf-8");
        httpServletResponse.setCharacterEncoding("utf-8");
        httpServletResponse.setHeader("Content-disposition", "attachment;filename=\"" + new String((fileName + ExcelTypeEnum.XLSX.getValue()).getBytes("UTF-8"),"ISO-8859-1") + "\"");
        httpServletResponse.setHeader("fileName", URLEncoder.encode(fileName+ExcelTypeEnum.XLSX,"UTF-8"));
        ExcelWriterBuilder excelWriterBuilder = EasyExcel.write(httpServletResponse.getOutputStream());
        ExcelWriter excelWriter = excelWriterBuilder.build();
        // 创建一个表格，用于 Sheet 中使用
        ArrayList<List<String>> headerList = new ArrayList<>();
        firstHeader.forEach(name -> {
            List<String> nameList = new LinkedList<>();
            nameList.add(name);
            headerList.add(nameList);
        });
        WriteTable writeTable = convertHeaderListToTable(headerList);
        WriteSheet writeSheet = new WriteSheet();
        writeSheet.setSheetName(LocalDate.now().toString());
        excelWriter.write(!Optional.ofNullable(dataList).isPresent() || dataList.isEmpty() ? null : convertDataMapToResultData(dataList), writeSheet, writeTable);
        // 写数据
        excelWriter.finish();
        httpServletResponse.getOutputStream().close();
    }

    /**
     * 生成单表头的excel
     *
     * @param httpServletResponse httpServletResponse
     * @param writeTable          表属性
     * @param writeSheet          sheet属性
     * @param fileName            文件名称
     * @param dataList            数据集
     * @throws IOException
     */
    public static void exportData(HttpServletResponse httpServletResponse, String fileName, WriteTable writeTable, WriteSheet writeSheet, List<Map<String, Object>> dataList) throws IOException {
        httpServletResponse.setContentType("application/vnd.ms-excel;charset=utf-8");
        httpServletResponse.setCharacterEncoding("utf-8");
        httpServletResponse.setHeader("Content-disposition", "attachment;filename=\"" + new String((fileName + ExcelTypeEnum.XLSX.getValue()).getBytes("utf-8"),"ISO-8859-1") + "\"");
        httpServletResponse.setHeader("fileName", URLEncoder.encode(fileName+ExcelTypeEnum.XLSX,"UTF-8"));
        ExcelWriterBuilder excelWriterBuilder = EasyExcel.write(httpServletResponse.getOutputStream());
        ExcelWriter excelWriter = excelWriterBuilder.build();
        excelWriter.write(!Optional.ofNullable(dataList).isPresent() || dataList.isEmpty() ? null : convertDataMapToResultData(dataList), writeSheet, writeTable);
        // 写数据
        excelWriter.finish();
        httpServletResponse.getOutputStream().close();
    }

    /**
     * 生成EXCEL并存入服务器路径中
     * @param fileOutputStream
     * @param firstHeader
     * @param dataList
     */
    public static void putExcelToServer(FileOutputStream fileOutputStream, List<String> firstHeader, List<Map<String, Object>> dataList) throws IOException {
        ExcelWriterBuilder excelWriterBuilder = EasyExcel.write(fileOutputStream);
        ExcelWriter excelWriter = excelWriterBuilder.build();
        // 创建一个表格，用于 Sheet 中使用
        ArrayList<List<String>> headerList = new ArrayList<>();
        firstHeader.forEach(name -> {
            List<String> nameList = new LinkedList<>();
            nameList.add(name);
            headerList.add(nameList);
        });
        WriteTable writeTable = convertHeaderListToTable(headerList);
        WriteSheet writeSheet = new WriteSheet();
        writeSheet.setSheetName(LocalDate.now().toString());
        excelWriter.write(!Optional.ofNullable(dataList).isPresent() || dataList.isEmpty() ? null : convertDataMapToResultData(dataList), writeSheet, writeTable);
        // 写数据
        excelWriter.finish();
    }

    /**
     * 将表头转换成easyExcel的表头类
     *
     * @param header 表头列表
     * @return
     */
    private static WriteTable convertHeaderListToTable(List<List<String>> header) {
        WriteTable writeTable = new WriteTable();
        writeTable.setHead(header);
        writeTable.setAutomaticMergeHead(true);
        return writeTable;
    }

    /**
     * 将查询结果转换成两组linkedList对应表头结果
     *
     * @param dataList 数据结果
     * @return
     */
    private static LinkedList<LinkedList<String>> convertDataMapToResultData(List<Map<String, Object>> dataList) {
        LinkedList<LinkedList<String>> resultData = new LinkedList<>();
        dataList.forEach(map -> {
            LinkedList<String> mapData = new LinkedList<>();
            map.forEach((s, o) -> mapData.add(Optional.ofNullable(o).isPresent() ? String.valueOf(o) : ""));
            resultData.add(mapData);
        });
        return resultData;
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
