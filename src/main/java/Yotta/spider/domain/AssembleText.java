package Yotta.spider.domain;

import javax.persistence.*;

/**
 * 文本碎片
 * Created by yuanhao on 2017/5/3.
 */
@Entity
@Table(name = "assemble_text")
public class AssembleText {

    @Id
    @GeneratedValue
    private Long textId;

    @Lob    //长整型，对应到mysql数据库为LongText
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false) // 设置该列值不为空
    private String textContent;

    private String textUrl;
    private String textPostTime;
    private String textScratchTime;
    private Long facetId;
    private String facetName;
    private Long facetLayer;
    private Long topicId;

    public AssembleText(String textContent, String facetName, Long facetLayer) {
        this.textContent = textContent;
        this.facetName = facetName;
        this.facetLayer = facetLayer;
    }

    @Override
    public String toString() {
        return "AssembleText{" +
                "textId=" + textId +
                ", textContent='" + textContent + '\'' +
                ", textUrl='" + textUrl + '\'' +
                ", textPostTime='" + textPostTime + '\'' +
                ", textScratchTime='" + textScratchTime + '\'' +
                ", facetId=" + facetId +
                ", facetName='" + facetName + '\'' +
                ", facetLayer=" + facetLayer +
                ", topicId=" + topicId +
                '}';
    }

    public Long getTextId() {
        return textId;
    }

    public void setTextId(Long textId) {
        this.textId = textId;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public String getTextUrl() {
        return textUrl;
    }

    public void setTextUrl(String textUrl) {
        this.textUrl = textUrl;
    }

    public String getTextPostTime() {
        return textPostTime;
    }

    public void setTextPostTime(String textPostTime) {
        this.textPostTime = textPostTime;
    }

    public String getTextScratchTime() {
        return textScratchTime;
    }

    public void setTextScratchTime(String textScratchTime) {
        this.textScratchTime = textScratchTime;
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

    public AssembleText() {

    }

    public AssembleText(String textContent, String textUrl, String textPostTime, String textScratchTime, Long facetId, String facetName, Long facetLayer, Long topicId) {

        this.textContent = textContent;
        this.textUrl = textUrl;
        this.textPostTime = textPostTime;
        this.textScratchTime = textScratchTime;
        this.facetId = facetId;
        this.facetName = facetName;
        this.facetLayer = facetLayer;
        this.topicId = topicId;
    }
}
