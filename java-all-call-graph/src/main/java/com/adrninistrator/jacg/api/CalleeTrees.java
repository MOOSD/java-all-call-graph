package com.adrninistrator.jacg.api;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * 方法的被调用树
 */
public class CalleeTrees {


    //查询方法的被调用的树的列表(仅存储根节点)
    //key:方法的完全限定名
    //value:以此方法为根节点的向上的调用树
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String,CalleeNode> trees;

    //失败执行的方法
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> failTaskList;
    //实例化
    public static CalleeTrees instantiate(){
        HashMap<String,CalleeNode> trees = new HashMap<>();
        CalleeTrees calleeTrees = new CalleeTrees();
        calleeTrees.trees = trees;
        return calleeTrees;
    }

    /**
     * 循环整颗此调用树
     * @param consumer 消费方法
     */
    public void forEach(Consumer<CalleeNode> consumer) {
        if(Objects.isNull(trees)){
            return;
        }
        for (CalleeNode root : trees.values()) {
            if (Objects.isNull(root)){
                continue;
            }
            root.forEach(consumer);
        }
    }


    public CalleeNode addTree(String fullMethod,CalleeNode calleeNode){
        return trees.put(fullMethod,calleeNode);
    }

    public CalleeNode getTree(String fullMethod){
        return trees.get(fullMethod);
    }

    public Map<String, CalleeNode> getTrees() {
        return trees;
    }

    public void setTrees(Map<String, CalleeNode> trees) {
        this.trees = trees;
    }

    public List<String> getFailTaskList() {
        return failTaskList;
    }

    public void setFailTaskList(List<String> failTaskList) {
        this.failTaskList = failTaskList;
    }


}
