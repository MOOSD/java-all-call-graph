package com.adrninistrator.jacg.precisionrunner.base;

import com.adrninistrator.jacg.annotation.formatter.AbstractAnnotationFormatter;
import com.adrninistrator.jacg.api.*;
import com.adrninistrator.jacg.common.DC;
import com.adrninistrator.jacg.common.JACGCommonNameConstants;
import com.adrninistrator.jacg.common.JACGConstants;
import com.adrninistrator.jacg.common.enums.*;
import com.adrninistrator.jacg.dto.annotation.BaseAnnotationAttribute;
import com.adrninistrator.jacg.dto.method_call.ObjArgsInfoInMethodCall;
import com.adrninistrator.jacg.dto.write_db.WriteDbData4LambdaMethodInfo;
import com.adrninistrator.jacg.handler.dto.business_data.BaseBusinessData;
import com.adrninistrator.jacg.handler.dto.generics_type.MethodArgGenericsTypeInfo;
import com.adrninistrator.jacg.util.JACGCallGraphFileUtil;
import com.adrninistrator.jacg.util.JACGClassMethodUtil;
import com.adrninistrator.jacg.util.JACGSqlUtil;
import com.adrninistrator.javacg.common.JavaCGCommonNameConstants;
import com.adrninistrator.javacg.common.enums.JavaCGCallTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.adrninistrator.jacg.common.JACGConstants.UNKNOWN_CALL_FLAGS;


/**
 * 生成调用树所用的抽象类
 */
public abstract class AbstractGenCallGraphPRunner extends AbstractGenCallGraphBaseRunner {
    private static final Logger logger = LoggerFactory.getLogger(AbstractGenCallGraphPRunner.class);

    // 单颗树最大节点生成数量
    protected int maxNodeGenNum;

    // 是否跨基于OpenFeign的服务生成调用链路
    protected boolean crossServiceByOpenFeign;

    // 生成节点时的id
    protected AtomicInteger idNum = new AtomicInteger(0);

    // 保存各个方法已处理过的所有注解信息
    protected Map<String, Map<String, Map<String, BaseAnnotationAttribute>>> methodAllAnnotationInfoMap = new HashMap<>();

    // 保存执行过程中的错误以及警告信息。
    protected List<String> warningMessages = new ArrayList<>();
    protected List<String> errorMessages = new ArrayList<>();

    /**
     * 横向压缩 and 纵向压缩
     * @param root 树的根节点
     */
    protected void hCompressTree(MethodNode root) {
        MethodNode fristNode = null;
        Stack<MethodNode> methodNodeStack = new Stack<>();
        methodNodeStack.push(root);
        while(!methodNodeStack.isEmpty()){
            // 出栈
            MethodNode methodNode = methodNodeStack.pop();
            // 当前节点非叶子节点
            if (Objects.nonNull(methodNode.getChildren())) {
                // 无额外子节点
                if(methodNode.getChildren().size() == 1){
                    // 记录此节点
                    if (Objects.isNull(fristNode)) {
                        fristNode = methodNode;
                    }
                    methodNodeStack.push(methodNode.getChildren().get(0));
                // 子节点只有一个
                }else {
                    // 当前非叶子节点，且有多个子节点
                    if(fristNode!= null){
                        fristNode.getChildren().set(0,methodNode);
                        methodNode.setBefore(fristNode);
                        fristNode = null;
                    }
                    // 有分支节点
                    for (int i = 0; i < methodNode.getChildren().size(); i++) {
                        methodNodeStack.push(methodNode.getChildren().get(i));
                    }
                }
            }
            // 当前节点是叶子节点
            if(Objects.isNull(methodNode.getChildren())){
                if(fristNode!= null){
                    fristNode.getChildren().set(0,methodNode);
                    methodNode.setBefore(fristNode);
                    fristNode = null;
                }
            }
        }

    }
    /**
     * 纵向压缩树
     * @param root 树的根节点
     */
    protected void vCompressTree(MethodNode root) {
        // 声明需要移出的子树。
        ArrayList<MethodNode> removeSubTree = new ArrayList<>();


    }

