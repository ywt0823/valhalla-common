package com.valhalla.common.utils.sequence;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import java.net.Inet4Address;
import java.net.UnknownHostException;

/**
 * <p>雪花算法工具<p/>
 *
 * @author ywt
 */
public class SnowflakeUtil {

    // 获取默认的工作ID
    private static Long getWorkId() {
        try {
            String hostAddress = Inet4Address.getLocalHost().getHostAddress();
            int[] ints = StringUtils.toCodePoints(hostAddress);
            int sums = 0;
            for (int b : ints) {
                sums += b;
            }
            return (long) (sums % 32);
        } catch (UnknownHostException e) {
            // 如果获取失败，则使用随机数备用
            return RandomUtils.nextLong(0, 31);
        }
    }

    // 获取默认的数据中心
    private static Long getDataCenterId() {
        int[] ints = StringUtils.toCodePoints(SystemUtils.getHostName());
        int sums = 0;
        for (int i : ints) {
            sums += i;
        }
        return (long) (sums % 32);
    }

    public static Long getSnowflakeId() {
        SnowflakeSequence SNOWFLAKE_SEQUENCE = SnowflakeSequence.getInstance(getWorkId(), getDataCenterId());
        return SNOWFLAKE_SEQUENCE.getId();
    }

}
