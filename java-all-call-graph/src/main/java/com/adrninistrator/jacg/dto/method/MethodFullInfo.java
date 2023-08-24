package com.adrninistrator.jacg.dto.method;

public class MethodFullInfo {
    // 完整或简单类名
    private String simpleClassName;

    // 方法名
    private String methodName;

    // 方法HASH+长度
    private String methodHash;

    // 方法全限定名
    private String fullMethod;

    public String getSimpleClassName() {
        return simpleClassName;
    }

    public void setSimpleClassName(String simpleClassName) {
        this.simpleClassName = simpleClassName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
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
