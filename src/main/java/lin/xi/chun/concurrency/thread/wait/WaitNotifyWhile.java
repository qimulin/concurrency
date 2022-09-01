package lin.xi.chun.concurrency.thread.wait;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author zhou.wu
 * @description: 利用wait进行改进
 * @date 2022/8/9
 **/
@Slf4j
public class WaitNotifyWhile {

    static final Object room = new Object();
    static boolean hasCigarette = false;
    static boolean hasTakeout = false;

    public static void main(String[] args) throws InterruptedException{

        // 小南线程，有烟干活
        new Thread(() -> {
            synchronized (room) {
                log.debug("有烟没？[{}]", hasCigarette);
                // 将if改成while
                while (!hasCigarette) { // 即使被唤醒，但若不是送烟的，就还需要下一轮的等待
                    log.debug("没烟，先歇会！");
                    try {
                        room.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("有烟没？[{}]", hasCigarette);
                if (hasCigarette) {
                    log.debug("可以开始干活了");
                } else {
                    log.debug("没干成活...");
                }
            }
        }, "小南").start();

        new Thread(() -> {
            synchronized (room) {
                Thread thread = Thread.currentThread();
                log.debug("外卖送到没？[{}]", hasTakeout);
                if (!hasTakeout) {
                    log.debug("没外卖，先歇会！");
                    try {
                        room.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("外卖送到没？[{}]", hasTakeout);
                if (hasTakeout) {
                    log.debug("可以开始干活了");
                } else {
                    log.debug("没干成活...");
                }
            }
        }, "小女").start();

        TimeUnit.SECONDS.sleep(1);

        new Thread(() -> {
            synchronized (room) {
                hasTakeout = true;
                log.debug("外卖到了噢！");
                room.notifyAll();  // 【虚假唤醒】极易唤醒小南，但是小南不要外卖，所以没有唤醒到该唤醒的小女
            }
        }, "送外卖的").start();
    }
}
/**
 * 后记：
 * 所以推荐的wait操作的方法，建议格式：
 * synchronized(lock) {
 *  while(条件不成立) {
 *      lock.wait();
 *  }
 *  // 干活
 * }
 * //另一个线程
 * synchronized(lock) {
 *  lock.notifyAll();
 * }
 * */
