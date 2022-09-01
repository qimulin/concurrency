package lin.xi.chun.concurrency.thread.activity.dead_lock.philosophers_dining;

/**
 * @author zhou.wu
 * @description: 哲学家就餐死锁现象
 * 如果没有死锁，程序会一直运行下去；当大家都只拿一根筷子，就出现死锁了
 * @date 2022/8/15
 **/
public class PhilosopherDiningTest {
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
        new Philosopher("阿基米德", c5, c1).start();
    }
}
/**
 * 结果：
 * 执行不多会，就执行不下去了，出现死锁了。
 * */
