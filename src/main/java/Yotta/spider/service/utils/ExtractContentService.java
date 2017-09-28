package Yotta.spider.service.utils;

import Yotta.spider.domain.AssembleImage;
import Yotta.spider.domain.AssembleText;
import com.spreada.utils.chinese.ZHConverter;
import org.apache.commons.io.IOUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * 解析中文维基百科页面
 * 1. 现在返回的Assemble的List对象集合
 * 2. 三级分面信息
 * 3. 每一级分面有多个碎片，按照一个p标签作为一个碎片文本
 * Created by yuanhao on 2017/5/3.
 */
@Service
public class ExtractContentService {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
    private static ZHConverter converter = ZHConverter.getInstance(ZHConverter.SIMPLIFIED);// 转化为简体中文

    /**
     *  读取一级或者二级标题，确定 LinkedList 是有序的
     */
    public void reviewTitles(Document doc){
        logger.info("-------------firstTitle----------------");
        LinkedList<String> firstTitle = getFirstTitle(doc);
        logger.info(firstTitle + "");
        logger.info("-------------secondTitle----------------");
        LinkedList<String> secondTitle = getSecondTitle(doc);
        logger.info(secondTitle + "");
        logger.info("-------------thirdTitle----------------");
        LinkedList<String> thirdTitle = getThirdTitle(doc);
        logger.info(thirdTitle + "");
        logger.info("-------------allTitle----------------");
        LinkedList<String> allTitle = getAllTitle(doc);
        logger.info(allTitle + "");
        logger.info("--------------title index---------------");
        LinkedList<Element> nodes = getNodes(doc);
        LinkedList<Integer> firstTitleIndex = getTitleIndex(firstTitle, nodes);
        compareTitleIndex(firstTitle, firstTitleIndex);
        LinkedList<Integer> secondTitleIndex = getTitleIndex(secondTitle, nodes);
        compareTitleIndex(secondTitle, secondTitleIndex);
        LinkedList<Integer> thirdTitleIndex = getTitleIndex(thirdTitle, nodes);
        compareTitleIndex(thirdTitle, thirdTitleIndex);
        LinkedList<Integer> allTitleIndex = getTitleIndex(allTitle, nodes);
        compareTitleIndex(allTitle, allTitleIndex);
        logger.info("--------------title content---------------");
        getFirstContent(doc);
        getSecondContent(doc);
        getThirdContent(doc);
        getSummary(doc);
    }


    /**
     * 网页没有一级或者二级标题，网页内容的获取
     * @param doc
     * @return
     */
    public List<AssembleText> getSpecialContent(Document doc){
        List<AssembleText> assembleList = new ArrayList<>();
        logger.info("------------------ 页面所有内容 ----------------------");
        Elements para = doc.select("div#mw-content-text");
        if(para.size() != 0){
            String con = para.get(0).text();

            con = converter.convert(con);
            AssembleText assemble = new AssembleText(con, "摘要", 1L);
            assembleList.add(assemble);
        }
        return assembleList;
    }

    /**
     * 获取介绍信息
     * @param doc
     * @return
     */
    public List<AssembleText> getSummary(Document doc) {
        List<AssembleText> assembleList = new ArrayList<>();
        logger.info("------------------ 摘要内容 ----------------------");
        LinkedList<Element> list = getNodes(doc);
        String summary = "";
        int tocId = 0;

        // 获取summary的下标
        for (int i = 0; i < list.size(); i++) {
            Element child = list.get(i);
            Elements toc = child.select("div#toc");
            if (toc.size() != 0) {
                tocId = i;
                break;
            } else {
                Elements h = child.select("span.mw-headline");
                if(h.size()!=0){
                    tocId = i;
                    break;
                }
            }
        }

        // 获取summary内容
        for (int i = 0; i < tocId; i++) {
            Element child = list.get(i);
            summary = child.text();
            summary = converter.convert(summary);
            logger.info("摘要" + "--->" + summary);
            AssembleText assemble = new AssembleText(summary, "摘要", 1L);
            assembleList.add(assemble);
        }
        return assembleList;
    }

