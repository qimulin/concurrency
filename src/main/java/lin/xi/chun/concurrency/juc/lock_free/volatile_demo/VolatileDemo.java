package lin.xi.chun.concurrency.juc.lock_free.volatile_demo;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;

/**
 * @author zhou.wu
 * @description Volatile Demo
 * @date 2023/7/27
 **/
@Slf4j
public class VolatileDemo {

    public static void main(String[] args) {
        VolatileDemoObj obj = new VolatileDemoObj();
        // 主线程先修改name的值
//        obj.getName("main");
        for (int i = 0; i < 20; i++) {
            new Thread(() -> {
                try {
                    int mill = (new Random()).nextInt(10000);
                    log.info("sleep {} ms", mill);
                    Thread.sleep(mill);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.info("result: {}", obj.getName(Thread.currentThread().getName()));
            }, "t-"+i).start();
        }
    }
}
