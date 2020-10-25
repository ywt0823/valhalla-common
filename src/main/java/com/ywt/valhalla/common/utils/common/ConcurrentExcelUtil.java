package com.ywt.valhalla.common.utils.common;

import com.ywt.valhalla.common.utils.BasicConstant;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.FileOutputStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * ConcurrentExcelUtil
 *
 * @author yangwentao
 * @desc 并发生成excel文件
 * @date 2020/9/27 15:11
 **/
public class ConcurrentExcelUtil {
    private static final Logger LOG = LoggerFactory.getLogger(ConcurrentExcelUtil.class);
    private static Long sheetMaxNumber = 1000000L;
    private static Long batchMaxNumber = 200000L;
    private static DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(ZoneId.systemDefault());

    public static String downloadExcelFile(String excelName, String sql, JdbcTemplate jdbcTemplate, List<String> firstHeader, Map<String, String> zhEnRelation) {
        Long beginTime = Instant.now().toEpochMilli();
        Integer threadNum = Runtime.getRuntime().availableProcessors();
        Long totalCount = queryTotalCount(sql, jdbcTemplate);
        Long batchNum = computeBatchNum(threadNum, totalCount);
//        excelName = generateNewExcelName(excelName);
        String path = generateExcelFile(excelName, totalCount, threadNum, batchNum, sql, jdbcTemplate, firstHeader, zhEnRelation);
        Long endTime = Instant.now().toEpochMilli();
        Long castSecond = (endTime - beginTime) / 1000;
        LOG.info(excelName + "生成结束，总耗时" + castSecond + "秒");
        return path;
    }


    public static String downloadExcelFile(String excelName, Integer threadNum, Long batchNum, String sql, JdbcTemplate jdbcTemplate, List<String> firstHeader, Map<String, String> zhEnRelation) {
        Long beginTime = Instant.now().toEpochMilli();
        int cpuCount = Runtime.getRuntime().availableProcessors();
        if (threadNum > cpuCount) {
            threadNum = cpuCount;
        }
        Long totalCount = queryTotalCount(sql, jdbcTemplate);
//        excelName = generateNewExcelName(excelName);
        String path = generateExcelFile(excelName, totalCount, threadNum, batchNum, sql, jdbcTemplate, firstHeader, zhEnRelation);
        Long endTime = Instant.now().toEpochMilli();
        Long castSecond = (endTime - beginTime) / 1000;
        LOG.info(excelName + "生成结束，总耗时" + castSecond + "秒");
        return path;
    }

    private static String generateNewExcelName(String excelName) {
        String newExcelName;
        if (excelName.endsWith(BasicConstant.EXCEL03_SUFFIX) || excelName.endsWith(BasicConstant.EXCEL07_SUFFIX)) {
            String[] split = excelName.split("[.]");
            newExcelName = split[0] + df.format(Instant.now()) + BasicConstant.STR_DOT + split[1];
        } else {
            newExcelName = excelName + df.format(Instant.now()) + BasicConstant.EXCEL07_SUFFIX;
        }
        return newExcelName;
    }

    private static Long queryTotalCount(String sql, JdbcTemplate jdbcTemplate) {
        String countSql = "SELECT COUNT(1) as query_count FROM (" + sql + ") a";
        Map<String, Object> stringObjectMap = jdbcTemplate.queryForMap(countSql);
        Long totalCount = (Long) stringObjectMap.get("query_count");
        LOG.info("查询总条数：" + totalCount);
        return totalCount;
    }

    private static Long computeBatchNum(Integer threadNum, Long totalCount) {
        Long batchNum;
        if (totalCount > sheetMaxNumber) {
            Long l = sheetMaxNumber / threadNum;
            Long l1 = l > 10000 ? l / 10000 * 10000 : 10000;
            batchNum = l > batchMaxNumber ? batchMaxNumber : l1;
        } else {
            Long l = totalCount / threadNum;
            batchNum = l > 10000 ? l / 10000 * 10000 : 10000;
        }
        return batchNum;
    }


