package com.valhalla.common.utils.common;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author: yangwentao
 * @date: 2018/10/26 15:25
 * @description: 公共工具
 */
public class LogWrapperUtil {

    private static final Logger LOG = LoggerFactory.getLogger(LogWrapperUtil.class);

    public static String wrapperErrorLog(Exception exception) {
        InetAddress address = null;
        String error = "";
        try {
            address = InetAddress.getLocalHost();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            exception.printStackTrace(new PrintStream(baos));
            error = baos.toString();
        } catch (UnknownHostException e) {
            LOG.error(wrapperErrorLog(e));
        }
        return "RUNTIME_ERROR【" + address + "】:" + error;
    }

    public static String wrapperNormalLog(String info) {
        InetAddress address = null;
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            LOG.error(wrapperErrorLog(e));
        }
        return "【" + address + "】:" + info;
    }

    public static String getLocalHost() {
        InetAddress address = null;
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            LOG.error(wrapperErrorLog(e));
        }
        return address.toString();
    }

}
