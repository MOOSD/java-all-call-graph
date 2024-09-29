package com.adrninistrator.jacg.dto.feign;

public class FeignClientInfo {

    private String fullMethod;
    private String methodHash;

    private String serviceName;

    private String contextId;

    private String showUri;

    private String requestMethod;


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

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

}
