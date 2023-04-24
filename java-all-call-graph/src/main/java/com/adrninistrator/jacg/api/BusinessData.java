package com.adrninistrator.jacg.api;

public class BusinessData {
    public BusinessData(String dataType, String dataValue) {
        this.dataType = dataType;
        this.dataValue = dataValue;
    }

    private String dataType;
    private String dataValue;



    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDataValue() {
        return dataValue;
    }

    public void setDataValue(String dataValue) {
        this.dataValue = dataValue;
    }
}
