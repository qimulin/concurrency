package lin.xi.chun.concurrency.thread.interrupt;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author zhou.wu
 * @description: 中断wait方法，打断后的线程会退出阻塞（退出WAITING或TIMED_WAITING）
 * @date 2022/8/24
 **/
@Slf4j
public class InterruptWaitTest1 {

    final static Object obj = new Object();

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            synchronized (obj) {
                log.debug("执行....");
                try {
                    obj.wait();
//                    obj.wait(3000); // 让线程在obj上一直等待下去
                } catch (InterruptedException e) {
                    log.debug("被打断后state：{}", Thread.currentThread().getState());
                    e.printStackTrace();
                }
                log.debug("后续代码....");
            }
        }, "t1");
        t1.start();

        // 主线程两秒后执行
        TimeUnit.SECONDS.sleep(1);
        log.debug("中断t1[state={}]", t1.getState());
        t1.interrupt();
    }
}


