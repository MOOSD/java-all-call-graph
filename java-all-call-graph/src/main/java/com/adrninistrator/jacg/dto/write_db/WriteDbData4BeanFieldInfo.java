package com.adrninistrator.jacg.dto.write_db;

/**
 * @author adrninistrator
 * @date 2022/11/16
 * @description: 用于写入数据库的数据，属性的信息
 */
public class WriteDbData4BeanFieldInfo extends AbstractWriteDbData {
    private String fieldHash;
    private String simpleClassName;
    private String fqcn;
    private int accessFlags;
    private String fieldName;

    private String fullFieldName;
    private String fieldType;
    private String fieldSimpleType;

    private boolean hasGetter;

    private boolean hasSetter;

    public WriteDbData4BeanFieldInfo(String fieldHash, String simpleClassName, String fqcn, int accessFlags, String fieldName, String fullFieldName, String fieldType, String fieldSimpleType, boolean hasGetter, boolean hasSetter) {
        this.fieldHash = fieldHash;
        this.simpleClassName = simpleClassName;
        this.fqcn = fqcn;
        this.accessFlags = accessFlags;
        this.fieldName = fieldName;
        this.fullFieldName = fullFieldName;
        this.fieldType = fieldType;
        this.fieldSimpleType = fieldSimpleType;
        this.hasGetter = hasGetter;
        this.hasSetter = hasSetter;
    }

    public String getFieldHash() {
        return fieldHash;
    }

    public void setFieldHash(String fieldHash) {
        this.fieldHash = fieldHash;
    }

    public String getSimpleClassName() {
        return simpleClassName;
    }

    public void setSimpleClassName(String simpleClassName) {
        this.simpleClassName = simpleClassName;
    }

    public int getAccessFlags() {
        return accessFlags;
    }

    public void setAccessFlags(int accessFlags) {
        this.accessFlags = accessFlags;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFullFieldName() {
        return fullFieldName;
    }

    public void setFullFieldName(String fullFieldName) {
        this.fullFieldName = fullFieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public boolean isHasGetter() {
        return hasGetter;
    }

    public void setHasGetter(boolean hasGetter) {
        this.hasGetter = hasGetter;
    }

    public boolean isHasSetter() {
        return hasSetter;
    }

    public void setHasSetter(boolean hasSetter) {
        this.hasSetter = hasSetter;
    }

    public String getFqcn() {
        return fqcn;
    }

    public void setFqcn(String fqcn) {
        this.fqcn = fqcn;
    }

    public String getFieldSimpleType() {
        return fieldSimpleType;
    }

    public void setFieldSimpleType(String fieldSimpleType) {
        this.fieldSimpleType = fieldSimpleType;
    }
}
