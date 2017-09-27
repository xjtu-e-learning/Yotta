package Yotta.common.domain;

/**
 * 枚举管理所有失败原因
 * Created by 18710 on 2017/8/9.
 */
public enum ResultEnum {
    SUCCESS(200, "成功"),
    UNKONW_ERROR(-1, "未知错误"),
    DOMAINID_ERROR(100, "领域ID找不到对应的领域名，数据库中不存在该领域ID"),
    DUPLICATEDCRAWLER_ERROR(101, "领域ID对应的主题信息已经爬取，不需要再次爬取"),
    EMPTYDOMAINNAME_ERROR(102, "领域名在中文维基百科不存在目录，无法爬取主题信息"),
    DOMAINQUERY_ERROR(103, "领域查询失败"),
    DOMAININSERT_ERROR(104, "领域插入失败"),
    DOMAINDELETE_ERROR(105, "领域删除失败"),
    DOMAINUPDATE_ERROR(106, "领域更新失败"),
    PAGE_ERROR(107, "查询的页数超过最大页数"),
    SOURCE_ERROR(108, "数据源查询失败"),
    DOMAININSERTDUPLICATE_ERROR(109, "插入重复领域"),
    DOMAININSERTNOTNULL_ERROR(110, "插入领域名不能为空"),
    DOMAINTOPICNOTSPIDER_ERROR(111, "领域ID对应的主题信息没有爬取"),

    TOPICQUERY_ERROR(112, "主题查询失败，该领域的主题数据没有爬取"),
    TOPICINSERTDUPLICATE_ERROR(113, "插入重复主题"),
    TOPICINSERTNOTNULL_ERROR(114, "插入主题名不能为空"),
    TOPICINSERT_ERROR(115, "主题插入失败"),
    TOPICRELATIONINSERT_ERROR(116, "主题关系插入失败"),

    FACETQUERY_ERROR(117, "分面查询失败，该主题的分面没有爬取"),
    FACETDUPLICATEDCRAWLER_ERROR(118, "分面和碎片信息已经爬取，不需要再次爬取"),

    ;

    private Integer code;

    private String msg;

    ResultEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
