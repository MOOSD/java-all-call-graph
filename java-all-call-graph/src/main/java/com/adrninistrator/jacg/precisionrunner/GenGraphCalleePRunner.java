package com.adrninistrator.jacg.precisionrunner;

import com.adrninistrator.jacg.api.*;
import com.adrninistrator.jacg.common.DC;
import com.adrninistrator.jacg.common.JACGConstants;
import com.adrninistrator.jacg.common.enums.*;
import com.adrninistrator.jacg.conf.ConfigureWrapper;
import com.adrninistrator.jacg.dto.annotation.BaseAnnotationAttribute;
import com.adrninistrator.jacg.dto.call_graph.CallGraphNode4Callee;
import com.adrninistrator.jacg.dto.call_graph.DomainInfo;
import com.adrninistrator.jacg.dto.call_graph.SuperCallChildInfo;
import com.adrninistrator.jacg.dto.feign.FeignClientInfo;
import com.adrninistrator.jacg.dto.method.MethodAndHash;
import com.adrninistrator.jacg.dto.method.MethodFullInfo;
import com.adrninistrator.jacg.dto.method.SimpleMethodCallDTO;
import com.adrninistrator.jacg.dto.task.CalleeTaskInfo;
import com.adrninistrator.jacg.dto.write_db.WriteDbData4MethodCall;
import com.adrninistrator.jacg.exception.RunnerBreakException;
import com.adrninistrator.jacg.handler.dto.spring.SpringControllerComplexInfo;
import com.adrninistrator.jacg.precisionrunner.base.AbstractGenCallGraphPRunner;
import com.adrninistrator.jacg.runner.RunnerGenAllGraph4Callee;
import com.adrninistrator.jacg.util.*;
import com.adrninistrator.jacg.util.spring.SpringMvcRequestMappingUtil;
import com.adrninistrator.javacg.common.JavaCGCommonNameConstants;
import com.adrninistrator.javacg.common.JavaCGConstants;
import com.adrninistrator.javacg.common.enums.JavaCGYesNoEnum;
import com.adrninistrator.javacg.dto.stack.ListAsStack;
import com.adrninistrator.javacg.util.JavaCGMethodUtil;
import com.adrninistrator.javacg.util.JavaCGUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.adrninistrator.javacg.common.enums.JavaCGCallTypeEnum.*;

/**
 * 生成java对象的向上调用链的runner
 */
public class GenGraphCalleePRunner extends AbstractGenCallGraphPRunner {
    private static final Logger logger = LoggerFactory.getLogger(RunnerGenAllGraph4Callee.class);

    private CallTrees<CalleeNode> calleeTrees;


    @Override
    public String setRunnerName() {
        return "向上调用树生成Runner";
    }


    /**
     * 生成指定方法向上的调用链路
     * @param config
     * @return
     */
    public CallTrees<CalleeNode> getLink(ConfigureWrapper config) throws RunnerBreakException{
        //运行方法，结果收集到指定对象中。
        run(config);
        // 错误处理记录抛出
        // 压缩树
//        vCompressTrees();
        calleeTrees.setFailTaskList(failTaskList);
        calleeTrees.setWarningMessages(warningMessages);
        calleeTrees.setErrorMessages(errorMessages);

        return calleeTrees;
    }

    private void hCompressTrees() {
        for (CalleeNode tree : calleeTrees.getTrees()) {
            hCompressTree(tree);
        }
    }


    private void vCompressTrees() {
        for (CalleeNode tree : calleeTrees.getTrees()) {
            vCompressTree(tree);
        }
    }

    public CallTrees<CalleeNode> getLink(ConfigureWrapper config, RunnerController runnerController) throws RunnerBreakException {
        this.runnerController = runnerController;
        //运行方法，结果收集到指定对象中。
        run(config);
        //错误处理记录抛出
        calleeTrees.setFailTaskList(failTaskList);
        calleeTrees.setWarningMessages(warningMessages);
        calleeTrees.setErrorMessages(errorMessages);

        return calleeTrees;
    }

    /**
     * 1.初始化
     * 2.预检查
     * 3.预处理
     */
    @Override
    protected boolean preHandle() {
        //实例化调用书
        calleeTrees = CallTrees.instantiate();

        // 抽象类中的公共预处理
        if (!commonPreHandle()) {
            return false;
        }

        // 读取配置文件中指定的需要处理的任务
        return readTaskInfo(OtherConfigFileUseSetEnum.OCFUSE_METHOD_CLASS_4CALLEE);
    }

    /**
     * 4.执行处理
     */
    @Override
    protected void handle() {
        //处理需要生成链路的方法
        Map<String, CalleeTaskInfo> calleeTaskInfoMap = genCalleeTaskInfo();
        //实例化线程池
        createThreadPoolExecutor(null);
        //线程池中批量添加任务
        for (Map.Entry<String, CalleeTaskInfo> entry : Objects.requireNonNull(calleeTaskInfoMap, "方法/类信息错误").entrySet()) {
            //处理一个类中指定方法的调用链路
            if (!handleOneCalleeClass(entry.getKey(), entry.getValue())) {
                //任务处理失败，结束任务
                wait4TPEDone();
                return;
            }
        }
        wait4TPEDone();
    }


    private boolean handleOneCalleeClass(String className, CalleeTaskInfo taskInfo){

        // 查询被调用类的全部方的被调用信息
        List<SimpleMethodCallDTO> methodCallList = Collections.emptyList();
        if (taskInfo.isGenAllMethods() || taskInfo.isFindMethodByName()) {
            // 假如需要生成指定类的全部方法向上调用链，或需要根据方法名称查询方法时，需要查询被调用类的全部方法信息
            methodCallList = queryMethodsOfCalleeClass(className);
        }

        // 处理此类中所有方法
        if (taskInfo.isGenAllMethods()) {
            // 需要生成指定类的全部方法向上调用链
            if (methodCallList.isEmpty()) {
                logger.error("以下类需要为所有方法生成向上方法调用链，但未查找到其他方法调用该类的方法\n{}", className);
                return false;
            }

            // 生成类中所有方法的嗲用链路信息
            for (SimpleMethodCallDTO methodCallInfo : methodCallList) {
                handleOneCalleeMethod(methodCallInfo, null);
            }
            return true;
        }

        // 生成指定类的名称或代码行号匹配的方法向上调用链
        for (Map.Entry<String, String> methodInfoEntry : taskInfo.getMethodInfoMap().entrySet()) {
            //用户输入的原始文本
            String origTaskText = methodInfoEntry.getKey();
            //方法信息
            String methodInfoInTask = methodInfoEntry.getValue();
            if (!JavaCGUtil.isNumStr(methodInfoInTask)) {
                // 通过方法名查找对应的方法并处理
                if (!handleOneCalleeMethodByName(className, methodCallList, origTaskText, methodInfoInTask)) {
                    logger.warn("未找到类:{} 的方法:{} 的相关信息",className,methodInfoInTask);
                }
            } else {
                // 通过代码行号查找对应的方法并处理
                if (!handleOneCalleeMethodByLineNumber(className, origTaskText, methodInfoInTask)) {
                    logger.warn("未找到类:{} 在{}行处的方法信息",className,methodInfoInTask);
                }
            }
        }

        return true;
    }


