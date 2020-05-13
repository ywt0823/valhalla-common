package com.zichan360.bigdata.dataportalcommons.common.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author: sunweihong
 * @date: 2018/10/26 15:25
 * @description: 公共工具
 */
public class CommonUtil {

    private static final Logger LOG = LogManager.getLogger(CommonUtil.class);

    public static String wrapperErrorLog(Exception exception) {
        InetAddress address = null;
        String error = "";
        try {
            address = InetAddress.getLocalHost();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            exception.printStackTrace(new PrintStream(baos));
            error = baos.toString();
        } catch (UnknownHostException e) {
            LOG.error(CommonUtil.wrapperErrorLog(e));
        }
        return "RUNTIME_ERROR【" + address + "】:" + error;
    }

    public static String wrapperNormalLog(String info) {
        InetAddress address = null;
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            LOG.error(CommonUtil.wrapperErrorLog(e));
        }
        return "【" + address + "】:" + info;
    }

    public static String getLocalHost() {
        InetAddress address = null;
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            LOG.error(CommonUtil.wrapperErrorLog(e));
        }
        return address.toString();
    }

}
