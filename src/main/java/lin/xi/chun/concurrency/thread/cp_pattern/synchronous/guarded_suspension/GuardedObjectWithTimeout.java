package lin.xi.chun.concurrency.thread.cp_pattern.synchronous.guarded_suspension;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhou.wu
 * @description: 带超时版的同步模式之保护性暂停
 * @date 2022/8/13
 **/
@Slf4j
public class GuardedObjectWithTimeout {

    private Object response;
    private final Object lock = new Object();

    // 获取结果
    public Object get(long millis) {
        synchronized (lock) {
            // 1) 记录最初时间
            long begin = System.currentTimeMillis();
            // 2) 已经经历的时间
            long timePassed = 0;
            while (response == null) {
                // 4) 假设 millis 是 1000，结果在 400 时唤醒了，那么还有 600 要等
                long waitTime = millis - timePassed;    // 剩余需要等待的时间
                log.debug("waitTime: {}", waitTime);
                if (waitTime <= 0) {    // 没有需要再等待的时间了，就退出循环
                    log.debug("break...");
                    break; }
                try {
                    lock.wait(waitTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 3) 如果提前被唤醒，这时已经经历的时间假设为 400
                timePassed = System.currentTimeMillis() - begin;
                log.debug("timePassed: {}, object is null {}",
                        timePassed, response == null);
            }
            return response; }
    }

    public void complete(Object response) {
        synchronized (lock) {
            // 条件满足，通知等待线程
            this.response = response;
            log.debug("notify...");
            lock.notifyAll();
        }
    }
}