    /**
     * 判断此树是否完全不包含Controller节点，
     * @param root
     * @return 所有不含有Controller子树的集合
     */
    private List<MethodNode> hasController(MethodNode root){
        ArrayList<MethodNode> removeMethod = new ArrayList<MethodNode>();
        List<MethodNode> childrenNodes = root.getChildren();
        if (Objects.nonNull(childrenNodes)) {
            for (MethodNode childrenNode : childrenNodes) {
                List<MethodNode> targetSubTrees = hasController(childrenNode);
                // 如果所有子树都不含Controller
                if(targetSubTrees.size() == childrenNodes.size()){

                }

            }
        }else {
            // 如果当前节点是一个controller节点
            if(!CollectionUtils.isEmpty(root.getControllerInfo())){
                ArrayList<MethodNode> singleMethodNodes = new ArrayList<>(1);
                singleMethodNodes.add(root);
                return singleMethodNodes;
            }
        }

        return null;
    }



    protected String getIdNum(){
        int nextId = idNum.getAndIncrement();
        return String.valueOf(nextId);
    }

    @Override
    protected boolean commonPreHandle() {
        maxNodeGenNum = configureWrapper.<Integer>getMainConfig(ConfigKeyEnum.MAX_NODE_NUM);
        if(maxNodeGenNum <= 0){
            throw new RuntimeException("单棵调用树的最大节点数量小于等于0，请检查配置");
        }
        crossServiceByOpenFeign = configureWrapper.<Boolean>getMainConfig(ConfigKeyEnum.CROSS_SERVICE_BY_OPENFEIGN);
        return super.commonPreHandle();
    }

    protected synchronized void addWarningMessage(String message){
        warningMessages.add(message);
    }

    protected synchronized void addErrorMessage(String message){
        errorMessages.add(message);
    }

    /**
     * 根据节点数判断调用树是否继续生成
     * @param genNodeNum 以生成的节点数
     * @return true: 中断树生成 ；false：继续树生成
     */
    protected boolean cutDownByNodeNum(int genNodeNum){
        return genNodeNum > maxNodeGenNum;
    }
    /**
     * 获取方法对应的注解信息
     *
     * @param fullMethod              完整方法
     * @param methodHash              完整方法HASH+长度
     * @return 当前方法上的注解信息
     */
    protected  Map<String, Map<String, BaseAnnotationAttribute>> getMethodAnnotationInfo(String fullMethod, String methodHash ,List<String> annotationList) {
        //注解缓存
        Map<String, Map<String, BaseAnnotationAttribute>> existedAnnotationInfo = methodAllAnnotationInfoMap.get(methodHash);
        if (existedAnnotationInfo != null) {
            // 当前方法对应的注解信息已查询过，直接使用
            return existedAnnotationInfo;
        }

        // 根据完整方法HASH+长度获取对应的注解信息
        Map<String, Map<String, BaseAnnotationAttribute>> methodAnnotationMap = annotationHandler.queryAnnotationMap4FullMethod(fullMethod);
        if (methodAnnotationMap == null) {
            // 当前方法上没有注解
            return null;
        }
        methodAllAnnotationInfoMap.putIfAbsent(methodHash, methodAnnotationMap);

        // 当前方法上有注解
        // 当前方法对应的注解信息未查询过

        // 遍历当前方法上的所有注解进行处理
        for (Map.Entry<String, Map<String, BaseAnnotationAttribute>> methodAnnotationMapEntry : methodAnnotationMap.entrySet()) {
            String annotationName = methodAnnotationMapEntry.getKey();
            // 遍历用于对方法上的注解进行处理的类
            for (AbstractAnnotationFormatter annotationFormatter : annotationFormatterList) {
                //过滤掉带有特定注解的方法
                if (!annotationFormatter.checkHandleAnnotation(annotationName)) {
                    continue;
                }

                String className = JACGClassMethodUtil.getClassNameFromMethod(fullMethod);
                // 找到能够处理的类进行处理
                String annotationInfo = annotationFormatter.handleAnnotation(fullMethod, className, annotationName, methodAnnotationMapEntry.getValue());
                if (annotationInfo != null) {
                    // 替换TAB、回车、换行等字符，再将半角的@替换为全角，避免影响通过@对注解进行分隔
                    String finalAnnotationInfo = JACGCallGraphFileUtil.replaceSplitChars(annotationInfo)
                            .replace(JACGConstants.FLAG_AT, JACGConstants.FLAG_AT_FULL_WIDTH);

                    // 注解信息以@开头，在以上方法中不需要返回以@开头
                    annotationList.add(JACGConstants.FLAG_AT+finalAnnotationInfo);
                }
                //这里是否要终止
                break;
            }
        }

        return methodAnnotationMap;
    }

