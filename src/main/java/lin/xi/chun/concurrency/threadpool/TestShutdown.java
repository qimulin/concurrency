package lin.xi.chun.concurrency.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author zhou.wu
 * @description 尝试invokeAny方法：找到一个最先执行的任务
 * @date 2023/8/21
 **/
@Slf4j
public class TestShutdown {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 第三个任务会进队列
        ExecutorService pool = Executors.newFixedThreadPool(2);
        // 异步提交
        pool.submit(() -> {
           log.debug("begin-1: {}");
           Thread.sleep(1000);
           log.debug("end-1");
           return "1";
        });

        pool.submit(() -> {
            log.debug("begin-2");
            Thread.sleep(1000);
            log.debug("end-2");
            return "2";
        });

        pool.submit(() -> {
            log.debug("begin-3");
            Thread.sleep(1000);
            log.debug("end-3");
            return "3";
        });

        log.debug("shutdown");
        pool.shutdown();

//        log.debug("shutdownNow");
//        List<Runnable> runnables = pool.shutdownNow();
//        log.info("runnables size: {}", runnables.size());
//        runnables.forEach(r -> System.out.println(r));

//        log.debug("submit-4");
//        pool.submit(() -> {
//            log.debug("begin-4");
//            Thread.sleep(1000);
//            log.debug("end-4");
//            return "4";
//        });

        log.debug("1-isTerminated: {}", pool.isTerminated());
        // 调用shutdown后，由于"调用线程"（本例为main线程）并不会等待所有任务运行结束，因此如果它想在线程池 TERMINATED 后做些事情，可以利用此方法等待
        pool.awaitTermination(5, TimeUnit.SECONDS);
       
//        // 感受"Tidying"状态
//        while(!pool.isTerminated()){
//            // debug到这，可以看到ctl是2，自解：对应的是 ThreadPoolExecutor中 private static final int TIDYING    =  2 << COUNT_BITS; int值等于1073741824
//            // 为什么不是等值对应？还得看源码的处理事怎样的
//            log.debug("Tidying");
//        }

        log.debug("2-isTerminated: {}", pool.isTerminated());
        log.debug("main end");
    }
}
/**
 * 【后记】
 * - 当用shutdown方法停止的时候，最终三个任务都会被执行完成
 * - 当用shutdown方法停止的时候，不会接收新任务，会将等待队列中的任务返回，并用 interrupt 的方式中断正在执行的任务
 * - awaitTermination的等待是有超时时间的，若执行了shutdown或者shutdownNow在超时时间内状态变为“Terminated”，或者真超时了，那就继续调用线程的代码执行
 * */