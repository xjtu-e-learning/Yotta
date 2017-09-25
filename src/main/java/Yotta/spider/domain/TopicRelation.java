package Yotta.spider.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 主题间上下位关系
 * Created by yuanhao on 2017/5/1.
 */
@Entity
@Table(name = "topic_relation")
public class TopicRelation {

    @Id
    @GeneratedValue
    private Long relationId;

    private Long parentTopicId;

    private String parentTopicName;

    private Long parentTopicLayer;

    private Long childTopicId;

    private String childTopicName;

    private Long childTopicLayer;

    private Long domainId;

    @Override
    public String toString() {
        return "TopicRelation{" +
                "relationId=" + relationId +
                ", parentTopicId=" + parentTopicId +
                ", parentTopicName='" + parentTopicName + '\'' +
                ", parentTopicLayer=" + parentTopicLayer +
                ", childTopicId=" + childTopicId +
                ", childTopicName='" + childTopicName + '\'' +
                ", childTopicLayer=" + childTopicLayer +
                ", domainId=" + domainId +
                '}';
    }

    public TopicRelation() {
    }

    public TopicRelation(String parentTopicName, Long parentTopicLayer, String childTopicName, Long childTopicLayer, Long domainId) {
        this.parentTopicName = parentTopicName;
        this.parentTopicLayer = parentTopicLayer;
        this.childTopicName = childTopicName;
        this.childTopicLayer = childTopicLayer;
        this.domainId = domainId;
    }

    public TopicRelation(Long parentTopicId, String parentTopicName, Long parentTopicLayer, Long childTopicId, String childTopicName, Long childTopicLayer, Long domainId) {
        this.parentTopicId = parentTopicId;
        this.parentTopicName = parentTopicName;
        this.parentTopicLayer = parentTopicLayer;
        this.childTopicId = childTopicId;
        this.childTopicName = childTopicName;
        this.childTopicLayer = childTopicLayer;
        this.domainId = domainId;
    }

    public Long getRelationId() {
        return relationId;
    }

    public void setRelationId(Long relationId) {
        this.relationId = relationId;
    }

    public Long getParentTopicId() {
        return parentTopicId;
    }

    public void setParentTopicId(Long parentTopicId) {
        this.parentTopicId = parentTopicId;
    }

    public String getParentTopicName() {
        return parentTopicName;
    }

    public void setParentTopicName(String parentTopicName) {
        this.parentTopicName = parentTopicName;
    }

    public Long getParentTopicLayer() {
        return parentTopicLayer;
    }

    public void setParentTopicLayer(Long parentTopicLayer) {
        this.parentTopicLayer = parentTopicLayer;
    }

    public Long getChildTopicId() {
        return childTopicId;
    }

    public void setChildTopicId(Long childTopicId) {
        this.childTopicId = childTopicId;
    }

    public String getChildTopicName() {
        return childTopicName;
    }

    public void setChildTopicName(String childTopicName) {
        this.childTopicName = childTopicName;
    }

    public Long getChildTopicLayer() {
        return childTopicLayer;
    }

    public void setChildTopicLayer(Long childTopicLayer) {
        this.childTopicLayer = childTopicLayer;
    }

    public Long getDomainId() {
        return domainId;
    }

    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }
}