    /**
     * 获取三级标题之间的内容
     * @param doc
     * @return
     */
    public List<AssembleText> getThirdContent(Document doc){
        List<AssembleText> assembleList = new ArrayList<>();
        LinkedList<String> allTitle = getAllTitle(doc);
        LinkedList<String> thirdTitle = getThirdTitle(doc);
        LinkedList<Element> nodes = getNodes(doc);

        /**
         * 寻找一级和二级标题在节点链表的下标
         */
        LinkedList<Integer> allTitleIndex = getTitleIndex(allTitle, nodes);

        /**
         * 比较标题链表和对应的下标链表的大小是否相同，原则上是相同的，不相同说明网页存在问题等。。。
         */
        int len = allTitle.size();
        int indexLen = allTitleIndex.size();
        if(len > indexLen){
            len = indexLen;
        }

        logger.info("------------------ 三级标题内容 ----------------------");
        /**
         * 获取每个三级标题的内容，为该标题与相邻标题下标之间的节点内容
         */
        for(int i = 0; i < len - 1; i++){
            String title = allTitle.get(i);
            for(int j = 0; j < thirdTitle.size(); j++){
                String thiTitle = thirdTitle.get(j);
                if(title.equals(thiTitle)){ // 遍历所有标题，寻找到三级标题
                    String content = "";
                    int begin = allTitleIndex.get(i);
                    int end = allTitleIndex.get(i+1);
                    logger.info(title + " ---> " + begin + "," + end);
                    for(int k = begin + 1; k < end; k++){
                        Element node = nodes.get(k);
                        content = node.text();
                        content = converter.convert(content);
                        AssembleText assemble = new AssembleText(content, title, 3L);
                        assembleList.add(assemble);
                    }
                }
            }
        }

        /**
         * 所有标题的最后一个标题是否为三级标题
         */
        String title = allTitle.get(len - 1);
        for(int j = 0; j < thirdTitle.size(); j++){
            String thiTitle = thirdTitle.get(j);
            if(title.equals(thiTitle)){ // 遍历所有标题，寻找到三级标题
                String content = "";
                int begin = allTitleIndex.get(len - 1);
                logger.info(title + " ---> " + begin + "," + (nodes.size()-1));
                for(int k = begin + 1; k < nodes.size(); k++){
                    Element node = nodes.get(k);
                    content = node.text();
                    content = converter.convert(content);
//					String imgTxt = "<img src=";
//					if(imgTxt.contains(imgTxt)){
//						content = content.substring(0, content.indexOf(imgTxt));
//					}
                    AssembleText assemble = new AssembleText(content, title, 3L);
                    assembleList.add(assemble);
                }
            }
        }
        return assembleList;
    }

    /**
     * 获取二级标题之间的内容
     * @param doc
     * @return
     */
    public List<AssembleText> getSecondContent(Document doc){
        List<AssembleText> assembleList = new ArrayList<>();
        LinkedList<String> allTitle = getAllTitle(doc);
        LinkedList<String> secondTitle = getSecondTitle(doc);
        LinkedList<Element> nodes = getNodes(doc);

        /**
         * 寻找一级和二级标题在节点链表的下标
         */
        LinkedList<Integer> allTitleIndex = getTitleIndex(allTitle, nodes);

        /**
         * 比较标题链表和对应的下标链表的大小是否相同，原则上是相同的，不相同说明网页存在问题等。。。
         */
        int len = allTitle.size();
        int indexLen = allTitleIndex.size();
        if(len > indexLen){
            len = indexLen;
        }

        logger.info("------------------ 二级标题内容 ----------------------");
        /**
         * 获取每个二级标题的内容，为该标题与相邻标题下标之间的节点内容
         */
        for(int i = 0; i < len - 1; i++){
            String title = allTitle.get(i);
            for(int j = 0; j < secondTitle.size(); j++){
                String secTitle = secondTitle.get(j);
                if(title.equals(secTitle)){ // 遍历所有标题，寻找到二级标题
                    String content = "";
                    int begin = allTitleIndex.get(i);
                    int end = allTitleIndex.get(i+1);
                    logger.info(title + " ---> " + begin + "," + end);
                    for(int k = begin + 1; k < end; k++){
                        Element node = nodes.get(k);
                        content = node.text();
                        content = converter.convert(content);
                        AssembleText assemble = new AssembleText(content, title, 2L);
                        assembleList.add(assemble);
                    }
                }
            }
        }

        /**
         * 所有标题的最后一个标题是否为二级标题
         */
        String title = allTitle.get(len - 1);
        for(int j = 0; j < secondTitle.size(); j++){
            String secTitle = secondTitle.get(j);
            if(title.equals(secTitle)){ // 遍历所有标题，寻找到二级标题
                String content = "";
                int begin = allTitleIndex.get(len - 1);
                logger.info(title + " ---> " + begin + "," + (nodes.size()-1));
                for(int k = begin + 1; k < nodes.size(); k++){
                    Element node = nodes.get(k);
                    content = node.text();
                    content = converter.convert(content);
//					String imgTxt = "<img src=";
//					if(imgTxt.contains(imgTxt)){
//						content = content.substring(0, content.indexOf(imgTxt));
//					}
                    AssembleText assemble = new AssembleText(content, title, 2L);
                    assembleList.add(assemble);
                }
            }
        }
        return assembleList;
    }

