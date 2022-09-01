package lin.xi.chun.concurrency.thread.daemon;

import java.util.concurrent.TimeUnit;

/**
 * @author zhou.wu
 * @description: 守护线程
 * @date 2022/8/6
 **/
public class DaemonTest {
    public static void main(String[] args) throws InterruptedException {
        /* 通常情况下，如果t不是守护线程，那么主程序结束，并不会影响t线程的结束；
        * 但一旦将将t设置为守护线程，那么当所有非守护线程结束后，守护线程也会跟着一起结束的（本例中非守护线程主要就是Main线程）*/
        Thread t = new Thread() {
            @Override
            public void run() {
                while (true){
                    try {
                        TimeUnit.MILLISECONDS.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("running……");
                }
            }
        };
        // 设置该线程为守护线程
        t.setDaemon(true);
        t.start();
        Thread.sleep(1000);
        System.out.println("主线程结束！");
    }
}
