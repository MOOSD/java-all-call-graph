package com.adrninistrator.jacg.runner;

import com.adrninistrator.jacg.api.CalleeNode;
import com.adrninistrator.jacg.api.CalleeTrees;
import com.adrninistrator.jacg.common.DC;
import com.adrninistrator.jacg.common.JACGConstants;
import com.adrninistrator.jacg.common.enums.*;
import com.adrninistrator.jacg.conf.ConfigureWrapper;
import com.adrninistrator.jacg.dto.annotation.BaseAnnotationAttribute;
import com.adrninistrator.jacg.dto.call_graph.CallGraphNode4Callee;
import com.adrninistrator.jacg.dto.call_graph.SuperCallChildInfo;
import com.adrninistrator.jacg.dto.method.MethodAndHash;
import com.adrninistrator.jacg.dto.task.CalleeEntryMethodTaskInfo;
import com.adrninistrator.jacg.dto.task.CalleeTaskInfo;
import com.adrninistrator.jacg.dto.task.FindMethodTaskInfo;
import com.adrninistrator.jacg.runner.base.AbstractRunnerGenApiCallGraph;
import com.adrninistrator.jacg.util.*;
import com.adrninistrator.jacg.util.spring.SpringMvcRequestMappingUtil;
import com.adrninistrator.javacg.common.JavaCGCommonNameConstants;
import com.adrninistrator.javacg.common.JavaCGConstants;
import com.adrninistrator.javacg.common.enums.JavaCGCallTypeEnum;
import com.adrninistrator.javacg.common.enums.JavaCGYesNoEnum;
import com.adrninistrator.javacg.dto.stack.ListAsStack;
import com.adrninistrator.javacg.util.JavaCGMethodUtil;
import com.adrninistrator.javacg.util.JavaCGUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

/**
 * 生成java对象的向上调用链的runner
 */
public class RunnerGenGraph4ApiCallee extends AbstractRunnerGenApiCallGraph {
    private static final Logger logger = LoggerFactory.getLogger(RunnerGenAllGraph4Callee.class);

    private CalleeTrees calleeTrees;
    // 生成指定方法向上的调用链路
    public CalleeTrees getLink(ConfigureWrapper config){
        //运行方法，结果收集到指定对象中。
        run(config);
        //错误处理记录抛出
        if (someTaskFail){
            calleeTrees.setFailTaskList(failTaskList);
        }
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
        calleeTrees = CalleeTrees.instantiate();

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

        // 查询被调用类的全部方法信息
        List<CalleeEntryMethodTaskInfo> calleeEntryMethodTaskInfoList = Collections.emptyList();

        if (taskInfo.isGenAllMethods() || taskInfo.isFindMethodByName()) {
            // 假如需要生成指定类的全部方法向上调用链，或需要根据方法名称查询方法时，需要查询被调用类的全部方法信息
            calleeEntryMethodTaskInfoList = queryMethodsOfCalleeClass(className);
            if (calleeEntryMethodTaskInfoList == null) {
                return false;
            }
        }

        // 处理此类中所有方法
        if (taskInfo.isGenAllMethods()) {
            // 需要生成指定类的全部方法向上调用链
            if (calleeEntryMethodTaskInfoList.isEmpty()) {
                logger.error("以下类需要为所有方法生成向上方法调用链，但未查找到其他方法调用该类的方法\n{}", className);
                return false;
            }

            for (CalleeEntryMethodTaskInfo calleeEntryMethodTaskInfo : calleeEntryMethodTaskInfoList) {
                // 处理一个被调用方法
                handleOneCalleeMethod(className, calleeEntryMethodTaskInfo.getMethodHash(), calleeEntryMethodTaskInfo.getFullMethod(),
                        calleeEntryMethodTaskInfo.getCallFlags(), null);
            }
            return true;
        }

        // 生成指定类的名称或代码行号匹配的方法向上调用链 ，//这里的循环没有意义。
        for (Map.Entry<String, String> methodInfoEntry : taskInfo.getMethodInfoMap().entrySet()) {
            //用户输入的原始文本
            String origTaskText = methodInfoEntry.getKey();
            //方法信息
            String methodInfoInTask = methodInfoEntry.getValue();
            if (!JavaCGUtil.isNumStr(methodInfoInTask)) {
                // 通过方法名查找对应的方法并处理
                if (!handleOneCalleeMethodByName(className, calleeEntryMethodTaskInfoList, origTaskText, methodInfoInTask)) {
                    return false;
                }
            } else {
                // 通过代码行号查找对应的方法并处理
                if (!handleOneCalleeMethodByLineNumber(className, origTaskText, methodInfoInTask)) {
                    return false;
                }
            }
        }

        return true;
    }


