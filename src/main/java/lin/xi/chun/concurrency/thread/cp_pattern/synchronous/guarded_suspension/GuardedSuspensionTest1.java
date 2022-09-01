package lin.xi.chun.concurrency.thread.cp_pattern.synchronous.guarded_suspension;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhou.wu
 * @description: 测试1
 * @date 2022/8/13
 **/
@Slf4j
public class GuardedSuspensionTest1 {
    public static void main(String[] args) {
        GuardedObject guardedObject = new GuardedObject();
        new Thread(() -> {
            try {
                // 子线程执行下载
                List<String> response = download();
                log.debug("download complete...");
                guardedObject.complete(response);
                log.info("我比Join好的地方是，join必须等我这个线程结束，而我Notify后还能干其他事");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, "t1").start();
        log.debug("waiting...");
        // 主线程阻塞等待
        Object response = guardedObject.get();
        log.debug("get response: [{}] lines", ((List<String>) response).size());
    }

    public static List<String> download() throws IOException{
        log.info("模拟下载内容");
        List<String> list = new ArrayList<>();
        list.add("第一行内容");
        list.add("第二行内容");
        list.add("第三行内容");
        return list;
    }
}
