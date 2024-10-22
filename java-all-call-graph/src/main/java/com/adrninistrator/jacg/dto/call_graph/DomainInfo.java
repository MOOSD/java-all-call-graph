package com.adrninistrator.jacg.dto.call_graph;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class DomainInfo {
    String domainName;
    String domainCode;

    public DomainInfo() {
    }

    /**
     * 默认业务域名称和code相同
     */

    public DomainInfo( String domainCode, String domainName) {
        this.domainName = domainName;
        this.domainCode = domainCode;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getDomainCode() {
        return domainCode;
    }

    public void setDomainCode(String domainCode) {
        this.domainCode = domainCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        DomainInfo that = (DomainInfo) o;

        return new EqualsBuilder().append(domainCode, that.domainCode).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(domainCode).toHashCode();
    }
}