    // 通过方法名查找对应的方法并处理
    private boolean handleOneCalleeMethodByName(String calleeSimpleClassName, List<CalleeEntryMethodTaskInfo> calleeEntryMethodTaskInfoList, String origTaskText,
                                                String methodInfoInTask) {
        //匹配到的方法DO
        CalleeEntryMethodTaskInfo usedCalleeEntryMethodTaskInfo = null;
        List<String> calleeFullMethodList = new ArrayList<>();

        // 遍历从数据库中查找到的方法信息，查找当前指定的方法名称或参数匹配的方法
        for (CalleeEntryMethodTaskInfo calleeEntryMethodTaskInfo : calleeEntryMethodTaskInfoList) {
            //这里做了模糊匹配，拿方法的完全限定名与用户输入的方法名进行匹配。
            if (StringUtils.startsWith(calleeEntryMethodTaskInfo.getMethodNameAndArgs(), methodInfoInTask)) {
                usedCalleeEntryMethodTaskInfo = calleeEntryMethodTaskInfo;
                calleeFullMethodList.add(calleeEntryMethodTaskInfo.getFullMethod());
            }
        }

        if (calleeFullMethodList.isEmpty()) {
            // 未查找到匹配的方法，生成空文件
            logger.error("未查询到类{}存在的方法:{}被调用过",calleeSimpleClassName,methodInfoInTask);
            return false;
        }

        if (calleeFullMethodList.size() > 1) {
            // 查找到匹配的方法多于1个，返回处理失败
            logger.error("根据配置文件 {}\n中的方法前缀 {} 找到多于一个方法，请指定更精确的方法信息\n{}", OtherConfigFileUseSetEnum.OCFUSE_METHOD_CLASS_4CALLEE,
                    origTaskText, StringUtils.join(calleeFullMethodList, "\n"));
            return false;
        }

        // 处理一个被调用方法
        handleOneCalleeMethod(calleeSimpleClassName, usedCalleeEntryMethodTaskInfo.getMethodHash(), calleeFullMethodList.get(0), usedCalleeEntryMethodTaskInfo.getCallFlags(),
                origTaskText);
        return true;
    }

    // 通过代码行号查找对应的方法并处理
    private boolean handleOneCalleeMethodByLineNumber(String calleeSimpleClassName, String origTaskText, String methodInfoInTask) {
        int methodLineNum = Integer.parseInt(methodInfoInTask);

        // 通过代码行号获取对应方法
        FindMethodTaskInfo findMethodTaskInfo = findMethodByLineNumber(true, calleeSimpleClassName, methodLineNum);
        if (findMethodTaskInfo.isError()) {
            // 返回处理失败
            return false;
        }

        if (findMethodTaskInfo.isGenEmptyFile()) {
            // 需要生成空文件
            return genEmptyFile(calleeSimpleClassName, methodInfoInTask);
        }

        // 处理一个被调用方法
        handleOneCalleeMethod(calleeSimpleClassName, findMethodTaskInfo.getMethodHash(), findMethodTaskInfo.getFullMethod(), findMethodTaskInfo.getCallFlags(), origTaskText);
        return true;
    }


    /**
     * 异步处理被调用方法
     */
    private void handleOneCalleeMethod(String entryCalleeSimpleClassName,
                                       String entryCalleeMethodHash,
                                       String entryCalleeFullMethod,
                                       int callFlags,
                                       String origTaskText) {

        threadPoolExecutor.execute(() -> {
            try {
                // 执行处理一个被调用方法
                if (!doHandleOneCalleeMethod(entryCalleeSimpleClassName, entryCalleeMethodHash, entryCalleeFullMethod, callFlags)) {
                    // 记录执行失败的任务信息
                    recordTaskFail(origTaskText != null ? origTaskText : entryCalleeFullMethod);
                }
            } catch (Exception e) {
                logger.error("error {} ", origTaskText, e);
                // 记录执行失败的任务信息
                recordTaskFail(origTaskText != null ? origTaskText : entryCalleeFullMethod);
            }
        });
    }

