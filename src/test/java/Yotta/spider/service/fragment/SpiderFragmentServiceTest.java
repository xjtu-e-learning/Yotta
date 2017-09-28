package Yotta.spider.service.fragment;

import Yotta.spider.domain.Topic;
import junit.framework.TestCase;

/**
 *
 * Created by yuanhao on 2017/9/28.
 */
public class SpiderFragmentServiceTest extends TestCase {
    public void testStoreFragmentByTopic() throws Exception {
        Topic topic = new Topic();
        topic.setTopicId(1L);
        topic.setTopicName("程式语言历史");
        topic.setTopicUrl("https://zh.wikipedia.org/wiki/%E7%A8%8B%E5%BC%8F%E8%AA%9E%E8%A8%80%E6%AD%B7%E5%8F%B2");
        SpiderFragmentService spiderFragmentService = new SpiderFragmentService();
        spiderFragmentService.storeFragmentByTopic(topic);
    }

}