package com.adrninistrator.jacg.runner.base;

import com.adrninistrator.jacg.annotation.formatter.AbstractAnnotationFormatter;
import com.adrninistrator.jacg.api.BusinessData;
import com.adrninistrator.jacg.api.CalleeNode;
import com.adrninistrator.jacg.api.ControllerInfo;
import com.adrninistrator.jacg.api.MethodArgument;
import com.adrninistrator.jacg.common.DC;
import com.adrninistrator.jacg.common.JACGCommonNameConstants;
import com.adrninistrator.jacg.common.JACGConstants;
import com.adrninistrator.jacg.common.enums.*;
import com.adrninistrator.jacg.dto.annotation.BaseAnnotationAttribute;
import com.adrninistrator.jacg.dto.method_call.ObjArgsInfoInMethodCall;
import com.adrninistrator.jacg.dto.write_db.WriteDbData4LambdaMethodInfo;
import com.adrninistrator.jacg.dto.write_db.WriteDbData4MethodLineNumber;
import com.adrninistrator.jacg.handler.dto.business_data.BaseBusinessData;
import com.adrninistrator.jacg.handler.dto.method_arg_generics_type.MethodArgGenericsTypeInfo;
import com.adrninistrator.jacg.util.JACGCallGraphFileUtil;
import com.adrninistrator.jacg.util.JACGClassMethodUtil;
import com.adrninistrator.jacg.util.JACGJsonUtil;
import com.adrninistrator.jacg.util.JACGSqlUtil;
import com.adrninistrator.javacg.common.JavaCGCommonNameConstants;
import com.adrninistrator.javacg.common.enums.JavaCGCallTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


public abstract class AbstractRunnerGenApiCallGraph extends AbstractRunnerGenCallGraph {
    private static final Logger logger = LoggerFactory.getLogger(AbstractRunnerGenApiCallGraph.class);

    //是否跨基于OpenFeign的服务生成调用链路
    protected boolean crossServiceByOpenFeign;

    // 保存各个方法已处理过的所有注解信息
    protected Map<String, Map<String, Map<String, BaseAnnotationAttribute>>> methodAllAnnotationInfoMap = new HashMap<>();

    //复写公共预处理方法