    /**
     * 获取一级标题之间的内容
     * @param doc
     * @return
     */
    public List<AssembleText> getFirstContent(Document doc){
        List<AssembleText> assembleList = new ArrayList<>();
        LinkedList<String> firstTitle = getFirstTitle(doc);
        LinkedList<Element> nodes = getNodes(doc);

        /**
         * 寻找一级标题在节点链表的下标
         */
        LinkedList<Integer> firstTitleIndex = getTitleIndex(firstTitle, nodes);

        /**
         *  比较标题链表和对应的下标链表的大小是否相同，原则上是相同的，不相同说明网页存在问题等。。。
         */
        int len = firstTitle.size();
        int indexLen = firstTitleIndex.size();
        if(len > indexLen){
            len = indexLen;
        }

        logger.error("一级标题数量为：" + firstTitle.size());
        logger.error("一级标题下标数量为：" + firstTitleIndex.size());

        logger.info("------------------ 一级标题内容 ----------------------");

        /**
         * 获取每个一级标题的内容，为该标题与相邻标题下标之间的节点内容
         */
        for(int i = 0; i < len - 1; i++){
            String title = firstTitle.get(i);
            String content = "";
            int begin = firstTitleIndex.get(i);
            int end = firstTitleIndex.get(i + 1);
            logger.info(title + " ---> " + begin + "," + end);
            for(int j = begin + 1; j < end; j++){
                Element node = nodes.get(j);
                content = node.text();
                content = converter.convert(content);
                AssembleText assemble = new AssembleText(content, title, 1L);
                assembleList.add(assemble);
            }
        }

        /**
         * 一级标题最后一个标题为该下标到节点最后
         */
        String title = firstTitle.get(len - 1);
        String content = "";
        int begin = firstTitleIndex.get(len - 1);
        logger.info(title + " ---> " + begin + "," + (nodes.size()-1));
        for(int j = begin + 1; j < nodes.size(); j++){
            Element node = nodes.get(j);
            content = node.text();
            content = converter.convert(content);
            AssembleText assemble = new AssembleText(content, title, 1L);
            assembleList.add(assemble);
        }
        return assembleList;
    }

    /**
     * 解析得到图片的链接
     * @param doc
     * @return
     */
    public LinkedList<String> getImages(Document doc){
        LinkedList<String> allTitle = new LinkedList<String>();
        Elements titles = doc.select("div#mw-content-text").select("a").select("img");
        if(titles.size() != 0){
            for(int i = 0; i < titles.size(); i++){
                String url = titles.get(i).attr("src");
                int width = Integer.parseInt(titles.get(i).attr("width"));
                int height = Integer.parseInt(titles.get(i).attr("height"));
                logger.info(url);
                logger.info(width + "");
                logger.info(height + "");
            }
        }
        return allTitle;
    }

