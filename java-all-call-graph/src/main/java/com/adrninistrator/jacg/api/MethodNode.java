package com.adrninistrator.jacg.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * 方法节点信息
 */
@JsonTypeInfo(use= JsonTypeInfo.Id.CLASS,property = "@class")
@JsonSubTypes({@JsonSubTypes.Type(value = CallerNode.class),@JsonSubTypes.Type(value = CalleeNode.class)})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public abstract class MethodNode {

    protected String id;

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
    protected CallInfo callInfo = new CallInfo();

    protected boolean isRoot;
    // 修改的方法信息
    protected List<String> originTextInfo;
    // 调用树重复出现的次数
    protected AtomicInteger repeatNum;
    // 下一个节点
    protected List<MethodNode> children = new ArrayList<>();

    @JsonIgnore
    protected MethodNode before;

    // 节点生成时候的数据信息
    protected List<String> genMessage;

    // 根节点创建的时候，必然是一个被修改的节点
    public void isRoot(String originText){
        this.isRoot = true;
        this.originTextInfo = new ArrayList<>();
        originTextInfo.add(originText);
    }
    /**
     * 当前树是否有任意叶子节点
     * @return
     */
    public boolean hasNext(){
        return children == null || children.size() != 0;
    }

    /**
     * @param consumer 消费行为
     */
    void forEach(Consumer<MethodNode> consumer){
        // 处理当前节点
        consumer.accept(this);
        Queue<MethodNode> taskQueue = new LinkedList<>(this.getChildren());
        // 广度遍历所有节点，执行消费程序
        while(!taskQueue.isEmpty()){
            // 出队
            MethodNode node = taskQueue.poll();
            consumer.accept(node);
            if (node.getCallInfo().getCycleCall() != -1) {
                return;
            }
            // 将其子节点添加到队列
            if (Objects.nonNull(node.getChildren()) && !node.getChildren().isEmpty()){
                taskQueue.addAll(node.getChildren());
            }
        }

    }

    public void addNext(MethodNode methodNode){
        children.add(methodNode);
    }

    /**
     * 增加此节点的修改次数
     */
    public void incrementModifyNum(){
        if (Objects.isNull(repeatNum)){
            this.repeatNum = new AtomicInteger(1);
            return;
        }
        repeatNum.incrementAndGet();
    };

    /**
     * 此方法运行在声明式事务中
     */
    public void runInTransaction(){
        this.inTransaction = true;
    }

    /**
     * 添加节点生成信息
     */
    public void addGenMessage(String message){
        // 懒加载
        if (Objects.isNull(genMessage)) {
            genMessage = new ArrayList<>();
        }
        genMessage.add(message);
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

    public AtomicInteger getRepeatNum() {
        return repeatNum;
    }

    public void setRepeatNum(AtomicInteger repeatNum) {
        this.repeatNum = repeatNum;
    }

    public List<MethodNode> getChildren() {
        return children;
    }

    public MethodNode getBefore() {
        return before;
    }

    public List<String> getGenMessage() {
        return genMessage;
    }

    public void setGenMessage(List<String> genMessage) {
        this.genMessage = genMessage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
