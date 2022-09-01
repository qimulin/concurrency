package lin.xi.chun.concurrency.juc.reentrantLock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author zhou.wu
 * @description: 锁超时
 * @date 2022/8/15
 **/
@Slf4j
public class TimeoutTest {
    public static void main(String[] args) {
        ReentrantLock lock = new ReentrantLock();
        Thread t1 = new Thread(() -> {
            log.debug("启动...");
            // tryLock 尝试获取锁
      /*      if (!lock.tryLock()) { // 获取不到锁，没有任何等待时间
                log.debug("获取立刻失败，返回");
                return;
            }*/
            try {
                // 与lock方法的区别是，lock方法是获取不到锁会阻塞，像synchronized一样
                // 而tryLock，会在规定的超时时间（若有设置超时时间）内都在尝试获取锁：如果获取到了，就返回true，没有就返回false
                if (!lock.tryLock(1, TimeUnit.SECONDS)) {   // 带超时时间参数的tryLock
                    log.debug("获取等待 1s 后失败，返回");
                    return;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.debug("获取不到锁");
                return;
            }
            try {
                log.debug("获得了锁");
            } finally {
                lock.unlock();
            }
        }, "t1");

        lock.lock();
        log.debug("获得了锁");
        t1.start();
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            log.debug("解锁");
            lock.unlock();
        }
    }
}