    /**
     * 添加controller相关信息
     * @param methodAnnotationMap 注解信息
     * @param node 方法节点
     */
    protected void addControllerInfo(Map<String, Map<String, BaseAnnotationAttribute>> methodAnnotationMap, MethodNode node) {
        if(Objects.isNull(methodAnnotationMap) || methodAnnotationMap.isEmpty()){
            return;
        }
        for (String springMvcMappingAnnotation : JACGCommonNameConstants.SPRING_MVC_MAPPING_ANNOTATIONS) {
            if (Objects.isNull(methodAnnotationMap.get(springMvcMappingAnnotation))){
                continue;
            }
            // 查询controller信息
            List<ControllerInfo> controllerInfoDOByFullMethod = getControllerInfoDOByFullMethod(node.getFullMethod());
            // 如果接口包含多种请求方式、则将其拆分
            node.setControllerInfo(splitController(controllerInfoDOByFullMethod));
        }
    }

    private List<ControllerInfo> splitController(List<ControllerInfo> controllerInfoList){
        ArrayList<ControllerInfo> newControllerInfoList = new ArrayList<>();
        for (ControllerInfo controllerInfo : controllerInfoList) {
            String requestMethod = controllerInfo.getRequestMethod();
            if (requestMethod == null) {
                continue;
            }
            String[] requestMethodArray = StringUtils.split(requestMethod,",");
            if(requestMethodArray.length > 1){
                // 将原本的请求方式重新赋值
                controllerInfo.setRequestMethod(requestMethodArray[0]);
                for (int i = 1; i < requestMethodArray.length; i++) {
                    ControllerInfo newControllerinfo = new ControllerInfo(controllerInfo);
                    newControllerinfo.setRequestMethod(requestMethodArray[i]);
                    newControllerInfoList.add(newControllerinfo);
                }
            }
        }
        controllerInfoList.addAll(newControllerInfoList);
        return controllerInfoList;
    }

    /**
     * 添加事务标识
     */
    protected void addRunInTransaction(Map<String, Map<String, BaseAnnotationAttribute>> methodAnnotationMap, MethodNode node) {
        int methodCallId = node.getCallInfo().getCallId();
        String callType = node.getCallInfo().getCallType();
        if (StringUtils.equalsAny(callType,
                JavaCGCallTypeEnum.CTE_TX_CALLBACK_INIT_CALL2.getType(),
                JavaCGCallTypeEnum.CTE_TX_CALLBACK_WR_INIT_CALL2.getType())) {
            // 方法调用类型属于事务调用，在方法调用上增加在事务中执行的标志
            node.runInTransaction();
            return;
        }

        if (methodAnnotationMap != null && methodAnnotationMap.get(JACGCommonNameConstants.SPRING_TX_ANNOTATION) != null) {
            // 方法上的注解包括@Transactional，在方法调用上增加在事务中执行的标志
            node.runInTransaction();
            return;
        }

        if (JavaCGCallTypeEnum.CTE_LAMBDA.getType().equals(callType)) {
            WriteDbData4LambdaMethodInfo lambdaCalleeInfo = dbOperWrapper.getLambdaCalleeInfo(methodCallId);
            if (lambdaCalleeInfo != null && (
                    (JavaCGCommonNameConstants.CLASS_NAME_TRANSACTION_CALLBACK.equals(lambdaCalleeInfo.getLambdaCalleeClassName()) &&
                            JavaCGCommonNameConstants.METHOD_DO_IN_TRANSACTION.equals(lambdaCalleeInfo.getLambdaCalleeMethodName()))
            )) {
                // 方法为Lambda表达式，且属于事务调用，在方法调用上增加在事务中执行的标志
                node.runInTransaction();
            }
        }
    }

