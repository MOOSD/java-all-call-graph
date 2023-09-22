package com.adrninistrator.jacg.dto.method;

public class SimpleMethodInfo {
    // 完整方法HASH+长度
    private String methodHash;

    // 完整方法信息
    private String fullMethod;

    // 方法调用的标志
    private int callFlags;

    public SimpleMethodInfo(String methodHash, String fullMethod, int callFlags) {
        this.methodHash = methodHash;
        this.fullMethod = fullMethod;
        this.callFlags = callFlags;
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
}
