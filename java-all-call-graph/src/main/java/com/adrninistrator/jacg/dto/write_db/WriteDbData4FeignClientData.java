package com.adrninistrator.jacg.dto.write_db;

/**
 * 将FeignClient信息写入数据库的DO类
 */
public class WriteDbData4FeignClientData extends AbstractWriteDbData {

    private String methodHash;

    private int seq;

    private String serviceName;

    private String contextId;

    private String showUri;

    private String classPath;

    private String methodPath;

    private String simpleClassName;

    private String className;

    private String fullMethod;

    public String getMethodHash() {
        return methodHash;
    }

    public void setMethodHash(String methodHash) {
        this.methodHash = methodHash;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public String getShowUri() {
        return showUri;
    }

    public void setShowUri(String showUri) {
        this.showUri = showUri;
    }

    public String getClassPath() {
        return classPath;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    public String getMethodPath() {
        return methodPath;
    }

    public void setMethodPath(String methodPath) {
        this.methodPath = methodPath;
    }

    public String getSimpleClassName() {
        return simpleClassName;
    }

    public void setSimpleClassName(String simpleClassName) {
        this.simpleClassName = simpleClassName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getFullMethod() {
        return fullMethod;
    }

    public void setFullMethod(String fullMethod) {
        this.fullMethod = fullMethod;
    }

    @Override
    public String toString() {
        return "WriteDbData4FeignClientData{" +
                "methodHash='" + methodHash + '\'' +
                ", seq=" + seq +
                ", serviceName='" + serviceName + '\'' +
                ", contextPath='" + contextId + '\'' +
                ", showUri='" + showUri + '\'' +
                ", classPath='" + classPath + '\'' +
                ", methodPath='" + methodPath + '\'' +
                ", simpleClassName='" + simpleClassName + '\'' +
                ", className='" + className + '\'' +
                ", fullMethod='" + fullMethod + '\'' +
                '}';
    }
}
