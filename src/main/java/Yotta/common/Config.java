package Yotta.common;

/**
 * Created by shilei on 2017/3/14.
 */
public class Config {

    /**
     * 可视化时候，提取数据时候用的
     */
    public static String PICTURE_HOST = "115.182.41.85:8080";  //存rdf图片的时候，需要些写入图片你的uri，写明图片服务器的地址
    public static String PICTURE_Suffix = "/KG/SpiderTest/getImage";

    /**
     * 奥鹏的API网址，获取课程的章节等信息
     */
    public static String MOOC2U_API_GET_COURSE = "http://www.mooc2u.com/API/Open/CourseOpen/GetCourseData";


    /**
     * Spider 爬虫参数设置
     *
     * @author 郑元浩
     */
    public final static int CONTENTLENGTH = 30; // 保留下来的文本内容的长度

//    public static String project = "";  //工程的名字，设计到api的路径
//    public static String server = "localhost:8080";
//    public static String imageAPICata = "spider";
//    public static String catalogTable = "datainput_catalog";
//    public static String relationTable = "datainput_relation";
//    public static String termTable = "datainput_term"; // term的表名
//    public static String systemTable = "system_step_status";
//    public static int imageCount = 3; // 设置每个分面下的图片数量
}
