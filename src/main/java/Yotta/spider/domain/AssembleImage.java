package Yotta.spider.domain;

import javax.persistence.*;
import java.util.Arrays;

/**
 * 图片碎片
 * Created by yuanhao on 2017/5/3.
 */
@Entity
@Table(name = "assemble_image")
public class AssembleImage {

    @Id
    @GeneratedValue
    private Long imageId;

    @Lob
    @Basic(fetch= FetchType.LAZY)  //二进制数据，且延迟加载
    @Column(columnDefinition = "LONGBLOB", nullable = false) // 存储图片的二进制内容
    private byte[] imageContent;

    private String imageUrl;
    private Long imageWidth;
    private Long imageHeight;
    private String imageScratchTime;
    private String imageApi;
    private Long facetId;
    private String facetName;
    private Long facetLayer;
    private Long topicId;

    public AssembleImage(String imageUrl, Long imageWidth, Long imageHeight, String facetName, Long facetLayer) {
        this.imageUrl = imageUrl;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.facetName = facetName;
        this.facetLayer = facetLayer;
    }

    @Override
    public String toString() {
        return "AssembleImage{" +
                "imageId=" + imageId +
                ", imageContent=" + Arrays.toString(imageContent) +
                ", imageUrl='" + imageUrl + '\'' +
                ", imageWidth=" + imageWidth +
                ", imageHeight=" + imageHeight +
                ", imageScratchTime='" + imageScratchTime + '\'' +
                ", imageApi='" + imageApi + '\'' +
                ", facetId=" + facetId +
                ", facetName='" + facetName + '\'' +
                ", facetLayer=" + facetLayer +
                ", topicId=" + topicId +
                '}';
    }

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public byte[] getImageContent() {
        return imageContent;
    }

    public void setImageContent(byte[] imageContent) {
        this.imageContent = imageContent;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(Long imageWidth) {
        this.imageWidth = imageWidth;
    }

    public Long getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(Long imageHeight) {
        this.imageHeight = imageHeight;
    }

    public String getImageScratchTime() {
        return imageScratchTime;
    }

    public void setImageScratchTime(String imageScratchTime) {
        this.imageScratchTime = imageScratchTime;
    }

    public String getImageApi() {
        return imageApi;
    }

    public void setImageApi(String imageApi) {
        this.imageApi = imageApi;
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

    public AssembleImage() {

    }

    public AssembleImage(byte[] imageContent, String imageUrl, Long imageWidth, Long imageHeight, String imageScratchTime, String imageApi, Long facetId, String facetName, Long facetLayer, Long topicId) {

        this.imageContent = imageContent;
        this.imageUrl = imageUrl;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.imageScratchTime = imageScratchTime;
        this.imageApi = imageApi;
        this.facetId = facetId;
        this.facetName = facetName;
        this.facetLayer = facetLayer;
        this.topicId = topicId;
    }
}
