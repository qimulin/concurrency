package lin.xi.chun.concurrency.thread.synchronize.multi_method;

import java.util.concurrent.TimeUnit;

/**
 * @author zhou.wu
 * @date 2023/12/20
 **/
public class MultiSynchronizedMethodDemo {

    /**
     * 验证多线程下多个同步方法是否互相影响方法执行
     * 结论：所有synchronized修饰的方法都是互相影响的，既执行m1的时候，m2也是需要等待m1执行完释放锁的
     */
    public static void main(String[] args) {
        MultiSynchronizedMethodObj obj = new MultiSynchronizedMethodObj();

        Thread t1 = new Thread(() -> obj.method1(), "t1");
        Thread t2 = new Thread(() -> obj.method2(), "t2");
        Thread t3 = new Thread(() -> {
            obj.method1();
            // 停止1s后t2容易先争抢到资源
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            obj.method2();
            }
        , "t3");

        t1.start();
        t2.start();
        t3.start();
    }
}
