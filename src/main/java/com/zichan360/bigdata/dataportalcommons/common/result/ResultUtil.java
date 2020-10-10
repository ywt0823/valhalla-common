package com.zichan360.bigdata.dataportalcommons.common.result;

import com.github.pagehelper.PageInfo;
import com.sun.istack.NotNull;

import java.util.List;

/**
 * @author ywt
 * @date 2020年2月9日 08:58:39
 **/
public class ResultUtil {
    /**
     * 返回成功，传入返回体具体出參
     *
     * @param t
     * @return
     */
    public static <T> Result<T> success(@NotNull final T t) {
        return success(t, null);
    }

    public static <T> Result<T> success(@NotNull final T t, final ResultPageInfo resultPageInfo) {
        Result<T> result = new Result<>();
        result.setStatus(ResultEnum.SUCCESS);
        result.setStatus_code(200);
        result.setMsg("接口调用成功！");
        result.setData(t);
        result.setPageInfo(resultPageInfo);
        return result;
    }

    public static <T> Result<T> success(@NotNull final T t, @NotNull final String message, final ResultPageInfo resultPageInfo) {
        Result<T> result = new Result<>();
        result.setStatus(ResultEnum.SUCCESS);
        result.setStatus_code(200);
        result.setMsg(message);
        result.setData(t);
        result.setPageInfo(resultPageInfo);
        return result;
    }

    public static <T> Result<List<T>> success(@NotNull final PageInfo<T> pageResult) {
        Result<List<T>> result = new Result<>();
        result.setData(pageResult.getList());
        result.setMsg("接口调用成功");
        result.setStatus(ResultEnum.SUCCESS);
        result.setStatus_code(200);
        ResultPageInfo resultPageInfo = new ResultPageInfo(String.valueOf(pageResult.getTotal()), String.valueOf(pageResult.getPageNum()), String.valueOf(pageResult.getPageSize()), String.valueOf(pageResult.getPages()));
        result.setPageInfo(resultPageInfo);
        return result;
    }

    /**
     * 提供给部分不需要出參的接口
     *
     * @return
     */
    public static Result success() {
        return success(null, null);
    }

    /**
     * 自定义错误信息
     *
     * @param status
     * @param msg
     * @return
     */
    public static Result error(@NotNull final ResultEnum status, @NotNull final String msg) {
        Result result = new Result();
        result.setStatus(status);
        //1000,程序默认错误
        result.setStatus_code(1000);
        result.setMsg(msg);
        result.setData(null);
        ResultPageInfo resultPageInfo = new ResultPageInfo("0", "0", "0", "0");
        result.setPageInfo(resultPageInfo);
        return result;
    }

    public static Result error(@NotNull final ResultEnum status, @NotNull final Integer status_code, @NotNull final String msg) {
        Result result = new Result();
        result.setStatus(status);
        result.setStatus_code(status_code);
        result.setMsg(msg);
        ResultPageInfo resultPageInfo = new ResultPageInfo("0", "0", "0", "0");
        result.setPageInfo(resultPageInfo);
        return result;
    }
}
