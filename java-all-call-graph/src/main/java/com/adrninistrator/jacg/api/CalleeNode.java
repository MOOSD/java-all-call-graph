package com.adrninistrator.jacg.api;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * 被调用者树的节点，即向上的调用树
 * 根节点代表查询的方法A，叶子节点代表方法A的调用者们，深度属性直接表示调用层级。
 * 因此在向上的调用树中，从根向叶子节点看有许多路径；从一个叶子节点到根节点的路径唯一。
 */
public class CalleeNode extends MethodNode<CalleeNode>{

    //当前节点深度
    private int depth;
    //是否方法入口
    private boolean isEntrance;
    //此方法被哪些方法调用
    private List<CalleeNode> callees;
    @JsonIgnore
    //此方法调用的方法
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
        calleeNode.callInfo = new CallInfo();
        return calleeNode;
    }

    public static CalleeNode instantiate(boolean isRoot){
        CalleeNode calleeNode = new CalleeNode();
        calleeNode.callees = new ArrayList<>();
        //实例化调用信息
        calleeNode.callInfo = new CallInfo();
        return calleeNode;
    }

    /**
     * 添加一个此方法的调用者
     * @param calleeNode 调用此方法的方法节点是唯一的
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
     * 获取调用此方法的方法节点
     * @return
     */
    @JsonIgnore
    public CalleeNode getCaller(){
        return this.callers.get(0);
    }
    /**
     * 遍历整个调用树
     * @param consumer 迭代方法
     */
    public void forEach(Consumer<CalleeNode> consumer){
        // 执行消费逻辑
        consumer.accept(this);
        // 递归出口
        if(Objects.isNull(callees) || callees.isEmpty()){
            return;
        }
        for (CalleeNode callee : callees) {
            callee.forEach(consumer);
        }
    }

    public List<CalleeNode> getCallers() {
        return callers;
    }

    public CallInfo getCallInfo() {
        return callInfo;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public boolean isEntrance() {
        return isEntrance;
    }

    public void setEntrance(boolean entrance) {
        isEntrance = entrance;
    }

    public List<CalleeNode> getCallees() {
        return callees;
    }
}
