package Yotta.spider.service.utils;

import Yotta.spider.domain.Topic;
import com.spreada.utils.chinese.ZHConverter;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 解析中文维基
 * 1. 获得每一分类的子分类
 * 2. 获得每一分类的子页面
 * Created by yuanhao on 2017/4/28.
 */
@Service
public class ExtractTopicService {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
    private static ZHConverter converter = ZHConverter.getInstance(ZHConverter.SIMPLIFIED);// 转化为简体中文

    /**
     * 解析得到Category中不包含子主题的主题信息
     * @param doc 维基百科目录页面对应的Document文档
     * @return
     */
    public List<Topic> getTopicNotChild(Document doc){
        List<Topic> topicList = new ArrayList<>();
        Elements mwPages = doc.select("#mw-pages").select("li");
        int len = mwPages.size();
        logger.info(len + "");
        for (int i = 0; i < mwPages.size(); i++) {
            String topicUrl = "https://zh.wikipedia.org" + mwPages.get(i).select("a").attr("href");
            String topicName = mwPages.get(i).text();
            topicName = converter.convert(topicName);
            logger.info("topicNotChild is : " + topicName + "  url is : " + topicUrl);
            Topic topic = new Topic();
            topic.setTopicName(topicName);
            topic.setTopicUrl(topicUrl);
            topicList.add(topic);
        }
        return topicList;
    }

    /**
     * 解析得到Category中含有子主题的主题信息
     * @param doc 维基百科目录页面对应的Document文档
     * @return
     */
    public List<Topic> getTopicHasChild(Document doc){
        List<Topic> topicSimples = new ArrayList<>();
        if(doc.select("#mw-subcategories").size()==0){
            logger.info("没有下一层子分类...");
        } else {
            Elements mwPages = doc.select("#mw-subcategories").select("li");
            int len = mwPages.size();
            logger.info(len + "");
            for (int i = 0; i < mwPages.size(); i++) {
                String topicUrl = "https://zh.wikipedia.org" + mwPages.get(i).select("a").attr("href");
                String topicName = mwPages.get(i).select("a").text();
                topicName = converter.convert(topicName);
                logger.info("Layer is : " + topicName + "  url is : " + topicUrl);
                Topic topic = new Topic();
                topic.setTopicName(topicName);
                topic.setTopicUrl(topicUrl);
                topicSimples.add(topic);
            }
        }
        return topicSimples;
    }

    /**
     * 判断是否存在目录页面
     * @param doc 维基百科目录页面对应的Document文档
     * @return
     */
    public boolean getCategoryPage(Document doc){
        Elements firstHeading = doc.select("#firstHeading");
        if (firstHeading.size() > 0) {
            String heading = firstHeading.get(0).text();
            logger.info(heading);
            if (heading.split(":")[0].contains("分类")){
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

}
