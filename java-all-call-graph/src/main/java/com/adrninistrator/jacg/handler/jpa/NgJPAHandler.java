package com.adrninistrator.jacg.handler.jpa;

import com.adrninistrator.jacg.api.DaoOperateInfo;
import com.adrninistrator.jacg.api.MethodNode;
import com.adrninistrator.jacg.dboper.DbOperWrapper;
import com.adrninistrator.jacg.domain.ClassSignatureEi1DO;
import com.adrninistrator.jacg.dto.annotation.BaseAnnotationAttribute;
import com.adrninistrator.jacg.dto.annotation.StringAnnotationAttribute;
import com.adrninistrator.jacg.handler.annotation.AnnotationHandler;
import com.adrninistrator.jacg.handler.classes.ClassSignatureEi1Handler;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NgJPAHandler {

    /**
     * 继承关系查询
     */
    private final ClassSignatureEi1Handler classSignatureEi1Handler;

    /**
     * 注解查询handler
     */
    private final AnnotationHandler annotationHandler;

    public NgJPAHandler(DbOperWrapper dbOperWrapper) {
        classSignatureEi1Handler = new ClassSignatureEi1Handler(dbOperWrapper);
        annotationHandler = new AnnotationHandler(dbOperWrapper);
    }

    public void addJPAOperateInfo(MethodNode<?> methodNode){
        // 仅对以dao和rule结尾的文件进行处理
        String lowCaseClassName = methodNode.getClassName().toLowerCase();
        if(!lowCaseClassName.endsWith("dao") && !lowCaseClassName.endsWith("rule")){
            return;
        }
        DaoOperateInfo daoOperateInfo = new DaoOperateInfo();
        // 添加调用的表相关信息
        addableInfo(methodNode.getFQCN(),daoOperateInfo);
        // 识别本次dao层操作内容
        addOperationInfo(methodNode.getMethodName(), daoOperateInfo);

        methodNode.getCallInfo().setDaoOperateInfo(daoOperateInfo);
    }

    /**
     * 向daoOperateInfo中添加表相关信息
     * @param className 类名
     * @param daoOperateInfo 要添加信息的daoOperateInfo
     */
    private void addableInfo(String className, DaoOperateInfo daoOperateInfo){
        // 分析表信息
        // 查询此类的继承关系
        List<ClassSignatureEi1DO> classSignatureEi1DOS = classSignatureEi1Handler.queryClassSignatureEi1ByClassName(className);
        if(Objects.isNull(classSignatureEi1DOS)){
            return;
        }

        for (ClassSignatureEi1DO classSignatureEi1DO : classSignatureEi1DOS) {
            String signClassName = classSignatureEi1DO.getSignClassName();
            // 查询类上注解信息
            Map<String, Map<String, BaseAnnotationAttribute>> annoMap = annotationHandler.queryAnnotationMap4Class(signClassName);
            if (annoMap.isEmpty()){
                continue;
            }
            annoMap.forEach((annoName, attributeMap)->{
                if("javax.persistence.Table".equals(annoName)){
                    StringAnnotationAttribute tableName = annotationHandler.getAttributeFromMap(attributeMap, "name", StringAnnotationAttribute.class);
                    if (Objects.nonNull(tableName)) {
                        daoOperateInfo.setTableName(tableName.getAttributeString());
                    }
                }
                if("io.swagger.annotations.ApiModel".equals(annoName)){
                    daoOperateInfo.setDesc(getSwaggerApiModelInfo(attributeMap));
                }
            });

        }
    }

    private void addOperationInfo(String methodName, DaoOperateInfo daoOperateInfo){
        if(methodName.contains("save") || methodName.contains("update")){
            daoOperateInfo.setOperateInfo("新增/更新操作");
        }

    }


    public String getSwaggerApiModelInfo(Map<String, BaseAnnotationAttribute> attributeMap){
        String[] descAttrName = {"value", "description"};
        for (String attrName : descAttrName) {
            StringAnnotationAttribute attrValue = annotationHandler.getAttributeFromMap(attributeMap, attrName, StringAnnotationAttribute.class);
            if(Objects.nonNull(attrValue)){
                return attrValue.getAttributeString();
            }
        }
        return null;
    }

}
