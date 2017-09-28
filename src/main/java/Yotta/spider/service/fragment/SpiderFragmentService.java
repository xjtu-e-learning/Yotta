package Yotta.spider.service.fragment;

import Yotta.common.Config;
import Yotta.spider.domain.*;
import Yotta.spider.repository.*;
import Yotta.spider.service.utils.DownloaderService;
import Yotta.spider.service.utils.ExtractContentService;
import Yotta.utils.TimeUtil;
import com.spreada.utils.chinese.ZHConverter;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 中文维基的文本和图片：碎片的 “爬取、增、删、改、查”
 * Created by yuanhao on 2017/5/3.
 */
@RestController
@RequestMapping("SpiderFragmentService")
public class SpiderFragmentService {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
    private static ZHConverter converter = ZHConverter.getInstance(ZHConverter.SIMPLIFIED);// 转化为简体中文
    private static DownloaderService downloaderService = new DownloaderService();
    private static ExtractContentService extractContentService = new ExtractContentService();
    private static SpiderFragmentService spiderFragmentService = new SpiderFragmentService();

    @Autowired
    private DomainRepository domainRepository;
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private FacetRepository facetRepository;
    @Autowired
    private FacetRelationRepository facetRelationRepository;
    @Autowired
    private AssembleTextRepository assembleTextRepository;
    @Autowired
    private AssembleImageRepository assembleImageRepository;


    /**
     * 按照领域爬取所有主题的碎片
     * @param domainId 领域Id
     * @throws Exception
     */
    public void storeFragmentByDomainId(@RequestParam(value = "domainId", defaultValue = "1") Long domainId) throws Exception {
        List<Topic> topicList = topicRepository.findByDomainId(domainId);
        for(int i = 0; i < topicList.size(); i++){
            Topic topic = topicList.get(i);
            storeFragmentByTopic(topic);
        }
    }


    /**
     * 按照主题爬取所有碎片
     * @param topic 主题
     * @throws Exception
     */
    @GetMapping("/storeFragmentByTopic")
    public List<Object> storeFragmentByTopic(Topic topic) throws Exception {
        Long topicId = topic.getTopicId();
        String topicName = topic.getTopicName();
        String topicUrl = topic.getTopicUrl();

        List<Object> result = new ArrayList<>(); // 返回结果
        /**
         * 判断该主题的信息是不是在所有表格中已经存在
         * 只要有一个不存在就需要再次爬取（再次模拟加载浏览器）
         */
        if(assembleTextRepository.findByTopicId(topicId).size() == 0 || assembleImageRepository.findByTopicId(topicId).size() == 0){
            // selenium解析网页
            topicUrl = extractContentService.dealUrl(topicUrl);
            String topicHtml = downloaderService.seleniumWikiCNIE(topicUrl);
            Document doc = downloaderService.parseHtmlText(topicHtml);
            // 获取并存储各级分面之间的关系FacetRelation
            List<FacetRelation> facetRelationList = getFacetRelation(doc);
            boolean flagFirst = true; // 一级标题
            boolean flagSecond = true; // 二级标题
            boolean flagThird = true; // 三级标题
            String postTime = extractContentService.getPostTime(doc);
            // 一级分面下如果有二级分面，那么一级分面应该没有碎片文本
            List<AssembleText> assembleTextList = getAssembleTextAd(doc, flagFirst, flagSecond, flagThird, facetRelationList);
            List<AssembleImage> assembleImageList = getAssembleImageAd(doc, flagFirst, flagSecond, flagThird, facetRelationList);
//            logger.error(assembleTextList.size()+"");
//            logger.error(assembleImageList.size()+"");
            // 存储前进行判断，已经存在的不用存储
            if(assembleTextRepository.findByTopicId(topicId).size() == 0){
                storeAssembleText(topicId, topicUrl, postTime, assembleTextList);
                result.add(assembleTextList);
                logger.info("主题：" + topicName + " 的文本碎片爬取结束");
            } else {
                logger.info("主题：" + topicName + " 的文本碎片已经存在，无需爬取存储");
            }
            // 存储前进行判断，已经存在的不用存储
            if(assembleImageRepository.findByTopicId(topicId).size() == 0){
                storeAssembleImage(topicId, topicUrl, assembleImageList);
                result.add(assembleImageList);
                logger.info("主题：" + topicName + " 的图片碎片爬取结束");
            } else {
                logger.info("主题：" + topicName + " 的图片碎片已经存在，无需爬取存储");
            }
        } else {
            logger.info("主题：" + topicName + " 的文本碎片和图片碎片已经存在，无需爬取存储");
        }
        return result;
    }




