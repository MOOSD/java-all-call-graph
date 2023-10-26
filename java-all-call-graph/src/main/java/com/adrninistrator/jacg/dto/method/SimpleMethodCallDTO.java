package com.adrninistrator.jacg.dto.method;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class SimpleMethodCallDTO {
    // 完整方法HASH+长度
    private String methodHash;

    // 完整方法信息
    private String fullMethod;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SimpleMethodCallDTO that = (SimpleMethodCallDTO) o;

        return new EqualsBuilder().append(methodHash, that.methodHash).append(fullMethod, that.fullMethod).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(methodHash).append(fullMethod).toHashCode();
    }

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
