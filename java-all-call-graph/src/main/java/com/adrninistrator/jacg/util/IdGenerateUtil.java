package com.adrninistrator.jacg.util;

public class IdGenerateUtil {


    public static long genId(){
        //机器码默认为1
        SnowFlake snowFlake = new SnowFlake(1);
        return snowFlake.nextId();
    }
}
