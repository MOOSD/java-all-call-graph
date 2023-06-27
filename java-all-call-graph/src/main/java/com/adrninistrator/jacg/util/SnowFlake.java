package com.adrninistrator.jacg.util;


/**
 * 获取基于时间和机器码的生成递增long型id
 * 正负号0（1位） + 时间戳（41位） +  机器码（5位） + 序列号（17位）
 */
public class SnowFlake {

    /**
     * 起始的时间戳
     * 2023-06-26 15:42:46
     */
    private final static long START_STMP = 1687765366618L;

    /**
     * 每一部分占用的位数
     */
    private final static long SEQUENCE_BIT = 17; //序列号占用的位数
    private final static long MACHINE_BIT = 5;   //机器标识占用的位数

    /**
     * 每一部分的最大值
     */
    private final static long MAX_MACHINE_NUM = ~(-1L << MACHINE_BIT);
    private final static long MAX_SEQUENCE = ~(-1L << SEQUENCE_BIT);

    /**
     * 每一部分向左的位移
     */
    private final static long MACHINE_LEFT = SEQUENCE_BIT;
    private final static long TIMESTMP_LEFT = MACHINE_BIT + SEQUENCE_BIT;

    // 机器标识不能超过5位,即不超过32
    private final long machineId;     //机器标识
    private long sequence = 0L;  //序列号
    private long lastStmp = -1L; //上一次时间戳

    public SnowFlake(long machineId) {
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException("machineId can't be greater than MAX_MACHINE_NUM or less than 0");
        }
        this.machineId = machineId;
    }

    /**
     * 产生下一个ID
     */
    public synchronized long nextId() {
        long currStmp = getNewstmp();
        if (currStmp < lastStmp) {
            throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
        }

        if (currStmp == lastStmp) {
            //相同毫秒内，序列号自增
            sequence = (sequence + 1) & MAX_SEQUENCE;
            //同一毫秒的序列数已经达到最大
            if (sequence == 0L) {
                currStmp = getNextMill();
            }
        } else {
            //不同毫秒内，序列号置为0
            sequence = 0L;
        }

        lastStmp = currStmp;

        return (currStmp - START_STMP) << TIMESTMP_LEFT //时间戳部分
                | machineId << MACHINE_LEFT             //机器标识部分
                | sequence;                             //序列号部分
    }

    private long getNextMill() {
        long mill = getNewstmp();
        while (mill <= lastStmp) {
            mill = getNewstmp();
        }
        return mill;
    }

    private long getNewstmp() {
        return System.currentTimeMillis();
    }

    public static void main(String[] args) {
        SnowFlake snowFlake = new SnowFlake(2);

        for (int i = 0; i < (1 << 12); i++) {
            System.out.println(snowFlake.nextId());
        }

    }
}