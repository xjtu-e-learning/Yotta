package Yotta.spider.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 分面
 * Created by yuanhao on 2017/5/3.
 */
@Entity
@Table(name = "facet")
public class Facet {

    @Id
    @GeneratedValue
    private Long facetId;
    private String facetName;
    private Long facetLayer;
    private Long topicId;

    public Facet(String facetName, Long facetLayer, Long topicId) {
        this.facetName = facetName;
        this.facetLayer = facetLayer;
        this.topicId = topicId;
    }

    public Facet() {
    }

    public Facet(String facetName, Long facetLayer) {
        this.facetName = facetName;
        this.facetLayer = facetLayer;
    }

    @Override
    public String toString() {
        return "Facet{" +
                "facetId=" + facetId +
                ", facetName='" + facetName + '\'' +
                ", facetLayer=" + facetLayer +
                ", topicId=" + topicId +
                '}';
    }

    public Long getFacetId() {
        return facetId;
    }

    public void setFacetId(Long facetId) {
        this.facetId = facetId;
    }

    public String getFacetName() {
        return facetName;
    }

    public void setFacetName(String facetName) {
        this.facetName = facetName;
    }

    public Long getFacetLayer() {
        return facetLayer;
    }

    public void setFacetLayer(Long facetLayer) {
        this.facetLayer = facetLayer;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }
}
