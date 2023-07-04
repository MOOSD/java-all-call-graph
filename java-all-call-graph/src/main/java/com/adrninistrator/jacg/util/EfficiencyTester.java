package com.adrninistrator.jacg.util;

public class EfficiencyTester {
    long beginTime;
    long endTime;


    public EfficiencyTester begin(){
        beginTime = System.currentTimeMillis();
        return this;
    }
    public long end(){
        endTime = System.currentTimeMillis();
        return endTime - beginTime;
    }

}
