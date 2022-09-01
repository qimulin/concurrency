package lin.xi.chun.concurrency.juc.reentrantLock.philosophers_dining;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author zhou.wu
 * @description: 哲学家
 * @date 2022/8/15
 **/
@Slf4j
public class Philosopher extends Thread {

    // 左边那根筷子
    Chopstick left;
    // 右边那根筷子
    Chopstick right;

    public Philosopher(String name, Chopstick left, Chopstick right) {
        super(name);
        this.left = left;
        this.right = right;
    }
    private void eat() {
        log.debug("eating...");
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            // 尝试获得左手筷子
            if (left.tryLock()) {
                try {
                    // 尝试获得右手筷子
                    if (right.tryLock()) {
                        try {
                            // 得到两把筷子，就可以吃饭了
                            eat();
                        } finally {
                            // 释放右手筷子
                            right.unlock();
                        }
                    }
                } finally {
                    // 【重点】获取右手筷子失败了，会释放左手筷子
                    left.unlock();
                }
            }
        }
    }
}