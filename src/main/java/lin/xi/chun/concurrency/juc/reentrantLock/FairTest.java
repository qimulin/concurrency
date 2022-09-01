package lin.xi.chun.concurrency.juc.reentrantLock;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author zhou.wu
 * @description: 公平测试（本意是用来解决饥饿问题的，但实际用tryLock更好，公平锁会降低并发度）
 * @date 2022/8/15
 **/
public class FairTest {

    public static void main(String[] args) throws InterruptedException {
        ReentrantLock lock = new ReentrantLock(true);
        lock.lock();
        for (int i = 0; i < 500; i++) {
            new Thread(() -> {
                lock.lock();
                try {
                    System.out.println(Thread.currentThread().getName() + " running...");

                } finally {
                    lock.unlock();
                }
            }, "t" + i).start();
        }
        // 1s 之后去争抢锁
        Thread.sleep(500);
        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " start...");
            lock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + " running...");
            } finally {
                lock.unlock();
            }
        }, "强行插入").start();
        lock.unlock();
    }
}
/**
 * 结果分析：
 * - 非公平 fair=false 的时候，强行插入，有机会在中间输出。注意：该实验不一定总能复现。讲源码的时候再回头看
 * - 设置公平 fair=true 之后，强行插入，总是在最后输出
 * */
