package com.adrninistrator.jacg.api;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 被调用者树的节点，即向上的调用树
 * 根节点代表查询的方法A，叶子节点代表方法A的调用者们，深度属性直接表示调用层级。
 * 因此在向上的调用树中，从根向叶子节点看有许多路径；从一个叶子节点到根节点的路径唯一。
 */
public class CalleeNode {

    //当前节点深度
    private int depth;
    //方法名
    private String methodName;
    //当前方法所在类的类名
    private String className;
    //是否方法入口
    private boolean isEntrance;
    //当前方法所在类的完全限定类名
    private String fqcn;
    //方法参数
    private List<MethodArgument> methodArguments;
    //方法注解
    private String annotation;
    //业务数据信息
    private List<BusinessData> businessData;
    //是否运行在声明式方法中 todo:默认值不序列化
    private boolean inTransaction;
    //泛型信息
    private List<GenericsInfo> genericsInfo;
    //被调用信息
    private CalleeInfo calleeInfo;
    //此方法的被调用者
    private List<CalleeNode> callees;
    //此方法调用的方法
    @JsonIgnore
    private List<CalleeNode> callers;

    /**
     * 实例化一个CalleeNodeNode
     * @return new calleNode object
     */
    public static CalleeNode instantiate(){
        CalleeNode calleeNode = new CalleeNode();
        calleeNode.callees = new ArrayList<>();
        calleeNode.callers = new ArrayList<>();
        //实例化调用信息
        calleeNode.calleeInfo = new CalleeInfo();
        return calleeNode;
    }

    public static CalleeNode instantiate(boolean isRoot){
        CalleeNode calleeNode = new CalleeNode();
        calleeNode.callees = new ArrayList<>();
        //实例化调用信息
        calleeNode.calleeInfo = new CalleeInfo();
        return calleeNode;
    }

    /**
     * 添加一个此方法的调用者
     * @param calleeNode 调用此方法的方法节点
     */
    public void addCallee(CalleeNode calleeNode){
        Objects.requireNonNull(callees).add(Objects.requireNonNull(calleeNode));

    }

    /**
     * 添加一个此方法调用的方法
     * @param calleeNode
     */
    public void addCaller(CalleeNode calleeNode){
        Objects.requireNonNull(callers).add(Objects.requireNonNull(calleeNode));
    }

    /**
     * 当前树是否有任意叶子节点
     * @return
     */
    public boolean hasNext(){
        return callees == null || callees.size() != 0;
    }



    /**
     * 此方法运行在声明式事务中
     */
    public void runInTransaction(){
        this.inTransaction = true;
    }

    public List<GenericsInfo> getGenericsInfo() {
        return genericsInfo;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public String getFqcn() {
        return fqcn;
    }

    public void setFqcn(String fqcn) {
        this.fqcn = fqcn;
    }

    public List<MethodArgument> getMethodArguments() {
        return methodArguments;
    }

    public void setMethodArguments(List<MethodArgument> methodArguments) {
        this.methodArguments = methodArguments;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public CalleeInfo getCalleeInfo() {
        return calleeInfo;
    }

    public List<CalleeNode> getCallees() {
        return callees;
    }

    public List<CalleeNode> getCallers() {
        return callers;
    }

    public boolean isEntrance() {
        return isEntrance;
    }

    public void setEntrance(boolean entrance) {
        isEntrance = entrance;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }


    public void setGenericsInfo(List<GenericsInfo> genericsInfo) {
        this.genericsInfo = genericsInfo;
    }

    public List<BusinessData> getBusinessData() {
        return businessData;
    }

    public void setBusinessData(List<BusinessData> businessData) {
        this.businessData = businessData;
    }

    public boolean isInTransaction() {
        return inTransaction;
    }

}
