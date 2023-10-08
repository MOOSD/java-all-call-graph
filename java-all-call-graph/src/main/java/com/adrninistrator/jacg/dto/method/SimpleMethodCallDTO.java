package com.adrninistrator.jacg.dto.method;

public class SimpleMethodCallDTO {
    // 完整方法HASH+长度
    private String methodHash;

    // 完整方法信息
    private String fullMethod;

    // 方法调用的标志
    private int callFlags;

    private int callId;

    private String callTypes;

    public SimpleMethodCallDTO() {
    }

    public SimpleMethodCallDTO(String methodHash, String fullMethod, int callFlags, int callId, String callTypes) {
        this.methodHash = methodHash;
        this.fullMethod = fullMethod;
        this.callFlags = callFlags;
        this.callId = callId;
        this.callTypes = callTypes;
    }

    public String getMethodHash() {
        return methodHash;
    }

    public void setMethodHash(String methodHash) {
        this.methodHash = methodHash;
    }

    public String getFullMethod() {
        return fullMethod;
    }

    public void setFullMethod(String fullMethod) {
        this.fullMethod = fullMethod;
    }

    public int getCallFlags() {
        return callFlags;
    }

    public void setCallFlags(int callFlags) {
        this.callFlags = callFlags;
    }

    public int getCallId() {
        return callId;
    }

    public void setCallId(int callId) {
        this.callId = callId;
    }

    public String getCallTypes() {
        return callTypes;
    }

    public void setCallTypes(String callTypes) {
        this.callTypes = callTypes;
    }
}
