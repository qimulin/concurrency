package lin.xi.chun.concurrency.thread.activity.live_lock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author zhou.wu
 * @description: 活锁：活锁出现在两个线程互相改变对方的结束条件【注意：是结束条件，而不是锁了】，最后谁也无法结束
 * 活锁：并没有阻塞线程；解决方案：执行时间交错开，增加一些随机的睡眠时间等
 * @date 2022/8/15
 **/
@Slf4j
public class LiveLockTest {

    static volatile int count = 10;
    static final Object lock = new Object();

    public static void main(String[] args) {
        new Thread(() -> {
            // 期望减到 0 退出循环
            while (count > 0) {
                try {
                    TimeUnit.MILLISECONDS.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                count--;
                log.debug("count: {}", count);
            }
        }, "t1").start();

        new Thread(() -> {
            // 期望超过 20 退出循环
            while (count < 20) {
                try {
                    TimeUnit.MILLISECONDS.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                count++;
                log.debug("count: {}", count);
            }
        }, "t2").start();
    }
}
/**
 * 结果：
 * 本地测的时候出现过一次正常停止的情况，但大多数情况都会执行的比较久。估计如果count的便捷比0~20更广的话，估计有可能锁的更久，甚至不结束。
 * */
