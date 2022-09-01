package lin.xi.chun.concurrency.thread.activity.starvation;

import lin.xi.chun.concurrency.thread.activity.dead_lock.philosophers_dining.Chopstick;
import lin.xi.chun.concurrency.thread.activity.dead_lock.philosophers_dining.Philosopher;

/**
 * @author zhou.wu
 * @description:
 * 很多教程中把饥饿定义为，一个线程由于优先级太低，始终得不到 CPU 调度执行，也不能够结束，
 * 饥饿的情况不易演示，讲读写锁时会涉及饥饿问题
 * @date 2022/8/15
 **/
public class StarvationTest {

    /**
     * 还是哲学家就餐问题
     * */
    public static void main(String[] args) {
        // 共享的5根筷子
        Chopstick c1 = new Chopstick("1");
        Chopstick c2 = new Chopstick("2");
        Chopstick c3 = new Chopstick("3");
        Chopstick c4 = new Chopstick("4");
        Chopstick c5 = new Chopstick("5");
        new Philosopher("苏格拉底", c1, c2).start();
        new Philosopher("柏拉图", c2, c3).start();
        new Philosopher("亚里士多德", c3, c4).start();
        new Philosopher("赫拉克利特", c4, c5).start();
        // 为解决死锁，按顺序选择加锁（如果不好想象，就想象两把锁的例子Lock1和lock2）
        new Philosopher("阿基米德", c1, c5).start();
    }
}
/**
 * 后记：
 * 结果你会发现[赫拉克利特]获取锁的概览大，[阿基米德]线程基本就获取不到锁，这就是一种饥饿现象。
 * 上述例子虽然用上锁顺序一致性解决了"死锁"问题，但是导致了"饥饿"问题
 * */