    /**
     * 异步处理一个调用方法，关注公共资源的使用
     */
    // 执行处理一个被调用方法
    private boolean doHandleOneCalleeMethod(String entryCalleeSimpleClassName,
                                            String entryCalleeMethodHash,
                                            String entryCalleeFullMethod,
                                            int callFlags) {

        // 判断配置文件中是否已指定忽略当前方法
        if (ignoreCurrentMethod(null, entryCalleeFullMethod)) {
            logger.info("配置文件中已指定忽略当前方法，不处理 {}", entryCalleeFullMethod);
            return true;
        }
        // 记录一个被调用方法的调用链信息
        return recordOneCalleeMethod(entryCalleeSimpleClassName, entryCalleeMethodHash, entryCalleeFullMethod, callFlags);

    }

    // 记录一个被调用方法的调用链信息
    private boolean recordOneCalleeMethod(String entryCalleeSimpleClassName,
                                          String entryCalleeMethodHash,
                                          String entryCalleeFullMethod,
                                          int callFlags
                                          ){
        // 构建调用树
        CalleeNode root = createTree(entryCalleeSimpleClassName, entryCalleeFullMethod);

        // 判断被调用方法上是否有注解
        if (MethodCallFlagsEnum.MCFE_EE_METHOD_ANNOTATION.checkFlag(callFlags)) {
            // 添加方法注解信息
            List<String> methodAnnotationInfo = new ArrayList<>();
            getMethodAnnotationInfo(entryCalleeFullMethod, entryCalleeMethodHash,methodAnnotationInfo);
            if (methodAnnotationInfo.size() > 0) {
                root.setAnnotation(methodAnnotationInfo);
            }
        }

        if (businessDataTypeSet.contains(DefaultBusinessDataTypeEnum.BDTE_METHOD_ARG_GENERICS_TYPE.getType())) {
            // 显示方法参数泛型类型
            if (!addMethodArgGenericsTypeInfo(true, callFlags, entryCalleeMethodHash, root)) {
                return false;
            }
        }

        // 根据指定的调用者方法HASH，查找所有被调用的方法信息
        if (!genAllGraph4Callee(entryCalleeMethodHash, entryCalleeFullMethod)) {
            return false;
        }

        return true;
    }

    // 生成空文件
    private boolean genEmptyFile(String calleeSimpleClassName, String methodInfoInTask) {
        // 生成内容为空的调用链文件名
        String outputFilePath4EmptyFile = currentOutputDirPath + File.separator + JACGConstants.DIR_OUTPUT_METHODS + File.separator +
                JACGCallGraphFileUtil.getEmptyCallGraphFileName(calleeSimpleClassName, methodInfoInTask);
        logger.info("生成空文件 {} {} {}", calleeSimpleClassName, methodInfoInTask, outputFilePath4EmptyFile);
        // 创建文件
        return JACGFileUtil.createNewFile(outputFilePath4EmptyFile);
    }

    // 确定写入输出文件的当前被调用方法信息
    private CalleeNode createTree(String calleeSimpleClassName, String calleeFullMethod) {
        CalleeNode root = CalleeNode.instantiate(true);
        //添加到调用树列表
        root.setClassName(calleeSimpleClassName);
        root.setFqcn(JACGClassMethodUtil.getClassNameFromMethod(calleeFullMethod));
        root.setMethodName(JACGClassMethodUtil.getMethodNameFromFull(calleeFullMethod));
        root.setMethodArguments(MethodUtil.genMethodArgTypeList(calleeFullMethod));
        root.setDepth(JACGConstants.CALL_GRAPH_METHOD_LEVEL_START);

        calleeTrees.addTree(calleeFullMethod,root);
        return root;
    }

