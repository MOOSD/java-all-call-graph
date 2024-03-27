package com.adrninistrator.jacg.api;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

/**
 * controller方法信息表
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ControllerInfo {
    private String showUri;
    private String requestMethod;
    private String classPath;
    private String methodPath;
    private String annotationName;
    private String simpleClassName;
    private String fullMethod;


    public ControllerInfo(){

    }

    public ControllerInfo(ControllerInfo controllerInfo){
        this.showUri = controllerInfo.showUri;
        this.requestMethod = controllerInfo.requestMethod;
        this.classPath = controllerInfo.classPath;
        this.methodPath = controllerInfo.methodPath;
        this.annotationName = controllerInfo.annotationName;
        this.simpleClassName = controllerInfo.simpleClassName;
        this.fullMethod = controllerInfo.fullMethod;
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

    public String getAnnotationName() {
        return annotationName;
    }

    public void setAnnotationName(String annotationName) {
        this.annotationName = annotationName;
    }

    public String getSimpleClassName() {
        return simpleClassName;
    }

    public void setSimpleClassName(String simpleClassName) {
        this.simpleClassName = simpleClassName;
    }

    public String getFullMethod() {
        return fullMethod;
    }

    public void setFullMethod(String fullMethod) {
        this.fullMethod = fullMethod;
    }

    @Override
    public String toString() {
        return "ControllerInfo{" +
                "showUri='" + showUri + '\'' +
                ", requestMethod='" + requestMethod + '\'' +
                ", classPath='" + classPath + '\'' +
                ", methodPath='" + methodPath + '\'' +
                ", annotationName='" + annotationName + '\'' +
                ", simpleClassName='" + simpleClassName + '\'' +
                ", fullMethod='" + fullMethod + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ControllerInfo that = (ControllerInfo) o;
        return Objects.equals(showUri, that.showUri) && Objects.equals(requestMethod, that.requestMethod) && Objects.equals(classPath, that.classPath) && Objects.equals(methodPath, that.methodPath) && Objects.equals(annotationName, that.annotationName) && Objects.equals(simpleClassName, that.simpleClassName) && Objects.equals(fullMethod, that.fullMethod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(showUri, requestMethod, classPath, methodPath, annotationName, simpleClassName, fullMethod);
    }
}
