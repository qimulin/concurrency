package lin.xi.chun.concurrency.thread.synchronize.biased;

import lombok.extern.slf4j.Slf4j;
import org.openjdk.jol.info.ClassLayout;

/**
 * -XX:BiasedLockingStartupDelay=0 禁用偏向锁延迟
 * @author zhou.wu
 * @description: 撤销偏向锁-由于调用wait/notify，这个很好理解，wait和notify本来就是配合重量级锁使用的，当然就不再适用于偏向锁了
 * @date 2022/8/8
 **/
@Slf4j
public class RevokeBias2 {

    public static void main(String[] args) throws InterruptedException {
        Dog d = new Dog();
        Thread t1 = new Thread(() -> {
            log.debug(ClassLayout.parseInstance(d).toPrintable());  // 还未对d上锁
            synchronized (d) {
                log.debug(ClassLayout.parseInstance(d).toPrintable());  // 刚上锁后的d
                try {
                    d.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug(ClassLayout.parseInstance(d).toPrintable());  // d调用wait方法之后，t2已结提醒t1继续了
            }
        }, "t1");
        t1.start();

        new Thread(() -> {
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (d) {
                log.debug("notify");
                d.notify();
            }
        }, "t2").start();
    }
}
/**
 * 简化结果：
 * [t1] - 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000101 (biasable; age: 0)    d对象一开始未被任何线程上锁，但是它默认可偏向
 * [t1] - 00000000 00000000 00000000 00000000 00011111 10110011 11111000 00000101 (biased: 0x000000000007cd62; epoch: 0; age: 0)    t1线程刚对d进行上锁，此时锁偏向于t1
 * [t2] - notify
 * [t1] - 00000000 00000000 00000000 00000000 00011100 11010100 00001101 11001010 (fat lock: 0x000000001c7a061a)    调用wait方法后，这已结是重量级锁层面的使用了
 * */
