package lin.xi.chun.concurrency.juc.lock_free.volatile_demo;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author zhou.wu
 * @description My Counter测试
 * @date 2023/7/27
 **/
@Slf4j
public class MyCounterDemo {

    public static void main(String[] args) {
        MyCounter obj = new MyCounter();
        // volatile的可见性，保证了这些线程只会从主存中取值
        for (int i = 0; i < 1000; i++) {
            new Thread(() -> {
                log.info("thread[{}] increment", Thread.currentThread().getName());
                obj.increment();
            }, "t-"+i).start();
        }
        // 等待，让上面循环全部完成
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("increment finish, count={}", obj.getCount());
    }

}
