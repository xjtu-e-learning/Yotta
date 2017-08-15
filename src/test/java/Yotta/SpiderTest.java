package Yotta;

import Yotta.spider.domain.Topic;
import Yotta.spider.service.SpiderTopicService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanhao on 2017/5/2.
 */
public class SpiderTest {

    private static SpiderTopicService spiderTopicService = new SpiderTopicService();

    public static void main(String[] args) {
        test1();
    }

    public static void test1() {
        List<Topic> list = new ArrayList<>();
        Topic topic1 = new Topic("你好", "你好", 1L, 1L);
        Topic topic2 = new Topic("你", "你", 1L, 1L);
        Topic topic3 = new Topic("我好", "我好", 1L, 1L);
        Topic topic4 = new Topic("你", "你", 1L, 1L);
        list.add(topic1);
        list.add(topic2);
        list.add(topic3);
        list.add(topic4);
        List<Topic> result = spiderTopicService.deleteDuplicateTopic(list);
        for (Topic topic : result) {
            System.out.println(topic.toString());
        }
    }

}
