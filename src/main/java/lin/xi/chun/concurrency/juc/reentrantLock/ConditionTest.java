package lin.xi.chun.concurrency.juc.reentrantLock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author zhou.wu
 * @description: 条件变量测试
 * synchronized 中也有条件变量，就是我们讲原理时那个 waitSet 休息室，当条件不满足时进入 waitSet 等待
 * ReentrantLock 的条件变量比 synchronized 强大之处在于，它是支持多个条件变量的，这就好比
 * - synchronized 是那些不满足条件的线程都在一间休息室等消息
 * - 而 ReentrantLock 支持多间休息室，有专门等烟的休息室、专门等早餐的休息室、唤醒时也是按休息室来唤醒
 *
 * 使用要点（和synchronized内用wait类似）：
 * - await 前需要获得锁
 * - await 执行后，会释放锁（不阻塞其他线程），进入 conditionObject 等待
 * - await 的线程被唤醒（或打断、或超时）去重新竞争 lock 锁
 * - 竞争 lock 锁成功后，从 await 后继续执行
 * @date 2022/8/15
 **/
@Slf4j
public class ConditionTest {

    static ReentrantLock lock = new ReentrantLock();
    static Condition waitCigaretteQueue = lock.newCondition();
    static Condition waitBreakfastQueue = lock.newCondition();
    static volatile boolean hasCigrette = false;
    static volatile boolean hasBreakfast = false;

    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            try {
                lock.lock();
                while (!hasCigrette) {
                    try {
                        // 在等烟的条件里去等待，注意方法名是await（自解：a wait）
                        waitCigaretteQueue.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("等到了它的烟");

         /*       if (!hasCigrette) {
                    try {
                        // await也有多种方法，带超时时间、不可打断的等
                        waitCigaretteQueue.await(2, TimeUnit.SECONDS);
                        log.debug("等待超时！不等待了");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }*/
            } finally {
                lock.unlock();
            }
        }).start();

        new Thread(() -> {
            try {
                lock.lock();
                while (!hasBreakfast) {
                    try {
                        // 在等早餐的条件里去等待
                        waitBreakfastQueue.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("等到了它的早餐");
            } finally {
                lock.unlock();
            }
        }).start();

        TimeUnit.SECONDS.sleep(5);
        sendBreakfast();
        TimeUnit.SECONDS.sleep(1);
        sendCigarette();
    }

    /**
     * 送烟方法
     * */
    private static void sendCigarette() {
        lock.lock();
        try {
            log.debug("送烟来了");
            hasCigrette = true;
            // 通知等烟的
            waitCigaretteQueue.signal();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 送早餐方法
     * */
    private static void sendBreakfast() {
        lock.lock();
        try {
            log.debug("送早餐来了");
            hasBreakfast = true;
            // 通知等早餐的
            waitBreakfastQueue.signal();
        } finally {
            lock.unlock();
        }
    }
}