    /**
     * 为方法调用信息增加是否在其他线程执行标志
     *
     */
    protected void addRunInOtherThread(Map<String, Map<String, BaseAnnotationAttribute>> methodAnnotationMap, MethodNode node) {
        int methodCallId = node.getCallInfo().getCallId();
        String callType = node.getCallInfo().getCallType();

        if (StringUtils.equalsAny(callType,
                JavaCGCallTypeEnum.CTE_RUNNABLE_INIT_RUN2.getType(),
                JavaCGCallTypeEnum.CTE_CALLABLE_INIT_CALL2.getType(),
                JavaCGCallTypeEnum.CTE_THREAD_START_RUN.getType())) {
            // 方法调用类型属于线程调用，在方法调用上增加在其他线程执行的标志
            node.getCallInfo().runInOtherThread();
            return;
        }

        if (methodAnnotationMap != null && methodAnnotationMap.get(JACGCommonNameConstants.SPRING_ASYNC_ANNOTATION) != null) {
            // 方法上的注解包括@Async，在方法调用上增加在其他线程执行的标志
            node.getCallInfo().runInOtherThread();
            return;
        }

        if (JavaCGCallTypeEnum.CTE_LAMBDA.getType().equals(callType)) {
            WriteDbData4LambdaMethodInfo lambdaCalleeInfo = dbOperWrapper.getLambdaCalleeInfo(methodCallId);
            if (lambdaCalleeInfo != null &&
                    ((JavaCGCommonNameConstants.CLASS_NAME_RUNNABLE.equals(lambdaCalleeInfo.getLambdaCalleeClassName()) &&
                            JavaCGCommonNameConstants.METHOD_RUNNABLE_RUN.equals(lambdaCalleeInfo.getLambdaCalleeMethodName()))
                            || (JavaCGCommonNameConstants.CLASS_NAME_CALLABLE.equals(lambdaCalleeInfo.getLambdaCalleeClassName()) &&
                            JavaCGCommonNameConstants.METHOD_CALLABLE_CALL.equals(lambdaCalleeInfo.getLambdaCalleeMethodName()))
                    )) {
                // 方法为Lambda表达式，且属于线程调用，在方法调用上增加在其他线程执行的标志
                node.getCallInfo().runInOtherThread();
            }
        }
    }

    /**
     * 向当前节点中添加注解信息
     * @param methodNode 方法节点
     * @return
     */
    protected Map<String, Map<String, BaseAnnotationAttribute>> addMethodAnnotationInfo(MethodNode methodNode){
        Integer callFlags = methodNode.getCallInfo().getCallFlags();
        Map<String, Map<String, BaseAnnotationAttribute>> methodAnnotationMap = null;
        // 如果是未知的调用标识，或者是具有注解的调用标识则扫描注解信息
        if (callFlags == UNKNOWN_CALL_FLAGS ||
                (order4ee && MethodCallFlagsEnum.MCFE_ER_METHOD_ANNOTATION.checkFlag(callFlags)) ||
                (!order4ee && MethodCallFlagsEnum.MCFE_EE_METHOD_ANNOTATION.checkFlag(callFlags))
        ) {
            // 向下调用链路生成的总是被调用者节点，因此仅关注被调用者是否有信息需要生成，向上相反
            List<String> methodAnnotations = new ArrayList<>();
            // 添加方法注解信息
            methodAnnotationMap = getMethodAnnotationInfo(methodNode.getFullMethod(), methodNode.getMethodHash(), methodAnnotations);
            if (methodAnnotations.size() > 0) {
                methodNode.setAnnotation(methodAnnotations);
            }
        }
        return methodAnnotationMap;
    }

    /**
     * 为方法节点添加泛型信息
     * @param callFlags 调用方式
     * @param methodHash 方法的hash值
     * @param node 方法节点
     * @return 执行是否成功
     */
    protected boolean addMethodArgGenericsTypeInfo(int callFlags, String methodHash, MethodNode node) {
        // 如果是未知的方法标识，则直接查询。
        if(!(UNKNOWN_CALL_FLAGS == callFlags ||
                (order4ee && MethodCallFlagsEnum.MCFE_ER_ARGS_WITH_GENERICS_TYPE.checkFlag(callFlags)) ||
                (!order4ee && MethodCallFlagsEnum.MCFE_EE_ARGS_WITH_GENERICS_TYPE.checkFlag(callFlags))
            )
        ){
            /*
             * 默认调用标识的方法 默认均处理
             * 向上调用链路时，处理调用者上的泛型信息
             * 向下调用链路时，处理被盗用这的泛型信息
             */
            return true;
        }

        MethodArgGenericsTypeInfo methodArgGenericsTypeInfo = methodArgGenericsTypeHandler.queryGenericsTypeInfo(methodHash);
        if (methodArgGenericsTypeInfo == null) {
            return false;
        }
        // 遍历所有的方法泛型信息
        List<MethodArgument> methodArguments = node.getMethodFormalArguments();
        // 为节点添加方法泛型信息
        methodArgGenericsTypeInfo.forEach((argSeq,genInfo)-> methodArguments.get(argSeq).setGenericsInfo(genInfo.getGenericsTypeList()));
        return true;
    }

