package Yotta.spider.service;

import Yotta.spider.service.utils.DownloaderService;
import Yotta.spider.service.utils.ExtractTopicService;
import junit.framework.TestCase;
import org.jsoup.nodes.Document;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;

/**
 * 测试抽取主题的程序
 * Created by 18710 on 2017/8/9.
 */
public class ExtractTopicServiceTest extends TestCase {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
    private static DownloaderService downloaderService = new DownloaderService();

    public void testGetTopicNotChild() throws Exception {
        ExtractTopicService e = new ExtractTopicService();
        String url = "https://zh.wikipedia.org/wiki/Category:%E6%95%B0%E6%8D%AE%E7%BB%93%E6%9E%84";
        String html = downloaderService.seleniumWikiCNIE(url);
//		String html = downloaderService.seleniumWikiCN(url);
//        String filePath = "file/";
//        new File(filePath).mkdir();
//        downloaderService.saveHtml(filePath + "1.html", html);
        Document doc = downloaderService.parseHtmlText(html);
        e.getTopicNotChild(doc);
        e.getTopicHasChild(doc);
    }

    public void testGetCategoryPage() throws Exception {
        String domainName = "树";
        String domain_url = "https://zh.wikipedia.org/wiki/Category:" + URLEncoder.encode(domainName ,"UTF-8");
        String html = downloaderService.seleniumWikiCNIE(domain_url); // Selenium方式获取
        Document doc = downloaderService.parseHtmlText(html);
        ExtractTopicService e = new ExtractTopicService();
        logger.info(e.getCategoryPage(doc) + "");
    }


}