    @Override
    protected boolean commonPreHandle() {
        crossServiceByOpenFeign = configureWrapper.<Boolean>getMainConfig(ConfigKeyEnum.CROSS_SERVICE_BY_OPENFEIGN);
        return super.commonPreHandle();
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
    protected void addControllerInfo(String fullMethod ,Map<String, Map<String, BaseAnnotationAttribute>> methodAnnotationMap, CalleeNode node) {
        if(Objects.isNull(methodAnnotationMap) || methodAnnotationMap.isEmpty()){
            return;
        }
        for (String springMvcMappingAnnotation : JACGCommonNameConstants.SPRING_MVC_MAPPING_ANNOTATIONS) {
            if (Objects.isNull(methodAnnotationMap.get(springMvcMappingAnnotation))){
                continue;
            }
            // 查询controller信息
            ControllerInfo controllerInfo = getControllerInfoDOByFullMethod(fullMethod);
            node.setControllerInfo(controllerInfo);
        }
    }

    /**
     * 添加事务标识
     */
    protected void addRunInTransaction(int methodCallId, String callType, Map<String, Map<String, BaseAnnotationAttribute>> methodAnnotationMap, CalleeNode node) {
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
    protected void addRunInOtherThread(int methodCallId, String callType, Map<String, Map<String, BaseAnnotationAttribute>> methodAnnotationMap, CalleeNode node) {
        if (StringUtils.equalsAny(callType,
                JavaCGCallTypeEnum.CTE_RUNNABLE_INIT_RUN2.getType(),
                JavaCGCallTypeEnum.CTE_CALLABLE_INIT_CALL2.getType(),
                JavaCGCallTypeEnum.CTE_THREAD_START_RUN.getType())) {
            // 方法调用类型属于线程调用，在方法调用上增加在其他线程执行的标志
            node.getCalleeInfo().runInOtherThread();
            return;
        }

        if (methodAnnotationMap != null && methodAnnotationMap.get(JACGCommonNameConstants.SPRING_ASYNC_ANNOTATION) != null) {
            // 方法上的注解包括@Async，在方法调用上增加在其他线程执行的标志
            node.getCalleeInfo().runInOtherThread();
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
                node.getCalleeInfo().runInOtherThread();
            }
        }
    }

    /**
     * 为方法节点添加泛型信息
     * @param handleEntryMethod 方法是否入口方法
     * @param callFlags 调用方式
     * @param methodHash 方法的hash值
     * @param node 方法节点
     * @return 执行是否成功
     */
    protected boolean addMethodArgGenericsTypeInfo(boolean handleEntryMethod, int callFlags, String methodHash, CalleeNode node) {
        if (handleEntryMethod) {
             /*
                处理入口方法
                生成向上的完整方法调用链时，判断被调用方法（即入口方法）是否存在参数泛型类型
                生成向下的完整方法调用链时，判断调用方法（即入口方法）是否存在参数泛型类型
             */
            if ((order4ee && !MethodCallFlagsEnum.MCFE_EE_WITH_GENERICS_TYPE.checkFlag(callFlags)) ||
                    (!order4ee && !MethodCallFlagsEnum.MCFE_ER_WITH_GENERICS_TYPE.checkFlag(callFlags))) {
                return true;
            }
        } else if ((order4ee && !MethodCallFlagsEnum.MCFE_ER_WITH_GENERICS_TYPE.checkFlag(callFlags)) ||
                (!order4ee && !MethodCallFlagsEnum.MCFE_EE_WITH_GENERICS_TYPE.checkFlag(callFlags))) {
             /*
                处理非入口方法
                生成向上的完整方法调用链时，判断调用方法是否存在参数泛型类型
                生成向下的完整方法调用链时，判断被调用方法是否存在参数泛型类型
             */
            return true;
        }

        MethodArgGenericsTypeInfo methodArgGenericsTypeInfo = methodArgGenericsTypeHandler.queryGenericsTypeInfo(methodHash);
        if (methodArgGenericsTypeInfo == null) {
            return false;
        }
        //遍历所有的方法泛型信息
        List<MethodArgument> methodArguments = node.getMethodArguments();
        //补充泛型信息
        methodArgGenericsTypeInfo.forEach((argSeq,genInfo)-> methodArguments.get(argSeq).setGenericsInfo(genInfo.getArgGenericsTypeList()));

        return true;
    }

    /**
     * 添加方法调用业务功能数据
     *
     * @param methodCallId  方法调用ID
     * @param callFlags     方法调用标志
     * @param methodHash    对应的方法HASH+长度
     * @param node 节点信息
     * @return
     */
    protected boolean addBusinessData(int methodCallId,
                                      int callFlags,
                                      String methodHash,
                                      CalleeNode node) {
        // 添加默认的(入参指定的)方法调用业务功能数据
        if (!addDefaultBusinessData(methodCallId, callFlags, methodHash, node)) {
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
        addBusinessData2Node((String) businessData.getDataType(), businessData.getDataValue(), node);
        return true;
    }


    private boolean addDefaultBusinessData(int methodCallId,
                                           int callFlags,
                                           String methodHash,
                                           CalleeNode node) {
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
                addBusinessData2Node(businessDataType, objArgsInfoInMethodCall, node);
            } else if (DefaultBusinessDataTypeEnum.BDTE_METHOD_ARG_GENERICS_TYPE.getType().equals(businessDataType)) {
                // 显示方法参数泛型类型
                if (!addMethodArgGenericsTypeInfo(false, callFlags, methodHash, node)) {
                    return false;
                }
            }
        }

        return true;
    }

    private void addBusinessData2Node(String dataType, Object dataValue, CalleeNode node){
        List<BusinessData> businessData = node.getBusinessData();
        if (Objects.isNull(businessData)) {
            businessData = new ArrayList<>();
            node.setBusinessData(businessData);
        }
        String jsonStr = JACGJsonUtil.getJsonStr(dataValue);

        businessData.add(new BusinessData(dataType,jsonStr));

    }

    /**
     * 通过代码行号获取对应方法
     */
    protected WriteDbData4MethodLineNumber getMethodInfoByLineNumber(String simpleClassName, int methodLineNum) {
        SqlKeyEnum sqlKeyEnum = SqlKeyEnum.MLN_QUERY_METHOD_HASH;
        String sql = dbOperWrapper.getCachedSql(sqlKeyEnum);
        if (sql == null) {
            sql = "select " + JACGSqlUtil.joinColumns(DC.MLN_METHOD_HASH, DC.MLN_FULL_METHOD) +
                    " from " + DbTableInfoEnum.DTIE_METHOD_LINE_NUMBER.getTableName() +
                    " where " + DC.MLN_SIMPLE_CLASS_NAME + " = ?" +
                    " and " + DC.MLN_MIN_LINE_NUMBER + " <= ?" +
                    " and " + DC.MLN_MAX_LINE_NUMBER + " >= ?" +
                    " limit 1";
            sql = dbOperWrapper.cacheSql(sqlKeyEnum, sql);
        }

        return dbOperator.queryObject(sql, WriteDbData4MethodLineNumber.class, simpleClassName, methodLineNum, methodLineNum);

    }

    /**
     * 根据方法的全名 获取所有的Spring Controller信息
     */
    public ControllerInfo getControllerInfoDOByFullMethod(String fullMethod) {
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

        return dbOperator.queryObject(sql, ControllerInfo.class, fullMethod);
    }
}
