package com.adrninistrator.jacg.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * 方法的被调用树(T 表示树的类型)
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CallTrees<T extends MethodNode> {


    //查询方法的被调用的树的列表(仅存储根节点)
    //key:方法的完全限定名
    //value:以此方法为根节点的向上的调用树
    private List<T> trees;

    //失败执行的方法
    private List<String> failTaskList;

    private List<String> warningMessages;

    private List<String> errorMessages;

    @JsonIgnore
    // 所有方法节点的索引，索引值是节点名称
    private Map<String, T> nodeIndex;

    //实例化
    public static <T extends MethodNode> CallTrees<T> instantiate(){
        CallTrees<T> calleeTrees = new CallTrees<>();
        calleeTrees.trees = new ArrayList<>();
        calleeTrees.nodeIndex = new ConcurrentHashMap<>();
        return calleeTrees;
    }

    /**
     * 循环整颗此调用树
     * @param consumer 消费方法
     */
    public void forEach(Consumer<MethodNode> consumer) {
        if(Objects.isNull(trees)){
            return;
        }
        for (T root : trees) {
            if (Objects.isNull(root)){
                continue;
            }
            root.forEach(consumer);
        }
    }

    /**
     * 添加一被调用棵树，重复添加时候影响节点的modifyNum
     * @param callNode 被调用树的根节点，当返回null时，则表示此调用树重复生成
     */
    public synchronized void addTree(T callNode){
        trees.add(callNode);
    }

    /**
     * 添加节点到节点索引集合
     * @param callNode 节点信息
     * @return false: 节点已经存在，未添加到索引集合中， 集合不存在，添加到索引集合中
     */
    public synchronized boolean addNode(T callNode){
        // 如果节点不存在，假如到索引集合中
        if (!nodeIndex.containsKey(callNode.getMethodHash())) {
            this.nodeIndex.put(callNode.getMethodHash(), callNode);
            return true;
        }
        T alreadyExistNode = nodeIndex.get(callNode.getMethodHash());
        // 如果目标节点是根节点
        if(callNode.isRoot){
            // 增加链路存在次数
            alreadyExistNode.incrementModifyNum();
            // 将节点中的原始信息加入
            alreadyExistNode.getOriginTextInfo().addAll(callNode.getOriginTextInfo());
            return false;
        }
        // 目标节点非根节点
        alreadyExistNode.incrementAffectedNum();
        // 将输入方法节点连接到已存在的链路上
        MethodNode before = callNode.getBefore();
        before.addNext(alreadyExistNode);
        return false;
    }

    public List<T> getTrees() {
        return trees;
    }

    public void setTrees(List<T> trees) {
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

    public Map<String, T> getNodeIndex() {
        return nodeIndex;
    }

    public void setNodeIndex(Map<String, T> nodeIndex) {
        this.nodeIndex = nodeIndex;
    }
}
