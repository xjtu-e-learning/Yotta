package Yotta.spider.domain;

import javax.persistence.*;

/**
 * 数据源表
 * Created by 18710 on 2017/8/10.
 */
@Entity
@Table(name = "source")
public class Source {

    @Id
    @GeneratedValue
    @Column(name="sourceId", columnDefinition="tinyint")
    private Long sourceId;
    private String sourceName;
    private String sourceType;
    private String note;

    public Source(String sourceName, String sourceType, String note) {
        this.sourceName = sourceName;
        this.sourceType = sourceType;
        this.note = note;
    }

    public Source() {
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
