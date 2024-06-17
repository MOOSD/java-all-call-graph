package com.adrninistrator.jacg.dto.write_db;

/**
 * @author adrninistrator
 * @date 2023/3/20
 * @description: 用于写入数据库的数据，bean属性的泛型类型
 */
public class WriteDbData4FieldGenericsType extends AbstractWriteDbData {
    private String methodHash;
    private String simpleClassName;
    private String type;
    private String simpleGenericsType;
    private String genericsType;
    private String fullFieldName;

    private String genericsPath;

    public WriteDbData4FieldGenericsType() {
    }

    public WriteDbData4FieldGenericsType(String methodHash, String simpleClassName, String type, String simpleGenericsType, String genericsType, String fullFieldName, String genericsPath) {
        this.methodHash = methodHash;
        this.simpleClassName = simpleClassName;
        this.type = type;
        this.simpleGenericsType = simpleGenericsType;
        this.genericsType = genericsType;
        this.fullFieldName = fullFieldName;
        this.genericsPath = genericsPath;
    }

    public String getMethodHash() {
        return methodHash;
    }

    public void setMethodHash(String methodHash) {
        this.methodHash = methodHash;
    }

    public String getSimpleClassName() {
        return simpleClassName;
    }

    public void setSimpleClassName(String simpleClassName) {
        this.simpleClassName = simpleClassName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSimpleGenericsType() {
        return simpleGenericsType;
    }

    public void setSimpleGenericsType(String simpleGenericsType) {
        this.simpleGenericsType = simpleGenericsType;
    }

    public String getGenericsType() {
        return genericsType;
    }

    public void setGenericsType(String genericsType) {
        this.genericsType = genericsType;
    }

    public String getFullFieldName() {
        return fullFieldName;
    }

    public void setFullFieldName(String fullFieldName) {
        this.fullFieldName = fullFieldName;
    }

    public String getGenericsPath() {
        return genericsPath;
    }

    public void setGenericsPath(String genericsPath) {
        this.genericsPath = genericsPath;
    }
}