    /**
     * 添加方法调用业务功能数据
     * @param node 节点信息
     * @return
     */
    protected boolean addBusinessData(MethodNode node) {
        int methodCallId = node.getCallInfo().getCallId();
        int callFlags = node.getCallInfo().getCallFlags();
        // 添加默认的(入参指定的)方法调用业务功能数据。其中包括:方法调用信息、方法参数泛型类型
        if (!addDefaultBusinessData(methodCallId, callFlags, node.getMethodHash(), node)) {
            return false;
        }

        if (!MethodCallFlagsEnum.MCFE_EE_BUSINESS_DATA.checkFlag(callFlags)) {
            // 被调用方法不存在自定义的业务功能数据
            return true;
        }

        // 存在程序识别的方法调用业务功能数据，从数据库查询
        SqlKeyEnum sqlKeyEnum = SqlKeyEnum.BD_QUERY_BUSINESS_DATA;
        String sql = dbOperWrapper.getCachedSql(sqlKeyEnum);
        if (sql == null) {
            sql = "select " + JACGSqlUtil.joinColumns(DC.BD_DATA_TYPE, DC.BD_DATA_VALUE) +
                    " from " + DbTableInfoEnum.DTIE_BUSINESS_DATA.getTableName() +
                    " where " + DC.BD_CALL_ID + " = ?";
            sql = dbOperWrapper.cacheSql(sqlKeyEnum, sql);
        }

        BaseBusinessData businessData = dbOperator.queryObject(sql, BaseBusinessData.class, methodCallId);
        if (Objects.isNull(businessData)) {
            logger.error("查询方法调用业务功能数据不存在 {}", methodCallId);
            return false;
        }

        // 将方法调用业务功能数据加入被调用方法信息中
        addBusinessData2Node(businessData.getDataType(), businessData.getDataValue(), node);
        return true;
    }


    private boolean addDefaultBusinessData(int methodCallId,
                                           int callFlags,
                                           String methodHash,
                                           MethodNode node) {
        for (String businessDataType : businessDataTypeList) {
            if (DefaultBusinessDataTypeEnum.BDTE_METHOD_CALL_INFO.getType().equals(businessDataType)) {
                // 显示方法调用信息
                if (!MethodCallFlagsEnum.MCFE_METHOD_CALL_INFO.checkFlag(callFlags)) {
                    continue;
                }

                ObjArgsInfoInMethodCall objArgsInfoInMethodCall = methodCallInfoHandler.queryObjArgsInfoInMethodCall(methodCallId);
                if (objArgsInfoInMethodCall == null) {
                    return false;
                }
                addMethodCallInfo2Node(objArgsInfoInMethodCall, node);
            } else if (DefaultBusinessDataTypeEnum.BDTE_METHOD_ARG_GENERICS_TYPE.getType().equals(businessDataType)) {
                // 显示方法参数泛型类型
                addMethodArgGenericsTypeInfo(callFlags, methodHash, node);
            }
        }

        return true;
    }

    protected void addMethodCallInfo2Node(ObjArgsInfoInMethodCall methodCallInfo, MethodNode node){
        CallInfo callInfo = node.getCallInfo();
        callInfo.setCallActualArguments(methodCallInfo.getArgInfoMap());
        callInfo.setCallObjectInfo(methodCallInfo.getObjInfo());
    }

    protected void addBusinessData2Node(String dataType, Object dataValue, MethodNode node){
        List<BusinessData> businessData = node.getBusinessData();
        if (Objects.isNull(businessData)) {
            businessData = new ArrayList<>();
            node.setBusinessData(businessData);
        }

        businessData.add(new BusinessData(dataType,dataValue));

    }


    /**
     * 根据方法的全名 获取所有的Spring Controller信息
     */
    public List<ControllerInfo> getControllerInfoDOByFullMethod(String fullMethod) {
        SqlKeyEnum sqlKeyEnum = SqlKeyEnum.SPC_QUERY_INFO_BY_METHOD;
        String sql = dbOperWrapper.getCachedSql(sqlKeyEnum);
        if (sql == null) {
            sql = "select " + JACGSqlUtil.joinColumns(DC.SPC_METHOD_HASH, DC.SPC_SEQ,
                    DC.SPC_SHOW_URI, DC.SPC_REQUEST_METHOD, DC.SPC_CLASS_PATH, DC.SPC_METHOD_PATH,
                    DC.SPC_ANNOTATION_ANNOTATION_NAME, DC.SPC_SIMPLE_CLASS_NAME, DC.SPC_FULL_METHOD) +
                    " from " + DbTableInfoEnum.DTIE_SPRING_CONTROLLER.getTableName() +
                    " where " + DC.SPC_FULL_METHOD + " = ? ";
            sql = dbOperWrapper.cacheSql(sqlKeyEnum, sql);
        }
        return dbOperator.queryList(sql,ControllerInfo.class,fullMethod);
    }
}
