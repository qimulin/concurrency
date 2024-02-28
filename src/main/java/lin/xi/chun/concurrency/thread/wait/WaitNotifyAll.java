package lin.xi.chun.concurrency.thread.wait;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 利用wait进行改进
 * @author zhou.wu
 * @date 2022/8/9
 **/
@Slf4j
public class WaitNotifyAll {

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
                room.notify();  // 【虚假唤醒】极易唤醒小南，但是小南不要外卖，所以没有唤醒到该唤醒的小女
            }
        }, "送外卖的").start();
    }
}
/**
 * 后记：
 * 用 notifyAll 仅解决某个线程的唤醒问题，但使用 if + wait 判断仅有一次机会，一旦条件不成立，就没有重新判断的机会了
 * 解决方法，用 while + wait，当条件不成立，再次 wait，见{@link zhou.wu.mytest.thread.wait.WaitNotifyWhile}
 *
 * 虚假唤醒解释：
 * 多线程环境的编程中，我们经常遇到让多个线程等待在一个条件上，等到这个条件成立的时候我们再去唤醒这些线程，让它们接着往下执行代码的场景。
 * 假如某一时刻条件成立，所有的线程都被唤醒了，然后去竞争锁，因为同一时刻只会有一个线程能拿到锁，其他的线程都会阻塞到锁上无法往下执行，
 * 等到成功争抢到锁的线程消费完条件，释放了锁，后面的线程继续运行，拿到锁时这个条件很可能已经不满足了，这个时候线程应该继续在这个条件上阻塞下去，
 * 而不应该继续执行，如果继续执行了，就说发生了虚假唤醒。
 *【简单自解】：被唤醒的线程在其被唤醒时已不能满足继续执行的条件，但是继续执行了。
 *
 * 什么是假唤醒？
 * 当一个条件满足时，很多线程都被唤醒了，但是只有其中部分是有用的唤醒，其它的唤醒都是无用功
 * 1.比如说买货，如果商品本来没有货物，突然进了一件商品，这是所有的线程都被唤醒了，但是只能一个人买，所以其他人都是假唤醒，获取不到对象的锁
 *
 * 为什么 if会出现虚假唤醒？
 * 因为if只会执行一次，执行完会接着向下执行if（）外边的
 * 而while不会，直到条件满足才会向下执行while（）外边的，所以通常while+wait可以避免虚假唤醒，继续等待被唤醒。
 *
 * */