    /**
     * 根据指定的被调用者方法HASH，查找所有调用方法信息
     */
    protected boolean genAllGraph4Callee(String entryCalleeMethodHash,
                                         String entryCalleeFullMethod) {
        //方法被调用方,默认是根节点
        CalleeNode callee = calleeTrees.getTree(entryCalleeFullMethod);
        //方法调用方
        CalleeNode caller;
        // 记录当前处理的方法调用信息的栈
        ListAsStack<CallGraphNode4Callee> callGraphNode4CalleeStack = new ListAsStack<>();
        // 记录父类方法调用子类方法对应信息的栈
        ListAsStack<SuperCallChildInfo> superCallChildInfoStack = new ListAsStack<>();

        // 初始加入最下层节点，callerMethodHash设为null
        CallGraphNode4Callee callGraphNode4CalleeHead = new CallGraphNode4Callee(entryCalleeMethodHash, null, entryCalleeFullMethod);
        callGraphNode4CalleeStack.push(callGraphNode4CalleeHead);

        // 输出结果数量
        int recordNum = 0;
        while (true) {
            // 从栈顶获取当前正在处理的节点
            CallGraphNode4Callee callGraphNode4Callee = callGraphNode4CalleeStack.peek();

            // 查询当前节点的一个上层调用方法
            Map<String, Object> callerMethodMap = queryOneByCalleeMethod(callGraphNode4Callee);
            if (callerMethodMap == null) {
                // 查询失败
                return false;
            }

            if (callerMethodMap.isEmpty()) {
                //如果开启跨微服务生成,且当前为方法是一个controller
                if(crossServiceByOpenFeign && Objects.nonNull(callee.getAnnotation()) &&
                        callee.getAnnotation().stream().anyMatch(SpringMvcRequestMappingUtil::isControllerHandlerMethod)){
                    //找到此controller对应的openfeign,将feignClient作为此Controller的调用者继续生成调用链路,对其重新赋值。
                    callerMethodMap = getControllerCaller4FeignClient(callGraphNode4Callee);
                }
                // 再次判空，为空则表示
                if (callerMethodMap.isEmpty()) {
                    // 查询到调用方法为空时的处理
                    if (handleCallerEmptyResult(callGraphNode4CalleeStack, superCallChildInfoStack ,callee)) {
                        return true;
                    }
                    //若未遍历到初始节点，则节点向上遍历,回到上一层回到上一层时，只可能是单个节点，因此取第一个元素，即当前方法的调用者。
                    callee = callee.getCallers().get(0);
                    continue;
                }
            }

            // 查询到记录
            if (++recordNum % JACGConstants.NOTICE_LINE_NUM == 0) {
                logger.info("记录数达到 {} {}", recordNum, entryCalleeFullMethod);
            }

            String calleeFullMethod = callGraphNode4Callee.getCalleeFullMethod();
            String callerFullMethod = (String) callerMethodMap.get(DC.MC_CALLER_FULL_METHOD);
            String origCallerMethodHash = (String) callerMethodMap.get(DC.MC_CALLER_METHOD_HASH);
            int methodCallId = (int) callerMethodMap.get(DC.MC_CALL_ID);
            int enabled = (int) callerMethodMap.get(DC.MC_ENABLED);
            String callType = (String) callerMethodMap.get(DC.MC_CALL_TYPE);

            // 处理父类方法调用子类方法的相关信息，更新调用者
            MethodAndHash callerMethodAndHash = handleSuperCallChildInfo(superCallChildInfoStack, callGraphNode4CalleeStack.getHead(), calleeFullMethod, callerFullMethod,
                    callType, origCallerMethodHash);
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

            int callFlags = (int) callerMethodMap.get(DC.MC_CALL_FLAGS);
            int callerLineNum = (int) callerMethodMap.get(DC.MC_CALLER_LINE_NUMBER);
            // 获取方法调用方信息
            caller = recordCallerInfo(callerFullMethod, methodCallId, callFlags, callType, callerLineNum, callGraphNode4CalleeStack.getHead(),
                    callerMethodHash, back2Level);
            //调用方更新调用列表
            caller.addCaller(callee);
            //被调用方更新被调用列表
            callee.addCallee(caller);

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
            CallGraphNode4Callee nextCallGraphNode4Callee = new CallGraphNode4Callee(callerMethodHash, null, callerFullMethod);
            callGraphNode4CalleeStack.push(nextCallGraphNode4Callee);
            callee = caller;
        }
    }

