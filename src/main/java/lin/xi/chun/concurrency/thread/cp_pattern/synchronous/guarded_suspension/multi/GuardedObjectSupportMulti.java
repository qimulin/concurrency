package lin.xi.chun.concurrency.thread.cp_pattern.synchronous.guarded_suspension.multi;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhou.wu
 * @description: 支持多任务
 * @date 2022/8/13
 **/
@Slf4j
public class GuardedObjectSupportMulti {

    // 标识 Guarded Object
    private int id;

    public GuardedObjectSupportMulti(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }

    private Object response;
    private final Object lock = new Object();

    // 获取结果
    // timeout 表示要等待多久 2000
    public Object get(long timeout) {
        synchronized (this) {
            // 开始时间 15:00:00
            long begin = System.currentTimeMillis();
            // 经历的时间
            long passedTime = 0;
            while (response == null) {
                // 这一轮循环应该等待的时间
                long waitTime = timeout - passedTime;
                // 经历的时间超过了最大等待时间时，退出循环
                if (timeout - passedTime <= 0) {
                    break;
                }
                try {
                    this.wait(waitTime); // 虚假唤醒 15:00:01
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 求得经历时间
                passedTime = System.currentTimeMillis() - begin; // 15:00:02 1s
            }
            return response;
        }
    }

    // 产生结果
    public void complete(Object response) {
        synchronized (lock) {
            // 条件满足，通知等待线程
            this.response = response;
            log.debug("notify...");
            lock.notifyAll();
        }
    }
}