    /**
     * 获取介绍部分的图片
     * @param doc
     * @return
     */
    public List<AssembleImage> getSummaryImage(Document doc) {
        List<AssembleImage> assembleImageList = new ArrayList<>();
        logger.info("------------------ 摘要图片 ----------------------");
        LinkedList<Element> list = getNodes(doc);
        int tocId = 0;

        /**
         * 获取summary的下标
         */
        for (int i = 0; i < list.size(); i++) {
            Element child = list.get(i);
            Elements toc = child.select("div#toc");
            if (toc.size() != 0) {
                tocId = i;
                break;
            } else {
                Elements h = child.select("span.mw-headline");
                if(h.size()!=0){
                    tocId = i;
                    break;
                }
            }
        }

        /**
         * 获取summary内容
         */
        for (int i = 0; i < tocId; i++) {
            Element child = list.get(i);
            Elements images = child.select("a").select("img");
            int imagesLenth = images.size();
            if (imagesLenth != 0) {
                try {
                    String url = images.get(0).attr("src");
                    Long width = Long.parseLong(images.get(0).attr("width"));
                    Long height = Long.parseLong(images.get(0).attr("height"));
                    logger.info("摘要图片链接: " +  url + "---> 高度: " + width + "宽度: " + height);
                    AssembleImage assembleimage = new AssembleImage(url, width, height, "摘要", 1L);
                    assembleImageList.add(assembleimage);
                } catch (Exception e) {
                    logger.info("this image is not a standard picture...");
                }

            }
        }
        return assembleImageList;
    }

    /**
     * 获取一级标题的图片
     * @param doc
     * @return
     */
    public List<AssembleImage> getFirstImage(Document doc){
        List<AssembleImage> assembleImageList = new ArrayList<>();
        LinkedList<String> firstTitle = getFirstTitle(doc);
        LinkedList<Element> nodes = getNodes(doc);

        /**
         * 寻找一级标题在节点链表的下标
         */
        LinkedList<Integer> firstTitleIndex = getTitleIndex(firstTitle, nodes);

        /**
         *  比较标题链表和对应的下标链表的大小是否相同，原则上是相同的，不相同说明网页存在问题等。。。
         */
        int len = firstTitle.size();
        int indexLen = firstTitleIndex.size();
        if(len > indexLen){
            len = indexLen;
        }

        logger.info("------------------ 一级标题内图片----------------------");

        /**
         * 获取每个一级标题的图片，为该标题与相邻标题下标之间的节点图片
         */
        for(int i = 0; i < len - 1; i++){
            String title = firstTitle.get(i);
            int begin = firstTitleIndex.get(i);
            int end = firstTitleIndex.get(i + 1);
            logger.info(title + " ---> " + begin + "," + end);
            for(int j = begin + 1; j < end; j++){
                Element node = nodes.get(j);
                Elements images = node.select("a").select("img");
                int imagesLenth = images.size();
                if (imagesLenth != 0) {
                    try {
                        String url = images.get(0).attr("src");
                        Long width = Long.parseLong(images.get(0).attr("width"));
                        Long height = Long.parseLong(images.get(0).attr("height"));
                        logger.info("一级标题图片链接: " +  url + "---> 高度: " + width + "宽度: " + height);
                        AssembleImage assembleimage = new AssembleImage(url, width, height, title, 1L);
                        assembleImageList.add(assembleimage);
                    } catch (Exception e) {
                        logger.info("this image is not a standard picture...");
                    }
                }
            }
        }

        /**
         * 一级标题最后一个标题为该下标到节点最后
         */
        String title = firstTitle.get(len - 1);
        int begin = firstTitleIndex.get(len - 1);
        logger.info(title + " ---> " + begin + "," + (nodes.size()-1));
        for(int j = begin + 1; j < nodes.size(); j++){
            Element node = nodes.get(j);
            Elements images = node.select("a").select("img");
            int imagesLenth = images.size();
            if (imagesLenth != 0) {
                try {
                    String url = images.get(0).attr("src");
                    Long width = Long.parseLong(images.get(0).attr("width"));
                    Long height = Long.parseLong(images.get(0).attr("height"));
                    logger.info("一级标题图片链接: " +  url + "---> 高度: " + width + "宽度: " + height);
                    AssembleImage assembleimage = new AssembleImage(url, width, height, title, 1L);
                    assembleImageList.add(assembleimage);
                } catch (Exception e) {
                    logger.info("this image is not a standard picture...");
                }

            }
        }
        return assembleImageList;
    }

