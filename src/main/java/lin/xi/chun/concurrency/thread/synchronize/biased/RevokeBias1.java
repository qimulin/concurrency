package lin.xi.chun.concurrency.thread.synchronize.biased;

import lombok.extern.slf4j.Slf4j;
import org.openjdk.jol.info.ClassLayout;

/**
 * -XX:BiasedLockingStartupDelay=0 禁用偏向锁延迟
 * 我们知道调用被锁对象的hashCode方法会撤销偏向锁，不仅如此，本例还有一种情况还能撤销偏向锁
 * @author zhou.wu
 * @description: 撤销偏向锁-由于其他线程错开接触
 * @date 2022/8/8
 **/
@Slf4j
public class RevokeBias1 {

    public static void main(String[] args) throws InterruptedException {
        test2();
    }

    private static void test2() throws InterruptedException {
        Dog d = new Dog();
        Thread t1 = new Thread(() -> {
            synchronized (d) {
                log.debug(ClassLayout.parseInstance(d).toPrintable());
            }
            synchronized (RevokeBias1.class) {
                RevokeBias1.class.notify();  // 通知等待的线程，让线程交错开
            }
            // 如果不用 wait/notify 使用 join 必须打开下面的注释
            // 因为：t1 线程不能结束，否则底层线程可能被 jvm 重用作为 t2 线程，底层线程 id 是一样的
             /*try {
             System.in.read();
             } catch (IOException e) {
             e.printStackTrace();
             }*/
        }, "t1");
        t1.start();

        Thread t2 = new Thread(() -> {
            synchronized (RevokeBias1.class) {   // 不锁要测试的d对象，所以用本类.class来进行wait和notify
                try {
                    RevokeBias1.class.wait();    // 等待，配合线程t1交错开
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.debug(ClassLayout.parseInstance(d).toPrintable());
            synchronized (d) {
                log.debug(ClassLayout.parseInstance(d).toPrintable());
            }
            log.debug(ClassLayout.parseInstance(d).toPrintable());
        }, "t2");
        t2.start();
    }
}

/**
 * 简化结果：
 * [t1] 00011111 11000011 10011000 00000101   偏向锁标记为1（倒数第3位）
 * [t2] 00011111 11000011 10011000 00000101   t2此时还未争抢d，所以d还是持有t1的偏向锁
 * [t2] 00100000 10000000 11101111 11110000   t2也需要争夺共享资源d，所以锁升级为轻量级锁
 * [t2] 00000000 00000000 00000000 00000001   t2释放锁，重置为无锁(不可偏向)
 * */
