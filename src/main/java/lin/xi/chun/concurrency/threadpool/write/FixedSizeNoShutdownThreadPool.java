package lin.xi.chun.concurrency.threadpool.write;

import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 手写一个线程池
 * 【注意】此处先不考虑shutDown方法的处理，便更好让新手理解
 * @author Lin.xc
 * @date 2020/5/14
 */
public class FixedSizeNoShutdownThreadPool {
    /**
     * 如果需要手写一个线程池，我们需要什么？
     * 1、需要一个仓库；
     * 2、需要一个放线程的集合；
     * 3、需要一个码农来干活；
     * 4、需要初始化仓库和线程集合；
     * 5、需要向仓库放任务的方法“不阻塞”返回一个特殊值
     * 6、需要向仓库放任务的方法“阻塞”
     * 7、需要一个关闭线程池的方法
     */
    // 需要一个仓库
    private BlockingQueue<Runnable> blockingQueue;

    // 需要一个放线程的集合
    private List<Thread> workers;

    // 需要干活的：为上面workers中的一个元素对象
    public static class Worker extends Thread {

        private FixedSizeNoShutdownThreadPool pool;

        public Worker(FixedSizeNoShutdownThreadPool pool) {
            this.pool = pool;
        }

        @Override
        public void run() {
            // 去仓库拿东西
            while(true){
                Runnable task = null;
                try {
                    // 用take方法去任务，可以让其他任务阻塞地等着
                    task = this.pool.blockingQueue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(task!=null){
                    // 执行这个任务
                    task.run();
                    System.out.println("线程："+Thread.currentThread().getName()+"执行完毕");
                }
            }

        }
    }

    // 需要初始化仓库和线程集合
    @SneakyThrows
    public FixedSizeNoShutdownThreadPool(int poolSize, int taskSize)  {
        // 参数校验
        if(poolSize<=0 || taskSize<=0){
            throw new IllegalAccessException("非法参数");
        }
        // 创建blockingQueue
        this.blockingQueue = new LinkedBlockingQueue<>(taskSize);
//        this.workers = new ArrayList<>();
        // 上述写法线程不安全，所以改成下面一种写法
        this.workers = Collections.synchronizedList(new ArrayList<>());
        // 放入工作线程
        for(int i=0;i<poolSize;i++){
            Worker worker = new Worker(this);
            // 让线程就绪
            worker.start();
            // 添加到工作组中
            workers.add(worker);
        }
    }

    // 需要向仓库放任务的方法“不阻塞”返回一个特殊值
    public boolean submit(Runnable task){
        return this.blockingQueue.offer(task);
    }

    // 需要向仓库放任务的方法“阻塞”
    public void execute(Runnable task){
        try {
            this.blockingQueue.put(task);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 需要一个关闭线程池的方法
    // a、关闭的时候，仓库要停止有新的线程进来
    // b、关闭的时候，如果仓库还有任务需要执行完
    // c、关闭的时候，如果线程去仓库拿东西，不能阻塞（拿得到即可，拿不到就算）
    // d、关闭的时候，如果去仓库拿东西线程已经阻塞了，那需要中断
    private volatile boolean isWorking = true;

    public void shutDown(){
        this.isWorking = false;
    }

    // main方法使用线程池
    public static void main(String[] args) {
        FixedSizeNoShutdownThreadPool pool = new FixedSizeNoShutdownThreadPool(3,6);
        for(int i=0; i<6; i++){
//            pool.submit(
//                new Runnable() {
//                    @Override
//                    public void run() {
//                        System.out.println("一个线程"+Thread.currentThread().getName()+"放到了我们仓库中");
//                        try {
//                            // 延迟，好看出效果
//                            Thread.sleep(3000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            );

            pool.execute(
                    new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("一个线程"+Thread.currentThread().getName()+"放到了我们仓库中");
                            try {
                                // 延迟，好看出效果
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
            );
        }

        System.out.println("Main方法完毕！");
        /**
         * 从控制台结果可以看出仓库满的任务被执行掉之后才能重新再放入新的任务
         * 还有，如果7没有实现的话，控制台就没有结束，线程池就一直开在那里
         * */
    }
}
