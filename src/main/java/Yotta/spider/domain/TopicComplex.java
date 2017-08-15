package Yotta.spider.domain;

import java.util.List;

/**
 * 一门课程三层主题及其上下位关系的信息
 * Created by yuanhao on 2017/5/2.
 */
public class TopicComplex {

    List<Topic> topicFirst;
    List<Topic> topicSecond;
    List<Topic> topicThird;
    List<TopicRelation> classToFirst;
    List<TopicRelation> firstToSecond;
    List<TopicRelation> secondToThird;

    public TopicComplex(List<Topic> topicFirst, List<Topic> topicSecond, List<Topic> topicThird, List<TopicRelation> classToFirst, List<TopicRelation> firstToSecond, List<TopicRelation> secondToThird) {
        this.topicFirst = topicFirst;
        this.topicSecond = topicSecond;
        this.topicThird = topicThird;
        this.classToFirst = classToFirst;
        this.firstToSecond = firstToSecond;
        this.secondToThird = secondToThird;
    }

    public TopicComplex() {
    }

    @Override
    public String toString() {
        return "TopicComplex{" +
                "topicFirst=" + topicFirst +
                ", topicSecond=" + topicSecond +
                ", topicThird=" + topicThird +
                ", classToFirst=" + classToFirst +
                ", firstToSecond=" + firstToSecond +
                ", secondToThird=" + secondToThird +
                '}';
    }

    public List<Topic> getTopicFirst() {
        return topicFirst;
    }

    public void setTopicFirst(List<Topic> topicFirst) {
        this.topicFirst = topicFirst;
    }

    public List<Topic> getTopicSecond() {
        return topicSecond;
    }

    public void setTopicSecond(List<Topic> topicSecond) {
        this.topicSecond = topicSecond;
    }

    public List<Topic> getTopicThird() {
        return topicThird;
    }

    public void setTopicThird(List<Topic> topicThird) {
        this.topicThird = topicThird;
    }

    public List<TopicRelation> getClassToFirst() {
        return classToFirst;
    }

    public void setClassToFirst(List<TopicRelation> classToFirst) {
        this.classToFirst = classToFirst;
    }

    public List<TopicRelation> getFirstToSecond() {
        return firstToSecond;
    }

    public void setFirstToSecond(List<TopicRelation> firstToSecond) {
        this.firstToSecond = firstToSecond;
    }

    public List<TopicRelation> getSecondToThird() {
        return secondToThird;
    }

    public void setSecondToThird(List<TopicRelation> secondToThird) {
        this.secondToThird = secondToThird;
    }
}
