package com.adrninistrator.jacg.api;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;
import java.util.function.Consumer;

/**
 * 方法节点信息
 */
public abstract class MethodNode<T> {

    //方法名
    protected String methodName;

    //当前方法所在类的类名
    protected String className;

    //当前方法所在类的完全限定类名
    protected String FQCN;

    //方法参数
    protected List<MethodArgument> methodArguments;

    //方法注解
    protected List<String> annotation;

    //泛型信息
    protected List<GenericsInfo> genericsInfo;

    //当前方法所在服务的服务名
    protected String serviceName;

    //controller信息，为空则表示不为controller
    protected ControllerInfo controllerInfo;

    //是否运行在声明式事务中 todo:默认值不序列化
    protected boolean inTransaction;

    //业务数据信息
    protected List<BusinessData> businessData;

    //调用信息
    protected CallInfo callInfo;

    /**
     * 进行消费的类型由子类继承时决定
     * @param consumer 消费行为
     */
    abstract void forEach(Consumer<T> consumer);

    /**
     * 此方法运行在声明式事务中
     */
    public void runInTransaction(){
        this.inTransaction = true;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getFQCN() {
        return FQCN;
    }

    public void setFQCN(String FQCN) {
        this.FQCN = FQCN;
    }

    public List<MethodArgument> getMethodArguments() {
        return methodArguments;
    }

    public void setMethodArguments(List<MethodArgument> methodArguments) {
        this.methodArguments = methodArguments;
    }

    public List<String> getAnnotation() {
        return annotation;
    }

    public void setAnnotation(List<String> annotation) {
        this.annotation = annotation;
    }

    public List<GenericsInfo> getGenericsInfo() {
        return genericsInfo;
    }

    public void setGenericsInfo(List<GenericsInfo> genericsInfo) {
        this.genericsInfo = genericsInfo;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public ControllerInfo getControllerInfo() {
        return controllerInfo;
    }

    public void setControllerInfo(ControllerInfo controllerInfo) {
        this.controllerInfo = controllerInfo;
    }

    public boolean isInTransaction() {
        return inTransaction;
    }

    public void setInTransaction(boolean inTransaction) {
        this.inTransaction = inTransaction;
    }

    public CallInfo getCallInfo() {
        return callInfo;
    }

    public void setCallInfo(CallInfo callInfo) {
        this.callInfo = callInfo;
    }

    public List<BusinessData> getBusinessData() {
        return businessData;
    }

    public void setBusinessData(List<BusinessData> businessData) {
        this.businessData = businessData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        MethodNode<?> that = (MethodNode<?>) o;

        return new EqualsBuilder().append(methodName, that.methodName)
                .append(FQCN, that.FQCN)
                .append(methodArguments, that.methodArguments)
                .append(annotation, that.annotation)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(methodName)
                .append(FQCN)
                .append(methodArguments)
                .append(annotation).toHashCode();
    }
}
