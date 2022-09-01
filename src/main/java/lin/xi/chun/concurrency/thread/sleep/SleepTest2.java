package lin.xi.chun.concurrency.thread.sleep;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author zhou.wu
 * @description: Sleep方法测试
 * @date 2022/7/30
 **/
@Slf4j
public class SleepTest2 {

    static ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread("t1") {
            @Override
            public void run() {
                lock.lock();
                log.info("lock");
                try {
                    log.info("enter sleep");
                    // 睡眠
                    TimeUnit.SECONDS.sleep(3);
                    log.info("continue execute……current state:{}", getState());
                } catch (InterruptedException e) {
                    log.info("wake up!current state:{}", getState());
                    e.printStackTrace();
                } finally {
                    log.info("unlock");
                    lock.unlock();
                }
            }
        };

        // 先启动t1
        t1.start();
        TimeUnit.MILLISECONDS.sleep(50);

        Thread t2 = new Thread("t2") {
            @Override
            public void run() {
                lock.lock();
                log.info("lock");
                try {
                    log.info("do something");
                }finally {
                    log.info("unlock");
                    lock.unlock();
                }
            }
        };
        t2.start();

        t1.join();
        t2.join();
    }
}
