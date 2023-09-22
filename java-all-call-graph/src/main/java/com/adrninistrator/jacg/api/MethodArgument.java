package com.adrninistrator.jacg.api;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

/**
 * 方法参数类
 */
public class MethodArgument {
    //参数的完全限定类名
    private String fqcn;

    //参数的泛型信息
    private List<String> genericsInfo;


    public MethodArgument(String fqcn, List<String> genericsInfo) {
        this.fqcn = fqcn;
        this.genericsInfo = genericsInfo;
    }

    public MethodArgument() {
    }

    public String getFqcn() {
        return fqcn;
    }

    public void setFqcn(String fqcn) {
        this.fqcn = fqcn;
    }

    public List<String> getGenericsInfo() {
        return genericsInfo;
    }

    public void setGenericsInfo(List<String> genericsInfo) {
        this.genericsInfo = genericsInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        MethodArgument that = (MethodArgument) o;

        return new EqualsBuilder()
                .append(fqcn, that.fqcn)
                .append(genericsInfo, that.genericsInfo)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(fqcn)
                .append(genericsInfo)
                .toHashCode();
    }
}
