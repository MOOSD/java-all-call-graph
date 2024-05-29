package com.adrninistrator.jacg.handler.write_db;

import com.adrninistrator.jacg.annotation.util.AnnotationAttributesParseUtil;
import com.adrninistrator.jacg.common.JACGConstants;
import com.adrninistrator.jacg.common.annotations.JACGWriteDbHandler;
import com.adrninistrator.jacg.common.enums.DbTableInfoEnum;
import com.adrninistrator.jacg.dto.write_db.WriteDbData4BeanFieldAnnotation;
import com.adrninistrator.jacg.util.JACGUtil;
import com.adrninistrator.javacg.common.enums.JavaCGOutPutFileTypeEnum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.adrninistrator.jacg.util.JACGCallGraphFileUtil.getFullClassNameByFullFieldName;


/**
 * @author adrninistrator
 * @date 2022/11/15
 * @description: 写入数据库，类的注解
 */
@JACGWriteDbHandler(
        readFile = true,
        mainFile = true,
        mainFileTypeEnum = JavaCGOutPutFileTypeEnum.OPFTE_BEAN_FIELD_ANNOTATION,
        minColumnNum = JACGConstants.ANNOTATION_COLUMN_NUM_WITHOUT_ATTRIBUTE,
        maxColumnNum = JACGConstants.ANNOTATION_COLUMN_NUM_WITH_ATTRIBUTE,
        dbTableInfoEnum = DbTableInfoEnum.DTIE_BEAN_FIELD_ANNOTATION
)
public class WriteDbHandler4BeanFieldAnnotation extends AbstractWriteDbHandler<WriteDbData4BeanFieldAnnotation> {
    /*
        保存Spring MVC相关类名及@RequestMapping注解属性值
        key
            Spring MVC相关类的唯一类名
        value
            @RequestMapping注解属性值列表
     */
    private final Map<String, List<String>> classRequestMappingMap = new HashMap<>();

    private final Map<String, Map<String,String>> feignClientClassMap = new HashMap<>();

    @Override
    protected WriteDbData4BeanFieldAnnotation genData(String[] array) {
        // 拆分时限制列数，最后一列注解属性中可能出现空格
        String fullFieldName = array[0];
        String className = getFullClassNameByFullFieldName(fullFieldName);
        String fieldHash = JACGUtil.genHashWithLen(fullFieldName);

        // 根据类名前缀判断是否需要处理
        if (!isAllowedClassPrefix(className)) {
            return null;
        }

        String simpleClassName = dbOperWrapper.getSimpleClassName(className);
        String annotationName = array[1];
        // 假如当前行的注解信息无属性，注解属性名称设为空字符串
        String attributeName = "";
        String attributeType = null;
        String attributeValue = null;
        if (array.length > JACGConstants.ANNOTATION_COLUMN_NUM_WITHOUT_ATTRIBUTE) {
            // 当前行的注解信息有属性
            attributeName = array[2];
            attributeType = array[3];
            // 从文件记录解析注解属性
            attributeValue = AnnotationAttributesParseUtil.parseFromFile(attributeType, array[4]);
        }

        return new WriteDbData4BeanFieldAnnotation(simpleClassName, fieldHash, annotationName, attributeName, attributeType, attributeValue, fullFieldName);
    }

    @Override
    protected Object[] genObjectArray(WriteDbData4BeanFieldAnnotation data) {
        return new Object[]{
            genNextRecordId(),
            data.getSimpleClassName(),
            data.getFieldHash(),
            data.getAnnotationName(),
            data.getAttributeName(),
            data.getAttributeType(),
            data.getAttributeValue(),
            data.getFullFieldName()
        };
    }

    @Override
    public String[] chooseFileColumnDesc() {
        return new String[]{
                "完整属性名",
                "注解类名",
                "注解属性名称，空字符串代表无注解属性",
                "注解属性类型，s:字符串；bs:包含回车换行的字符串；m:JSON字符串，Map；ls:JSON字符串，List+String；lm:JSON字符串，List+Map",
                "注解属性值"
        };
    }

    @Override
    public String[] chooseOtherFileDetailInfo() {
        return new String[]{
                "类上指定的注解信息",
                "若注解没有属性值，则相关字段为空",
                "若注解有属性值，则每个属性值占一行"
        };
    }

    public Map<String, List<String>> getClassRequestMappingMap() {
        return classRequestMappingMap;
    }

    public Map<String, Map<String, String>> getFeignClientClassMap() {
        return feignClientClassMap;
    }

}