    // 通过方法名查找对应的方法并处理
    private boolean handleOneCalleeMethodByName(String calleeSimpleClassName, List<SimpleMethodCallDTO> methodCallList, String origTaskText,
                                                String methodInfoInTask) {
        //匹配到的被调用方法的DO
        SimpleMethodCallDTO usedMethodCall = null;
        List<String> calleeFullMethodList = new ArrayList<>();

        // 遍历从数据库中查找到的方法信息，查找当前指定的方法名称或参数匹配的方法
        for (SimpleMethodCallDTO methodCall : methodCallList) {
            //这里做了模糊匹配，拿方法的完全限定名与用户输入的方法名进行匹配。
            String methodNameAndArgs = JACGClassMethodUtil.getMethodNameWithArgsFromFull(methodCall.getFullMethod());
            if (StringUtils.startsWith(methodNameAndArgs, methodInfoInTask)) {
                usedMethodCall = methodCall;
                calleeFullMethodList.add(methodCall.getFullMethod());
            }
        }

        // 此类没有被调用的记录时,从方法信息表中查询数据（按照simpleClassName查找记录，然后模糊匹配所有方法）
        if (calleeFullMethodList.isEmpty()) {
            List<MethodFullInfo> methodInfos = getMethodInfoBySimpleClassName(calleeSimpleClassName);
            for (MethodFullInfo methodInfo : methodInfos) {
                // 计算MethodNameAndArgs
                String methodNameAndArgs = JACGClassMethodUtil.getMethodNameWithArgsFromFull(methodInfo.getFullMethod());
                if (StringUtils.startsWith(methodNameAndArgs, methodInfoInTask)) {
                    // 如果匹配，构建一个虚拟的SimpleMethodCallDTO对象
                    SimpleMethodCallDTO methodCallDTO = new SimpleMethodCallDTO(methodInfo.getMethodHash()
                            ,methodInfo.getFullMethod());
                    usedMethodCall = methodCallDTO;
                    calleeFullMethodList.add(methodCallDTO.getFullMethod());
                }
            }
        }

        if(calleeFullMethodList.isEmpty()){
            // 未查找到匹配的方法
            String errorMessage = "未查询到方法["+calleeSimpleClassName+":"+methodInfoInTask+"]的相关信息";
            logger.error(errorMessage);
            addErrorMessage(errorMessage);
            return false;
        }

        if (calleeFullMethodList.size() > 1) {
            // 查找到匹配的方法多于1个，返回处理失败
            String errorMessage = "方法 "+origTaskText+" 匹配到了多于一个的方法，请指定更精确的方法信息\n匹配到的方法信息:"+StringUtils.join(calleeFullMethodList, "\n");
            logger.error(errorMessage);
            addErrorMessage(errorMessage);
            return false;
        }

        // 处理一个被调用方法
        handleOneCalleeMethod(usedMethodCall,origTaskText);
        return true;
    }

    // 通过代码行号查找对应的方法并处理
    private boolean handleOneCalleeMethodByLineNumber(String calleeSimpleClassName, String origTaskText, String methodInfoInTask) {
        int methodLineNum = Integer.parseInt(methodInfoInTask);

        // 通过代码行号获取对应方法信息
        SimpleMethodCallDTO simpleMethodCall = findMethodByLineNumber(calleeSimpleClassName, methodLineNum);
        if (Objects.isNull(simpleMethodCall)) {
            // 返回处理失败
            return false;
        }

        // 处理一个被调用方法
        handleOneCalleeMethod(simpleMethodCall, origTaskText);
        return true;
    }


    /**
     * 异步处理被调用方法
     */
    private void handleOneCalleeMethod(SimpleMethodCallDTO methodCallDTO,
                                       String origTaskText) {
        // 设置中断点
        setBreakPoint();
        String fullMethod = methodCallDTO.getFullMethod();
        threadPoolExecutor.execute(() -> {
            try {
                // 执行处理一个被调用方法
                if (!recordOneCalleeMethod(methodCallDTO.getMethodHash(), fullMethod, origTaskText)) {
                    // 记录执行失败的任务信息
                    recordTaskFail(origTaskText != null ? origTaskText : fullMethod);
                }
            } catch (Exception e) {
                logger.error("error {} ", origTaskText, e);
                // 记录执行失败的任务信息
                recordTaskFail(origTaskText != null ? origTaskText : fullMethod);
            }
        });
    }

    // 记录一个被调用方法的调用链信息
    private boolean recordOneCalleeMethod(String entryCalleeMethodHash,
                                          String entryCalleeFullMethod,
                                          String origTaskText
    ){

        // 判断配置文件中是否已指定忽略当前方法
        if (ignoreCurrentMethod(null, entryCalleeFullMethod)) {
            String errorMessage = "配置文件中已指定忽略当前方法，不处理 " + entryCalleeFullMethod;
            logger.info("配置文件中已指定忽略当前方法，不处理 {}", entryCalleeFullMethod);
            addWarningMessage(errorMessage);
            return true;
        }
        // 创建根节点，调用关系使用默认值
        CalleeNode root = genCalleeNode(entryCalleeFullMethod, JACGConstants.UNKNOWN_CALL_ID, JACGConstants.UNKNOWN_CALL_FLAGS,
                JACGConstants.UNKNOWN_CALL_TYPE, 0, JACGConstants.CALL_GRAPH_METHOD_LEVEL_START,
                entryCalleeMethodHash, JACGConstants.NO_CYCLE_CALL_FLAG, null, null, origTaskText,
                true, getDomain());

        calleeTrees.addTree(root);

        // 根据指定的调用者方法HASH，生成调用树
        return genAllGraph4Callee(entryCalleeMethodHash, entryCalleeFullMethod, root);
    }

