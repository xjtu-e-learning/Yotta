package Yotta.spider.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 分面之间的关系
 * Created by yuanhao on 2017/5/3.
 */
@Entity
@Table(name = "facet_relation")
public class FacetRelation {

    @Id
    @GeneratedValue
    private Long relationId;
    private Long parentFacetId;
    private String parentFacetName;
    private Long parentFacetLayer;
    private Long childFacetId;
    private String childFacetName;
    private Long childFacetLayer;
    private Long topicId;

    public FacetRelation(String parentFacetName, Long parentFacetLayer, String childFacetName, Long childFacetLayer) {
        this.parentFacetName = parentFacetName;
        this.parentFacetLayer = parentFacetLayer;
        this.childFacetName = childFacetName;
        this.childFacetLayer = childFacetLayer;
    }

    public FacetRelation(Long parentFacetId, String parentFacetName, Long parentFacetLayer, Long childFacetId, String childFacetName, Long childFacetLayer, Long topicId) {
        this.parentFacetId = parentFacetId;
        this.parentFacetName = parentFacetName;
        this.parentFacetLayer = parentFacetLayer;
        this.childFacetId = childFacetId;
        this.childFacetName = childFacetName;
        this.childFacetLayer = childFacetLayer;
        this.topicId = topicId;
    }

    public FacetRelation() {
    }

    @Override
    public String toString() {
        return "FacetRelation{" +
                "relationId=" + relationId +
                ", parentFacetId=" + parentFacetId +
                ", parentFacetName='" + parentFacetName + '\'' +
                ", parentFacetLayer=" + parentFacetLayer +
                ", childFacetId=" + childFacetId +
                ", childFacetName='" + childFacetName + '\'' +
                ", childFacetLayer=" + childFacetLayer +
                ", topicId=" + topicId +
                '}';
    }

    public Long getRelationId() {
        return relationId;
    }

    public void setRelationId(Long relationId) {
        this.relationId = relationId;
    }

    public Long getParentFacetId() {
        return parentFacetId;
    }

    public void setParentFacetId(Long parentFacetId) {
        this.parentFacetId = parentFacetId;
    }

    public String getParentFacetName() {
        return parentFacetName;
    }

    public void setParentFacetName(String parentFacetName) {
        this.parentFacetName = parentFacetName;
    }

    public Long getParentFacetLayer() {
        return parentFacetLayer;
    }

    public void setParentFacetLayer(Long parentFacetLayer) {
        this.parentFacetLayer = parentFacetLayer;
    }

    public Long getChildFacetId() {
        return childFacetId;
    }

    public void setChildFacetId(Long childFacetId) {
        this.childFacetId = childFacetId;
    }

    public String getChildFacetName() {
        return childFacetName;
    }

    public void setChildFacetName(String childFacetName) {
        this.childFacetName = childFacetName;
    }

    public Long getChildFacetLayer() {
        return childFacetLayer;
    }

    public void setChildFacetLayer(Long childFacetLayer) {
        this.childFacetLayer = childFacetLayer;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }
}
