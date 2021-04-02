package com.valhalla.common.utils.sequence;


import java.util.Optional;

/**
 * <p>雪花算法<p/>
 *
 * @author ywt
 */
public class SnowflakeSequence {

    private static volatile SnowflakeSequence snowflakeSequence;

    /**
     * 机器id所占的位数
     */
    private final long workerIdBits = 5L;

    /**
     * 数据标识id所占的位数
     */
    private final long dataCenterIdBits = 5L;

    /**
     * 序列在id中占的位数
     */
    private final long sequenceBits = 12L;

    /**
     * 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095)
     */
    private final long sequenceMask = ~(-1L << sequenceBits);

    /**
     * 工作机器ID(0~31)
     */
    private final long workerId;

    /**
     * 数据中心ID(0~31)
     */
    private final long dataCenterId;

    /**
     * 上次生成ID的时间截
     */
    private long lastTimestamp = -1L;


    /**
     * 构造函数
     *
     * @param workerId     工作ID [0~31]
     * @param dataCenterId 数据中心ID [0~31]
     */
    private SnowflakeSequence(long workerId, long dataCenterId) {

        // 检查工作ID和数据中心的合法性
        long maxWorkerId = ~(-1L << workerIdBits);
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("workerId can't be greater than %d or less than 0", maxWorkerId));
        }
        long maxDataCenterId = ~(-1L << dataCenterIdBits);
        if (dataCenterId > maxDataCenterId || dataCenterId < 0) {
            throw new IllegalArgumentException(String.format("dataCenterId can't be greater than %d or less than 0", maxDataCenterId));
        }

        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
    }

    /**
     * double check 方式初始化实例
     *
     * @param workerId
     * @param dataCenterId
     * @return
     */
    public static SnowflakeSequence getInstance(long workerId, long dataCenterId) {
        if (Optional.ofNullable(snowflakeSequence).isEmpty()) {
            synchronized (SnowflakeSequence.class) {
                if (Optional.ofNullable(snowflakeSequence).isEmpty()) {
                    snowflakeSequence = new SnowflakeSequence(workerId, dataCenterId);
                }
            }
        }
        return snowflakeSequence;
    }

    /**
     * <p>获得下一个ID <p/>
     * <p>加了synchronized来保证线程安全<p/>
     *
     * @return SnowflakeId
     */
    public synchronized long getId() {
        long timestamp = timeGen();

        //如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            //如果是同一时间生成的，则进行毫秒内序列
            long sequence = 0L;
            //上次生成ID的时间截
            lastTimestamp = timestamp;
            //移位并通过或运算拼到一起组成64位的ID
            long timestampLeftShift = sequenceBits + workerIdBits + dataCenterIdBits;
            long dataCenterIdShift = sequenceBits + workerIdBits;
            long epoch = 1556766860027L;
            return ((timestamp - epoch) << timestampLeftShift)
                    | (dataCenterId << dataCenterIdShift)
                    | (workerId << sequenceBits)
                    | sequence;
        }
        return timestamp;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     *
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 返回以毫秒为单位的当前时间
     *
     * @return 当前时间(毫秒)
     */
    protected long timeGen() {
        return System.currentTimeMillis();
    }

}