package Yotta.spider.service;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.selector.Html;

import java.io.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 爬虫连接器：
 * 1. selenium 爬取
 * 2. httpclient 爬取
 * 3. webmagic 爬取
 * 4. Jsoup 爬取
 *
 * 输入是需要爬取的链接
 * 输出是网页的源码
 *
 * Created by yuanhao on 2017/4/28.
 */
@Service
public class DownloaderService {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 返回网页链接对应的HTML源码，IE浏览器模拟
     * @param url 待爬取链接
     * @return 页面源码
     * @throws Exception
     */
    public String seleniumWikiCNIE(String url) throws Exception {
//        System.setProperty("webdriver.ie.driver", "C:\\workspace\\java\\Yotta\\src\\resources\\phantomjs\\IEDriverServer.exe");
        int randomTimeout = random(1000, 2000);
        WebDriver driver = new InternetExplorerDriver();
        int m = 1;
        driver.manage().timeouts().pageLoadTimeout(randomTimeout, TimeUnit.SECONDS);
        while (m < 4) {
            try{
                driver.get(url);
            }
            catch (Exception e) {
                logger.info("第" + m + "次重载页面...");
                m++;
                driver.quit();
                driver = new InternetExplorerDriver();
                int randomTimeout2 = random(1000, 2000);
                driver.manage().timeouts().pageLoadTimeout(randomTimeout2, TimeUnit.SECONDS);
                continue;
            }
            break;
        }
        logger.info("Page title is: " + driver.getTitle());
        // roll the page
        JavascriptExecutor JS = (JavascriptExecutor) driver;
        try {
            JS.executeScript("scrollTo(0, document.body.scrollHeight)");
            Thread.sleep(500);
        } catch (Exception e) {
            logger.error("Error at loading the page ...");
            e.printStackTrace();
            driver.quit();
        }
        // save page
        String html = driver.getPageSource();
        // Close the browser
        Thread.sleep(random(1000, 2000));
        driver.quit();
        return html;
    }

    /**
     * 返回网页链接对应的HTML源码，Chrome浏览器模拟
     * @param url 待爬取链接
     * @return 页面源码
     * @throws Exception
     */

    public String seleniumWikiCN(String url) throws Exception {
//        System.setProperty("webdriver.chrome.driver", "C:\\workspace\\java\\Yotta\\src\\resources\\phantomjs\\chromedriver.exe");
        int randomTimeout = random(1000, 2000);
        WebDriver driver = new ChromeDriver();
        int m = 1;
        driver.manage().timeouts().pageLoadTimeout(randomTimeout, TimeUnit.SECONDS);
        while (m < 4) {
            try{
                driver.get(url);
            }
            catch (Exception e) {
                logger.info("第" + m + "次重载页面...");
                m++;
                driver.quit();
                driver = new ChromeDriver();
                int randomTimeout2 = random(1000, 2000);
                driver.manage().timeouts().pageLoadTimeout(randomTimeout2, TimeUnit.SECONDS);
                continue;
            }
            break;
        }
        logger.info("Page title is: " + driver.getTitle());
        // roll the page
        JavascriptExecutor JS = (JavascriptExecutor) driver;
        try {
            JS.executeScript("scrollTo(0, document.body.scrollHeight)");
            Thread.sleep(500);
        } catch (Exception e) {
            logger.error("Error at loading the page ...");
            e.printStackTrace();
            driver.quit();
        }
        // save page
        String html = driver.getPageSource();
        // close the browser
        Thread.sleep(random(1000, 2000));
        driver.quit();
        return html;
    }


    /**
     * 返回网页链接对应的HTML源码，httpClient方式
     * @param url 待爬取链接
     * @return 页面源码
     */
    public String httpClientWikiCN(String url){
        String html = "";
        @SuppressWarnings("resource")
        HttpClient hc = new DefaultHttpClient();
        try
        {
            String charset = "UTF-8";
            logger.info(String.format("\nFetching %s...", url));
            HttpGet hg = new HttpGet(url);
            hg.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            hg.setHeader("Accept-Language", "zh-cn,zh;q=0.5");
            hg.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:7.0.1) Gecko/20100101 Firefox/7.0.1)");
            hg.setHeader("Accept-Charset", "utf-8;q=0.7,*;q=0.7");
            hg.setHeader("Host", "zh.wikipedia.org");
            hg.setHeader("Connection", "Keep-Alive");
            HttpResponse response = hc.execute(hg);
            HttpEntity entity = response.getEntity();
            InputStream htmInput = null;
            if(entity != null){
                htmInput = entity.getContent();
                html = inStream2String(htmInput,charset);
                logger.info("爬取成功:" + " 网页长度为  " + entity.getContentLength());
            }
        }
        catch(Exception err) {
            logger.error("爬取失败...失败原因: " + err.getMessage());
        }
        finally {
            //关闭连接，释放资源
            hc.getConnectionManager().shutdown();
        }
        return html;
    }

    /**
     * 返回网页链接对应的HTML源码，httpClient方式
     * @param url 待爬取链接
     * @return
     */
    public String webmagicWikiCN(String url) {
        logger.info("connect to " + url + "....");
        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
//		Html html = httpClientDownloader.download(url);
        Html html = httpClientDownloader.download(url, "utf-8");
        String content = html.toString();
        logger.info("success connect to : " + url);
        return content;
    }

    /**
     * 取两个整数之间的随机数
     * @param min 最小值
     * @param max 最大值
     * @return 随机数大小
     */
    public int random(int min, int max){
        Random random = new Random();
        int s = random.nextInt(max)%(max-min+1) + min;
        return s;
    }

    /**
     * 输入流转为字符串流
     * @param in_str
     * @param charset
     */
    public String inStream2String(InputStream in_st,String charset) throws IOException {
        BufferedReader buff = new BufferedReader(new InputStreamReader(in_st, charset));
        StringBuffer res = new StringBuffer();
        String line = "";
        while((line = buff.readLine()) != null){
            res.append(line);
        }
        return res.toString();
    }

    /**
     * 保存字符串流到本地html文件
     * @param filePath 文件路径
     * @param str 需要保存的字符串
     */
    public void saveHtml(String filePath, String str) {
        try {
            OutputStreamWriter outs = new OutputStreamWriter(new FileOutputStream(filePath, true), "utf-8");
            outs.write(str);
            outs.close();
        } catch (IOException e) {
            logger.info("Error at save html...");
            e.printStackTrace();
        }
    }

    /**
     * 解析一个html字符串
     * @param html 待解析的字符串
     */
    public Document parseHtmlText(String html) {
//		String html = "<html><head><title>First parse</title></head>"
//				  + "<body><p>Parsed HTML into a doc.</p></body></html>";
        Document doc = Jsoup.parse(html);
        return doc;
    }

}
