package com.adrninistrator.jacg.api;

import com.adrninistrator.jacg.common.JACGConstants;
import com.adrninistrator.jacg.dto.method_call.MethodCallInfo;
import com.adrninistrator.javacg.common.enums.JavaCGCallTypeEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * 此方法表示节点与节点之间连线的信息
 * 即调用关系。
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CallInfo {

    private int callId;

    private Integer callFlags;

    private String callType;

    private String callTypeDesc;

    //当前方法被调用的调用行,如果节点没有被调用，那么值为空
    private Integer callerRow;

    // 方法调用时的实参信息
    private Map<Integer, List<MethodCallInfo>> callActualArguments;

    // 方法调用时的对象信息
    private List<MethodCallInfo> callObjectInfo;

    //当前方法调用者所在的类,如果节点没有被调用，那么值为空
    private String callerClassName;

    //循环调用
    private int cycleCall = JACGConstants.NO_CYCLE_CALL_FLAG;

    //是否运行在其他线程 todo:默认值不序列化
    private boolean isAsync;

    //是否是rpc调用
    private boolean isRpc;
    // dao层操作信息
    private boolean unreliableInvocation;
    private DaoOperateInfo daoOperateInfo;

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

    @JsonProperty("isAsync")
    public boolean isAsync() {
        return isAsync;
    }

    @JsonProperty("isRpc")
    public boolean isRpc() {
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
        setCallTypeDesc(JavaCGCallTypeEnum.getFromType(callType).getDesc());
    }

    public String getCallTypeDesc() {
        return callTypeDesc;
    }

    public void setCallTypeDesc(String callTypeDesc) {
        this.callTypeDesc = callTypeDesc;
    }

    public Map<Integer, List<MethodCallInfo>> getCallActualArguments() {
        return callActualArguments;
    }

    public void setCallActualArguments(Map<Integer, List<MethodCallInfo>> callActualArguments) {
        this.callActualArguments = callActualArguments;
    }

    public List<MethodCallInfo> getCallObjectInfo() {
        return callObjectInfo;
    }

    public void setCallObjectInfo(List<MethodCallInfo> callObjectInfo) {
        this.callObjectInfo = callObjectInfo;
    }

    public DaoOperateInfo getDaoOperateInfo() {
        return daoOperateInfo;
    }

    public void setDaoOperateInfo(DaoOperateInfo daoOperateInfo) {
        this.daoOperateInfo = daoOperateInfo;
    }

    public boolean isUnreliableInvocation() {
        return unreliableInvocation;
    }

    public void setUnreliableInvocation(boolean unreliableInvocation) {
        this.unreliableInvocation = unreliableInvocation;
    }
}
