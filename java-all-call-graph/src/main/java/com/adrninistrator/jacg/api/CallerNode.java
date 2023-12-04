package com.adrninistrator.jacg.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CallerNode extends MethodNode {

    //当前节点深度
    private int depth;

    //是否为出口
    private boolean isEntrance;

    private Integer startLineNum;

    private Integer endLineNum;


    /**
     * 设置此方法的调用者
     * @param calleeNode 调用此方法的方法节点
     */
    public void setCallee(CallerNode calleeNode){
        before = calleeNode;

    }

    /**
     * 添加一个此方法调用的方法
     * @param calleeNode
     */
    public void addCaller(CallerNode calleeNode){
        super.addNext(calleeNode);
    }



    /**
     * 实例化一个CalleeNodeNode
     * @return new calleNode object
     */
    public static CallerNode instantiate(){
        CallerNode calleeNode = new CallerNode();
        return calleeNode;
    }



    /**
     * 返回此节点的调用者
     */
    @JsonIgnore
    public CallerNode getCallee(){
        return (CallerNode) this.before;
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

    @JsonProperty(value = "isEntrance")
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

}
