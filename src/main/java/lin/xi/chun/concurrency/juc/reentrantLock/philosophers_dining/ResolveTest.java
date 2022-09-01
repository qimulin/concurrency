package lin.xi.chun.concurrency.juc.reentrantLock.philosophers_dining;

/**
 * @author zhou.wu
 * @description: 用ReentrantLock方式解决“哲学家就餐”问题
 * @date 2022/8/15
 **/
public class ResolveTest {

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
        new Philosopher("阿基米德", c5, c1).start();    // 还是允许c5->c1顺序
    }
}