    /**
     * 根据指定的被调用者方法HASH，查找所有调用方法信息
     */
    protected boolean genAllGraph4Callee(String entryCalleeMethodHash,
                                         String entryCalleeFullMethod,
                                         CalleeNode root) {
        //方法被调用方,默认是根节点
        CalleeNode methodNode = root;
        // 记录当前处理的方法调用信息的栈
        ListAsStack<CallGraphNode4Callee> callGraphNode4CalleeStack = new ListAsStack<>();
        // 记录父类方法调用子类方法对应信息的栈
        ListAsStack<SuperCallChildInfo> superCallChildInfoStack = new ListAsStack<>();

        // 初始加入最下层节点，callerMethodHash设为null，默认业务域是开启时的业务域
        CallGraphNode4Callee callGraphNode4CalleeHead = new CallGraphNode4Callee(entryCalleeMethodHash, null,
                entryCalleeFullMethod, getDomainCode());
        callGraphNode4CalleeStack.push(callGraphNode4CalleeHead);


        // 输出结果数量
        int genNodeNum = 0;
        while (true) {
            // 从栈顶获取当前正在处理的节点
            CallGraphNode4Callee callGraphNode4Callee = callGraphNode4CalleeStack.peek();

            // 查询当前节点的一个上层调用方法
            WriteDbData4MethodCall callerMethod = queryOneByCalleeMethod(callGraphNode4Callee, methodNode.getCallInfo().getCallType());
            String serviceName = null;
            if (Objects.isNull(callerMethod)) {
                // 如果开启跨微服务生成,且当前为方法是一个controller
                if(crossServiceByOpenFeign && Objects.nonNull(methodNode.getAnnotation()) && isControllerMethod(methodNode.getAnnotation())){
                    //找到此controller对应的openfeign,将feignClient作为此Controller的调用者继续生成调用链路,即伪造一次调用
                    FeignAndControllerInfo feignAndControllerInfo = getFeignInfoByControllerCrossDomain(callGraphNode4Callee);
                    if (Objects.nonNull(feignAndControllerInfo)){
                        serviceName = feignAndControllerInfo.getServiceName();
                        callerMethod = getFeignCallByControllerMethodHash(feignAndControllerInfo);
                    }
                }
                // 再次判空，为空则表示此方法没有对应的feignClient，表示无远程调用。
                if (Objects.isNull(callerMethod)) {
                    // 查询到调用方法为空时的处理
                    if (handleCallerEmptyResult(callGraphNode4CalleeStack, superCallChildInfoStack ,methodNode)) {
                        return true;
                    }
                    //若未遍历到初始节点，则节点向上遍历,回到上一层回到上一层时，只可能是单个节点，因此取第一个元素，即当前方法的调用者。
                    methodNode = methodNode.getCaller();
                    tryChangeAppDomain(methodNode.getDomainCode());
                    continue;
                }
            }

            // 尝试切换业务域

            tryChangeAppDomain(callerMethod.getDomainCode());



            String calleeFullMethod = callGraphNode4Callee.getCalleeFullMethod();
            String callerFullMethod = callerMethod.getCallerFullMethod();
            String origCallerMethodHash = callerMethod.getCallerMethodHash();
            int methodCallId = callerMethod.getCallId();
            int enabled =  callerMethod.getEnabled();
            String callType =  callerMethod.getCallType();

            // 处理父类方法调用子类方法的相关信息，更新调用者
            MethodAndHash callerMethodAndHash = handleSuperCallChildInfo(superCallChildInfoStack, callGraphNode4CalleeStack.getHead(),
                    calleeFullMethod, callerFullMethod, callType, origCallerMethodHash);
            if (callerMethodAndHash == null) {
                // 处理失败
                return false;
            }

            callerFullMethod = callerMethodAndHash.getFullMethod();
            String callerMethodHash = callerMethodAndHash.getMethodHash();

            // 处理被忽略的方法
            if (handleIgnoredMethod(callType, callerFullMethod, callerMethodHash, callGraphNode4CalleeStack, enabled, methodCallId)) {
                continue;
            }

            // 检查是否出现循环调用
            int back2Level = checkCycleCall(callGraphNode4CalleeStack, callerMethodHash, callerFullMethod);


            // 获取此方法调用者信息，处理注解泛型信息等
            CalleeNode callee = genCalleeNode(callerFullMethod, methodCallId, callerMethod.getCallFlags(), callType,
                    callerMethod.getCallerLineNumber(), callGraphNode4CalleeStack.getHead()+1,
                    callerMethodHash, back2Level, serviceName, methodNode,null ,false, getDomain());
            // 未生成节点，节点向上遍历
            if(Objects.isNull(callee)){
                if (handleCallerEmptyResult(callGraphNode4CalleeStack, superCallChildInfoStack ,methodNode)) {
                    return true;
                }
                methodNode = methodNode.getCaller();
                continue;
            }
            // 节点数量自增
            if(cutDownByNodeNum(++genNodeNum)){
                logger.warn(root.getFullMethod()+"树节点数量过多,中断生成");
                root.addGenMessage("当前树节点数量大于" + maxNodeGenNum +  ",中断生成");
                return true;
            }

            // 记录可能出现一对多的方法调用
            if (!recordMethodCallMayBeMulti(methodCallId, callType)) {
                return false;
            }

            // 更新当前处理节点的callerMethodHash，使用原始调用方法HASH+长度
            callGraphNode4CalleeStack.peek().setCallerMethodHash(origCallerMethodHash);

            if (back2Level != JACGConstants.NO_CYCLE_CALL_FLAG) {
                // 将当前处理的层级指定到循环调用的节点，不再往上处理调用方法
                continue;
            }

            // 继续上一层处理
            CallGraphNode4Callee nextCallGraphNode4Callee = new CallGraphNode4Callee(callerMethodHash,
                    null, callerFullMethod, getDomainCode());
            callGraphNode4CalleeStack.push(nextCallGraphNode4Callee);
            methodNode = callee;
        }

    }



    private boolean isControllerMethod(List<String> annotationList){
        return annotationList.stream().anyMatch(SpringMvcRequestMappingUtil::isControllerHandlerMethod);
    }
    /**
     * 获取作为controller的调用者的FeignClient
     * 相当于是Controller被一个Feign调用了。根据controller信息构造一个feign的调用信息
     * 查询的表是所有业务域的表
     * @return 组装成的一个条调用记录
     */
    private WriteDbData4MethodCall getFeignCallByControllerMethodHash(FeignAndControllerInfo feignAndControllerInfo){


        // 构建一条调用记录。
        WriteDbData4MethodCall callerMethodCall = new WriteDbData4MethodCall();
        callerMethodCall.setCallerFullMethod(feignAndControllerInfo.getFeignFullMethod());
        callerMethodCall.setCallerMethodHash(feignAndControllerInfo.getFeignMethodHash());
        //给与一个不存在的调用id
        callerMethodCall.setCallId(-feignAndControllerInfo.hashCode());

        callerMethodCall.setEnabled(1);
        //设置调用方式为rpc调用
        callerMethodCall.setCallType(ExtendCallTypeEnum.RPC.getType());
        //设置调用标识为调用者带有注解
        // todo setflag(0) 等价于 MethodCallFlagsEnum.MCFE_EE_METHOD_ANNOTATION.getFlag();
        callerMethodCall.setCallFlags(MethodCallFlagsEnum.MCFE_EE_METHOD_ANNOTATION.setFlag(0));
        //调用行设置为0
        callerMethodCall.setCallerLineNumber(0);
        // todo feign 调用client，那么feign节点算作当前业务域
        callerMethodCall.setDomainCode(getDomainCode());

        return callerMethodCall;
    }