    /**
     * 获取二级标题的图片
     * @param doc
     * @return
     */
    public List<AssembleImage> getSecondImage(Document doc){
        List<AssembleImage> assembleImageList = new ArrayList<>();
        LinkedList<String> allTitle = getAllTitle(doc);
        LinkedList<String> secondTitle = getSecondTitle(doc);
        LinkedList<Element> nodes = getNodes(doc);

        /**
         * 寻找一级和二级标题在节点链表的下标
         */
        LinkedList<Integer> allTitleIndex = getTitleIndex(allTitle, nodes);

        /**
         * 比较标题链表和对应的下标链表的大小是否相同，原则上是相同的，不相同说明网页存在问题等。。。
         */
        int len = allTitle.size();
        int indexLen = allTitleIndex.size();
        if(len > indexLen){
            len = indexLen;
        }

        logger.info("------------------ 二级标题内容 ----------------------");
        /**
         * 获取每个二级标题的内容，为该标题与相邻标题下标之间的节点内容
         */
        for(int i = 0; i < len - 1; i++){
            String title = allTitle.get(i);
            for(int j = 0; j < secondTitle.size(); j++){
                String secTitle = secondTitle.get(j);
                if(title.equals(secTitle)){ // 遍历所有标题，寻找到二级标题
                    int begin = allTitleIndex.get(i);
                    int end = allTitleIndex.get(i+1);
                    logger.info(title + " ---> " + begin + "," + end);
                    for(int k = begin + 1; k < end; k++){
                        Element node = nodes.get(k);
                        Elements images = node.select("a").select("img");
                        int imagesLenth = images.size();
                        if (imagesLenth != 0) {
                            try {
                                String url = images.get(0).attr("src");
                                Long width = Long.parseLong(images.get(0).attr("width"));
                                Long height = Long.parseLong(images.get(0).attr("height"));
                                logger.info("二级标题图片链接: " +  url + "---> 高度: " + width + "宽度: " + height);
                                AssembleImage assembleimage = new AssembleImage(url, width, height, secTitle, 2L);
                                assembleImageList.add(assembleimage);
                            } catch (Exception e) {
                                logger.info("this image is not a standard picture...");
                            }

                        }
                    }
                }
            }
        }

        /**
         * 所有标题的最后一个标题是否为二级标题
         */
        String title = allTitle.get(len - 1);
        for(int j = 0; j < secondTitle.size(); j++){
            String secTitle = secondTitle.get(j);
            if(title.equals(secTitle)){ // 遍历所有标题，寻找到二级标题
                int begin = allTitleIndex.get(len - 1);
                logger.info(title + " ---> " + begin + "," + (nodes.size()-1));
                for(int k = begin + 1; k < nodes.size(); k++){
                    Element node = nodes.get(k);
                    Elements images = node.select("a").select("img");
                    int imagesLenth = images.size();
                    if (imagesLenth != 0) {
                        try {
                            String url = images.get(0).attr("src");
                            Long width = Long.parseLong(images.get(0).attr("width"));
                            Long height = Long.parseLong(images.get(0).attr("height"));
                            logger.info("二级标题图片链接: " +  url + "---> 高度: " + width + "宽度: " + height);
                            AssembleImage assembleimage = new AssembleImage(url, width, height, secTitle, 2L);
                            assembleImageList.add(assembleimage);
                        } catch (Exception e) {
                            logger.info("this image is not a standard picture...");
                        }

                    }
                }
            }
        }
        return assembleImageList;
    }

