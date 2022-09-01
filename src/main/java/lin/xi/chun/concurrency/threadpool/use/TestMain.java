package lin.xi.chun.concurrency.threadpool.use;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author lin.xc
 * @date 2021/5/28
 **/
@Slf4j
public class TestMain {
    public static void main(String[] args) {
        ThreadFactory namedThreadFactory
                = new ThreadFactoryBuilder().setNameFormat("default-pool-%d").build();
        ExecutorService executor = new ThreadPoolExecutor(
                10,
                50,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(5),
                namedThreadFactory,
                // 交由主线程处理
                new ThreadPoolExecutor.CallerRunsPolicy());
        int size = 62;
        
        try {
            long start = System.currentTimeMillis();
            // submit测试
            List<Future> runResult = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                RunTask1 task1 = new RunTask1();
                runResult.add(executor.submit(task1));
//                Future<?> f = executor.submit(task1);
//                f.get();
//                executor.execute(task1);
//                task1.sRun();
            }
            for (int i = 0; i<runResult.size(); i++) {
                Future f = runResult.get(i);
                f.get();
                log.info("i={} 线程操作成功！", i);
            }
            // invoke测试
//            List<CallTask1> callTask1s = new ArrayList<>();
//            for (int i = 0; i < size; i++) {
//                CallTask1 task1 = new CallTask1();
//                callTask1s.add(task1);
//            }
//            List<Future<String>> futures = executor.invokeAll(callTask1s);
//            for (int i = 0; i<futures.size(); i++) {
//                Future<String> f = futures.get(i);
//                String fr = f.get();
//                log.info("i={} 线程{}操作成功！", i, fr);
//            }
            log.info("任务执行耗时{}ms", System.currentTimeMillis()-start);
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            log.info("关闭线程池");
            executor.shutdown();
        }
    }
}
