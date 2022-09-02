package lin.xi.chun.concurrency.juc.atomic.accumulator;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhou.wu
 * @description: 不要用于实践！！！原因：被挡住的线程需要频繁的空循环，消耗cpu
 * @date 2022/9/2
 **/
@Slf4j
public class LockCas {

    // 0:没加锁；1：加锁
    private AtomicInteger state = new AtomicInteger(0);

    public void lock() {
        // 若是用了while-true，假如t1线程和t2线程都来，t1和t2都得到0，t1线程加锁成功，将0改为1；这时候若t1不解锁，则t2会一直在空循环
        while (true) {
            if (state.compareAndSet(0, 1)) {
                break;
            }
        }
    }

    public void unlock() {
        log.debug("unlock...");
        state.set(0);
    }
}
