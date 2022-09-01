package lin.xi.chun.concurrency.juc.reentrantLock.philosophers_dining;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author zhou.wu
 * @description: 筷子
 * @date 2022/8/15
 **/
public class Chopstick extends ReentrantLock {  // 继承可重入锁
    String name;
    public Chopstick(String name) {
        this.name = name;
    }
    @Override
    public String toString() {
        return "筷子{" + name + '}';
    }
}