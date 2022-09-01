package lin.xi.chun.concurrency.juc.reentrantLock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author zhou.wu
 * @description: 重入测试
 * 相对于 synchronized 它具备如下特点
 * - 可中断，见 {@link InterruptiblyTest}
 * - 可以设置超时时间，见 {@link TimeoutTest}
 * - 可以设置为公平锁，见 {@link FairTest}
 * - 支持多个条件变量（类似支持多个waitSet），见 {@link ConditionTest}
 * 与 synchronized 一样，都支持可重入
 *
 * 相同点：可重入
 * 可重入是指同一个线程如果首次获得了这把锁，那么因为它是这把锁的拥有者，因此有权利再次获取这把锁
 * 如果是不可重入锁，那么第二次获得锁时，自己也会被锁挡住
 *
 * 基本语法
 * // 获取锁
 * reentrantLock.lock();    // 加锁，try块内核try块外没有区别
 * try {
 *  // 临界区
 * } finally {
 *  // 释放锁
 *  reentrantLock.unlock();
 * }
 * @date 2022/8/15
 **/
@Slf4j
public class ReentrantTest {

    /**
     * 以前把Synchronize的修饰的对象当做锁，那其实是其关联的Monitor对象才是锁
     * 而到了ReentrantLock这里，可以直接把它当成一把锁，它内部也有个等待队列
     * */
    static ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) {
        // 演示可重入
        method1();
    }

    public static void method1() {
        lock.lock();
        try {
            log.debug("execute method1");
            method2();
        } finally {
            log.debug("method1 unlock");
            lock.unlock();
        }
    }

    public static void method2() {
        lock.lock();
        try {
            log.debug("execute method2");
            method3();
        } finally {
            log.debug("method2 unlock");
            lock.unlock();
        }
    }

    public static void method3() {
        lock.lock();
        try {
            log.debug("execute method3");
        } finally {
            log.debug("method3 unlock");
            lock.unlock();
        }
    }
}
