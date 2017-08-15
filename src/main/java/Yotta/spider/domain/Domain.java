package Yotta.spider.domain;

import javax.persistence.*;

/**
 * 领域表
 * Created by yuanhao on 2017/5/1.
 */
@Entity
@Table(name = "domain")
public class Domain {
    @Id
    @GeneratedValue
    private Long domainId;
    private String domainName;
    private String domainUrl;
    private String note;
    @Column(name="sourceId", columnDefinition="tinyint")
    private Long sourceId;

    public Domain() {
    }

    public Domain(String domainName, String domainUrl, String note, Long sourceId) {
        this.domainName = domainName;
        this.domainUrl = domainUrl;
        this.note = note;
        this.sourceId = sourceId;
    }

    public String getDomainUrl() {
        return domainUrl;
    }

    public void setDomainUrl(String domainUrl) {
        this.domainUrl = domainUrl;
    }

    public Long getDomainId() {
        return domainId;
    }

    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    @Override
    public String toString() {
        return "Domain{" +
                "domainId=" + domainId +
                ", domainName='" + domainName + '\'' +
                ", domainUrl='" + domainUrl + '\'' +
                ", note='" + note + '\'' +
                ", sourceId=" + sourceId +
                '}';
    }
}
