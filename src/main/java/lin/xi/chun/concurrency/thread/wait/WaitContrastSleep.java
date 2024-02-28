package lin.xi.chun.concurrency.thread.wait;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * wait对比sleep，开始之前先看看
 * sleep(long n) 和 wait(long n) 的区别
 * 1) sleep 是 Thread 方法，而 wait 是 Object 的方法
 * 2) sleep 不需要强制和 synchronized 配合使用，但 wait 需要和 synchronized 一起用
 * 3) sleep 在睡眠的同时，不会释放对象锁的，但 wait 在等待的时候会释放对象锁
 * 共同点：它们状态都是 TIMED_WAITING
 * @author zhou.wu
 * @date 2022/8/9
 **/
@Slf4j
public class WaitContrastSleep {

    static final Object room = new Object();
    static boolean hasCigarette = false;
    static boolean hasTakeout = false;

    public static void main(String[] args) throws InterruptedException{

        // 小南线程，有烟干活
        new Thread(() -> {
            synchronized (room) {
                log.debug("有烟没？[{}]", hasCigarette);
                if (!hasCigarette) {
                    log.debug("没烟，先歇会！");
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("有烟没？[{}]", hasCigarette);
                if (hasCigarette) {
                    log.debug("可以开始干活了");
                }
            }
        }, "小南").start();

        // 小南用sleep方法缺点：这些其他人线程都不能干活了，因为sleep仍然占有锁（自解：sleep是线程层面的阻塞，对象owner的那把锁没有解开）
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                synchronized (room) {
                    log.debug("可以开始干活了");
                }
            }, "其它人").start();
        }

        TimeUnit.SECONDS.sleep(1);
        // 送烟线程
        new Thread(() -> {
            // 这里能不能加 synchronized (room)？ 会让送烟的无法送到烟
            hasCigarette = true;
            log.debug("烟到了噢！");
        }, "送烟的").start();
    }
}
/**
 * 结果解读：
 * - 其它干活的线程，都要一直阻塞，效率太低
 * - 小南线程必须睡足 2s 后才能醒来，就算烟提前送到，也无法立刻醒来
 * - 加了 synchronized (room) 后，就好比小南在里面反锁了门睡觉，烟根本没法送进门，main 没加synchronized 就好像 main 线程是翻窗户进来的
 * 解决方法，使用 wait - notify 机制, 见{@link zhou.wu.mytest.thread.wait.WaitNotifyAll}
 * */
