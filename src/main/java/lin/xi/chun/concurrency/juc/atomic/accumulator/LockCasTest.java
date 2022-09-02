package lin.xi.chun.concurrency.juc.atomic.accumulator;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author zhou.wu
 * @description: CAS来实现锁
 * @date 2022/9/2
 **/
@Slf4j
public class LockCasTest {
    public static void main(String[] args) {
        LockCas lock = new LockCas();

        new Thread(() -> {
            log.debug("begin...");
            lock.lock();
            try {
                log.debug("lock...");
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }).start();

        new Thread(() -> {
            log.debug("begin...");
            lock.lock();
            try {
                log.debug("lock...");
            } finally {
                lock.unlock();
            }
        }).start();
    }
}
