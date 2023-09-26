package com.adrninistrator.jacg.api;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * 方法的被调用树(T 表示树的类型)
 */
public class CallTrees<T extends MethodNode<T>> {


    //查询方法的被调用的树的列表(仅存储根节点)
    //key:方法的完全限定名
    //value:以此方法为根节点的向上的调用树
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String,T> trees;

    //失败执行的方法
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> failTaskList;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> warningMessages;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> errorMessages;

    //实例化
    public static <T extends MethodNode<T>> CallTrees<T> instantiate(){
        CallTrees<T> calleeTrees = new CallTrees<>();
        calleeTrees.trees = new HashMap<>();
        return calleeTrees;
    }

    /**
     * 循环整颗此调用树
     * @param consumer 消费方法
     */
    public  void forEach(Consumer<T> consumer) {
        if(Objects.isNull(trees)){
            return;
        }
        for (T root : trees.values()) {
            if (Objects.isNull(root)){
                continue;
            }
            root.forEach(consumer);
        }
    }

    /**
     * 添加一被调用棵树
     * @param fullMethod 方法信息
     * @param calleeNode 被调用树的根节点
     */
    public T addTree(String fullMethod,T calleeNode){
        return trees.put(fullMethod,calleeNode);
    }

    public T getTree(String fullMethod){
        return trees.get(fullMethod);
    }

    public Map<String, T> getTrees() {
        return trees;
    }

    public void setTrees(Map<String, T> trees) {
        this.trees = trees;
    }

    public List<String> getFailTaskList() {
        return failTaskList;
    }

    public void setFailTaskList(List<String> failTaskList) {
        this.failTaskList = failTaskList;
    }

    public List<String> getWarningMessages() {
        return warningMessages;
    }

    public void setWarningMessages(List<String> warningMessages) {
        this.warningMessages = warningMessages;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }
}
