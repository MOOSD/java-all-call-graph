package com.adrninistrator.jacg.dto.write_db;

/**
 * @author adrninistrator
 * @date 2022/11/15
 * @description: 用于写入数据库的数据，方法形参的注解
 */
public class WriteDbData4MethodArgAnnotation extends AbstractWriteDbData {

    private String fullMethod;
    private String methodHash;
    private String argSeq;
    private String annotationName;
    private String attributeName;
    private String attributeType;
    private String attributeValue;


    public WriteDbData4MethodArgAnnotation(String fullMethod, String methodHash, String argSeq, String annotationName, String attributeName, String attributeType, String attributeValue) {
        this.fullMethod = fullMethod;
        this.methodHash = methodHash;
        this.argSeq = argSeq;
        this.annotationName = annotationName;
        this.attributeName = attributeName;
        this.attributeType = attributeType;
        this.attributeValue = attributeValue;
    }

    public String getFullMethod() {
        return fullMethod;
    }

    public void setFullMethod(String fullMethod) {
        this.fullMethod = fullMethod;
    }

    public String getMethodHash() {
        return methodHash;
    }

    public void setMethodHash(String methodHash) {
        this.methodHash = methodHash;
    }

    public String getArgSeq() {
        return argSeq;
    }

    public void setArgSeq(String argSeq) {
        this.argSeq = argSeq;
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
}
