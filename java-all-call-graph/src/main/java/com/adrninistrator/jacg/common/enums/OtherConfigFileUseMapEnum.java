package com.adrninistrator.jacg.common.enums;

import com.adrninistrator.jacg.common.enums.interfaces.ConfigInterface;

/**
 * @author adrninistrator
 * @date 2022/4/20
 * @description:
 */
public enum OtherConfigFileUseMapEnum implements ConfigInterface {
    ;

    // 参数配置文件名
    private final String fileName;
    // 参数配置描述
    private final String desc;

    OtherConfigFileUseMapEnum(String fileName, String desc) {
        this.fileName = fileName;
        this.desc = desc;
    }

    @Override
    public String getKey() {
        return fileName;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    @Override
    public String toString() {
        return fileName;
    }

    public static String getDescFromKey(String key) {
        for (OtherConfigFileUseMapEnum otherConfigFileUseSetEnum : OtherConfigFileUseMapEnum.values()) {
            if (otherConfigFileUseSetEnum.getKey().equals(key)) {
                return otherConfigFileUseSetEnum.getDesc();
            }
        }
        return "";
    }
}
