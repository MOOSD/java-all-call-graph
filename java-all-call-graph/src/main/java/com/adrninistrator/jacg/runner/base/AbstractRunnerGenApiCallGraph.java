package com.adrninistrator.jacg.runner.base;

import com.adrninistrator.jacg.api.BusinessData;
import com.adrninistrator.jacg.api.CalleeNode;
import com.adrninistrator.jacg.api.MethodArgument;
import com.adrninistrator.jacg.common.DC;
import com.adrninistrator.jacg.common.JACGCommonNameConstants;
import com.adrninistrator.jacg.common.enums.DbTableInfoEnum;
import com.adrninistrator.jacg.common.enums.DefaultBusinessDataTypeEnum;
import com.adrninistrator.jacg.common.enums.MethodCallFlagsEnum;
import com.adrninistrator.jacg.common.enums.SqlKeyEnum;
import com.adrninistrator.jacg.dto.annotation.BaseAnnotationAttribute;
import com.adrninistrator.jacg.dto.method.ClassAndMethodName;
import com.adrninistrator.jacg.dto.method_call.ObjArgsInfoInMethodCall;
import com.adrninistrator.jacg.handler.dto.method_arg_generics_type.MethodArgGenericsTypeInfo;
import com.adrninistrator.jacg.util.JACGJsonUtil;
import com.adrninistrator.jacg.util.JACGSqlUtil;
import com.adrninistrator.jacg.util.JACGUtil;
import com.adrninistrator.javacg.common.JavaCGCommonNameConstants;
import com.adrninistrator.javacg.common.enums.JavaCGCallTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public abstract class AbstractRunnerGenApiCallGraph extends AbstractRunnerGenCallGraph {
    private static final Logger logger = LoggerFactory.getLogger(AbstractRunnerGenApiCallGraph.class);


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
            ClassAndMethodName lambdaCalleeInfo = dbOperWrapper.getLambdaCalleeInfo(methodCallId);
            if (lambdaCalleeInfo != null && (
                    (JavaCGCommonNameConstants.CLASS_NAME_TRANSACTION_CALLBACK.equals(lambdaCalleeInfo.getClassName()) && JavaCGCommonNameConstants.METHOD_DO_IN_TRANSACTION.equals(lambdaCalleeInfo.getMethodName()))
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
            ClassAndMethodName lambdaCalleeInfo = dbOperWrapper.getLambdaCalleeInfo(methodCallId);
            if (lambdaCalleeInfo != null && (
                    (JavaCGCommonNameConstants.CLASS_NAME_RUNNABLE.equals(lambdaCalleeInfo.getClassName()) && JavaCGCommonNameConstants.METHOD_RUNNABLE_RUN.equals(lambdaCalleeInfo.getMethodName())) ||
                            (JavaCGCommonNameConstants.CLASS_NAME_CALLABLE.equals(lambdaCalleeInfo.getClassName()) && JavaCGCommonNameConstants.METHOD_CALLABLE_CALL.equals(lambdaCalleeInfo.getMethodName()))
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

        Map<String, Object> businessDataMap = dbOperator.queryOneRow(sql, new Object[]{methodCallId});
        if (JACGUtil.isMapEmpty(businessDataMap)) {
            logger.error("查询方法调用业务功能数据不存在 {}", methodCallId);
            return false;
        }

        // 将方法调用业务功能数据加入被调用方法信息中
        addBusinessData2Node((String) businessDataMap.get(DC.BD_DATA_TYPE), businessDataMap.get(DC.BD_DATA_VALUE), node);
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
}