    /**
     * @return 如果返回null，则表示节点已经生成过了，不应继续重复生成链路
     */
    protected CalleeNode genCalleeNode(String callerFullMethod,
                                       int methodCallId,
                                       int callFlags,
                                       String callType,
                                       int callerLineNum,
                                       int currentNodeLevel,
                                       String callerMethodHash,
                                       int back2Level,
                                       String serviceName,
                                       CalleeNode callee,
                                       String originText,
                                       boolean isRoot,
                                       DomainInfo domainCode) {
        String callerClassName = JACGClassMethodUtil.getClassNameFromMethod(callerFullMethod);
        String callerSimpleClassName = dbOperWrapper.getSimpleClassName(callerClassName);

        // 实例化一个新节点（子节点）
        CalleeNode caller = CalleeNode.instantiate();
        caller.setId(getIdNum());
        caller.setDomainCode(domainCode.getDomainCode());
        caller.setDomainName(domainCode.getDomainName());
        caller.setFullMethod(callerFullMethod);
        caller.setMethodHash(callerMethodHash);
        caller.setDepth(currentNodeLevel);
        caller.setFQCN(callerClassName);
        caller.setMethodName(JACGClassMethodUtil.getMethodNameFromFull(callerFullMethod));
        caller.setMethodFormalArguments((MethodUtil.genMethodArgTypeList(callerFullMethod)));
        // 检查是否为远程过程调用
        CallInfo callInfo = caller.getCallInfo();
        callInfo.setCallId(methodCallId);
        callInfo.setRpc(ExtendCallTypeEnum.RPC.getType().equals(callType));
        callInfo.setCallFlags(callFlags);
        callInfo.setCallerRow(callerLineNum);
        callInfo.setCallerClassName(callerSimpleClassName);
        callInfo.setCallType(callType);
        if ((Objects.nonNull(callee) && callee.getCallInfo().isUnreliableInvocation()) || CTE_SUPER_CALL_CHILD_OVERRIDE.getType().equals(callType)
        || CTE_SUPER_CALL_CHILD_INTERFACE_OVERRIDE.getType().equals(callType)) {
            callInfo.setUnreliableInvocation(true);
        }
        // 调用方更新调用列表
        caller.setCaller(callee);
        caller.setClassName(callerSimpleClassName);
        caller.setServiceName(serviceName);
        // 对根节点进行单独处理
        if(isRoot){
            caller.isRoot(originText);
        }

        // 获取方法上的注解信息
        Map<String, Map<String, BaseAnnotationAttribute>> methodAnnotationMap = addMethodAnnotationInfo(caller);

        // 添加方法调用业务功能数据
        addBusinessData(caller);

        // 方法添加controller相关信息
        addControllerInfo(methodAnnotationMap, caller);

        // 为方法调用信息增加是否在其他线程执行标志
        addRunInOtherThread(methodAnnotationMap, caller);

        // 为方法调用信息增加是否在事务中执行标志
        addRunInTransaction(methodAnnotationMap, caller);

        // 添加循环调用标志
        if (back2Level != JACGConstants.NO_CYCLE_CALL_FLAG) {
            callInfo.setCycleCall(back2Level);
        }
        //被调用方更新被调用列表
        if(Objects.nonNull(callee)){
            callee.addCallee(caller);
        }
        return caller;
    }

    // 处理父类方法调用子类方法的相关信息
    private MethodAndHash handleSuperCallChildInfo(ListAsStack<SuperCallChildInfo> superCallChildInfoStack,
                                                   int nodeLevel,
                                                   String calleeFullMethod,
                                                   String callerFullMethod,
                                                   String callType,
                                                   String callerMethodHash) {
        // 获取子类的调用方法
        Pair<Boolean, MethodAndHash> pair = getSCCChildFullMethod(superCallChildInfoStack, callerFullMethod);
        if (pair == null) {
            // 处理失败
            return null;
        }

        if (Boolean.TRUE.equals(pair.getLeft())) {
            // 使用子类的调用方法
            return pair.getRight();
        }

        // 使用原始的调用方法
        if (CTE_SUPER_CALL_CHILD.getType().equals(callType)) {
            // 当前方法调用类型是父类调用子类方法，记录父类方法调用子类方法对应信息的栈入栈
            String calleeClassName = JACGClassMethodUtil.getClassNameFromMethod(calleeFullMethod);
            String calleeSimpleClassName = dbOperWrapper.getSimpleClassName(calleeClassName);
            SuperCallChildInfo superCallChildInfo = new SuperCallChildInfo(nodeLevel, calleeSimpleClassName, calleeClassName, calleeFullMethod);
            superCallChildInfoStack.push(superCallChildInfo);
        }
        return new MethodAndHash(callerFullMethod, callerMethodHash);
    }

    /**
     * 是否结束调用树生成
     */
    private boolean handleCallerEmptyResult(ListAsStack<CallGraphNode4Callee> callGraphNode4CalleeStack,
                                            ListAsStack<SuperCallChildInfo> superCallChildInfoStack,
                                            CalleeNode calleeNode) {
        if (callGraphNode4CalleeStack.atBottom()) {
            // 当前处理的节点为最下层节点，结束循环
            // 将调用方法列表中最后一条记录设置为入口方法
            markMethodAsEntry(calleeNode);
            return true;
        }

        if (!superCallChildInfoStack.isEmpty()) {
            // 记录父类方法调用子类方法对应信息的栈非空
            SuperCallChildInfo topSuperCallChildInfo = superCallChildInfoStack.peek();
            if (topSuperCallChildInfo.getChildCalleeNodeLevel() == callGraphNode4CalleeStack.getHead()) {
                // 记录父类方法调用子类方法对应信息的栈顶元素，与方法调用节点栈出栈的级别相同，出栈
                superCallChildInfoStack.removeTop();
            }
        }

        // 当前处理的节点不是最下层节点，返回下一层处理，出栈
        callGraphNode4CalleeStack.removeTop();

        // 将调用方法列表中最后一条记录设置为入口方法
        markMethodAsEntry(calleeNode);

        return false;
    }


    /**
     * 检查是否出现循环调用
     *
     * @param callGraphNode4CalleeStack
     * @param callerMethodHash
     * @param callerFullMethod
     * @return -1: 未出现循环调用，非-1: 出现循环调用，值为发生循环调用的层级
     */
    private int checkCycleCall(ListAsStack<CallGraphNode4Callee> callGraphNode4CalleeStack,
                               String callerMethodHash,
                               String callerFullMethod) {
        /*
            应该根据当前找到的callerMethodHash，从列表中找到calleeMethodHash相同的节点，以这一层级作为被循环调用的层级

            node4CalleeList中的示例
            层级:    ee   er
            [0]:    a <- b
            [1]:    b <- c
            [2]:    c <- d
            [3]:    d <- a

            第3层级: er: a，根据ee为a，找到第0层级
         */
        // 循环调用的日志信息
        StringBuilder cycleCallLogInfo = new StringBuilder();

        int cycleCallLevel = JACGConstants.NO_CYCLE_CALL_FLAG;
        for (int i = callGraphNode4CalleeStack.getHead(); i >= 0; i--) {
            CallGraphNode4Callee callGraphNode4Callee = callGraphNode4CalleeStack.getElement(i);
            if (callerMethodHash.equals(callGraphNode4Callee.getCalleeMethodHash())) {
                // 找到循环调用
                cycleCallLevel = i;
                break;
            }
        }

        // 每个层级的调用方法遍历完之后的处理
        if (cycleCallLevel != JACGConstants.NO_CYCLE_CALL_FLAG) {
            // 显示被循环调用的信息
            cycleCallLogInfo.append(JACGCallGraphFileUtil.genCycleCallFlag(cycleCallLevel))
                    .append(" ")
                    .append(callerFullMethod);
            // 记录循环调用信息
            for (int i = callGraphNode4CalleeStack.getHead(); i >= 0; i--) {
                CallGraphNode4Callee callGraphNode4Callee = callGraphNode4CalleeStack.getElement(i);
                if (cycleCallLogInfo.length() > 0) {
                    cycleCallLogInfo.append("\n");
                }
                cycleCallLogInfo.append(JACGCallGraphFileUtil.genOutputLevelFlag(i))
                        .append(" ")
                        .append(callGraphNode4Callee.getCalleeFullMethod());
            }
            logger.info("找到循环调用的方法\n{}", cycleCallLogInfo);
        }

        return cycleCallLevel;
    }


