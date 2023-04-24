package com.adrninistrator.jacg.api;

import com.adrninistrator.jacg.common.JACGConstants;

public class CalleeInfo {


    //当前方法被调用的调用行
    private int row;

    //循环调用
    private int cycleCall = JACGConstants.NO_CYCLE_CALL_FLAG;

    //是否运行在其他线程 todo:默认值不序列化
    private boolean isAsync;

    /**
     * 此方法运行在其他线程
     */
    public void runInOtherThread() {
        this.isAsync = true;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }


    public int getCycleCall() {
        return cycleCall;
    }

    public void setCycleCall(int cycleCall) {
        this.cycleCall = cycleCall;
    }


    public boolean isAsync() {
        return isAsync;
    }
}
