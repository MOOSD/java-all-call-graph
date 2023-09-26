package com.adrninistrator.jacg.api;

public class BusinessData {
    public BusinessData() {
    }

    public BusinessData(String dataType, Object dataValue) {
        this.dataType = dataType;
        this.dataValue = dataValue;
    }

    private String dataType;
    private Object dataValue;



    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Object getDataValue() {
        return dataValue;
    }

    public void setDataValue(String dataValue) {
        this.dataValue = dataValue;
    }
}
