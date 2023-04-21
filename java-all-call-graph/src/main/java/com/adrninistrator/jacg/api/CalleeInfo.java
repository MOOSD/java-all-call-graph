package com.adrninistrator.jacg.api;

import com.adrninistrator.jacg.common.JACGConstants;

public class CalleeInfo {


    //当前方法被调用的调用行
    int row;

    //循环调用
    int cycleCall = JACGConstants.NO_CYCLE_CALL_FLAG;

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
}
