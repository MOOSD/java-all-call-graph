package com.adrninistrator.jacg.api;

import com.adrninistrator.jacg.common.JACGConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 此方法表示节点与节点之间连线的信息
 * 即调用关系。
 */
public class CallInfo {

    @JsonIgnore
    private int callId;

    private Integer callFlags;

    private String callType;

    //当前方法被调用的调用行,如果节点没有被调用，那么值为空
    private Integer callerRow;

    //当前方法调用者所在的类,如果节点没有被调用，那么值为空
    private String callerClassName;

    //循环调用
    private int cycleCall = JACGConstants.NO_CYCLE_CALL_FLAG;

    //是否运行在其他线程 todo:默认值不序列化
    private boolean isAsync;

    //是否是rpc调用
    private boolean isRpc;


    /**
     * 此方法运行在其他线程
     */
    public void runInOtherThread() {
        this.isAsync = true;
    }

    public Integer getCallerRow() {
        return this.callerRow;
    }

    public void setCallerRow(int callerRow) {
        this.callerRow = callerRow;
    }


    public int getCycleCall() {
        return cycleCall;
    }

    public void setCycleCall(int cycleCall) {
        this.cycleCall = cycleCall;
    }


    public boolean getAsync() {
        return isAsync;
    }

    public boolean getRpc() {
        return isRpc;
    }

    public void setRpc(boolean rpc) {
        isRpc = rpc;
    }

    public String getCallerClassName() {
        return callerClassName;
    }

    public void setCallerClassName(String callerClassName) {
        this.callerClassName = callerClassName;
    }

    public int getCallId() {
        return callId;
    }

    public void setCallId(int callId) {
        this.callId = callId;
    }

    public Integer getCallFlags() {
        return callFlags;
    }

    public void setCallFlags(Integer callFlags) {
        this.callFlags = callFlags;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }


}
