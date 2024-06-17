package com.adrninistrator.jacg.handler.write_db;

import com.adrninistrator.jacg.common.annotations.JACGWriteDbHandler;
import com.adrninistrator.jacg.common.enums.DbTableInfoEnum;
import com.adrninistrator.jacg.dto.write_db.WriteDbData4FieldGenericsType;
import com.adrninistrator.jacg.util.JACGUtil;
import com.adrninistrator.javacg.common.enums.JavaCGOutPutFileTypeEnum;

import static com.adrninistrator.jacg.util.JACGCallGraphFileUtil.getFullClassNameByFullFieldName;

/**
 * @author adrninistrator
 * @date 2023/3/20
 * @description: 写入数据库，bean属性的泛型类型
 */
@JACGWriteDbHandler(
        readFile = true,
        mainFile = true,
        mainFileTypeEnum = JavaCGOutPutFileTypeEnum.OPFTE_BEAN_FIELD_GENERICS_TYPE,
        minColumnNum = 4,
        maxColumnNum = 4,
        dbTableInfoEnum = DbTableInfoEnum.DTIE_BEAN_FIELD_GENERICS_INFO
)
public class WriteDbHandler4BeanFieldGenericsType extends AbstractWriteDbHandler<WriteDbData4FieldGenericsType> {
    // 方法参数存在泛型类型的方法HASH+长度

    @Override
    protected WriteDbData4FieldGenericsType genData(String[] array) {
        String fullField = array[0];
        // 根据完整方法前缀判断是否需要处理
        if (!isAllowedClassPrefix(fullField)) {
            return null;
        }
        String className = getFullClassNameByFullFieldName(fullField);
        String simpleClassName = dbOperWrapper.getSimpleClassName(className);
        String fieldHash = JACGUtil.genHashWithLen(fullField);
        String type = array[1];
        String genericsType = array[2];
        String genericsPath = array[3];

        return new WriteDbData4FieldGenericsType(fieldHash,
                simpleClassName,
                type,
                dbOperWrapper.getSimpleClassName(genericsType),
                genericsType,
                fullField,
                genericsPath);
    }

    @Override
    protected Object[] genObjectArray(WriteDbData4FieldGenericsType data) {
        return new Object[]{
                genNextRecordId(),
                data.getMethodHash(),
                data.getSimpleClassName(),
                data.getType(),
                data.getGenericsPath(),
                data.getSimpleGenericsType(),
                data.getGenericsType(),
                data.getFullFieldName(),
        };
    }

    @Override
    public String[] chooseFileColumnDesc() {
        return new String[]{
                "完整方法（类名+方法名+参数）",
                "类型，t:参数类型，gt:参数泛型类型",
                "泛型类型或参数类型类名",
                "描述泛型的层级结构所用路径"
        };
    }


    @Override
    public String[] chooseOtherFileDetailInfo() {
        return new String[]{
                "方法参数中使用的泛型类型信息",
                "示例：”TestArgumentGenerics1.testAll(int i, List<TestArgument1> list)“",
                "对于以上示例，会记录对应的方法，以及方法参数中涉及泛型的List、TestArgument1"
        };
    }


}
