package com.adrninistrator.jacg.handler.write_db;


import com.adrninistrator.jacg.common.annotations.JACGWriteDbHandler;
import com.adrninistrator.jacg.common.enums.DbTableInfoEnum;
import com.adrninistrator.jacg.dto.write_db.WriteDbData4BeanFieldInfo;
import com.adrninistrator.jacg.util.JACGUtil;
import com.adrninistrator.javacg.common.enums.JavaCGOutPutFileTypeEnum;

import static com.adrninistrator.jacg.util.JACGCallGraphFileUtil.getFieldNameAndFullClassName;

/**
 * @author adrninistrator
 * @date 2022/11/16
 * @description: 写入数据库，类的信息
 */
@JACGWriteDbHandler(
        readFile = true,
        mainFile = true,
        mainFileTypeEnum = JavaCGOutPutFileTypeEnum.OPFTE_BEAN_FIELD_INFO,
        minColumnNum = 6,
        maxColumnNum = 6,
        dbTableInfoEnum = DbTableInfoEnum.DTIE_BEAN_FIELD_INFO
)
public class WriteDbHandler4BeanFieldInfo extends AbstractWriteDbHandler<WriteDbData4BeanFieldInfo> {

    @Override
    protected WriteDbData4BeanFieldInfo genData(String[] array) {
        String accessFlags = array[0];
        String fullFieldName = array[1];
        String fieldHash = JACGUtil.genHashWithLen(fullFieldName);
        String fieldName = array[2];
        String type = array[3];
        String hasGetter = array[4];
        String hasSetter = array[5];

        // 根据类名前缀判断是否需要处理
        if (!isAllowedClassPrefix(fullFieldName)) {
            return null;
        }
        String className = "";
        String[] split = getFieldNameAndFullClassName(fullFieldName);
        if (split !=null && split.length>0){
            className = split[0];
        }

        return new WriteDbData4BeanFieldInfo(fieldHash, dbOperWrapper.getSimpleClassName(className),Integer.parseInt(accessFlags)
                ,fieldName, fullFieldName, type, Boolean.parseBoolean(hasGetter), Boolean.parseBoolean(hasSetter));
    }

    @Override
    protected Object[] genObjectArray(WriteDbData4BeanFieldInfo data) {
        return new Object[]{
                data.getFieldHash(),
                data.getSimpleClassName(),
                data.getAccessFlags(),
                data.getFieldName(),
                data.getFullFieldName(),
                data.getFieldType(),
                data.isHasGetter(),
                data.isHasSetter()
        };
    }

    @Override
    public String[] chooseFileColumnDesc() {
        return new String[]{
                "访问修饰符",
                "属性的完全限定名",
                "属性名",
                "类型",
                "是否有getter",
                "是否有setter"
        };
    }

    @Override
    public String[] chooseOtherFileDetailInfo() {
        return new String[]{
                "类的信息，主要是access_flags"
        };
    }


}
