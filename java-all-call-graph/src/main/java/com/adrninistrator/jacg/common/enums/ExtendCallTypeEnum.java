package com.adrninistrator.jacg.common.enums;

public enum ExtendCallTypeEnum {
    RPC("rpc", "远程过程调用");

    final String type;
    final String desc;

    ExtendCallTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
