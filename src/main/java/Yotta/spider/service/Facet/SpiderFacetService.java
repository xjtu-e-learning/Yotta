package Yotta.spider.service.Facet;

import Yotta.spider.domain.*;
import Yotta.spider.repository.*;
import Yotta.spider.service.utils.DownloaderService;
import Yotta.spider.service.utils.ExtractContentService;
import com.spreada.utils.chinese.ZHConverter;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 中文维基的分面及其分面关系：分面及其分面关系的 “爬取、增、删、改、查”
 * Created by yuanhao on 2017/5/3.
 */
@RestController
@RequestMapping("/spiderFacetService")
public class SpiderFacetService {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
    private static ZHConverter converter = ZHConverter.getInstance(ZHConverter.SIMPLIFIED);// 转化为简体中文
    private static DownloaderService downloaderService = new DownloaderService();
    private static ExtractContentService extractContentService = new ExtractContentService();
    private static SpiderFacetService spiderFacetService = new SpiderFacetService();

    @Autowired
    private DomainRepository domainRepository;
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private FacetRepository facetRepository;
    @Autowired
    private FacetRelationRepository facetRelationRepository;


    /**
     * 按照领域爬取所有主题的分面
     * @param domainId 领域Id
     * @throws Exception
     */
    @GetMapping(value = "/storeFacetAndRelationByDomainId")
    public void storeFacetAndRelationByDomainId(@RequestParam(value = "domainId", defaultValue = "1") Long domainId) throws Exception {
        List<Topic> topicList = topicRepository.findByDomainId(domainId);
        for(int i = 0; i < topicList.size(); i++){
            Topic topic = topicList.get(i);
            storeFacetAndRelationByTopic(topic);
        }
    }

    /**
     * 按照主题爬取该主题的所有分面
     * @param topic 主题
     * @throws Exception
     */
    @GetMapping(value = "/storeFacetAndRelationByTopic")
    public List<Object> storeFacetAndRelationByTopic(Topic topic) throws Exception {
        Long topicId = topic.getTopicId();
        String topicName = topic.getTopicName();
        String topicUrl = topic.getTopicUrl();

        List<Object> result = new ArrayList<>(); // 返回结果
        // selenium解析网页
        topicUrl = extractContentService.dealUrl(topicUrl);
        String topicHtml = downloaderService.seleniumWikiCNIE(topicUrl);
        Document doc = downloaderService.parseHtmlText(topicHtml);
        /**
         * 判断该主题的信息是不是在所有表格中已经存在
         * 只要有一个不存在就需要再次爬取（再次模拟加载浏览器）
         */
        // 获取并存储所有分面信息Facet
        if(facetRepository.findByTopicId(topicId).size() == 0){
            List<Facet> facetList = storeFacet(doc, topicId);
            result.add(facetList);
            logger.info("主题：" + topicName + " 的分面爬取结束");
        } else {
            logger.info("主题：" + topicName + " 的分面已经存在，无需爬取存储");
        }
        // 获取并存储各级分面之间的关系FacetRelation
        if(facetRelationRepository.findByTopicId(topicId).size() == 0){
            List<FacetRelation> facetRelationList = storeFacetRelation(doc, topicId);
            result.add(facetRelationList);
            logger.info("主题：" + topicName + " 的分面关系爬取结束");
        } else {
            logger.info("主题：" + topicName + " 的分面关系已经存在，无需爬取存储");
        }
        return result;
    }

