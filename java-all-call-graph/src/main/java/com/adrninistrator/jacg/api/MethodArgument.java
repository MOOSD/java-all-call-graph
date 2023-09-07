package com.adrninistrator.jacg.api;

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
}
