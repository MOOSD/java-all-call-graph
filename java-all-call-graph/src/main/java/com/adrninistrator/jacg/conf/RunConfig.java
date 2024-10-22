package com.adrninistrator.jacg.conf;

import com.adrninistrator.jacg.common.enums.OtherConfigFileUseMapEnum;

import java.util.HashMap;
import java.util.Map;

public class RunConfig extends ConfigureWrapper{

    private Map<String, Map<String, String>> otherConfigMapMap = new HashMap<>();

    @Override
    public void tryPrintUsedConfigInfo(String simpleClassName, String outputDirPath) {
        //取消配置文件的打印
    }

    public void setOtherConfigMap(OtherConfigFileUseMapEnum configDbKeyEnum, Map<String,String> map) {
        otherConfigMapMap.put(configDbKeyEnum.getKey(),map);
    }

    public Map<String,String> getOtherConfigMap(OtherConfigFileUseMapEnum configDbKeyEnum){
        Map<String, String> stringStringMap = otherConfigMapMap.get(configDbKeyEnum.getKey());
        return stringStringMap;
    }
}
