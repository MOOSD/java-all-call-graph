package com.adrninistrator.jacg.api;

import java.util.HashMap;
import java.util.Map;

/**
 * 方法的被调用树
 */
public class CalleeTrees {


    //查询方法的被调用的树的列表(仅存储根节点)
    //key:方法的完全限定名
    //value:以此方法为根节点的向上的调用树
    private Map<String,CalleeNode> trees;

    //实例化
    public static CalleeTrees instantiate(){
        HashMap<String,CalleeNode> trees = new HashMap<>();
        CalleeTrees calleeTrees = new CalleeTrees();
        calleeTrees.trees = trees;
        return calleeTrees;
    }
    //json打印


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

}
