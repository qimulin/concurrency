package lin.xi.chun.concurrency.thread.synchronize.biased;

import lin.xi.chun.concurrency.jol.MySimpleClassLayout;
import lombok.extern.slf4j.Slf4j;

import java.util.Vector;
import java.util.concurrent.locks.LockSupport;

/**
 * -XX:BiasedLockingStartupDelay=0 禁用偏向锁延迟
 * 当撤销偏向锁达到阈值（<40），最大39次后，jvm 会这样觉得，自己确实偏向错了，根本就不该偏向。于是整个类的所有对象都会变为不可偏向的，新建的对象也是不可偏向的（
 * 【自解注意！！！】 不可偏向的不一定是指无锁的情况，只要对象不是可偏向的，都是满足条件的
 * @author zhou.wu
 * @description: 批量撤销
 * @date 2022/8/8
 **/
@Slf4j
public class BatchRevoke {

    static Thread t1,t2,t3;

    public static void main(String[] args) throws InterruptedException {
        test4();
    }

    private static void test4() throws InterruptedException {
        Vector<Dog> list = new Vector<>();
        int loopNumber = 39;
        t1 = new Thread(() -> {
            for (int i = 0; i < loopNumber; i++) {
                Dog d = new Dog();
                list.add(d);
                synchronized (d) {  // 给loopNumber个对象加锁
                    log.debug(i + "\t" + MySimpleClassLayout.printMarkDown(d));
                }
            }
            // 唤醒t2
            LockSupport.unpark(t2);
        }, "t1");
        t1.start();

        t2 = new Thread(() -> {
            LockSupport.park(); // 等待t1的通知
            log.debug("===============> ");
            for (int i = 0; i < loopNumber; i++) {
                Dog d = list.get(i);
                log.debug(i + "\t" + MySimpleClassLayout.printMarkDown(d));
                synchronized (d) {
                    log.debug(i + "\t" + MySimpleClassLayout.printMarkDown(d));
                }
                log.debug(i + "\t" + MySimpleClassLayout.printMarkDown(d));
            }
            LockSupport.unpark(t3); // t2唤醒t3
        }, "t2");
        t2.start();

        t3 = new Thread(() -> {
            LockSupport.park(); // 等待t2唤醒
            log.debug("===============> ");
            for (int i = 0; i < loopNumber; i++) {
                Dog d = list.get(i);
                log.debug(i + "\t" + MySimpleClassLayout.printMarkDown(d));
                synchronized (d) {
                    log.debug(i + "\t" + MySimpleClassLayout.printMarkDown(d));
                }
                log.debug(i + "\t" + MySimpleClassLayout.printMarkDown(d));
            }

           /* // 从这里开始，新创建的对象就是非偏向的
            Dog d = new Dog();
            log.debug("demo \t" + MySimpleClassLayout.printMarkDown(d));
            synchronized (d) {
                // 即使加锁，轻量级锁也还是非偏向的
                log.debug("demo \t" + MySimpleClassLayout.printMarkDown(d));
            }
            // 解锁后，更是非偏向的
            log.debug("demo \t" + MySimpleClassLayout.printMarkDown(d));*/
        }, "t3");
        t3.start();
        t3.join();
        // 前面已经撤销了39次了，所以这是第40次，开始就是不可偏向的状态了（注意：批量重定向是针对类的，所以）
        // 这里最终打印的是non-biasable不可偏向的，要是t3的循环里判断条件设置为loopNumber-1，会大仙最终打印的是biasable可偏向的
        log.debug(MySimpleClassLayout.printMarkDown(new Dog()));
    }
}
/**
 * 由{@link zhou.wu.mytest.thread.synchronize.biased.BatchRebias}例子可知
 * // t1
 * [t1] 0   00011111 01100010 11110000 00000101
 * …… 中间同 ……
 * [t1] 38  00011111 01100010 11110000 00000101
 *
 * // t2在t1通知后，前19（i=0~18）个都是被撤销偏向的
 * [t2-1] 0 00011111 01100010 11110000 00000101 // 原偏向t1
 * [t2-2] 0 00100000 00001111 11110001 01011000 // 升级轻量级锁
 * [t2-3] 0 00000000 00000000 00000000 00000001 // 退出临界区，撤销锁状态
 * …… 中间同 ……
 * [t2-1] 18 00011111 01100010 11110000 00000101
 * [t2-2] 18 00100000 00001111 11110001 01011000
 * [t2-3] 18 00000000 00000000 00000000 00000001
 * // 至此，已撤销了19次
 * // 循环到第20个，会开始将list中的Dog对象实例都重偏向为t2；
 * [t2-1] 19 00011111 01100010 11110000 00000101
 * [t2-2] 19 00011111 01100011 00001001 00000101 // 重偏向t2
 * [t2-3] 19 00011111 01100011 00001001 00000101
 * …… 中间同，一直重偏向t3到 ……
 * [t2-1] 38 00011111 01100010 11110000 00000101
 * [t2-2] 38 00011111 01100011 00001001 00000101 // 重偏向t2
 * [t2-3] 38 00011111 01100011 00001001 00000101
 *
 * // t3在t2通知后，由于上一步的操作，list中前19个对象已经撤销偏向过了(不可偏向了)，所以这19个的synchronized打印的结果是加轻量级锁；
 * [t3-1] 0 00000000 00000000 00000000 00000001
 * [t3-2] 0 00100000 00011111 11101110 01011000 // 轻量级锁
 * [t3-3] 0 00000000 00000000 00000000 00000001 // 不可偏向
 * …… 中间同 ……
 * [t3-1] 18 00000000 00000000 00000000 00000001
 * [t3-2] 18 00100000 00011111 11101110 01011000 // 轻量级锁
 * [t3-3] 18 00000000 00000000 00000000 00000001 // 不可偏向
 * // 但在第20个开始，上一步是将后面的20个（i=19~38）已经批量重偏向给t2了，不会再次进行偏向了（偏向锁重偏向一次之后不可再次重偏向），所以在t3这边还需对这后20个进行撤销偏向
 * [t3-1] 19 00011111 01100011 00001001 00000101 // 偏向t2
 * [t3-2] 19 00100000 00011111 11101110 01011000 // 轻量级锁
 * [t3-3] 19 00000000 00000000 00000000 00000001 // 不可偏向
 * …… 中间同 ……
 * [t3-1] 38 00011111 01100011 00001001 00000101 // 偏向t2
 * [t3-2] 38 00100000 00011111 11101110 01011000 // 轻量级锁
 * [t3-3] 38 00000000 00000000 00000000 00000001 // 不可偏向
 * // 至此撤销19+20 已经达到39次
 * // 最后new的Dog对象（理论上此时）怎么样都是已经不可偏向
 * [main] new Dog 00000000 00000000 00000000 00000001
 *
 * 简单总结，对于一个类，按默认参数来说：
 * - 单个偏向撤销的计数达到 20，就会进行批量重偏向。
 * - 距上次批量重偏向 25 秒内，计数达到 40，就会发生批量撤销。
 * - 每隔 (>=) 25 秒，会重置在 [20, 40) 内的计数，这意味着可以发生多次批量重偏向。
 * 当然，本例的实验对于时间的把控并没有那么细。还复现不了所有的情况。
 * 注意：对于一个类来说，批量撤销只能发生一次，因为批量撤销后，该类禁用了可偏向属性，后面该类的对象都是不可偏向的，包括新创建的对象。
 *
 * 简单总结2：
 * - 批量重偏向和批量撤销是针对类的优化，和对象无关。
 * - 对象的偏向锁重偏向一次之后不可再次重偏向。
 * - 当某个类已经触发批量撤销机制后，JVM会默认当前类产生了严重的问题，剥夺了该类的新实例对象使用偏向锁的权利
 * */
