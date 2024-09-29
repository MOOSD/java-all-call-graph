package com.adrninistrator.jacg.handler.dto.spring;

public class SpringControllerComplexInfo {

    private String showUri;

    private String fullMethod;

    private String methodHash;

    private String requestMethod;

    public String getShowUri() {
        return showUri;
    }

    public void setShowUri(String showUri) {
        this.showUri = showUri;
    }

    public String getFullMethod() {
        return fullMethod;
    }

    public void setFullMethod(String fullMethod) {
        this.fullMethod = fullMethod;
    }

    public String getMethodHash() {
        return methodHash;
    }

    public void setMethodHash(String methodHash) {
        this.methodHash = methodHash;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }
}