    private static String generateExcelFile(String excelPathName, Long totalCount, Integer threadNum, Long batchNum, String sql, JdbcTemplate jdbcTemplate, List<String> firstHeader, Map<String, String> zhEnRelation) {

//        String excelPathName = "/" + excelName;
        try {
            FileOutputStream fos = new FileOutputStream(excelPathName, true);
            SXSSFWorkbook wb = new SXSSFWorkbook(10000);
            Map<String, List<Map<String, Long>>> allSheetBatchOffset = computeQueryBatch(totalCount, batchNum, sql, jdbcTemplate);
            ScheduledExecutorService producerThreamPool = new ScheduledThreadPoolExecutor(threadNum, new BasicThreadFactory.Builder().namingPattern("data-access-producer-%d").daemon(true).build());
            Map<Long, List<Map<String, Object>>> allData = new ConcurrentHashMap<>();
            //循环每个sheet
            for (String sheetName : allSheetBatchOffset.keySet()) {
                List<Map<String, Long>> batchOffset = allSheetBatchOffset.get(sheetName);
                //将每个sheet中的数量分批次
                for (int i = 0; i < batchOffset.size(); i++) {
                    Map<String, Long> stringIntegerMap = batchOffset.get(i);
                    Long batch = stringIntegerMap.get("batch");
                    Long begin = stringIntegerMap.get("begin");
                    Long size = stringIntegerMap.get("size");
                    producerThreamPool.execute(() -> {
                        try {
                            String batchSql = "SELECT a.* FROM (" + sql + ") a limit " + begin + "," + size;
                            LOG.info(Thread.currentThread().getName() + "执行sql\n" + batchSql);
                            List<Map<String, Object>> batchData = jdbcTemplate.queryForList(batchSql);
                            allData.put(batch, batchData);
                        } catch (Exception e) {
                            LOG.error(LogWrapperUtil.wrapperErrorLog(e));
                        } finally {
                            LOG.info(Thread.currentThread().getName() + "执行完毕! [" + sheetName + "] limit " + begin + "," + size);
                        }
                    });
                }
                Sheet sh = wb.createSheet(sheetName);
                //生成标题
                Row row = sh.createRow(0);
                for (int i = 0; i < firstHeader.size(); i++) {
                    String title = firstHeader.get(i);
                    Cell cell = row.createCell(i);
                    cell.setCellValue(title);
                }
                //主线程在这里循环等待子线程查回来的数据
                Integer currentIndex = 0;
                while (currentIndex < batchOffset.size()) {
                    //为了确保顺序，在这里按照预分好的批次写入数据，写入不能多线程
                    Map<String, Long> stringIntegerMap = batchOffset.get(currentIndex);
                    Long batch = stringIntegerMap.get("batch");
                    Long rowNum = stringIntegerMap.get("row");
                    if (allData.containsKey(batch)) {
                        List<Map<String, Object>> batchDatas = allData.get(batch);
                        for (int j = 0; j < batchDatas.size(); j++) {
                            Map<String, Object> currentRow = batchDatas.get(j);
                            Long currentRowNum = rowNum + j + 1;
                            Row currentExcelRow = sh.createRow(currentRowNum.intValue());
                            for (int h = 0; h < firstHeader.size(); h++) {
                                Cell cell = currentExcelRow.createCell(h);
                                String tableColumn = zhEnRelation.get(firstHeader.get(h));
                                cell.setCellValue(String.valueOf(currentRow.get(tableColumn)));
                            }
                        }
                        allData.remove(batch);
                        currentIndex++;
                    } else {
                        Thread.sleep(2000);
                    }
                }
            }
            wb.write(fos);
            wb.dispose();

        } catch (Exception e) {
            LOG.error(LogWrapperUtil.wrapperErrorLog(e));
            return "";
        }

        return excelPathName;
    }


    private static Map<String, List<Map<String, Long>>> computeQueryBatch(Long totalCount, Long batchNum, String sql, JdbcTemplate jdbcTemplate) {
        //如果数量超过100w，则分多个sheet来存放，07excel有限制，每个sheet最高可以存放100w条数据
        Map<String, Long> sheets = new LinkedHashMap<>();
        if (totalCount > sheetMaxNumber) {
            Long remainder = totalCount % sheetMaxNumber;
            Long sheetCount = totalCount / sheetMaxNumber;
            for (int i = 1; i <= sheetCount; i++) {
                sheets.put("Sheet" + i, sheetMaxNumber);
            }
            if (remainder > 0) {
                sheets.put("Sheet" + (sheetCount + 1), remainder);
            }
        } else {
            sheets.put("Sheet1", totalCount);
        }

        Map<String, List<Map<String, Long>>> allSheetBatchOffset = new LinkedHashMap<>();
        Long sheetIndex = 1L;
        for (String sheetName : sheets.keySet()) {
            Long sheetNum = sheets.get(sheetName);
            Long pageCount = sheetNum % batchNum > 0 ? (sheetNum / batchNum) + 1 : sheetNum / batchNum;
            List<Map<String, Long>> batchOffset = new ArrayList<>();
            Long startNum = (sheetIndex - 1) * sheetMaxNumber;
            for (long i = 1; i <= pageCount; i++) {
                Map<String, Long> batchStatistics = new HashMap<>(4);
                Long sqlStartNum = startNum + ((i - 1) * batchNum);
                Long rowStartNum = (i - 1) * batchNum;
                batchStatistics.put("batch", i);
                batchStatistics.put("begin", sqlStartNum);
                batchStatistics.put("row", rowStartNum);
                batchStatistics.put("size", i * batchNum > sheetNum ? sheetNum - rowStartNum : batchNum);
                batchOffset.add(batchStatistics);
            }
            allSheetBatchOffset.put(sheetName, batchOffset);
            sheetIndex++;
        }

        return allSheetBatchOffset;
    }


//    public static void main(String[] args) {
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
//        dataSource.setUrl("jdbc:mysql://47.92.12.99:3306/zichan360_case?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&useAffectedRows=true&zeroDateTimeBehavior=convertToNull&allowMultiQueries=true");
//        dataSource.setUsername("sunweihong");
//        dataSource.setPassword("zichan360_sunweihong");
//        JdbcTemplate template = new JdbcTemplate(dataSource);
//        List<String> firstHeader = new ArrayList<String>() {{
//            add("债务人id");
//            add("委案id");
//            add("债务类型");
//            add("省份id");
//            add("城市id");
//            add("县id");
//            add("委托方公司id");
//            add("不知道");
//            add("回款状态");
//        }};
//        Map<String, String> zhEnRelation = new HashMap<String, String>() {{
//            put("债务人id", "debtor_id");
//            put("委案id", "case_id");
//            put("债务类型", "debt_type");
//            put("省份id", "province_id");
//            put("城市id", "city_id");
//            put("县id", "county_id");
//            put("委托方公司id", "client_company_id");
//            put("不知道", "api_withdraw_id");
//            put("回款状态", "repayment_status");
//        }};
//        String sql = "SELECT debtor_id,case_id,debt_type,province_id,city_id,county_id,client_company_id,api_withdraw_id,repayment_status FROM zichan360_case.withdraw limit 5000000";
//        downloadExcelFile("测试excle.xlsx", sql, template, firstHeader, zhEnRelation);
//    }


}
