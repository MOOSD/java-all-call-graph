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

    //是否方法入口
    private boolean isEntrance;
    //当前节点深度
    private int depth;
    //当前方法所在类的完全限定类名
    private String fqcn;
    //当前方法所在类的类名
    private String className;
    //方法参数
    private List<String> methodArguments;
    //方法名
    private String methodName;
    //方法注解
    private String annotation;
    //泛型信息
    private GenericsInfo genericsInfo;
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
        calleeNode.genericsInfo = new GenericsInfo();
        return calleeNode;
    }

    public static CalleeNode instantiate(boolean isRoot){
        CalleeNode calleeNode = new CalleeNode();
        calleeNode.callees = new ArrayList<>();
        //实例化调用信息
        calleeNode.calleeInfo = new CalleeInfo();
        calleeNode.genericsInfo = new GenericsInfo();
        return calleeNode;
    }

    /**
     * 添加一个此方法的调用者
     * @param calleeNode 调用此方法的方法节点
     */
    public void addCallee(CalleeNode calleeNode){
        assert Objects.nonNull(callees);
        assert Objects.nonNull(calleeNode);
        // todo 按照调用行的前后
        callees.add(calleeNode);
    }

    /**
     * 添加一个此方法调用的方法
     * @param calleeNode
     */
    public void addCaller(CalleeNode calleeNode){
        assert Objects.nonNull(callers);
        assert Objects.nonNull(calleeNode);
        callers.add(calleeNode);
    }

    /**
     * 当前树是否有任意叶子节点
     * @return
     */
    public boolean hasNext(){
        return callees == null || callees.size() != 0;
    }


    public GenericsInfo getGenericsInfo() {
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

    public List<String> getMethodArguments() {
        return methodArguments;
    }

    public void setMethodArguments(List<String> methodArguments) {
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


}
