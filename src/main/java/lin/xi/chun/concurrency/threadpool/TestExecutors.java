package lin.xi.chun.concurrency.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhou.wu
 * @description 利用JDK Executors类使用线程池
 * @date 2023/8/18
 **/
@Slf4j(topic = "my.ExecutorsMain")
public class TestExecutors {

    public static void main(String[] args) {
//        testNewFixedThreadPool();
//        testNewCachedThreadPool();
        testNewSingleThreadExecutor();
    }

    /**
     * 测试固定大小的线程池
     * */
    public static void testNewFixedThreadPool(){
        // 自定义线程工厂：主要用于自定义命名线程名称
        ThreadFactory threadFactory = new ThreadFactory() {

            private AtomicInteger t = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                // t.getAndIncrement() 获取值并自增
                return new Thread(r, "my-pool-thread-"+t.getAndIncrement());
            }
        };
        // 固定大小线程池：任务都完成，都没有结束，main方法不会终止
        ExecutorService pool = Executors.newFixedThreadPool(2, threadFactory);

        pool.execute(() -> log.debug("1"));

        pool.execute(() -> log.debug("2"));

        pool.execute(() -> log.debug("3"));
    }

    /**
     * 带缓冲功能的线程池
     * */
    public static void testNewCachedThreadPool(){
        // 线程空闲60s后或被释放，main方法最终也会因此停止
        ExecutorService pool = Executors.newCachedThreadPool();

        pool.execute(() -> log.debug("1"));

        pool.execute(() -> log.debug("2"));

        pool.execute(() -> log.debug("3"));
    }


    /**
     * 单个线程的线程池
     * */
    public static void testNewSingleThreadExecutor(){
        // 线程空闲60s后或被释放，main方法最终也会因此停止
        ExecutorService pool = Executors.newSingleThreadExecutor();

        pool.execute(() -> {
            log.debug("1");
            // 模拟异常
            int i = 1/0;
        });

        pool.execute(() -> log.debug("2"));

        pool.execute(() -> log.debug("3"));
    }


}
