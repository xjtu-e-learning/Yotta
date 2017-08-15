package Yotta.spider.service;

import Yotta.common.Config;
import Yotta.spider.domain.*;
import Yotta.spider.repository.*;
import Yotta.utils.TimeUtil;
import com.spreada.utils.chinese.ZHConverter;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 爬取中文维基百科知识主题对应的分面及其碎片信息，包括分面之间的关系及碎片与分面之间的映射关系
 * Created by yuanhao on 2017/5/3.
 */
@RestController("/spiderWikiContent")
public class SpiderContentService {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
    private static ZHConverter converter = ZHConverter.getInstance(ZHConverter.SIMPLIFIED);// 转化为简体中文
    private static DownloaderService downloaderService = new DownloaderService();
    private static ExtractContentService extractContentService = new ExtractContentService();
    private static SpiderContentService spiderContentService = new SpiderContentService();

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
     * 将领域术语网页内容按照分面存储到数据库（所有课程）
     * @param classId
     * @throws Exception
     */
    @GetMapping(value = "/wikiContent")
    public void storeByClassID(@RequestParam(value = "classId", defaultValue = "1") Long classId) throws Exception {
        Domain domain = domainRepository.findByDomainId(classId);
        logger.info(domain.getDomainName());
        storeAllFacetAndContent(domain);
    }


    /**
     * 将领域术语网页内容按照分面存储到数据库
     * @throws Exception
     */
    public void storeAllFacetAndContent(Domain domain) throws Exception{
        String className = domain.getDomainName();
        Long classId = domain.getDomainId();

        /**
         * 读取数据库表格domain_topic，得到领域术语
         */
//		String domain = "数据结构";
        List<Topic> topicList = topicRepository.findByDomainId(classId);
        for(int i = 0; i < topicList.size(); i++){
            Topic topic = topicList.get(i);
            Long topicId = topic.getTopicId();
            String topicName = topic.getTopicName();
            String topicUrl = topic.getTopicUrl();

            /**
             * 判断数据是否已经存在
             */
            Boolean existFacet = facetRepository.findByTopicId(topicId).size() > 0;
            Boolean existFacetRelation = facetRelationRepository.findByTopicId(topicId).size() > 0;
            Boolean existAssembleText = assembleTextRepository.findByTopicId(topicId).size() > 0;
            Boolean existAssembleImage = assembleImageRepository.findByTopicId(topicId).size() > 0;

            /**
             * 判断该主题的信息是不是在所有表格中已经存在
             * 只要有一个不存在就需要再次爬取（再次模拟加载浏览器）
             */
            if(!existFacet || !existFacetRelation || !existAssembleText || !existAssembleImage){

                /**
                 * selenium解析网页
                 */
                topicUrl = extractContentService.dealUrl(topicUrl);
                String topicHtml = downloaderService.seleniumWikiCNIE(topicUrl);
                Document doc = downloaderService.parseHtmlText(topicHtml);

                /**
                 * 获取并存储所有分面信息Facet
                 */
                List<Facet> facetList = getAllFacet(doc);
                if(!existFacet){
                    storeFacet(topicId, facetList);
                    logger.info("domain : " + domain + ", topicName : " + topicName + " ---> store in facet...");
                } else {
                    logger.info("domain : " + domain + ", topicName : " + topicName + " ---> is already existing in facet...");
                }

                /**
                 * 获取并存储各级分面之间的关系FacetRelation
                 */
                List<FacetRelation> facetRelationList = getFacetRelation(doc);
                if(!existFacetRelation){
                    storeFacetRelation(topicId, facetRelationList);
                    logger.info("domain : " + domain + ", topicName : " + topicName + " ---> store in facet_relation...");
                } else {
                    logger.info("domain : " + domain + ", topicName : " + topicName + " ---> is already existing in facet_relation...");
                }

                /**
                 * 获得网页所有内容
                 */
                boolean flagFirst = true; // 一级标题
                boolean flagSecond = true; // 二级标题
                boolean flagThird = true; // 三级标题
                String postTime = extractContentService.getPostTime(doc);
                // 获取所有分面及其文本
//				List<Assemble> assembleList = CrawlerContentDAO.getAllContent(doc, flagFirst, flagSecond, flagThird);
//				List<AssembleImage> assembleImageList = CrawlerContentDAO.getAllImage(domain, flagFirst, flagSecond, flagThird);
                // 一级分面下如果有二级分面，那么一级分面应该没有碎片文本
                List<AssembleText> assembleList = getAllContentNew(doc, flagFirst, flagSecond, flagThird, facetRelationList);
                // 一级分面下如果有二级分面，那么一级分面应该没有图片文本
                List<AssembleImage> assembleImageList = getAllImageNew(doc, flagFirst, flagSecond, flagThird, facetRelationList);

                /**
                 * 获得Assemble_text
                 * 存储前进行判断，已经存在的不用存储
                 */
                if(!existAssembleText){
                    storeAssembleText(topicId, topicUrl, postTime, assembleList);
                    logger.info("domain : " + domain + ", topicName : " + topicName + " ---> store in assemble_text...");
                } else {
                    logger.info("domain : " + domain + ", topicName : " + topicName + " ---> is already existing in assemble_text...");
                }

                /**
                 * 获得Assemble_image
                 * 存储前进行判断，已经存在的不用存储
                 */
                if(!existAssembleImage){
                    storeAssembleImage(topicId, topicUrl, assembleImageList);
                    logger.info("domain : " + domain + ", topicName : " + topicName + " ---> store in assemble_image...");
                } else {
                    logger.info("domain : " + domain + ", topicName : " + topicName + " ---> is already existing in assemble_image...");
                }

            } else {
                logger.info("domain : " + domain + ", topicName : " + topicName
                        + " ---> is already existing in facet, spider_text, assemble_text, spider_image, assemble_image...");
            }

        }

    }


