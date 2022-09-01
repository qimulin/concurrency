package lin.xi.chun.concurrency.juc.reentrantLock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author zhou.wu
 * @description: 可打断测试
 * 为什么synchronized不能中断ReentrantLock能中断？
 * ReentrantLock是可中断锁，而synchronized 不是可中断锁。ReentrantLock可以获取到锁状态的。
 *
 * 例如：线程A和B都要获取对象O的锁定，假设A获取了对象O锁，B将等待A释放对O的锁定
 * - 如果使用 synchronized， 如果A不释放，B将一直阻塞下去，A不释放锁，B就一直不被中断（线程状态是Blocked的情况下无法被中断）
 * - 如果使用 ReentrantLock，如果A不释放，可以使B在等待了足够长的时间以后，中断等待，而干别的事情
 * @date 2022/8/15
 **/
@Slf4j
public class InterruptiblyTest {

    public static void main(String[] args) {
        useReentrantLock();
//        useSynchronized();
    }

    private static void useReentrantLock(){
        ReentrantLock lock = new ReentrantLock();
        Thread t1 = new Thread(() -> {
            log.debug("启动...");
            try {
                // 要可打断的话，能不能用lock方法，得用lockInterruptibly()方法
                // 如果没有竞争，那么此方法就会获取lock对象锁
                // 如果有竞争，进入阻塞队列，可以被其他线程用interrupt方法打断
                lock.lockInterruptibly();
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.debug("等锁的过程中被打断，没有获得锁！");
                // 在这里说明没有获得锁，就不需要往下解锁，直接返回
                return;
            }
//            lock.lock();    // 若用lock是不可被interrupt方法打断的，会一直等锁解开
            try {
                log.debug("获得了锁");
            } finally {
                lock.unlock();
            }
        }, "t1");

        // 主线程加锁加锁，t1线程就会进入阻塞队列
        lock.lock();
        log.debug("获得了锁");
        t1.start();
        try {
            TimeUnit.SECONDS.sleep(3);
            t1.interrupt();
            log.debug("执行打断");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 竞争不到锁而处于BLOCKED状态的线程能否响应中断？
     *
     * 其一：由于处于BLOCKED状态只能通过异常机制来响应中断，能使线程进入BLOCKED状态的只有synchronized关键字。但是关键字是无法抛出异常的，方法才能抛出异常。
     * 其二：一个线程不可能一直处于BLOCKED状态，不然就是线程饥饿或者线程死锁了，虽然BLOCKED无法响应中断，但是进入运行状态就可以感知到了，所以是没问题的。
     *
     * 自解：
     * 1、InterruptedException是要识别在使用方法上的，synchronized是关键字，不能设置抛出这种异常；
     * 2、BLOCKED状态本来就不不该是一个持久的状态，这个状态下本身就完全争抢不到时间片，所以结合第1点的原因，允许等到它转为其他可中断的状态时，再进行中断也行。
     * */
    private static void useSynchronized(){
        Object lock = new Object();
        Thread t1 = new Thread(() -> {
            log.debug("启动...");

            synchronized (lock){
                long i = 1000000;
                while(i-->0){
                    String str = "doSomething";
                }
            }
            log.debug("获得了锁");
        }, "t1");


        synchronized (lock){
            log.debug("获得了锁");
            t1.start();
            try {
                TimeUnit.SECONDS.sleep(3);
                // 就算调用t1的interrupt方法，还是不能对t1进行打断，这点从t1的代码都没有提醒处理InterruptedException异常能看出，synchronized关键字目前并不能强制要求处理InterruptedException
                t1.interrupt();
                log.debug("执行打断");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
