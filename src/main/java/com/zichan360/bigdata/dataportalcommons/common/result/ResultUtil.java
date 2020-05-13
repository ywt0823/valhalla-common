package com.zichan360.bigdata.dataportalcommons.common.result;

import java.util.Optional;

/**
 * @author ywt
 * @date 2020年2月9日 08:58:39
 **/
public class ResultUtil {
    public static final Integer PAGESIZE_MAX = 500;
    public static final Integer PAGESIZE_DEFAULT = 10;

    /**
     * 返回成功，传入返回体具体出參
     *
     * @param t
     * @return
     */
    public static <T> Result<T> success(T t) {
        return success(t, null);
    }

    public static <T> Result<T> success(T t, ResultPageInfo resultPageInfo) {
        Result<T> result = new Result<>();
        result.setStatus(ResultEnum.SUCCESS);
        result.setStatus_code(200);
        result.setMsg("接口调用成功！");
        result.setData(t);
        result.setPageInfo(resultPageInfo);
        return result;
    }


    /**
     * 提供给部分不需要出參的接口
     *
     * @return
     */
    public static Result success() {
        return success(null);
    }

    /**
     * 自定义错误信息
     *
     * @param status
     * @param msg
     * @return
     */
    public static Result error(ResultEnum status, String msg) {
        Result result = new Result();
        result.setStatus(status);
        result.setStatus_code(1000); //1000,程序默认错误
        result.setMsg(msg);
        result.setData(null);
        ResultPageInfo resultPageInfo = new ResultPageInfo("0", "0", "0");
        result.setPageInfo(resultPageInfo);
        return result;
    }

    public static Result error(ResultEnum status, Integer status_code, String msg) {
        Result result = new Result();
        result.setStatus(status);
        result.setStatus_code(status_code);
        result.setMsg(msg);
        ResultPageInfo resultPageInfo = new ResultPageInfo("0", "0", "0");
        result.setPageInfo(resultPageInfo);
        return result;
    }

    public static Integer formatCurrentPage(String currentPage) {
        //当前页不能小于0
        Integer rs;
        try {
            rs = Optional.ofNullable(currentPage)
                    .map(page -> {
                        int cp = Integer.parseInt(page);
                        return Math.max(cp, 0);
                    }).orElse(0);
        } catch (Exception e) {
            return 0;
        }
        return rs;
    }

    public static Integer formatPageSize(String pageSize) {
        //页面行数最大为50,最小行数为10
        Integer rs;
        try {
            rs = Optional.ofNullable(pageSize)
                    .map(size -> {
                        int pSize = Integer.parseInt(size);
                        if (pSize > PAGESIZE_MAX) //50
                        {
                            return PAGESIZE_MAX;
                        } else if (pSize < 0) {
                            return PAGESIZE_DEFAULT; //10
                        }
                        return pSize;
                    }).orElse(PAGESIZE_DEFAULT);
        } catch (Exception e) {
            return PAGESIZE_DEFAULT;
        }
        return rs;
    }

}
