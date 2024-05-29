package com.adrninistrator.jacg.dto.write_db;

/**
 * @author adrninistrator
 * @date 2022/11/15
 * @description: 用于写入数据库的数据，属性的注解
 */
public class WriteDbData4BeanFieldAnnotation extends AbstractWriteDbData {

    private String simpleClassName;
    private String fieldHash;
    private String annotationName;
    private String attributeName;
    private String attributeType;
    private String attributeValue;
    private String fullFieldName;

    public WriteDbData4BeanFieldAnnotation() {
    }

    public WriteDbData4BeanFieldAnnotation(String simpleClassName, String fieldHash, String annotationName, String attributeName, String attributeType, String attributeValue, String fullFieldName) {
        this.simpleClassName = simpleClassName;
        this.fieldHash = fieldHash;
        this.annotationName = annotationName;
        this.attributeName = attributeName;
        this.attributeType = attributeType;
        this.attributeValue = attributeValue;
        this.fullFieldName = fullFieldName;
    }

    public String getSimpleClassName() {
        return simpleClassName;
    }

    public void setSimpleClassName(String simpleClassName) {
        this.simpleClassName = simpleClassName;
    }

    public String getFieldHash() {
        return fieldHash;
    }

    public void setFieldHash(String fieldHash) {
        this.fieldHash = fieldHash;
    }

    public String getAnnotationName() {
        return annotationName;
    }

    public void setAnnotationName(String annotationName) {
        this.annotationName = annotationName;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getAttributeType() {
        return attributeType;
    }

    public void setAttributeType(String attributeType) {
        this.attributeType = attributeType;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    public String getFullFieldName() {
        return fullFieldName;
    }

    public void setFullFieldName(String fullFieldName) {
        this.fullFieldName = fullFieldName;
    }
}