    /**
     * 获取三级标题的图片
     * @param doc
     * @return
     */
    public List<AssembleImage> getThirdImage(Document doc){
        List<AssembleImage> assembleImageList = new ArrayList<>();
        LinkedList<String> allTitle = getAllTitle(doc);
        LinkedList<String> thirdTitle = getThirdTitle(doc);
        LinkedList<Element> nodes = getNodes(doc);

        /**
         * 寻找一级和二级标题在节点链表的下标
         */
        LinkedList<Integer> allTitleIndex = getTitleIndex(allTitle, nodes);

        /**
         * 比较标题链表和对应的下标链表的大小是否相同，原则上是相同的，不相同说明网页存在问题等。。。
         */
        int len = allTitle.size();
        int indexLen = allTitleIndex.size();
        if(len > indexLen){
            len = indexLen;
        }

        logger.info("------------------ 三级标题内容 ----------------------");
        /**
         * 获取每个三级标题的内容，为该标题与相邻标题下标之间的节点内容
         */
        for(int i = 0; i < len - 1; i++){
            String title = allTitle.get(i);
            for(int j = 0; j < thirdTitle.size(); j++){
                String thiTitle = thirdTitle.get(j);
                if(title.equals(thiTitle)){ // 遍历所有标题，寻找到三级标题
                    int begin = allTitleIndex.get(i);
                    int end = allTitleIndex.get(i+1);
                    logger.info(title + " ---> " + begin + "," + end);
                    for(int k = begin + 1; k < end; k++){
                        Element node = nodes.get(k);
                        Elements images = node.select("a").select("img");
                        int imagesLenth = images.size();
                        if (imagesLenth != 0) {
                            try {
                                String url = images.get(0).attr("src");
                                Long width = Long.parseLong(images.get(0).attr("width"));
                                Long height = Long.parseLong(images.get(0).attr("height"));
                                logger.info("三级标题图片链接: " +  url + "---> 高度: " + width + "宽度: " + height);
                                AssembleImage assembleimage = new AssembleImage(url, width, height, thiTitle, 3L);
                                assembleImageList.add(assembleimage);
                            } catch (Exception e) {
                                logger.info("this image is not a standard picture...");
                            }

                        }
                    }
                }
            }
        }

        /**
         * 所有标题的最后一个标题是否为三级标题
         */
        String title = allTitle.get(len - 1);
        for(int j = 0; j < thirdTitle.size(); j++){
            String thiTitle = thirdTitle.get(j);
            if(title.equals(thiTitle)){ // 遍历所有标题，寻找到三级标题
                int begin = allTitleIndex.get(len - 1);
                logger.info(title + " ---> " + begin + "," + (nodes.size()-1));
                for(int k = begin + 1; k < nodes.size(); k++){
                    Element node = nodes.get(k);
                    Elements images = node.select("a").select("img");
                    int imagesLenth = images.size();
                    if (imagesLenth != 0) {
                        try {
                            String url = images.get(0).attr("src");
                            Long width = Long.parseLong(images.get(0).attr("width"));
                            Long height = Long.parseLong(images.get(0).attr("height"));
                            logger.info("三级标题图片链接: " +  url + "---> 高度: " + width + "宽度: " + height);
                            AssembleImage assembleimage = new AssembleImage(url, width, height, thiTitle, 3L);
                            assembleImageList.add(assembleimage);
                        } catch (Exception e) {
                            logger.info("this image is not a standard picture...");
                        }

                    }
                }
            }
        }
        return assembleImageList;
    }

    /**
     * 寻找一级标题在节点链表的下标
     * @param titleList
     * @param nodes
     * @return
     */
    public LinkedList<Integer> getTitleIndex(LinkedList<String> titleList, LinkedList<Element> nodes){
        LinkedList<Integer> firstTitleIndex = new LinkedList<Integer>();
        // 寻找一级标题在节点链表的下标
        for(int i = 0; i < titleList.size(); i++){
            String title = titleList.get(i);
            for(int j = 0; j < nodes.size(); j++){
                Element node = nodes.get(j);
                Elements h2 = node.select("span.mw-headline");
                if(h2.size() != 0){
                    String level1 = h2.get(0).text();
                    level1 = converter.convert(level1);
                    if(title.equals(level1)){// 匹配到一级标题的下标
                        firstTitleIndex.add(j);
                    }
                }
            }
        }
        return firstTitleIndex;
    }

    /**
     * 读取一下标题的下标，确认是否正确
     * @param title
     * @param titleIndex
     */
    public void compareTitleIndex(LinkedList<String> title, LinkedList<Integer> titleIndex){
        logger.info("------------------ compare title and index ------------------");
        // 读取一下标题的下标，确认是否正确
        logger.info("title size is : " + title.size());
        logger.info("titleIndex size is : " + titleIndex.size());
        // 比较标题链表和对应的下标链表的大小是否相同，原则上是相同的，不相同说明网页存在问题等。。。
        int len = title.size();
        int indexLen = titleIndex.size();
        if(len > indexLen){
            len = indexLen;
        }
        if(len != indexLen){
            logger.info("+++++++++++++++++++ title don't suit index +++++++++++++++++++");
        }
        for(int i = 0; i < len; i++){
            String tit = title.get(i);
            int index = titleIndex.get(i);
            logger.info(tit + " ---> " + index);
        }
    }


