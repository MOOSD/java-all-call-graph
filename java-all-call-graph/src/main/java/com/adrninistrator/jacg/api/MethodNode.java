package com.adrninistrator.jacg.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * 方法节点信息
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public abstract class MethodNode {

    @JsonIgnore
    protected String fullMethod;

    protected String methodHash;

    //方法名
    protected String methodName;

    //当前方法所在类的类名
    protected String className;

    //当前方法所在类的完全限定类名
    protected String FQCN;

    //方法形参数
    protected List<MethodArgument> methodFormalArguments;

    //方法注解
    protected List<String> annotation;

    //泛型信息
    protected List<GenericsInfo> genericsInfo;

    //当前方法所在服务的服务名
    protected String serviceName;

    //controller信息，为空则表示不为controller
    protected List<ControllerInfo> controllerInfo;

    //是否运行在声明式事务中 todo:默认值不序列化
    protected boolean inTransaction;

    //业务数据信息
    protected List<BusinessData> businessData;

    //调用信息
    protected CallInfo callInfo;

    protected boolean isRoot;
    // 修改的方法信息
    protected List<String> originTextInfo;
    // 方法被修改的次数
    protected AtomicInteger modifyNum;
    // 方法受影响的次数
    protected AtomicInteger affectedNum;

    protected List<MethodNode> nextNodes;

    @JsonIgnore
    protected MethodNode before;

    public MethodNode() {
        this.originTextInfo = new LinkedList<>();
        this.modifyNum = new AtomicInteger(0);
        this.affectedNum = new AtomicInteger(1);
        this.nextNodes = new ArrayList<>();
        this.callInfo = new CallInfo();
    }

    // 根节点创建的时候，必然是一个受影响过的节点
    public void isRoot(){
        this.isRoot = true;
        this.modifyNum.incrementAndGet();
    }
    /**
     * 当前树是否有任意叶子节点
     * @return
     */
    public boolean hasNext(){
        return nextNodes == null || nextNodes.size() != 0;
    }

    /**
     * @param consumer 消费行为
     */
    void forEach(Consumer<MethodNode> consumer){
        // 处理当前节点
        consumer.accept(this);
        Queue<MethodNode> taskQueue = new LinkedList<>(this.getNextNodes());
        // 广度遍历所有节点，执行消费程序
        while(!taskQueue.isEmpty()){
            // 出队
            MethodNode node = taskQueue.poll();
            consumer.accept(node);
            // 将其子节点添加到队列
            if (Objects.nonNull(node.getNextNodes()) && !node.getNextNodes().isEmpty()){
                taskQueue.addAll(node.getNextNodes());
            }
        }

    }

    public void addNext(MethodNode methodNode){
        nextNodes.add(methodNode);
    }

    /**
     * 增加此节点的修改次数
     */
    public void incrementModifyNum(){
        // 此节点如果是被修改节点，那么一定是受影响的节点
        modifyNum.incrementAndGet();
        incrementAffectedNum();
    };

    /**
     * 如果此节点被影响，那么以此节点为根节点后续的调用树的影响次数均需要增加
     */
    public void incrementAffectedNum(){
        this.forEach(methodNode -> methodNode.getAffectedNum().incrementAndGet());
    }

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

    public List<MethodArgument> getMethodFormalArguments() {
        return methodFormalArguments;
    }

    public void setMethodFormalArguments(List<MethodArgument> methodFormalArguments) {
        this.methodFormalArguments = methodFormalArguments;
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

    public List<ControllerInfo> getControllerInfo() {
        return controllerInfo;
    }

    public void setControllerInfo(List<ControllerInfo> controllerInfo) {
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

    public List<String> getOriginTextInfo() {
        return originTextInfo;
    }

    public void setOriginTextInfo(List<String> originTextInfo) {
        this.originTextInfo = originTextInfo;
    }

    public AtomicInteger getModifyNum() {
        return modifyNum;
    }

    public void setModifyNum(AtomicInteger modifyNum) {
        this.modifyNum = modifyNum;
    }

    public AtomicInteger getAffectedNum() {
        return affectedNum;
    }

    public void setAffectedNum(AtomicInteger affectedNum) {
        this.affectedNum = affectedNum;
    }

    public List<MethodNode> getNextNodes() {
        return nextNodes;
    }

    public MethodNode getBefore() {
        return before;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        MethodNode that = (MethodNode) o;

        return new EqualsBuilder().append(methodName, that.methodName)
                .append(FQCN, that.FQCN)
                .append(methodFormalArguments, that.methodFormalArguments)
                .append(annotation, that.annotation)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(methodName)
                .append(FQCN)
                .append(methodFormalArguments)
                .append(annotation).toHashCode();
    }
}