    /**
     * 获取各级分面父子对应关系
     * @param doc
     * @return
     * @return
     */
    public List<FacetRelation> getFacetRelation(Document doc){
        LinkedList<String> indexs = new LinkedList<>();// 标题前面的下标
        LinkedList<String> facets = new LinkedList<>();// 各级标题的名字
        List<FacetRelation> facetRelationList = new ArrayList<>();

        try {

            /**
             * 获取标题
             */
            Elements titles = doc.select("div#toc").select("li");
            if(titles.size() != 0){
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

        return facetRelationList;
    }

    /**
     * 保存所有信息，如果某个分面含有子分面，那么这个分面下面应该没有碎片
     * 1. 三级分面及其文本碎片
     * 2. 有子分面的父分面没有文本碎片
     * 3. 一个段落作为一个碎片
     * @param domain
     * @param topic
     * @param doc
     * @param flagFirst
     * @param flagSecond
     * @param flagThird
     * @return
     */
    public List<AssembleText> getAssembleTextAd(Document doc, boolean flagFirst, boolean flagSecond, boolean flagThird, List<FacetRelation> facetRelationList){
        List<AssembleText> assembleTextResultList = new ArrayList<>();
        List<AssembleText> assembleTextList = getAssembleText(doc, flagFirst, flagSecond, flagThird);
        logger.error("处理前数据量大小：" + assembleTextList.size() + "");
        for(int i = 0; i < assembleTextList.size(); i++){
            AssembleText assemble = assembleTextList.get(i);
            /**
             * 判断该文本碎片对应的分面是否包含子分面
             * 判断该文本碎片为那个不需要的文本
             * 去除长度很短且无意义的文本碎片
             */
            Boolean exist = false;
            for (int j = 0; j < facetRelationList.size(); j++) {
                String parentFacetName = facetRelationList.get(j).getParentFacetName();
                Long parentFacetLayer = facetRelationList.get(j).getParentFacetLayer();
                if (parentFacetName.equals(assemble.getFacetName()) && parentFacetLayer.equals(assemble.getFacetLayer())) {
                    exist = true;
                }
            }
            Boolean existBadText = judgeBadText(assemble);
            Boolean lenBoolean = extractContentService.getContentLen(assemble.getTextContent()) > Config.CONTENTLENGTH;
            // 满足条件的文本碎片
            if (!exist && !existBadText && lenBoolean) {
                assembleTextResultList.add(assemble);
            }
        }
        return assembleTextResultList;
    }

    /**
     * 将从"摘要"到各级标题的所有分面内容全部存到一起
     * 1. 三级分面及其文本碎片
     * 2. 有子分面的父分面没有文本碎片
     * 3. 一个段落作为一个碎片
     * @param doc
     * @param flagFirst  一级标题标志位
     * @param flagSecond  二级标题标志位
     * @param flagThird  三级标题标志位
     * @return
     */
    public List<AssembleText> getAssembleText(Document doc, boolean flagFirst, boolean flagSecond, boolean flagThird){

        List<AssembleText> assembleList = new ArrayList<>();

        Elements mainContents = doc.select("div#mw-content-text").select("span.mw-headline");
        if(mainContents.size() == 0){
            /**
             * 网页全部内容
             */
            List<AssembleText> specialContent = extractContentService.getSpecialContent(doc); // 没有目录栏的词条信息
            assembleList.addAll(specialContent);
        } else {
            /**
             * 摘要信息
             */
            List<AssembleText> summaryContent = extractContentService.getSummary(doc); // 摘要内容
            assembleList.addAll(summaryContent);
            /**
             * flagFirst 为 true，保留一级分面数据
             */
            if(flagFirst){
                LinkedList<String> firstTitle = extractContentService.getFirstTitle(doc);
                if(firstTitle.size() != 0){
                    List<AssembleText> firstContent = extractContentService.getFirstContent(doc); // 一级分面内容
                    assembleList.addAll(firstContent);
                }
            }
            /**
             * flagSecond 为 true，保留二级分面数据
             */
            if(flagSecond){
                LinkedList<String> secondTitle = extractContentService.getSecondTitle(doc);
                if(secondTitle.size() != 0){
                    List<AssembleText> secondContent = extractContentService.getSecondContent(doc); // 二级分面内容
                    assembleList.addAll(secondContent);
                }
            }
            /**
             * flagThird 为 true，保留三级分面数据
             */
            if(flagThird){
                LinkedList<String> thirdTitle = extractContentService.getThirdTitle(doc);
                if(thirdTitle.size() != 0){
                    List<AssembleText> thirdContent = extractContentService.getThirdContent(doc); // 三级分面内容
                    assembleList.addAll(thirdContent);
                }
            }
        }
        return assembleList;
    }

    /**
     * 补全并存储文本信息
     * @param topicId
     * @param topicUrl
     * @param postTime
     * @param assembleTextList
     */
    public void storeAssembleText(Long topicId, String topicUrl, String postTime, List<AssembleText> assembleTextList){
        for (int i = 0; i < assembleTextList.size(); i++) {
            AssembleText assembleText = assembleTextList.get(i);
            assembleText.setTopicId(topicId);
            assembleText.setTextUrl(topicUrl);
            assembleText.setTextPostTime(postTime);
            assembleText.setTextScratchTime(TimeUtil.getSystemTime());
            assembleText.setFacetId(facetRepository.findByTopicIdAndFacetLayerAndFacetName(topicId, assembleText.getFacetLayer(), assembleText.getFacetName()).getFacetId());
            logger.info(assembleText.toString());
            assembleTextRepository.save(assembleText);
        }
    }

    /**
     * 判断分面内容是否包含最后一个多余的链接
     * @return
     */
    public static Boolean judgeBadText(AssembleText assemble){
        Boolean exist = false;
        String facetContent = assemble.getTextContent();
        String badTxt1 = "<img src=";
        String badTxt2 = "本条目没有列出任何参考或来源";
        String badTxt3 = "目标页面不存在";
        String badTxt4 = "本条目存在以下问题";
        String badTxt5 = "本条目需要扩充";
        String badTxt6 = "[隐藏] 查 论 编";
        if(facetContent.contains(badTxt1) || facetContent.contains(badTxt2) || facetContent.contains(badTxt3)
                || facetContent.contains(badTxt4) || facetContent.contains(badTxt5) || facetContent.contains(badTxt6)){
            exist = true;
        }
        return exist;
    }

    /**
     * 保存所有图片信息，如果某个分面含有子分面，那么这个分面下面应该没有图片碎片（目前只考虑一级分面和二级分面）
     * 1. 三级分面及其图片碎片
     * 2. 有子分面的父分面没有图片碎片
     * 3. 图片数量较少，一般可能为空
     * @param doc
     * @param flagFirst
     * @param flagSecond
     * @param flagThird
     * @param facetRelationList
     * @return
     */
    public List<AssembleImage> getAssembleImageAd(Document doc, boolean flagFirst, boolean flagSecond, boolean flagThird, List<FacetRelation> facetRelationList){
        List<AssembleImage> assembleResultList = new ArrayList<>();
        List<AssembleImage> assembleImageList = getAssembleImage(doc, flagFirst, flagSecond, flagThird);
        for(int i = 0; i < assembleImageList.size(); i++){
            AssembleImage assembleImage = assembleImageList.get(i);
            /**
             * 判断该图片对应的分面是否包含子分面
             * 判断图片是否为图标图片（不需要）
             */
            Boolean exist = false;
            for (int j = 0; j < facetRelationList.size(); j++) {
                String parentFacetName = facetRelationList.get(j).getParentFacetName();
                Long parentFacetLayer = facetRelationList.get(j).getParentFacetLayer();
                if (parentFacetName.equals(assembleImage.getFacetName()) && parentFacetLayer.equals(assembleImage.getFacetLayer())) {
                    exist = true;
                }
            }
            Boolean existUselessImg = judgeBadImage(assembleImage);
            /**
             * 保存满足条件的图片链接
             */
            if (!exist && !existUselessImg) {
                assembleResultList.add(assembleImage);
            }
        }
        return assembleResultList;
    }

    /**
     * 将从"摘要"到各级标题的所有分面图片内容全部存到一起
     * 1. 三级分面及其图片碎片
     * 2. 每一级分面都有图片碎片
     * 3. 图片数量较少，一般可能为空
     * @param doc
     * @param flagFirst  一级标题标志位
     * @param flagSecond  二级标题标志位
     * @param flagThird  三级标题标志位
     * @return
     */
    public List<AssembleImage> getAssembleImage(Document doc, boolean flagFirst, boolean flagSecond, boolean flagThird){

        List<AssembleImage> assembleImageList = new ArrayList<>();

        Elements images = doc.select("div#mw-content-text").select("a").select("img");
        if(images.size() == 0){
            logger.info("this page doesn't have any images...");
        } else {

            /**
             * 摘要信息
             */
            List<AssembleImage> summaryImage = extractContentService.getSummaryImage(doc); // 摘要图片
            assembleImageList.addAll(summaryImage);

            /**
             * flagFirst 为 true，保留一级分面数据
             */
            if(flagFirst){
                LinkedList<String> firstTitle = extractContentService.getFirstTitle(doc);
                if(firstTitle.size() != 0){
                    List<AssembleImage> firstContent = extractContentService.getFirstImage(doc); // 一级分面图片
                    assembleImageList.addAll(firstContent);
                }
            }

            /**
             * flagSecond 为 true，保留二级分面数据
             */
            if(flagSecond){
                LinkedList<String> secondTitle = extractContentService.getSecondTitle(doc);
                if(secondTitle.size() != 0){
                    List<AssembleImage> secondContent = extractContentService.getSecondImage(doc); // 二级分面图片
                    assembleImageList.addAll(secondContent);
                }
            }

            /**
             * flagThird 为 true，保留三级分面数据
             */
            if(flagThird){
                LinkedList<String> thirdTitle = extractContentService.getThirdTitle(doc);
                if(thirdTitle.size() != 0){
                    List<AssembleImage> thirdContent = extractContentService.getThirdImage(doc); // 三级分面图片
                    assembleImageList.addAll(thirdContent);
                }
            }

        }
        return assembleImageList;
    }

    /**
     * 补全并存储图片信息
     * @param topicId
     * @param topicUrl
     * @param assembleImageList
     */
    public void storeAssembleImage(Long topicId, String topicUrl, List<AssembleImage> assembleImageList) {
        for (int i = 0; i < assembleImageList.size(); i++) {
            AssembleImage assembleImage = assembleImageList.get(i);
            assembleImage.setTopicId(topicId);
            byte[] imageContent = null;
            try {
                imageContent = extractContentService.getImageFromNetByUrl(topicUrl);
                assembleImage.setImageContent(imageContent); // 设置图片内容为二进制流
                assembleImage.setImageScratchTime(TimeUtil.getSystemTime());
                assembleImage.setFacetId(facetRepository.findByTopicIdAndFacetLayerAndFacetName(topicId, assembleImage.getFacetLayer(), assembleImage.getFacetName()).getFacetId());
                logger.info(assembleImage.toString());
                assembleImageRepository.save(assembleImage);
            } catch (Exception e) {
                logger.error("图片内容为null，不进行存储。。。" + e);
            }
        }
    }


    /**
     * 判断是否为不需要的图片链接
     * @return
     */
    public static Boolean judgeBadImage(AssembleImage assembleImage){
        Boolean exist = false;
        String imgUrl = assembleImage.getImageUrl();
        String badImgUrl1 = "//upload.wikimedia.org/wikipedia/commons/thumb/2/2d/Stub_W.svg/40px-Stub_W.svg.png";
        String badImgUrl2 = "//upload.wikimedia.org/wikipedia/commons/thumb/4/4e/Tango-nosources.svg/45px-Tango-nosources.svg.png";
        String badImgUrl3 = "//upload.wikimedia.org/wikipedia/commons/thumb/1/1b/Ambox_question.svg/40px-Ambox_question.svg.png";
        String badImgUrl4 = "//upload.wikimedia.org/wikipedia/commons/thumb/b/be/PC_template.svg/25px-PC_template.svg.png";
        String badImgUrl5 = "//upload.wikimedia.org/wikipedia/commons/thumb/a/aa/Merge-arrow.svg/50px-Merge-arrow.svg.png";
        String badImgUrl6 = "//upload.wikimedia.org/wikipedia/commons/thumb/c/c9/Portal.svg/32px-Portal.svg.png";
        String badImgUrl7 = "//upload.wikimedia.org/wikipedia/commons/thumb/2/2a/Antistub.svg/44px-Antistub.svg.png";
        String badImgUrl8 = "//upload.wikimedia.org/wikipedia/commons/thumb/d/d7/Ambox_spelling.svg/48px-Ambox_spelling.svg.png";
        String badImgUrl9 = "//upload.wikimedia.org/wikipedia/commons/thumb/e/e1/Ambox_wikify.svg/40px-Ambox_wikify.svg.png";
        String badImgUrl10 = "//upload.wikimedia.org/wikipedia/commons/thumb/1/17/Formal_logic_template.svg/23px-Formal_logic_template.svg.png";
        String badImgUrl11 = "//upload.wikimedia.org/wikipedia/commons/thumb/0/0f/Mergefrom.svg/50px-Mergefrom.svg.png";
        String badImgUrl12 = "//upload.wikimedia.org/wikipedia/commons/thumb/b/be/Gamepad.svg/32px-Gamepad.svg.png";
        String badImgUrl13 = "//upload.wikimedia.org/wikipedia/commons/thumb/a/a4/Text_document_with_red_question_mark.svg/40px-Text_document_with_red_question_mark.svg.png";
        if(imgUrl.equals(badImgUrl1) || imgUrl.equals(badImgUrl2) || imgUrl.equals(badImgUrl3) ||
                imgUrl.equals(badImgUrl4) || imgUrl.equals(badImgUrl5) || imgUrl.equals(badImgUrl6) ||
                imgUrl.equals(badImgUrl7) || imgUrl.equals(badImgUrl8) || imgUrl.equals(badImgUrl9) ||
                imgUrl.equals(badImgUrl10) || imgUrl.equals(badImgUrl11) || imgUrl.equals(badImgUrl12) ||
                imgUrl.equals(badImgUrl13)){
            exist = true;
        }
        return exist;
    }

}
