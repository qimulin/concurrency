package lin.xi.chun.concurrency.cp_pattern.asynchronous.producer_consumer;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhou.wu
 * @description: 模拟下载
 * @date 2022/8/13
 **/
@Slf4j
public class Downloader {
    public static final List<String> download() throws IOException {
        log.info("模拟下载内容");
        List<String> list = new ArrayList<>();
        list.add("第一行内容");
        list.add("第二行内容");
        list.add("第三行内容");
        return list;
    }
}