    // 查询当前节点的一个上层调用方法
    private WriteDbData4MethodCall queryOneByCalleeMethod(CallGraphNode4Callee callGraphNode4Callee,String callType) {
        // 调用类型进行转换
        callType = transCallType(callType);
        // 确定通过调用方法进行查询使用的SQL语句
        Pair<String, List<Object>> sqlAndParamPair = chooseQueryByCalleeMethodSql(callGraphNode4Callee, callType);

        WriteDbData4MethodCall writeDbData4MethodCall = dbOperator.queryObject(sqlAndParamPair.getLeft(), WriteDbData4MethodCall.class, sqlAndParamPair.getRight().toArray());
        // 结果处理
        if (Objects.nonNull(writeDbData4MethodCall) && Objects.isNull(writeDbData4MethodCall.getDomainCode())) {
            writeDbData4MethodCall.setDomainCode(getDomainCode());
        }
        return writeDbData4MethodCall;
    }


    /**
     * 若此节点无子节点，则将此节点方法标记为入口
     * @param calleeNode 方法节点
     */
    private void markMethodAsEntry(CalleeNode calleeNode) {
        if(calleeNode.hasNext()){
            return;
        }
        calleeNode.setEntrance(Boolean.TRUE);
    }

    /**
     * 去所有业务域查询Feign信息
     * @return
     */
    private FeignAndControllerInfo getFeignInfoByControllerCrossDomain(CallGraphNode4Callee callGraphNode4Callee){
        if (Objects.nonNull(callGraphNode4Callee.getCallerMethodHash())){
            // 如果调用者信息已经生成,则表示此次的rpc调用被查询过不是第一次被查询，那么直接返回null，表示没有额外的rpc调用
            return null;
        }
        String controllerHash = callGraphNode4Callee.getCalleeMethodHash();
        // 获取本方法节点对应的Controller信息
        List<SpringControllerComplexInfo> springInfoList = getSpringInfoByMethodHash(callGraphNode4Callee.getCalleeMethodHash());
        if (CollectionUtils.isEmpty(springInfoList)){
            return null;
        }
        if (springInfoList.size() > 1) {
            logger.warn("此控制器方法对应复数个接口");
        }
        // todo 默认一个控制器方法对应一个接口,一个控制器方法对应多个接口的情况暂时忽略
        SpringControllerComplexInfo springInfo = springInfoList.get(0);
        logger.warn("此控制器的接口信息：{}:{}",springInfo.getRequestMethod(),springInfo.getShowUri());

        String selectFeignSql = "";
        ArrayList<Object> paramList = new ArrayList<>();
        Set<String> domainCodeSet = this.domainInfoMap.keySet();
        Iterator<String> iterator = domainCodeSet.iterator();
        while (iterator.hasNext()){
            String domain = iterator.next();
            // 去全局整个项目的FeignClient表中查询对应的Feign信息
            selectFeignSql += "select " +
                    DC.FC_FULL_METHOD + "," +
                    DC.FC_METHOD_HASH + "," +
                    DC.FC_SERVICE_NAME + "," +
                    DC.FC_CONTEXT_ID + "," +
                    DC.FC_SHOW_URI + "," +
                    DC.FC_REQUEST_METHOD +
                    " from " + DbTableInfoEnum.DTIE_FEIGN_CLIENT.getTableNameByAppName(getAppNameByDomain(domain)) +
                    " where " + DC.FC_SHOW_URI + " = ? " +
                    " and " + DC.FC_REQUEST_METHOD + " = ? ";
            paramList.add(springInfo.getShowUri());
            paramList.add(springInfo.getRequestMethod());
            if (iterator.hasNext()) {
                selectFeignSql += " union ";
            }
        }

        List<FeignClientInfo> feignList = dbOperator.queryList(selectFeignSql, FeignClientInfo.class, paramList.toArray());

        //假设一个feignClient对应多个接口的情况不存在。
        if(CollectionUtils.isEmpty(feignList)){
            logger.info("接口 {} 不是一个rpc接口",springInfo.getShowUri());
            return null;
        }
        // 结果集的校验
        StringBuilder warnMessage = new StringBuilder();
        if(feignList.size() > 1){
            warnMessage.append("controller和feign的对应大于一条,controllerHash: ").append(controllerHash);
            addWarningMessage(warnMessage.toString());
        }

        //todo 假设一个feignClient对应多个接口的情况不存在。
        FeignClientInfo feignClientInfo = feignList.get(0);
        FeignAndControllerInfo feignAndControllerInfo = new FeignAndControllerInfo();
        feignAndControllerInfo.setFeignMethodHash(feignClientInfo.getMethodHash());
        feignAndControllerInfo.setFeignShowUri(feignClientInfo.getShowUri());
        feignAndControllerInfo.setFeignFullMethod(feignClientInfo.getFullMethod());

        feignAndControllerInfo.setControllerFullMethod(springInfo.getFullMethod());
        feignAndControllerInfo.setControllerShowUri(springInfo.getShowUri());
        feignAndControllerInfo.setControllerFullMethod(springInfo.getFullMethod());
        return feignAndControllerInfo;
    }



    /**
     * 根据controller的methodhash 获取feign相关信息
     * @return
     */
    private FeignAndControllerInfo getFeignInfoByControllerMethodHash(CallGraphNode4Callee callGraphNode4Callee){
        if (Objects.nonNull(callGraphNode4Callee.getCallerMethodHash())){
            // 如果调用者信息已经生成,则表示此次的rpc调用被查询过不是第一次被查询，那么直接返回null，表示没有额外的rpc调用
            return null;
        }
        String controllerHash = callGraphNode4Callee.getCalleeMethodHash();
        SqlKeyEnum sqlKeyEnum = SqlKeyEnum.MC_QUERY_ONE_RPC1;
        String sql = dbOperWrapper.getCachedSql(sqlKeyEnum);
        if(sql == null){
            sql = "select " + "s."+DC.SPC_FULL_METHOD +" as controller_full_method " +
                    ",s."+DC.SPC_METHOD_HASH + " as controller_method_hash " +
                    ",s."+DC.SPC_SHOW_URI + " as controller_show_uri " +
                    ",f."+DC.FC_FULL_METHOD + " as feign_full_method " +
                    ",f."+DC.FC_METHOD_HASH + " as feign_method_hash " +
                    ",f."+DC.FC_SHOW_URI + " as feign_show_uri " +
                    ",f."+DC.FC_SERVICE_NAME +
                    " from " + DbTableInfoEnum.DTIE_SPRING_CONTROLLER.getTableName() + " as s " +
                    " inner join " + DbTableInfoEnum.DTIE_FEIGN_CLIENT.getTableName() + " as f " +
                    " on " + "s." + DC.SPC_SHOW_URI+ " = f." + DC.FC_SHOW_URI + " COLLATE utf8mb4_general_ci" +
                    " and ( s." + DC.SPC_REQUEST_METHOD +" = f."+ DC.FC_REQUEST_METHOD +" or s."+DC.SPC_REQUEST_METHOD +" is null)" +
                    " where " + " s." + DC.SPC_METHOD_HASH + " = ?";
            sql = dbOperWrapper.cacheSql(sqlKeyEnum, sql);
        }
        List<FeignAndControllerInfo> resultList = dbOperator.queryList(sql, FeignAndControllerInfo.class, controllerHash);
        //假设一个feignClient对应多个接口的情况不存在。
        logger.info("sql:"+sql +"入参:"+controllerHash);
        logger.info("根据controller查询对应feign结果:"+resultList);
        if(Objects.isNull(resultList) || resultList.size() == 0){
            return null;
        }
        // 结果集的校验
        StringBuilder warnMessage = new StringBuilder();
        if(resultList.size() > 1){
            warnMessage.append("controller和feign的对应大于一条,controllerHash: ").append(controllerHash);
            addWarningMessage(warnMessage.toString());
        }

        //假设一个feignClient对应多个接口的情况不存在。
        FeignAndControllerInfo feignAndControllerInfo = resultList.get(0);
        String feignShowUri = feignAndControllerInfo.getFeignShowUri();
        String springShowUri = feignAndControllerInfo.getControllerShowUri();
        if(!feignShowUri.equals(springShowUri)){
            warnMessage.append("服务:").append(feignAndControllerInfo.getServiceName()).append(" 的接口:")
                    .append(springShowUri).append(" 与FeignClient:").append(feignShowUri).append(" 的uri无法严格匹配");
            addWarningMessage(warnMessage.toString());
        }
        return feignAndControllerInfo;
    }