    /**
     * 获取一级、二级和三级标题
     * @param doc
     * @return
     */
    public LinkedList<String> getAllTitle(Document doc){
        LinkedList<String> allTitle = new LinkedList<String>();
        Elements titles = doc.select("div#mw-content-text").select("span.mw-headline");
        if(titles.size() != 0){
            for(int i = 0; i < titles.size(); i++){
                String head = titles.get(i).text();
                head = converter.convert(head);
                Boolean flag = delTitle(head);
                if(!flag){
                    allTitle.add(head);
                }
            }
        }
        return allTitle;
    }

    /**
     * 获取三级标题
     * @param doc
     * @return
     */
    public LinkedList<String> getThirdTitle(Document doc){
        LinkedList<String> thirdTitle = new LinkedList<String>();
        Elements titles = doc.select("div#mw-content-text").select("h4");
        if(titles.size() != 0){
            for(int i = 0; i < titles.size(); i++){
                String level3 = titles.get(i).select("span.mw-headline").get(0).text();
                level3 = converter.convert(level3);
                Boolean flag = delTitle(level3);
                if(!flag){
                    thirdTitle.add(level3);
                }
            }
        }
        return thirdTitle;
    }

    /**
     * 获取二级标题
     * @param doc
     * @return
     */
    public LinkedList<String> getSecondTitle(Document doc){
        LinkedList<String> secondTitle = new LinkedList<String>();
        Elements titles = doc.select("div#mw-content-text").select("h3");
        if(titles.size() != 0){
            for(int i = 0; i < titles.size(); i++){
                String level2 = titles.get(i).select("span.mw-headline").get(0).text();
                level2 = converter.convert(level2);
                Boolean flag = delTitle(level2);
                if(!flag){
                    secondTitle.add(level2);
                }
            }
        }
        return secondTitle;
    }

    /**
     * 获取一级标题
     * @param doc
     * @return
     */
    public LinkedList<String> getFirstTitle(Document doc){
        LinkedList<String> firstTitle = new LinkedList<String>();
        Elements titles = doc.select("div#mw-content-text").select("h2");
        if(titles.size() != 0){
            for(int i = 0; i < titles.size(); i++){
                Elements lel = titles.get(i).select("span.mw-headline");
                if(lel.size() != 0){
                    String level1 = lel.get(0).text();
                    level1 = converter.convert(level1);
                    Boolean flag = delTitle(level1);
                    if(!flag){
                        firstTitle.add(level1);
                    }
                }
            }
        }
        return firstTitle;
    }

    /**
     * 将html内容中的所有子节点写到链表中
     * @param doc
     * @return
     */
    public LinkedList<Element> getNodes(Document doc){
        Element mainContent = doc.select("div.mw-content-ltr").get(0).child(0);
        Elements childs = mainContent.children();
        LinkedList<Element> list = new LinkedList<Element>();
        for (Element e : childs) {
            list.offer(e);
        }
        return list;
    }

    /**
     * 去除无用标题
     * @param title
     * @return
     */
    public Boolean delTitle(String title){
//		Boolean useless = false;
        Boolean useless = title.equals("注释与参考文献")
                || title.equals("参考文献") || title.equals("外部链接")|| title.equals("参考资料")
                || title.equals("外部连结") || title.equals("相关条目")
                || title.equals("参见") || title.equals("另见")|| title.equals("参看")
                || title.equals("参考") || title.equals("参照")
                || title.equals("注释") || title.equals("延伸阅读"); // 判断标题是否为无用的
        return useless;
    }

    /**
     * 解析发布时间
     * @param doc
     * @return
     */
    public String getPostTime(Document doc) {
        String time = "";
        Elements content = doc.select("li#footer-info-lastmod");
        if (content.size() != 0) {
            Elements timeItem = content;
            time = timeItem.get(0).text();
            try {
                time = postTimeDeal(time);
            } catch (Exception e) {
                time = "2016-01-01 00:00:00";
            }
//			logger.info("post time is : " + time);
        } else {
            logger.info("crawler time has some bugs ...");
        }
        return time;
    }

