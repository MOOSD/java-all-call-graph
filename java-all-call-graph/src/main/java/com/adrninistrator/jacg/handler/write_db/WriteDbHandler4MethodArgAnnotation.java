package com.adrninistrator.jacg.handler.write_db;

import com.adrninistrator.jacg.annotation.util.AnnotationAttributesParseUtil;
import com.adrninistrator.jacg.common.JACGConstants;
import com.adrninistrator.jacg.common.annotations.JACGWriteDbHandler;
import com.adrninistrator.jacg.common.enums.DbTableInfoEnum;
import com.adrninistrator.jacg.dto.write_db.WriteDbData4MethodArgAnnotation;
import com.adrninistrator.jacg.util.JACGUtil;
import com.adrninistrator.javacg.common.enums.JavaCGOutPutFileTypeEnum;

import static com.adrninistrator.jacg.util.JACGClassMethodUtil.getClassNameFromMethod;


/**
 * @author adrninistrator
 * @date 2022/11/15
 * @description: 写入数据库，类的注解
 */
@JACGWriteDbHandler(
        readFile = true,
        mainFile = true,
        mainFileTypeEnum = JavaCGOutPutFileTypeEnum.OPFTE_METHOD_ARG_ANNOTATION,
        minColumnNum = JACGConstants.ANNOTATION_COLUMN_NUM_WITHOUT_ATTRIBUTE+1,
        maxColumnNum = JACGConstants.ANNOTATION_COLUMN_NUM_WITH_ATTRIBUTE+1,
        dbTableInfoEnum = DbTableInfoEnum.DTIE_METHOD_ARGS_ANNOTATION
)
public class WriteDbHandler4MethodArgAnnotation extends AbstractWriteDbHandler<WriteDbData4MethodArgAnnotation> {


    @Override
    protected WriteDbData4MethodArgAnnotation genData(String[] array) {
        // 拆分时限制列数，最后一列注解属性中可能出现空格
        String fullMethod = array[0];
        String argSeq = array[1];
        String methodHash = JACGUtil.genHashWithLen(fullMethod);


        // 根据类名前缀判断是否需要处理
        String className = getClassNameFromMethod(fullMethod);
        if (!isAllowedClassPrefix(className)) {
            return null;
        }
        String annotationName = array[2];
        // 假如当前行的注解信息无属性，注解属性名称设为空字符串
        String attributeName = "";
        String attributeType = null;
        String attributeValue = null;
        if (array.length > JACGConstants.ANNOTATION_COLUMN_NUM_WITHOUT_ATTRIBUTE+1) {
            // 当前行的注解信息有属性
            attributeName = array[3];
            attributeType = array[4];
            // 从文件记录解析注解属性
            attributeValue = AnnotationAttributesParseUtil.parseFromFile(attributeType, array[5]);
        }

        return new WriteDbData4MethodArgAnnotation(fullMethod, methodHash, argSeq,annotationName,attributeName,attributeType,attributeValue);
    }

    @Override
    protected Object[] genObjectArray(WriteDbData4MethodArgAnnotation data) {
        return new Object[]{
            genNextRecordId(),
            data.getFullMethod(),
            data.getMethodHash(),
            data.getArgSeq(),
            data.getAnnotationName(),
            data.getAttributeName(),
            data.getAttributeType(),
            data.getAttributeValue(),
        };
    }

    @Override
    public String[] chooseFileColumnDesc() {
        return new String[]{
                "完整方法名",
                "参数顺序",
                "注解名称",
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

}