    /**
     * 获取子类的调用方法，若不满足则使用原始方法
     *
     * @param superCallChildInfoStack
     * @param callerFullMethod
     * @return left true: 使用子类的调用方法 false: 使用原始的调用方法
     * @return right: 子类的调用方法、方法HASH+长度
     */
    private Pair<Boolean, MethodAndHash> getSCCChildFullMethod(ListAsStack<SuperCallChildInfo> superCallChildInfoStack, String callerFullMethod) {
        // 判断父类方法调用子类方法对应信息的栈是否有数据
        if (superCallChildInfoStack.isEmpty()) {
            return new ImmutablePair<>(Boolean.FALSE, null);
        }

        String callerMethodWithArgs = JACGClassMethodUtil.getMethodNameWithArgsFromFull(callerFullMethod);
        if (callerMethodWithArgs.startsWith(JavaCGCommonNameConstants.METHOD_NAME_INIT)) {
            // 调用方法为构造函数，使用原始调用方法
            return new ImmutablePair<>(Boolean.FALSE, null);
        }

        String callerClassName = JACGClassMethodUtil.getClassNameFromMethod(callerFullMethod);
        String callerSimpleClassName = dbOperWrapper.getSimpleClassName(callerClassName);

        String sccChildFullMethod = null;
        String sccChildMethodHash = null;
        // 保存上一次处理的被调用唯一类名
        String lastChildCalleeSimpleClassName = null;
        // 对父类方法调用子类方法对应信息的栈，从栈顶往下遍历
        for (int i = superCallChildInfoStack.getHead(); i >= 0; i--) {
            SuperCallChildInfo superCallChildSInfo = superCallChildInfoStack.getElement(i);
            String childCalleeSimpleClassName = superCallChildSInfo.getChildCalleeSimpleClassName();

            if (lastChildCalleeSimpleClassName != null) {
                if (!jacgExtendsImplHandler.checkExtendsOrImplBySimple(lastChildCalleeSimpleClassName, childCalleeSimpleClassName)) {
                    // 当前已不是第一次处理，判断上次的子类是否为当前子类的父类，若是则可以继续处理，若否则结束循环
                    break;
                }
                logger.debug("继续处理子类 {} {}", lastChildCalleeSimpleClassName, childCalleeSimpleClassName);
            }
            lastChildCalleeSimpleClassName = childCalleeSimpleClassName;

            // 判断父类方法调用子类方法对应信息的栈的调用类（对应子类）是否为当前调用类的子类
            if (!jacgExtendsImplHandler.checkExtendsOrImplBySimple(callerSimpleClassName, childCalleeSimpleClassName)) {
                // 父类方法调用子类方法对应信息的栈的调用类（对应子类）不是当前被调用类的子类
                break;
            }
            // 父类方法调用子类方法对应信息的栈的调用类为当前被调用类的子类
            String tmpSccChildFullMethod = JavaCGMethodUtil.formatFullMethodWithArgs(superCallChildSInfo.getChildCalleeClassName(), callerMethodWithArgs);

            // 判断子类方法是否有被调用方法
            SqlKeyEnum sqlKeyEnum = SqlKeyEnum.MI_QUERY_SIMPLE_CLASS_NAME;
            String sql = dbOperWrapper.getCachedSql(sqlKeyEnum);
            if (sql == null) {
                sql = " select " + DC.MI_SIMPLE_CLASS_NAME +
                        " from " + DbTableInfoEnum.DTIE_METHOD_INFO.getTableName() +
                        " where " + DC.MI_SIMPLE_CLASS_NAME + " = ?" +
                        " and " + DC.MI_FULL_METHOD + " like concat(?, '%')" +
                        " limit 1";
                sql = dbOperWrapper.cacheSql(sqlKeyEnum, sql);
            }
            String simpleClassName = dbOperator.queryObjectOneColumn(sql, String.class, childCalleeSimpleClassName, tmpSccChildFullMethod);
            if (simpleClassName != null) {
                // 子类方法存在，需要继续使用栈中的数据进行处理
                continue;
            }

            // 子类方法存在，使用子类方法
            sccChildFullMethod = tmpSccChildFullMethod;
            sccChildMethodHash = JACGUtil.genHashWithLen(sccChildFullMethod);
        }

        if (sccChildFullMethod != null && sccChildMethodHash != null) {
            logger.debug("替换子类的向上的方法调用 {} {}", callerFullMethod, sccChildFullMethod);
            // 使用子类对应的方法，返回子类方法及子类方法HASH+长度
            return new ImmutablePair<>(Boolean.TRUE, new MethodAndHash(sccChildFullMethod, sccChildMethodHash));
        }
        // 使用原始被调用方法
        return new ImmutablePair<>(Boolean.FALSE, null);
    }

    /**
     * 处理被忽略的方法
     *
     * @param callType
     * @param callerFullMethod
     * @param callerMethodHash
     * @param callGraphNode4CalleeStack
     * @param enabled
     * @param methodCallId
     * @return true: 当前方法需要忽略 false: 当前方法不需要忽略
     */
    private boolean handleIgnoredMethod(String callType,
                                        String callerFullMethod,
                                        String callerMethodHash,
                                        ListAsStack<CallGraphNode4Callee> callGraphNode4CalleeStack,
                                        int enabled,
                                        int methodCallId) {
        // 判断是否需要忽略
        if (ignoreCurrentMethod(callType, callerFullMethod) || !JavaCGYesNoEnum.isYes(enabled)) {
            // 当前记录需要忽略
            // 更新当前处理节点的调用者方法HASH
            callGraphNode4CalleeStack.peek().setCallerMethodHash(callerMethodHash);

            if (!JavaCGYesNoEnum.isYes(enabled)) {
                // 记录被禁用的方法调用
                recordDisabledMethodCall(methodCallId, callType);
            }
            return true;
        }
        return false;
    }