    /**
     * 保存一个主题的所有分面及其分面级数
     * @param doc
     * @param topicId 主题Id
     * @return
     */
    public List<Facet> storeFacet(Document doc, Long topicId){
        List<Facet> facetList = new ArrayList<>();
        List<String> firstTitle = extractContentService.getFirstTitle(doc);
        List<String> secondTitle = extractContentService.getSecondTitle(doc);
        List<String> thirdTitle = extractContentService.getThirdTitle(doc);

        /**
         * 判断条件和内容函数保持一致
         * facet中的分面与spider和assemble表格保持一致
         */
        Elements mainContents = doc.select("div#mw-content-text").select("span.mw-headline");
        if(mainContents.size() == 0){ // 存在没有分面的情况
            String facetName = "摘要";
            Long facetLayer = 1L;
            Facet facetSimple = new Facet(facetName, facetLayer);
            facetList.add(facetSimple);
        } else {
            String facetNameZhai = "摘要";
            Long facetLayerZhai = 1L;
            Facet facetSimpleZhai = new Facet(facetNameZhai, facetLayerZhai);
            facetList.add(facetSimpleZhai);
            /**
             * 保存一级分面名及其分面级数
             */
            for(int i = 0; i < firstTitle.size(); i++){
                String facetName = firstTitle.get(i);
                Long facetLayer = 1L;
                Facet facetSimple = new Facet(facetName, facetLayer);
                facetList.add(facetSimple);
            }
            /**
             * 保存二级分面名及其分面级数
             */
            for(int i = 0; i < secondTitle.size(); i++){
                String facetName = secondTitle.get(i);
                Long facetLayer = 2L;
                Facet facetSimple = new Facet(facetName, facetLayer);
                facetList.add(facetSimple);
            }
            /**
             * 保存三级分面名及其分面级数
             */
            for(int i = 0; i < thirdTitle.size(); i++){
                String facetName = thirdTitle.get(i);
                Long facetLayer = 3L;
                Facet facetSimple = new Facet(facetName, facetLayer);
                facetList.add(facetSimple);
            }
        }
        /**
         * 补全主题Id信息，并存储分面数据到数据库
         */
        for (int i = 0; i < facetList.size(); i++) {
            Facet facet = facetList.get(i);
            facet.setTopicId(topicId); // 设置主题ID
            facetRepository.save(facet); // 保存分面数据
        }
        return facetList;
    }

    /**
     * 获取各级分面父子对应关系
     * @param doc
     * @return
     * @return
     */
    public List<FacetRelation> storeFacetRelation(Document doc, Long topicId) {
        LinkedList<String> indexs = new LinkedList<>();// 标题前面的下标
        LinkedList<String> facets = new LinkedList<>();// 各级标题的名字
        List<FacetRelation> facetRelationList = new ArrayList<>();

        try {

            /**
             * 获取标题
             */
            Elements titles = doc.select("div#toc").select("li");
            logger.info(titles.size() + "");
            if(titles.size()!=0){
                for(int i = 0; i < titles.size(); i++){
                    String index = titles.get(i).child(0).child(0).text();
                    String text = titles.get(i).child(0).child(1).text();
                    text = converter.convert(text);
                    logger.info(index + " " + text);
                    indexs.add(index);
                    facets.add(text);
                }

                /**
                 * 将二级/三级标题全部匹配到对应的父标题
                 */
                logger.info("--------------------------------------------");
                for(int i = 0; i < indexs.size(); i++){
                    String index = indexs.get(i);
                    if(index.lastIndexOf(".") == 1){ // 二级分面
                        logger.info("二级标题");
                        String facetSecond = facets.get(i);
                        for(int j = i - 1; j >= 0; j--){
                            String index2 = indexs.get(j);
                            if(index2.lastIndexOf(".") == -1){
                                String facetOne = facets.get(j);
                                FacetRelation facetRelation = new FacetRelation(facetOne, 1L, facetSecond, 2L);
                                facetRelationList.add(facetRelation);
                                break;
                            }
                        }
                    }
                    else if (index.lastIndexOf(".") == 3) { // 三级分面
                        logger.info("三级标题");
                        String facetThird = facets.get(i);
                        for(int j = i - 1; j >= 0; j--){
                            String index2 = indexs.get(j);
                            if(index2.lastIndexOf(".") == 1){
                                String facetSecond = facets.get(j);
                                FacetRelation facetRelation = new FacetRelation(facetSecond, 2L, facetThird, 3L);
                                facetRelationList.add(facetRelation);
                                break;
                            }
                        }
                    }
                }

            } else {
                logger.info("该主题没有目录，不是目录结构，直接爬取 -->摘要<-- 信息");
            }

        } catch (Exception e) {
            logger.info("this is not a normal page...");
        }
        /**
         * 补全主题Id信息，并存储分面关系数据到数据库
         */
        for (int i = 0; i < facetRelationList.size(); i++) {
            FacetRelation facetRelation = facetRelationList.get(i);
            facetRelation.setTopicId(topicId); // 设置主题ID
            facetRelationRepository.save(facetRelation);  // 保存分面关系数据到数据库
        }
        return facetRelationList;
    }

}
