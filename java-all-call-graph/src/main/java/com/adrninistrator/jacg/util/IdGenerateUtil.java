package com.adrninistrator.jacg.util;

public class IdGenerateUtil {

    private final static SnowFlake snowFlake = new SnowFlake(1);

    public static long genId(){
        //机器码默认为1
        return snowFlake.nextId();
    }
}