    /**
     * 对中文维基百科的时间格式进行处理，使其可以用于数据库的插入
     * 原始格式：" 本页面最后修订于2016年1月22日 (星期五) 11:22。"
     * 标准格式：2016-01-22 11:22:00
     * @param time
     * @return
     */
    public String postTimeDeal(String time) {
        // String time = " 本页面最后修订于2016年1月22日 (星期五) 11:22。";
        String[] time0 = time.split("修订于");
        String[] time1 = time0[1].split("年");
        String[] time2 = time1[1].split("月");
        String[] time3 = time2[1].split("日");
        String[] time4 = time3[1].split("\\)");
        String[] time5 = time4[1].split("。");
        String year = time1[0];
        String month = time2[0];
        String day = time3[0];
        String clock = time5[0].substring(1, time5[0].length());
        time = year + "-" + month + "-" + day + " " + clock + ":00";
        return time;
    }

    /**
     * 获取各级标题与分面的对应情况
     * @param doc
     * @return
     */
    public HashMap<String, String> getTitleRelationWiki(Document doc){
        LinkedList<String> indexs = new LinkedList<String>();// 标题前面的下标
        LinkedList<String> facets = new LinkedList<String>();// 各级标题的名字
        LinkedList<String> results = new LinkedList<String>();// 二级/三级标题对应到一级标题之后的标题
        HashMap<String, String> relation = new HashMap<String, String>();

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
                results.add(text);
            }

            /**
             * 将二级/三级标题全部匹配到对应的一级标题
             */
            logger.info("--------------------------------------------");
            for(int i = 0; i < indexs.size(); i++){
                String index = indexs.get(i);
                if(index.contains(".")){
                    for(int j = i-1; j >= 0; j--){ // 从二级/三级标题往前搜索，遇到第一个下标不是"▪"的标题即是对应的一级标题
                        String indexCom = indexs.get(j);
                        if(!indexCom.contains(".")){
                            String facetOne = facets.get(j);
                            results.set(i, facetOne);
                            break;
                        }
                    }
                }
            }

            /**
             * 打印最新的标题信息，确定更新二级/三级标题成功
             */
            logger.info("--------------------------------------------");
            for(int i = 0; i < facets.size(); i++){
                relation.put(facets.get(i), results.get(i));
                logger.info(indexs.get(i) + "-->" + facets.get(i) + "-->" + results.get(i));
            }

        } else {
            logger.info("该主题没有目录，不是目录结构，直接爬取 -->摘要<-- 信息");
        }

        return relation;
    }

    /**
     * 获取字符串的长度，如果有中文，则每个中文字符计为2位
     * @param value 指定的字符串
     * @return
     * @return 字符串的长度
     */
    public int getContentLen(String value) {
//		String value = "hello你好";
        int valueLength = 0;
        String chinese = "[\u0391-\uFFE5]";
		/* 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1 */
        for (int i = 0; i < value.length(); i++) {
			/* 获取一个字符 */
            String temp = value.substring(i, i + 1);
			/* 判断是否为中文字符 */
            if (temp.matches(chinese)) {
				/* 中文字符长度为2 */
                valueLength += 2;
            } else {
				/* 其他字符长度为1 */
                valueLength += 1;
            }
        }
        logger.info(valueLength + "");
        return valueLength;
    }

    /**
     * 得到图片链接对应的输入流，用于数据库Blob图片字段的存储
     * @param imageUrl
     * @return
     */
    public byte[] getImageFromNetByUrl(String imageUrl) throws IOException {
        byte[] byt = null;
        byt = IOUtils.toByteArray(new URL(imageUrl));
        return byt;
//        byte[] byt = null;
//        try {
//            byt = IOUtils.toByteArray(new URL(imageUrl));
//        } catch (IOException e) {
//            logger.error("下载图片失败！", e);
//        }
//        return byt;
    }


    /**
     * 去除主题链接中的“Category:”字段，处理为维基词条页面链接形式
     * @param url
     * @return
     */
    public String dealUrl(String url) {
        String newUrl = url;
        if (url.contains("Category:")){
            newUrl = url.replace("Category:", "");
        }
        return newUrl;
    }


}
