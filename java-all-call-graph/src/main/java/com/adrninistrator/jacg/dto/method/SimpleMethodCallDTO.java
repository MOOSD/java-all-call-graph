package com.adrninistrator.jacg.dto.method;

public class SimpleMethodCallDTO {
    // 完整方法HASH+长度
    private String methodHash;

    // 完整方法信息
    private String fullMethod;


    public SimpleMethodCallDTO() {
    }

    public SimpleMethodCallDTO(String methodHash, String fullMethod) {
        this.methodHash = methodHash;
        this.fullMethod = fullMethod;
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

}
