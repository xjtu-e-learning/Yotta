package Yotta.start;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


/**
 * 服务启动执行的程序
 * 优先级：1>2
 */

@Component
@Order(value = 1)
public class StartOfOS implements CommandLineRunner {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void run(String... args) throws Exception {
        logger.info(">>>>>>>>>>>>>>>服务启动执行，开始设置phantomjs操作<<<<<<<<<<<<<");
        // 指定PhantomJS 可执行程序的位置
        if ("Linux".equals(System.getProperty("os.name"))) {
            System.setProperty("phantomjs.binary.path", StartOfOS.class.getClassLoader().getResource("").getPath() + "phantomjs/phantomjs");
        } else {
            System.setProperty("phantomjs.binary.path", StartOfOS.class.getClassLoader().getResource("").getPath() + "phantomjs/phantomjs.exe");
            System.setProperty("webdriver.ie.driver", StartOfOS.class.getClassLoader().getResource("").getPath() + "phantomjs/IEDriverServer.exe");
            System.setProperty("webdriver.chrome.driver", StartOfOS.class.getClassLoader().getResource("").getPath() + "phantomjs/chromedriver.exe");
        }

    }


}

