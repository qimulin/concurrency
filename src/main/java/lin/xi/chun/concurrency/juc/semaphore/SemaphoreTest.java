package lin.xi.chun.concurrency.juc.semaphore;

import java.util.concurrent.Semaphore;

/**
 * 翻译成字面意思为 信号量，Semaphore可以控同时访问的线程个数，通过 acquire() 获取一个许可，
 * 如果没有就等待，而 release() 释放一个许可。
 * @author lin.xc
 * @date 2020/8/11
 * public Semaphore(int permits) {
 *     //参数permits表示许可数目，即同时可以允许多少线程进行访问
 *     sync = new NonfairSync(permits);
 * }
 * public Semaphore(int permits, boolean fair) {
 *      //这个多了一个参数fair表示是否是公平的，即等待时间越久的越先获取许可
 *     sync = (fair)? new FairSync(permits) : new NonfairSync(permits);
 * }
 *
 * public void acquire() throws InterruptedException {  }     //获取一个许可
 * public void acquire(int permits) throws InterruptedException { }    //获取permits个许可
 * public void release() { }          //释放一个许可
 * public void release(int permits) { }    //释放permits个许可
 *
 * //尝试获取一个许可，若获取成功，则立即返回true，若获取失败，则立即返回false
 * public boolean tryAcquire() { };
 * //尝试获取一个许可，若在指定的时间内获取成功，则立即返回true，否则则立即返回false
 * public boolean tryAcquire(long timeout, TimeUnit unit) throws InterruptedException { };
 * //尝试获取permits个许可，若获取成功，则立即返回true，若获取失败，则立即返回false
 * public boolean tryAcquire(int permits) { };
 * //尝试获取permits个许可，若在指定的时间内获取成功，则立即返回true，否则则立即返回false
 * public boolean tryAcquire(int permits, long timeout, TimeUnit unit) throws InterruptedException { };
 *
 * CountDownLatch和CyclicBarrier都能够实现线程之间的等待，只不过它们侧重点不同：
 * CountDownLatch一般用于某个线程A等待若干个其他线程执行完任务之后，它才执行；
 * 而CyclicBarrier一般用于一组线程互相等待至某个状态，然后这一组线程再同时执行；
 * 另外，CountDownLatch是不能够重用的，而CyclicBarrier是可以重用的。
 *
 * Semaphore其实和锁有点类似，它一般用于控制对某组资源的访问权限。
 **/
public class SemaphoreTest {

    public static void main(String[] args) {
        int N = 8;            //工人数
        Semaphore semaphore = new Semaphore(5); //机器数目
        for(int i=0;i<N;i++)
            new Worker(i,semaphore).start();
    }

    static class Worker extends Thread{
        private int num;
        private Semaphore semaphore;
        public Worker(int num,Semaphore semaphore){
            this.num = num;
            this.semaphore = semaphore;
        }

        @Override
        public void run() {
            try {
                semaphore.acquire();
                System.out.println("工人"+this.num+"占用一个机器在生产...");
                Thread.sleep(2000);
                System.out.println("工人"+this.num+"释放出机器");
                semaphore.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
