package com.adrninistrator.jacg.api;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class CallerNode extends MethodNode<CallerNode> {

    //当前节点深度
    private int depth;

    //是否为出口
    private boolean isEntrance;

    private Integer startLineNum;

    private Integer endLineNum;

    //此方法被哪些方法调用
    @JsonIgnore
    private List<CallerNode> callees;

    //此方法调用的方法
    private List<CallerNode> callers;

    /**
     * 添加一个此方法的调用者
     * @param calleeNode 调用此方法的方法节点
     */
    public void addCallee(CallerNode calleeNode){
        Objects.requireNonNull(callees).add(Objects.requireNonNull(calleeNode));

    }

    /**
     * 添加一个此方法调用的方法
     * @param calleeNode
     */
    public void addCaller(CallerNode calleeNode){
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
     * 实例化一个CalleeNodeNode
     * @return new calleNode object
     */
    public static CallerNode instantiate(){
        CallerNode calleeNode = new CallerNode();
        calleeNode.callees = new ArrayList<>();
        calleeNode.callers = new ArrayList<>();
        //实例化调用信息
        calleeNode.callInfo = new CallInfo();
        return calleeNode;
    }

    public static CallerNode instantiate(boolean isRoot){
        CallerNode callerNode = new CallerNode();
        callerNode.callers = new ArrayList<>();
        //实例化调用信息
        callerNode.callInfo = new CallInfo();
        return callerNode;
    }

    @Override
    void forEach(Consumer<CallerNode> consumer) {
        consumer.accept(this);
        // 获取下一个节点
        if (Objects.isNull(callers) || callers.isEmpty()){
            return;
        }
        for (CallerNode caller : callers) {
            caller.forEach(consumer);
        }

    }

    /**
     * 返回此节点的调用者
     */
    @JsonIgnore
    public CallerNode getCallee(){
        return this.callees.get(0);
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

    public Integer getStartLineNum() {
        return startLineNum;
    }

    public void setStartLineNum(Integer startLineNum) {
        this.startLineNum = startLineNum;
    }

    public Integer getEndLineNum() {
        return endLineNum;
    }

    public void setEndLineNum(Integer endLineNum) {
        this.endLineNum = endLineNum;
    }

    public List<CallerNode> getCallees() {
        return callees;
    }

    public List<CallerNode> getCallers() {
        return callers;
    }
}
