package lin.xi.chun.concurrency.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @author zhou.wu
 * @description 尝试提交方法
 * @date 2023/8/21
 **/
@Slf4j
public class TestSubmit {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        Future<String> future = pool.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                log.debug("running...");
                Thread.sleep(1000);
                return "ok";
            }
        });
        // 主线程这边阻塞着，会等待结果。用的也是保护性暂停模式
        log.debug("{}", future.get());
    }
}
