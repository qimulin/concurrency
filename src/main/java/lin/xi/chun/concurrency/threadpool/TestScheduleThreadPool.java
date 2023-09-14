package lin.xi.chun.concurrency.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;

/**
* 测试 ScheduleThreadPool
* @author zhou.wu
* @date 2023/9/13
*/
@Slf4j
public class TestScheduleThreadPool {

    public static void main(String[] args) throws Exception{
        // 定义2个核心线程数的线程池
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
//        delayExecuteTask(executor);
//        scheduleExecuteTask(executor);
        scheduleExecuteTask2(executor);
    }

    /**
    * 延时执行任务
    * @author zhou.wu
    * @date 2023/9/13
    * @param executor 线程池对象
    */
    private static void delayExecuteTask(ScheduledExecutorService executor) {
        log.debug("start...");
        // 添加两个任务，希望它们都在1s后执行
        // 任务1
        executor.schedule(() -> {
            log.debug("task1");
            // 情况1：故意睡2s
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) { }
            // 情况2：异常代码
            int i = 1/0;    // 只这么写，不会被抛出，也不会在控制台打印出
        }, 1000, TimeUnit.MILLISECONDS);
        // 任务2
        executor.schedule(() -> {
            log.debug("task2");
        }, 1000, TimeUnit.MILLISECONDS);
    }

    /**
     * 定时执行任务
     * @author zhou.wu
     * @date 2023/9/13
     * @param executor 线程池对象
     */
    private static void scheduleExecuteTask(ScheduledExecutorService executor) {
        log.debug("start...");
        // 在2s后执行, 间隔1s后执行一次
        // scheduleAtFixedRate 以固定速率执行任务
        executor.scheduleAtFixedRate(() -> {
            log.debug("running...");
            // 情况1：故意睡眠，模拟任务执行比较久，这会影响下一次定时任务执行时间
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) { }
        }, 2000, 1000, TimeUnit.MILLISECONDS);
    }

    /**
     * 定时执行任务2
     * @author zhou.wu
     * @date 2023/9/13
     * @param executor 线程池对象
     */
    private static void scheduleExecuteTask2(ScheduledExecutorService executor) {
        log.debug("start...");
        // 区别于scheduleAtFixedRate
        ScheduledFuture<?> scheduledFuture = executor.scheduleWithFixedDelay(() -> {
            log.debug("running...");
            // 情况1：故意睡眠，模拟任务执行比较久，这会影响下一次定时任务执行时间
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
            }

        }, 2000, 1000, TimeUnit.MILLISECONDS);

        // 【自补充】关于scheduledFuture的使用
        try {
            Thread.sleep(6000);

            // 做法1：取消任务的执行
//            boolean cancel = scheduledFuture.cancel(true);
//            log.debug("cancel result: {}", cancel);
//            if(scheduledFuture.isCancelled()){
//                log.debug("task was canceled");
//            }else{
//                log.debug("task not canceled");
//            }

            /*
            * 做法2：关闭线程池，周期性任务默认情况下会一直执行下去，直到调用线程池的关闭方法来终止任务的执行。
            * 当关闭线程池时，线程池内部会取消周期性任务的执行。关闭线程池会导致线程池停止接受新的任务，并尝试取消尚未开始执行的任务。
            * 对于周期性任务，如果线程池被关闭，尚未开始执行的周期性任务将被取消。已经开始执行的周期性任务会继续执行完当前的任务，并且不会再进行后续的调度。
            * */
            executor.shutdown();
            // 等待任务执行完成
            executor.awaitTermination(10, TimeUnit.SECONDS);
            // 判断任务是否任务完成
            if (scheduledFuture.isDone()) {
                log.debug("Task was complete. isCancelled: {}", scheduledFuture.isCancelled());
                // 获取任务的执行结果，任务被取消的话，这里返回的都是会有异常
                Object result = scheduledFuture.get();  // 这里关联ExecutionException，我就不注释掉代码了
            } else {
                log.debug("Task not complete.");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}

/**
 * 后记：
 * 对于delayExecuteTask方法，本例线程池大小若设置成1，就还是串行执行了，即会被先执行的任务时间所影响。但是当前一个任务有异常情况，却不会影响下一个任务的运行，
 * 很好地弥补了 {@link lin.xi.chun.concurrency.threadpool.TestTimer} 实现的问题
 *
 * 对于上述代码中异常代码没被抛出或打印，可以采用：
 * - 方法1：自己try-catch主动捉异常，例如：
 * ExecutorService pool = Executors.newFixedThreadPool(1);
 * pool.submit(() -> {
 *      try {
 *      log.debug("task1");
 *      int i = 1 / 0;
 *  } catch (Exception e) {
 *      log.error("error:", e);
 *  }
 * });
 * - 方法2：使用 Future
 * ExecutorService pool = Executors.newFixedThreadPool(1);
 * Future<Boolean> f = pool.submit(() -> {
 *      log.debug("task1");
 *      int i = 1 / 0;
 *      return true;
 * });
 * log.debug("result:{}", f.get());
 * 在Java中，ScheduledExecutorService的scheduleWithFixedDelay方法不能直接返回每一次任务的结果。该方法用于周期性地执行任务，但它的返回类型是ScheduledFuture<?>，并不包含任务的执行结果。
 * 如果你需要获取每一次任务的返回结果，一种方法是在任务内部使用共享变量或回调函数来传递结果。你可以在任务执行完成后，将结果存储在共享变量中或通过回调函数将结果传递出来。
 *
 * 对于scheduleExecuteTask中的scheduleAtFixedRate方法，当任务执行时间超过了间隔时间，间隔被『撑』到了执行时间
 *
 * 对于scheduleExecuteTask2中的scheduleWithFixedDelay方法，会让前任务执行完，再根据设置的固定延时去执行
 * */
