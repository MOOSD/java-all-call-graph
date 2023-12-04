package com.adrninistrator.jacg.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 被调用者树的节点，即向上的调用树
 * 根节点代表查询的方法A，叶子节点代表方法A的调用者们，深度属性直接表示调用层级。
 * 因此在向上的调用树中，从根向叶子节点看有许多路径；从一个叶子节点到根节点的路径唯一。
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CalleeNode extends MethodNode {

    // 当前节点深度
    private int depth;
    // 是否方法入口
    private boolean isEntrance;


    /**
     * 实例化一个CalleeNodeNode
     * @return new calleNode object
     */
    public static CalleeNode instantiate(){
        CalleeNode calleeNode = new CalleeNode();
        return calleeNode;
    }

    /**
     * 添加一个此方法的调用者
     * @param calleeNode 调用此方法的方法节点是唯一的
     */
    public void addCallee(CalleeNode calleeNode){
        super.addNext(calleeNode);
    }

    /**
     * 添加一个此方法调用的方法
     * @param calleeNode
     */
    public void setCaller(CalleeNode calleeNode){
        before = calleeNode;
    }

    /**
     * 获取调用此方法的方法节点
     * @return
     */
    @JsonIgnore
    public CalleeNode getCaller(){
        return (CalleeNode) this.before;
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
}
