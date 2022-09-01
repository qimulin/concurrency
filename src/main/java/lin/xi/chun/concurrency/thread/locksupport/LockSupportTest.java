package lin.xi.chun.concurrency.thread.locksupport;

import java.util.concurrent.locks.LockSupport;

/**
 * @author Lin.xc
 * @date 2020/6/22
 */
public class LockSupportTest {

    public static void main(String[] args) {
        Thread parkThread = new Thread(new ParkThread());
        parkThread.start();
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        System.out.println("开始线程唤醒");
        LockSupport.unpark(parkThread);
        System.out.println("结束线程唤醒");

    }

    static class ParkThread implements Runnable{

        @Override
        public void run() {
            System.out.println("开始线程阻塞");
            LockSupport.park();
            System.out.println("结束线程阻塞");
        }
    }

    /**
     * 结果打印：(第1行和第2行因为是异步的，所以它顺序是不一定的，但是4一定是在3后面的)
     * 1 开始线程唤醒
     * 2 开始线程阻塞
     * 3 结束线程唤醒
     * 4 结束线程阻塞
     * */
}
