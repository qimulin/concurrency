package lin.xi.chun.concurrency.juc.count_down_latch;

import java.util.concurrent.CountDownLatch;

/**
 * 利用它可以实现类似计数器的功能。比如有一个任务A，它要等待其他4个任务执行完毕之后才能执行，
 * 此时就可以利用CountDownLatch来实现这种功能了。
 * @author lin.xc
 * @date 2020/8/7
 *
 * CountDownLatch{
 *      // 参数count为计数值
 *      public CountDownLatch(int count) {  };
 *      // 调用await()方法的线程会被挂起，它会等待直到count值为0才继续执行
 *      public void await() throws InterruptedException { };
 *      // 和await()类似，只不过等待一定的时间后count值还没变为0的话就会继续执行
 *      public boolean await(long timeout, TimeUnit unit) throws InterruptedException { };
 *      // 将count值减1
 *      public void countDown() { };
 * }
 **/
public class CountDownLatchTest {

    public static void main(String[] args) {
        int num = 2;
        // 初始计数两个线程
        final CountDownLatch latch = new CountDownLatch(num);
        new Thread(){
            public void run() {
                try {
                    System.out.println("子线程"+Thread.currentThread().getName()+"正在执行");
                    Thread.sleep(3000);
                    System.out.println("子线程"+Thread.currentThread().getName()+"执行完毕");
                    // 将count值减1
                    latch.countDown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            };
        }.start();

        new Thread(){
            public void run() {
                try {
                    System.out.println("子线程"+Thread.currentThread().getName()+"正在执行");
                    Thread.sleep(3000);
                    System.out.println("子线程"+Thread.currentThread().getName()+"执行完毕");
                    // 将count值再减1
                    latch.countDown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            };
        }.start();

        try {
            System.out.println("等待"+num+"个子线程执行完毕...");
            // 调用await()方法的线程会被挂起，它会等待直到count值为0才继续执行
            latch.await();
            System.out.println(num+"个子线程都已经执行完毕，才会走到这里！！！");
            System.out.println("继续执行主线程");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
