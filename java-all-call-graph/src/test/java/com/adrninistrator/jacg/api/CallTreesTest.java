package com.adrninistrator.jacg.api;


import com.adrninistrator.jacg.util.JACGJsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;

public class CallTreesTest {

    @Test
    public void seriTest1(){
        CallTrees<CalleeNode> calleeNodeCallTrees = CallTrees.instantiate();
        CalleeNode calleeNode = CalleeNode.instantiate();
        calleeNode.setMethodHash("1111");
        calleeNode.setFullMethod("2222");
        calleeNode.setDepth(3);
        calleeNode.setEntrance(true);
        calleeNodeCallTrees.addTree(calleeNode);
        String jsonStr = JACGJsonUtil.getJsonStr(calleeNodeCallTrees);
        System.out.println("序列化结果:" + jsonStr);
        CallTrees<CalleeNode> objFromJsonStr = JACGJsonUtil.getObjFromJsonStr(jsonStr, new TypeReference<CallTrees<CalleeNode>>() {
        });

        System.out.println("反序列化结果:" + JACGJsonUtil.getJsonStr(calleeNodeCallTrees));
    }

    @Test
    public void seriTest2(){
        String jsonStr = "{\"trees\": [{\"FQCN\": \"io.metersphere.api.controller.ApiDefinitionController\", \"fqcn\": \"io.metersphere.api.controller.ApiDefinitionController\", \"depth\": 0, \"@class\": \"com.adrninistrator.jacg.api.CalleeNode\", \"isRoot\": true, \"callInfo\": {\"rpc\": false, \"async\": false, \"isRpc\": false, \"callId\": 0, \"isAsync\": false, \"callFlags\": -1, \"callerRow\": 0, \"cycleCall\": -1, \"callTypeDesc\": \"ILLEGAL\", \"callerClassName\": \"ApiDefinitionController\"}, \"entrance\": true, \"className\": \"ApiDefinitionController\", \"modifyNum\": 1, \"annotation\": [\"@org.springframework.web.bind.annotation.PostMapping(/api/definition/sense/{projectId}/{url}/{enable})\"], \"isEntrance\": true, \"methodHash\": \"1dj5DwE2qmMvVQ1-Z6T4BA==#078\", \"methodName\": \"updateApiSense\", \"affectedNum\": 1, \"inTransaction\": false, \"controllerInfo\": [{\"showUri\": \"/api/definition/sense/{projectId}/{url}/{enable}\", \"classPath\": \"/api/definition\", \"fullMethod\": \"io.metersphere.api.controller.ApiDefinitionController:updateApiSense(java.lang.String,java.lang.String,java.lang.String)\", \"methodPath\": \"/sense/{projectId}/{url}/{enable}\", \"requestMethod\": \"POST\", \"annotationName\": \"org.springframework.web.bind.annotation.PostMapping\", \"simpleClassName\": \"ApiDefinitionController\"}], \"originTextInfo\": [\"io.metersphere.api.controller.ApiDefinitionController:430\"], \"methodFormalArguments\": [{\"fqcn\": \"java.lang.String\"}, {\"fqcn\": \"java.lang.String\"}, {\"fqcn\": \"java.lang.String\"}]}], \"failTaskList\": [\"指定类的代码行号未查找到方法ApiDefinitionService:3780，可能原因如下\\n2. 指定的方法是接口中未实现的方法\\n3. 指定的方法是抽象方法\\n\", \"指定类的代码行号未查找到方法ApiDefinitionService:3723，可能原因如下\\n2. 指定的方法是接口中未实现的方法\\n3. 指定的方法是抽象方法\\n\"]}";
        CallTrees<CalleeNode> objFromJsonStr = JACGJsonUtil.getObjFromJsonStr(jsonStr, new TypeReference<CallTrees<CalleeNode>>() {
        });

        System.out.println("反序列化结果:" + JACGJsonUtil.getJsonStr(objFromJsonStr));
    }

}