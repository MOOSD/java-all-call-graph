package com.adrninistrator.jacg.api;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class FeignAndControllerInfo {
    private String feignFullMethod;
    private String feignMethodHash;
    private String feignShowUri;
    private String controllerFullMethod;
    private String controllerMethodHash;
    private String controllerShowUri;
    private String serviceName;

    public String getFeignFullMethod() {
        return feignFullMethod;
    }

    public void setFeignFullMethod(String feignFullMethod) {
        this.feignFullMethod = feignFullMethod;
    }

    public String getFeignMethodHash() {
        return feignMethodHash;
    }

    public void setFeignMethodHash(String feignMethodHash) {
        this.feignMethodHash = feignMethodHash;
    }

    public String getControllerFullMethod() {
        return controllerFullMethod;
    }

    public void setControllerFullMethod(String controllerFullMethod) {
        this.controllerFullMethod = controllerFullMethod;
    }

    public String getControllerMethodHash() {
        return controllerMethodHash;
    }

    public void setControllerMethodHash(String controllerMethodHash) {
        this.controllerMethodHash = controllerMethodHash;
    }

    public String getFeignShowUri() {
        return feignShowUri;
    }

    public void setFeignShowUri(String feignShowUri) {
        this.feignShowUri = feignShowUri;
    }

    public String getControllerShowUri() {
        return controllerShowUri;
    }

    public void setControllerShowUri(String controllerShowUri) {
        this.controllerShowUri = controllerShowUri;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        FeignAndControllerInfo that = (FeignAndControllerInfo) o;

        return new EqualsBuilder().append(feignFullMethod, that.feignFullMethod).append(feignMethodHash, that.feignMethodHash).append(feignShowUri, that.feignShowUri).append(controllerFullMethod, that.controllerFullMethod).append(controllerMethodHash, that.controllerMethodHash).append(controllerShowUri, that.controllerShowUri).append(serviceName, that.serviceName).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(feignFullMethod).append(feignMethodHash).append(feignShowUri).append(controllerFullMethod).append(controllerMethodHash).append(controllerShowUri).append(serviceName).toHashCode();
    }
}