    /**
     * 获取作为controller的调用者的FeignClient
     * @param callGraphNode4Callee controller方法的hash值
     * @return 组装成的一个条调用记录
     */
    private Map<String, Object> getControllerCaller4FeignClient(CallGraphNode4Callee callGraphNode4Callee){
        // 一个feignClient接口，只可能对应一个controller接口,因此当不是第一次查询此controller被哪个feign调用的时候，直接返回即可。
        if(Objects.nonNull(callGraphNode4Callee.getCallerMethodHash())){
            return new HashMap<>(0);
        }
        Map<String, Object> callerMethodMap = getFeignInfoBySpringMethodHash(callGraphNode4Callee.getCalleeMethodHash());

        if (callerMethodMap.isEmpty()){
            return callerMethodMap;
        }
        //给与一个不存在的调用id
        callerMethodMap.put(DC.MC_CALL_ID,-(int)(Math.random()*100000));

        callerMethodMap.put(DC.MC_ENABLED,1);
        //设置调用方式为rpc调用
        callerMethodMap.put(DC.MC_CALL_TYPE,"RPC");
        //设置调用标识为调用者带有注解
        // todo setflag(0) 等价于 MethodCallFlagsEnum.MCFE_EE_METHOD_ANNOTATION.getFlag();
        callerMethodMap.put(DC.MC_CALL_FLAGS,MethodCallFlagsEnum.MCFE_EE_METHOD_ANNOTATION.setFlag(0));
        //调用行设置为0
        callerMethodMap.put(DC.MC_CALLER_LINE_NUMBER,0);

        return callerMethodMap;
    }
    // 记录调用方法信息
    protected CalleeNode recordCallerInfo(String callerFullMethod,
                                                     int methodCallId,
                                                     int callFlags,
                                                     String callType,
                                                     int callerLineNum,
                                                     int currentNodeLevel,
                                                     String callerMethodHash,
                                                     int back2Level) {
        String callerClassName = JACGClassMethodUtil.getClassNameFromMethod(callerFullMethod);
        String callerSimpleClassName = dbOperWrapper.getSimpleClassName(callerClassName);

        // 实例化一个新节点
        CalleeNode caller = CalleeNode.instantiate();
        caller.setDepth(currentNodeLevel + 1);
        caller.setFqcn(JACGClassMethodUtil.getClassNameFromMethod(callerFullMethod));
        caller.setMethodName(JACGClassMethodUtil.getMethodNameFromFull(callerFullMethod));
        caller.setMethodArguments((MethodUtil.genMethodArgTypeList(callerFullMethod)));

        // 判断调用方法上是否有注解
        Map<String, Map<String, BaseAnnotationAttribute>> methodAnnotationMap = null;
        if (MethodCallFlagsEnum.MCFE_ER_METHOD_ANNOTATION.checkFlag(callFlags)) {
            List<String> methodAnnotations = new ArrayList<>();
            // 添加方法注解信息
            methodAnnotationMap = getMethodAnnotationInfo(callerFullMethod, callerMethodHash, methodAnnotations);
            if (methodAnnotations.size() > 0) {
                caller.setAnnotation(methodAnnotations);
            }
        }

        caller.setClassName(callerSimpleClassName);
        caller.getCalleeInfo().setRow(callerLineNum);

        // 添加方法调用业务功能数据
        if (!addBusinessData(methodCallId, callFlags, callerMethodHash, caller)) {
            return null;
        }

        // 为方法调用信息增加是否在其他线程执行标志
        addRunInOtherThread(methodCallId, callType, methodAnnotationMap, caller);

        // 为方法调用信息增加是否在事务中执行标志
        addRunInTransaction(methodCallId, callType, methodAnnotationMap, caller);

        // 添加循环调用标志
        if (back2Level != JACGConstants.NO_CYCLE_CALL_FLAG) {
            caller.getCalleeInfo().setCycleCall(back2Level);
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
        if (JavaCGCallTypeEnum.CTE_SUPER_CALL_CHILD.getType().equals(callType)) {
            // 当前方法调用类型是父类调用子类方法，记录父类方法调用子类方法对应信息的栈入栈
            String calleeClassName = JACGClassMethodUtil.getClassNameFromMethod(calleeFullMethod);
            String calleeSimpleClassName = dbOperWrapper.getSimpleClassName(calleeClassName);
            SuperCallChildInfo superCallChildInfo = new SuperCallChildInfo(nodeLevel, calleeSimpleClassName, calleeClassName, calleeFullMethod);
            superCallChildInfoStack.push(superCallChildInfo);
        }
        return new MethodAndHash(callerFullMethod, callerMethodHash);
    }


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
    private Map<String, Object> queryOneByCalleeMethod(CallGraphNode4Callee callGraphNode4Callee) {
        // 确定通过调用方法进行查询使用的SQL语句
        String sql = chooseQueryByCalleeMethodSql(callGraphNode4Callee.getCallerMethodHash());

        List<Map<String, Object>> list;
        if (callGraphNode4Callee.getCallerMethodHash() == null) {
            list = dbOperator.queryList(sql, new Object[]{callGraphNode4Callee.getCalleeMethodHash()});
        } else {
            list = dbOperator.queryList(sql, new Object[]{callGraphNode4Callee.getCalleeMethodHash(), callGraphNode4Callee.getCallerMethodHash()});
        }

        if (list == null) {
            // 查询失败
            return null;
        }

        if (list.isEmpty()) {
            // 查询不到结果时，返回空Map
            return new HashMap<>(0);
        }

        return list.get(0);
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
     * 根据controller的methodhash 获取feign相关信息
     * @param ControllerHash
     * @return
     */
    private Map<String,Object> getFeignInfoBySpringMethodHash(String ControllerHash){
        // 第一次查询
        // 确定查询被调用关系时所需字段
        // todo 应该新增按照服务名过滤
        SqlKeyEnum sqlKeyEnum = SqlKeyEnum.MC_QUERY_ONE_RPC1;
        String sql = dbOperWrapper.getCachedSql(sqlKeyEnum);
        if(sql == null){
            sql = "select " + "f."+DC.FC_FULL_METHOD +" as "+DC.MC_CALLER_FULL_METHOD +",f."+DC.FC_METHOD_HASH + " as " + DC.MC_CALLER_METHOD_HASH +
                    " from " + DbTableInfoEnum.DTIE_SPRING_CONTROLLER.getTableName() + " as s " +
                    " inner join " + DbTableInfoEnum.DTIE_FEIGN_CLIENT.getTableName() + " as f " +
                    " on " + "s." + DC.SPC_SHOW_URI+ " = f." + DC.FC_SHOW_URI +
                    " and ( s." + DC.SPC_REQUEST_METHOD +" = f."+ DC.FC_REQUEST_METHOD +" or s."+DC.SPC_REQUEST_METHOD +" is null)" +
                    " where " + " s." + DC.SPC_METHOD_HASH + " = ?";
            sql = dbOperWrapper.cacheSql(sqlKeyEnum, sql);
        }
        List<Map<String, Object>> resultList = dbOperator.queryList(sql, new Object[]{ControllerHash});
        //假设一个feignClient对应多个接口的情况不存在。
        logger.info("sql:"+sql +"入参:"+ControllerHash);
        logger.info("根据controller查询对应feign结果:"+resultList);
        if(Objects.nonNull(resultList) && resultList.size() == 0){
            return new HashMap<>(0);
        }
        return resultList.get(0);
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

            List<Object> list = dbOperator.queryListOneColumn(sql, new Object[]{childCalleeSimpleClassName, tmpSccChildFullMethod});
            if (list == null) {
                // 查询失败
                return null;
            }

            if (!list.isEmpty()) {
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


    // 确定通过调用方法进行查询使用的SQL语句
    protected String chooseQueryByCalleeMethodSql(String callerMethodHash) {
        if (callerMethodHash == null) {
            // 第一次查询
            SqlKeyEnum sqlKeyEnum = SqlKeyEnum.MC_QUERY_ONE_CALLER1;
            String sql = dbOperWrapper.getCachedSql(sqlKeyEnum);
            if (sql == null) {
                // 确定查询被调用关系时所需字段
                sql = "select " + chooseCallerColumns() +
                        " from " + DbTableInfoEnum.DTIE_METHOD_CALL.getTableName() +
                        " where " + DC.MC_CALLEE_METHOD_HASH + " = ?" +
                        " order by " + DC.MC_CALLER_METHOD_HASH +
                        " limit 1";
                sql = dbOperWrapper.cacheSql(sqlKeyEnum, sql);
            }
            return sql;
        }

        // 不是第一次查询
        SqlKeyEnum sqlKeyEnum = SqlKeyEnum.MC_QUERY_ONE_CALLER2;
        String sql = dbOperWrapper.getCachedSql(sqlKeyEnum);
        if (sql == null) {
            // 确定查询被调用关系时所需字段
            sql = "select " + chooseCallerColumns() +
                    " from " + DbTableInfoEnum.DTIE_METHOD_CALL.getTableName() +
                    " where " + DC.MC_CALLEE_METHOD_HASH + " = ?" +
                    " and " + DC.MC_CALLER_METHOD_HASH + " > ?" +
                    " order by " + DC.MC_CALLER_METHOD_HASH +
                    " limit 1";
            sql = dbOperWrapper.cacheSql(sqlKeyEnum, sql);
        }
        return sql;
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
                return null;
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
     * 处理要生成链路的所有任务
     * such as SELECT distinct (callee_method_hash),callee_method_name,callee_full_method FROM method_call_i8 where callee_simple_class_name = "IBoqFeign";
     */
    private List<CalleeEntryMethodTaskInfo> queryMethodsOfCalleeClass(String calleeSimpleClassName) {
        List<CalleeEntryMethodTaskInfo> calleeEntryMethodTaskInfoList = new ArrayList<>();

        // 查找指定被调用类的全部方法
        SqlKeyEnum sqlKeyEnum = SqlKeyEnum.MC_QUERY_CALLEE_ALL_METHODS;
        String sql = dbOperWrapper.getCachedSql(sqlKeyEnum);
        if (sql == null) {
            sql = "select " + JACGSqlUtil.joinColumns("distinct(" + DC.MC_CALLEE_METHOD_HASH + ")", DC.MC_CALLEE_FULL_METHOD, DC.MC_CALL_FLAGS) +
                    " from " + DbTableInfoEnum.DTIE_METHOD_CALL.getTableName() +
                    " where " + DC.MC_CALLEE_SIMPLE_CLASS_NAME + " = ?";
            sql = dbOperWrapper.cacheSql(sqlKeyEnum, sql);
        }

        List<Map<String, Object>> calleeMethodList = dbOperator.queryList(sql, new Object[]{calleeSimpleClassName});
        if (calleeMethodList == null) {
            return null;
        }

        if (calleeMethodList.isEmpty()) {
            logger.warn("从方法调用关系表未找到被调用类对应方法 [{}] [{}]", sql, calleeSimpleClassName);
            return calleeEntryMethodTaskInfoList;
        }

        // 记录已被处理过的方法HASH+长度，因为以上查询时返回字段增加了call_flags，因此相同的方法可能会出现多条
        Set<String> handledCalleeMethodHashSet = new HashSet<>();
        for (Map<String, Object> map : calleeMethodList) {
            String calleeMethodHash = (String) map.get(DC.MC_CALLEE_METHOD_HASH);
            if (!handledCalleeMethodHashSet.add(calleeMethodHash)) {
                // 已处理过的方法跳过
                continue;
            }

            String calleeFullMethod = (String) map.get(DC.MC_CALLEE_FULL_METHOD);
            int callFlags = (int) map.get(DC.MC_CALL_FLAGS);
            CalleeEntryMethodTaskInfo calleeEntryMethodTaskInfo =
                    new CalleeEntryMethodTaskInfo(calleeMethodHash, calleeFullMethod, JACGClassMethodUtil.getMethodNameWithArgsFromFull(calleeFullMethod), callFlags);
            calleeEntryMethodTaskInfoList.add(calleeEntryMethodTaskInfo);
        }

        return calleeEntryMethodTaskInfoList;
    }
}
