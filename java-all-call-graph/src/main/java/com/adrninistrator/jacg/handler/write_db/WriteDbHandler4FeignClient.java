package com.adrninistrator.jacg.handler.write_db;

import com.adrninistrator.jacg.common.annotations.JACGWriteDbHandler;
import com.adrninistrator.jacg.common.enums.DbTableInfoEnum;
import com.adrninistrator.jacg.dto.write_db.WriteDbData4FeignClientData;
import com.adrninistrator.jacg.util.IdGenerateUtil;

/**
 * feignClient数据写入数据库
 */
@JACGWriteDbHandler(
        readFile = false,
        dbTableInfoEnum = DbTableInfoEnum.DTIE_FEIGN_CLIENT
)
public class WriteDbHandler4FeignClient extends AbstractWriteDbHandler<WriteDbData4FeignClientData> {


    @Override
    protected Object[] genObjectArray(WriteDbData4FeignClientData data) {
        return new Object[]{
                IdGenerateUtil.genId(),
                data.getVersionId(),
                data.getMethodHash(),
                data.getSeq(),
                data.getServiceName(),
                data.getContextId(),
                data.getShowUri(),
                data.getRequestMethod(),
                data.getClassPath(),
                data.getMethodPath(),
                data.getSimpleClassName(),
                data.getClassName(),
                data.getAnnotationName(),
                data.getFullMethod()
        };
    }
}