    // 确定通过调用方法进行查询使用的SQL语句,查询结果和调用者的调用类型有关
    protected Pair<String,List<Object>> chooseQueryByCalleeMethodSql(CallGraphNode4Callee callGraphNode4Callee, String callType) {
        ArrayList<Object> arguments = new ArrayList<>();
        String calleeMethodHash = callGraphNode4Callee.getCalleeMethodHash();
        String callerMethodHash = callGraphNode4Callee.getCallerMethodHash();

        // 根据被调用方法的调用类型，过滤掉一部分调用关系，确定要排除的调用关系
        // 第一次查询
        if (callerMethodHash == null) {
            if (CTE_SUPER_CALL_CHILD_INTERFACE_OVERRIDE.getType().equals(callType) ||
                    CTE_CHILD_CALL_SUPER_INTERFACE.getType().equals(callType)
            ){
                // 若排除的sql不空
                SqlKeyEnum sqlKeyEnum = SqlKeyEnum.MC_QUERY_ONE_EXCLUDED_CALLER1;
                String sql = dbOperWrapper.getCachedSql(sqlKeyEnum);
                if (sql == null) {
                    // 确定查询被调用关系时所需字段
                    sql = "select " + chooseCallerColumns() +
                            " from " + DbTableInfoEnum.DTIE_METHOD_CALL.getTableNameByAppName(getAppName()) +
                            " where " + DC.MC_CALLEE_METHOD_HASH + " = ? " +
                            " and " + DC.MC_CALL_TYPE + " != ? " +
                            " order by " + DC.MC_CALLER_METHOD_HASH +
                            " limit 1";
                    sql = dbOperWrapper.cacheSql(sqlKeyEnum, sql);
                }
                arguments.add(calleeMethodHash);
                arguments.add(callType);
                return new ImmutablePair<>(sql,arguments);
            // 如果是RPC调用，那么表示此节点是一个feign节点。需要去整个业务域的表中查询
            }else if(ExtendCallTypeEnum.RPC.getType().equals(callType)){
                String sql = "";
                Iterator<String> iterator = domainInfoMap.keySet().iterator();
                while (iterator.hasNext()){
                    String domain = iterator.next();
                    sql += " select '" + domain + "' as domain_code ," + chooseCallerColumns() +
                            " from " + DbTableInfoEnum.DTIE_METHOD_CALL.getTableNameByAppName(getAppNameByDomain(domain)) +
                            " where " + DC.MC_CALLEE_METHOD_HASH + " = ? ";
                    if (iterator.hasNext()){
                        sql += " union ";
                    }else {
                        sql += " order by domain_code , " + DC.MC_CALLER_METHOD_HASH +
                                " limit 1 ";
                    }
                    arguments.add(calleeMethodHash);
                }


                return new ImmutablePair<>(sql,arguments);
            }else {
                SqlKeyEnum sqlKeyEnum = SqlKeyEnum.MC_QUERY_ONE_CALLER1;
                String sql = dbOperWrapper.getCachedSql(sqlKeyEnum);
                if (sql == null) {
                    // 确定查询被调用关系时所需字段
                    sql = "select " + chooseCallerColumns() +
                            " from " + DbTableInfoEnum.DTIE_METHOD_CALL.getTableNameByAppName(getAppName()) +
                            " where " + DC.MC_CALLEE_METHOD_HASH + " = ? " +
                            " order by " + DC.MC_CALLER_METHOD_HASH +
                            " limit 1";
                    sql = dbOperWrapper.cacheSql(sqlKeyEnum, sql);
                }
                arguments.add(calleeMethodHash);
                return new ImmutablePair<>(sql,arguments);
                // 这是需要排除的调用类型
            }

        }else{
            if (CTE_SUPER_CALL_CHILD_INTERFACE_OVERRIDE.getType().equals(callType) ||
                    CTE_CHILD_CALL_SUPER_INTERFACE.getType().equals(callType)
            ) {
                SqlKeyEnum sqlKeyEnum = SqlKeyEnum.MC_QUERY_ONE_EXCLUDE_CALLER2;
                String sql = dbOperWrapper.getCachedSql(sqlKeyEnum);
                if (sql == null) {
                    // 确定查询被调用关系时所需字段
                    sql = "select " + chooseCallerColumns() +
                            " from " + DbTableInfoEnum.DTIE_METHOD_CALL.getTableNameByAppName(getAppName()) +
                            " where " + DC.MC_CALLEE_METHOD_HASH + " = ?" +
                            " and " + DC.MC_CALLER_METHOD_HASH + " > ?" +
                            " and " + DC.MC_CALL_TYPE + " != ? " +
                            " order by " + DC.MC_CALLER_METHOD_HASH +
                            " limit 1";
                    sql = dbOperWrapper.cacheSql(sqlKeyEnum, sql);
                }
                arguments.add(calleeMethodHash);
                arguments.add(callerMethodHash);
                arguments.add(callType);
                return  new ImmutablePair<>(sql,arguments);
            } else if (ExtendCallTypeEnum.RPC.getType().equals(callType)) {
                String sql = " select * from  ( ";
                Iterator<String> iterator = domainInfoMap.keySet().iterator();
                while (iterator.hasNext()){
                    String domain = iterator.next();
                    sql += " select '" + domain + "' as domain_code ," + chooseCallerColumns() +
                            " from " + DbTableInfoEnum.DTIE_METHOD_CALL.getTableNameByAppName(getAppNameByDomain(domain)) +
                            " where " + DC.MC_CALLEE_METHOD_HASH + " = ? ";
                    if (iterator.hasNext()){
                        sql += " union ";
                    }
                    arguments.add(calleeMethodHash);
                }

                sql += " order by domain_code , " + DC.MC_CALLER_METHOD_HASH + " )  as tamp " +
                        "where " +  DC.MC_CALLER_METHOD_HASH + " > ? limit 1 ";
                arguments.add(callerMethodHash);
                return new ImmutablePair<>(sql,arguments);
            } else {
                SqlKeyEnum sqlKeyEnum = SqlKeyEnum.MC_QUERY_ONE_CALLER2;
                String sql = dbOperWrapper.getCachedSql(sqlKeyEnum);
                if (sql == null) {
                    // 确定查询被调用关系时所需字段
                    sql = "select " + chooseCallerColumns() +
                            " from " + DbTableInfoEnum.DTIE_METHOD_CALL.getTableNameByAppName(getAppName()) +
                            " where " + DC.MC_CALLEE_METHOD_HASH + " = ?" +
                            " and " + DC.MC_CALLER_METHOD_HASH + " > ?" +
                            " order by " + DC.MC_CALLER_METHOD_HASH +
                            " limit 1";
                    sql = dbOperWrapper.cacheSql(sqlKeyEnum, sql);
                }
                arguments.add(calleeMethodHash);
                arguments.add(callerMethodHash);
                return new ImmutablePair<>(sql,arguments);
            }
        }
    }


