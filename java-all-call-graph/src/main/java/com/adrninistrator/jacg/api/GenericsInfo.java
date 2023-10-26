package com.adrninistrator.jacg.api;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GenericsInfo {
    private String genType;
    private String genValue;

    public GenericsInfo() {
    }

    public String getGenType() {
        return genType;
    }

    public GenericsInfo setGenType(String genType) {
        this.genType = genType;
        return this;
    }

    public String getGenValue() {
        return genValue;
    }

    public GenericsInfo setGenValue(String genValue) {
        this.genValue = genValue;
        return this;
    }
}