    /**
     * 补全并存储分面信息
     * @param topicId
     * @param facetList
     */
    public void storeFacet(Long topicId, List<Facet> facetList){
        for (int i = 0; i < facetList.size(); i++) {
            Facet facet = facetList.get(i);
            facet.setTopicId(topicId); // 设置主题ID
            facetRepository.save(facet);
        }
    }

    /**
     * 补全并存储分面关系信息
     * @param topicId
     * @param facetRelationList
     */
    public void storeFacetRelation(Long topicId, List<FacetRelation> facetRelationList){
        for (int i = 0; i < facetRelationList.size(); i++) {
            FacetRelation facetRelation = facetRelationList.get(i);
            facetRelation.setTopicId(topicId); // 设置主题ID
            facetRelationRepository.save(facetRelation);
        }
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
            assembleTextRepository.save(assembleText);
        }
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
                assembleImageRepository.save(assembleImage);
            } catch (Exception e) {
                logger.error("图片内容为null，不进行存储。。。" + e);
            }
        }
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

        return facetRelationList;
    }

    /**
     * 得到一个主题的所有分面及其分面级数
     * 1. 数据结构为: FacetSimple
     * @param doc
     * @return
     */
    public List<Facet> getAllFacet(Document doc){
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

        return facetList;

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
    public List<AssembleText> getAllContent(Document doc, boolean flagFirst, boolean flagSecond, boolean flagThird){

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
    public List<AssembleText> getAllContentNew(Document doc, boolean flagFirst, boolean flagSecond, boolean flagThird, List<FacetRelation> facetRelationList){
        List<AssembleText> assembleResultList = new ArrayList<>();
        List<AssembleText> assembleList = getAllContent(doc, flagFirst, flagSecond, flagThird);
        for(int i = 0; i < assembleList.size(); i++){
            AssembleText assemble = assembleList.get(i);
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
            Boolean existImg = judgeBadText(assemble);
            Boolean lenBoolean = extractContentService.getContentLen(assemble.getTextContent()) > Config.CONTENTLENGTH;
            /**
             * 保存满足条件的图片链接
             */
            if (!exist && !existImg && lenBoolean) {
                assembleResultList.add(assemble);
            }
        }
        return assembleResultList;
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
    public List<AssembleImage> getAllImage(Document doc, boolean flagFirst, boolean flagSecond, boolean flagThird){

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
    public List<AssembleImage> getAllImageNew(Document doc, boolean flagFirst, boolean flagSecond, boolean flagThird, List<FacetRelation> facetRelationList){
        List<AssembleImage> assembleResultList = new ArrayList<>();
        List<AssembleImage> assembleImageList = getAllImage(doc, flagFirst, flagSecond, flagThird);
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





//    public static void main(String[] args) throws Exception {
//        test();
//    }
//
//    /**
//     *  单个中文维基页面解析测试程序
//     * @throws Exception
//     */
//    public static void test() throws Exception{
//
//        /**
//         * 设置解析参数
//         */
//        String topicUrl = "https://zh.wikipedia.org/wiki/%E9%82%BB%E6%8E%A5%E7%9F%A9%E9%98%B5";
////		String topicUrl = "https://zh.wikipedia.org/wiki/%E6%95%B0%E6%8D%AE%E5%BA%93";
////		String topicUrl = "https://zh.wikipedia.org/wiki/%E9%93%BE%E8%A1%A8";
////		String topicUrl = "https://zh.wikipedia.org/wiki/%E8%B7%B3%E8%B7%83%E5%88%97%E8%A1%A8";
////		String topicUrl = "https://zh.wikipedia.org/wiki/%E6%95%B0%E6%8D%AE%E7%BB%93%E6%9E%84%E4%B8%8E%E7%AE%97%E6%B3%95%E5%88%97%E8%A1%A8";
//        String topicHtml = downloaderService.seleniumWikiCNIE(topicUrl);
//        Document doc = downloaderService.parseHtmlText(topicHtml);
//
//        /**
//         * 测试解析小程序
//         */
//        List<FacetRelation> facetRelationList = spiderContentService.getFacetRelation(doc);
//
//        /**
//         * 解析所有内容
//         */
////        spiderContentService.getAllContent(doc, false, false, false); // 只有summary内容
////        spiderContentService.getAllContent(doc, true, false, false); // summary内容 + 一级标题内容
////        spiderContentService.getAllContent(doc, true, true, false); // summary内容 + 一级和二级标题内容
//        spiderContentService.getAllContent(doc, true, true, true); // summary内容 + 一级/二级/三级标题内容
//
//        /**
//         * 解析图片内容
//         */
//        spiderContentService.getAllImage(doc, true, true, true);
//    }




//	/**
//	 * 二级/三级标题转化为一级标题
//	 * @param title
//	 * @param relation
//	 * @return
//	 */
//	public static String titleToFacet(String title, HashMap<String, String> relation){
//		String facetName = title;
//		for(Entry<String, String> entry : relation.entrySet()){
//			String tit = entry.getKey();
//			String facet = entry.getValue();
//			if(title.equals(tit)){
//				facetName = facet;
//				break;
//			}
//		}
//		return facetName;
//	}

}