    private String transCallType(String calleeCallType){
        if (CTE_SUPER_CALL_CHILD_INTERFACE_OVERRIDE.getType().equals(calleeCallType)) {
            return CTE_CHILD_CALL_SUPER_INTERFACE.getType();
        }
        if (CTE_CHILD_CALL_SUPER_INTERFACE.getType().equals(calleeCallType)) {
            return CTE_SUPER_CALL_CHILD_INTERFACE_OVERRIDE.getType();
        }
        if (ExtendCallTypeEnum.RPC.getType().equals(calleeCallType)){
            return ExtendCallTypeEnum.RPC.getType();
        }
        return null;
    }
    // 确定查询被调用关系时所需字段
    private String chooseCallerColumns() {
        return JACGSqlUtil.joinColumns(
                DC.MC_CALL_ID,
                DC.MC_CALL_TYPE,
                DC.MC_ENABLED,
                DC.MC_CALLER_METHOD_HASH,
                DC.MC_CALLER_FULL_METHOD,
                DC.MC_CALLER_LINE_NUMBER,
                DC.MC_CALL_FLAGS
        );
    }
    /**
     * 处理要生成链路的所有任务
     */
    private Map<String, CalleeTaskInfo> genCalleeTaskInfo() {
        /*
            当前方法返回的Map，每个键值对代表一个类
            含义
            key: 类名（简单类名或完整类名）
            value: 任务信息
         */
        Map<String, CalleeTaskInfo> calleeTaskInfoMap = new HashMap<>();
        // 生成需要处理的类名Set
        for (String task : taskSet) {
            // 将#替换为:
            task = task.replace("#",JavaCGConstants.FLAG_COLON);
            String[] taskArray = StringUtils.splitPreserveAllTokens(task, JavaCGConstants.FLAG_COLON);
            //这里校验可以去掉。
            if (taskArray.length != 1 && taskArray.length != 2) {
                logger.error("配置文件 {} 中指定的任务信息非法\n{}\n格式应为以下之一:\n" +
                                "1. [类名] （代表生成指定类所有方法向上的调用链）\n" +
                                "2. [类名]:[方法名] （代表生成指定类指定名称方法向上的调用链）\n" +
                                "3. [类名]:[方法中的代码行号] （代表生成指定类指定代码行号对应方法向上的调用链）",
                        OtherConfigFileUseSetEnum.OCFUSE_METHOD_CLASS_4CALLEE, task);
                return null;
            }

            String className = taskArray[0];

            // 获取唯一类名（简单类名或完整类名）
            String simpleClassName = getSimpleClassName(className);
            if (simpleClassName == null) {
                addWarningMessage("字节码信息库中不存在类 "+ className +" 跳过链路生成");
                continue;
            }

            CalleeTaskInfo calleeTaskInfo = calleeTaskInfoMap.computeIfAbsent(simpleClassName, k -> new CalleeTaskInfo());
            if (taskArray.length == 1) {
                // 仅指定了类名，需要处理所有的方法
                if (calleeTaskInfo.getMethodInfoMap() != null) {
                    logger.warn("{} 类指定了处理指定方法，也指定了处理全部方法，对该类的全部方法都会进行处理", simpleClassName);
                }

                calleeTaskInfo.setGenAllMethods(true);
            } else {
                // 除类名外还指定了方法信息，只处理指定的方法
                if (calleeTaskInfo.isGenAllMethods()) {
                    logger.warn("{} 类指定了处理全部方法，也指定了处理指定方法，对该类的全部方法都会进行处理", simpleClassName);
                    continue;
                }

                Map<String, String> methodInfoMap = calleeTaskInfo.getMethodInfoMap();
                if (methodInfoMap == null) {
                    methodInfoMap = new HashMap<>();
                    calleeTaskInfo.setMethodInfoMap(methodInfoMap);
                }
                String methodInfo = taskArray[1];
                 /*
                    以下put的数据：
                    key: 配置文件中指定的任务原始文本
                    value: 配置文件中指定的方法名或代码行号
                  */
                methodInfoMap.put(task, methodInfo);

                if (!JavaCGUtil.isNumStr(methodInfo)) {
                    // 当有指定通过方法名而不是代码行号获取方法时，设置对应标志
                    calleeTaskInfo.setFindMethodByName(true);
                }
            }
        }

        return calleeTaskInfoMap;
    }

    /**
     * 根据类名获取方法信息
     */
    public List<MethodFullInfo> getMethodInfoBySimpleClassName(String calleeSimpleClassName) {
        SqlKeyEnum sqlKeyEnum = SqlKeyEnum.MI_QUERY_INFO_BY_CLASSNAME;
        String sql = dbOperWrapper.getCachedSql(sqlKeyEnum);
        if (sql == null) {
            sql = "select " + JACGSqlUtil.joinColumns(DC.MI_SIMPLE_CLASS_NAME, DC.MI_METHOD_NAME, DC.MI_METHOD_HASH,
                    DC.MI_FULL_METHOD) +
                    " from " + DbTableInfoEnum.DTIE_METHOD_INFO.getTableName() +
                    " where " + DC.MI_SIMPLE_CLASS_NAME + " = ? ";
            sql = dbOperWrapper.cacheSql(sqlKeyEnum, sql);
        }
        return dbOperator.queryList(sql, MethodFullInfo.class, calleeSimpleClassName);
    }
    /**
     * 处理要生成链路的所有任务
     * such as SELECT distinct (callee_method_hash),callee_method_name,callee_full_method FROM method_call_i8 where callee_simple_class_name = "IBoqFeign";
     */
    private List<SimpleMethodCallDTO> queryMethodsOfCalleeClass(String calleeSimpleClassName) {
        List<SimpleMethodCallDTO> simpleMethodCallList = new ArrayList<>();

        // 查找指定被调用类的全部方法
        SqlKeyEnum sqlKeyEnum = SqlKeyEnum.MC_QUERY_CALLEE_ALL_METHODS;
        String sql = dbOperWrapper.getCachedSql(sqlKeyEnum);
        if (sql == null) {
            sql = "select " + JACGSqlUtil.joinColumns("distinct(" + DC.MC_CALLEE_METHOD_HASH + ")", DC.MC_CALLEE_FULL_METHOD) +
                    " from " + DbTableInfoEnum.DTIE_METHOD_CALL.getTableName() +
                    " where " + DC.MC_CALLEE_SIMPLE_CLASS_NAME + " = ?";
            sql = dbOperWrapper.cacheSql(sqlKeyEnum, sql);
        }

        List<WriteDbData4MethodCall> calleeMethodList = dbOperator.queryList(sql, WriteDbData4MethodCall.class, calleeSimpleClassName);
        if (JavaCGUtil.isCollectionEmpty(calleeMethodList)) {
            logger.warn("从方法调用关系表未找到被调用类对应方法 [{}] [{}]", sql, calleeSimpleClassName);
            return Collections.emptyList();
        }


        // 记录已被处理过的方法HASH+长度，因为以上查询时返回字段增加了call_flags，因此相同的方法可能会出现多条
        Set<String> handledCalleeMethodHashSet = new HashSet<>();
        for (WriteDbData4MethodCall methodCall : calleeMethodList) {
            String calleeMethodHash = methodCall.getCalleeMethodHash();
            if (!handledCalleeMethodHashSet.add(calleeMethodHash)) {
                // 已处理过的方法跳过
                continue;
            }


            SimpleMethodCallDTO methodCallDTO = new SimpleMethodCallDTO(calleeMethodHash, methodCall.getCalleeFullMethod());
            simpleMethodCallList.add(methodCallDTO);
        }

        return simpleMethodCallList;
    }
}
