package com.adrninistrator.jacg.handler.write_db;

import com.adrninistrator.jacg.common.enums.DbTableInfoEnum;
import com.adrninistrator.jacg.dto.write_db.WriteDbData4FeignClientData;
import com.adrninistrator.javacg.exceptions.JavaCGRuntimeException;

/**
 * @author adrninistrator
 * @date 2022/11/17
 * @description: 写入数据库，Spring Controller信息
 */
public class WriteDbHandler4FeignClient extends AbstractWriteDbHandler<WriteDbData4FeignClientData> {
    @Override
    protected WriteDbData4FeignClientData genData(String line) {
        throw new JavaCGRuntimeException("不会调用当前方法");
    }

    @Override
    protected DbTableInfoEnum chooseDbTableInfo() {
        return DbTableInfoEnum.DTIE_FEIGN_CLIENT;
    }

    @Override
    protected Object[] genObjectArray(WriteDbData4FeignClientData data) {
        return new Object[]{
                data.getMethodHash(),
                data.getSeq(),
                data.getServiceName(),
                data.getContextId(),
                data.getShowUri(),
                data.getClassPath(),
                data.getMethodPath(),
                data.getSimpleClassName(),
                data.getClassName(),
                data.getFullMethod()
        };
    }
}
