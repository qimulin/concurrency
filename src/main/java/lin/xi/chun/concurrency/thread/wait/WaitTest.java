package lin.xi.chun.concurrency.thread.wait;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * wait方法尝试
 * @author zhou.wu
 * @date 2022/8/9
 **/
@Slf4j
public class WaitTest {

    final static Object obj = new Object();

    public static void main(String[] args) throws InterruptedException {

        /* 没获得锁就直接使用wait、notify、notifyAll，会报java.lang.IllegalMonitorStateException异常 */
//        obj.wait();

        new Thread(() -> {
            synchronized (obj) {
                log.debug("执行....");
                try {
                    obj.wait(); // 让线程在obj上一直等待下去
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("其它代码....");
            }
        }, "t1").start();

        new Thread(() -> {
            synchronized (obj) {
                log.debug("执行....");
                try {
                    obj.wait(); // 让线程在obj上一直等待下去
//                    obj.wait(5000); // 有期限ms的等待，时间一到，回到Runnable
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("其它代码....");
            }
        }, "t2").start();

        // 主线程两秒后执行
        TimeUnit.SECONDS.sleep(2);
        log.debug("唤醒 obj 上其它线程");
        synchronized (obj) {
            obj.notify(); // 唤醒obj上一个线程
//            obj.notifyAll(); // 唤醒obj上所有等待线程
        }
    }
}

/**
 * wait还有一个比较坑的重载方法
 * public final void wait(long timeout, int nanos) throws InterruptedException {
 * 	if (timeout < 0) {
 * 		throw new IllegalArgumentException("timeout value is negative");
 *        }
 *
 * 	if (nanos < 0 || nanos > 999999) {
 * 		throw new IllegalArgumentException(
 * 							"nanosecond timeout value out of range");
 *    }
 *
 * 	if (nanos > 0) {   // 只要nanos参数值合法，都会让执行timeout++
 * 		timeout++;
 *    }
 *
 * 	wait(timeout);
 * }
 * */

