package lin.xi.chun.concurrency.juc.cyclic_barrier;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 字面意思回环栅栏，通过它可以实现让一组线程等待至某个状态之后再全部同时执行。
 * 叫做回环是因为当所有等待线程都被释放以后，CyclicBarrier可以被重用
 * @author lin.xc
 * @date 2020/8/7
 * CyclicBarrier{
 *      // 参数parties指让多少个线程或者任务等待至barrier状态
 *      public CyclicBarrier(int parties, Runnable barrierAction) {
 *       ……
 *      }
 *      // 参数barrierAction为当这些线程都达到barrier状态时会执行的内容
 *      public CyclicBarrier(int parties) {
 *       ……
 *      }
 *      // 比较常用，用来挂起当前线程，直至所有线程都到达barrier状态再同时执行后续任务；
 *      public int await() throws InterruptedException, BrokenBarrierException { };
 *      // 让这些线程等待至一定的时间，如果还有线程没有到达barrier状态就直接让到达barrier的线程执行后续任务
 *      public int await(long timeout, TimeUnit unit)throws InterruptedException,BrokenBarrierException,TimeoutException { };
 * }
 **/
public class CyclicBarrierTest {

    public static void main(String[] args) {
        int N = 4;
        CyclicBarrier barrier  = new CyclicBarrier(N);
        for(int i=0;i<N;i++){
            new Writer(barrier,(4-i)*1000).start();
        }
        System.out.println("main最后一行");
//        while(true){
//            if(barrier.isBroken()){
//                System.out.println("测试这样isBroken是否能处于最后");
//                break;
//            }else{
//                System.out.println("---检查中---");
//                try {
//                    // 休息3s
//                    Thread.sleep(3000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
    }

    static class Writer extends Thread {
        private CyclicBarrier cyclicBarrier;
        private Long sleepTime;

        public Writer(CyclicBarrier cyclicBarrier) {
            this.cyclicBarrier = cyclicBarrier;
            this.sleepTime = 2000L;
        }

        public Writer(CyclicBarrier cyclicBarrier, long sleepTime) {
            this.cyclicBarrier = cyclicBarrier;
            this.sleepTime = sleepTime;
        }

        @Override
        public void run() {
            System.out.println("线程"+Thread.currentThread().getName()+"正在写入数据...sleepTime="+sleepTime);
            try {
                Thread.sleep(this.sleepTime);      //以睡眠来模拟写入数据操作
                System.out.println("线程"+Thread.currentThread().getName()+"写入数据完毕，等待其他线程写入完毕");
//                cyclicBarrier.await();
                // 超过3秒，继续执行
                cyclicBarrier.await(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }catch(BrokenBarrierException e){
                e.printStackTrace();
            } catch (TimeoutException e) {
                System.out.println("线程"+Thread.currentThread().getName()+"中异常...");
                e.printStackTrace();
            }
            System.out.println("线程"+Thread.currentThread().getName()+"向此执行...");
        }
    }
}
