package lin.xi.chun.concurrency.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author zhou.wu
 * @description 尝试invokeAll方法
 * @date 2023/8/21
 **/
@Slf4j
public class TestInvokeAll {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        List<Future<String>> futures = pool.invokeAll(Arrays.asList(
                () -> {
                    log.debug("begin-1");
                    Thread.sleep(1000);
                    return "1";
                },
                () -> {
                    log.debug("begin-2");
                    Thread.sleep(500);
                    return "2";
                },
                () -> {
                    log.debug("begin-3");
                    Thread.sleep(2000);
                    return "3";
                }
        ));

        futures.forEach(f -> {
            try {
                log.debug("{}", f.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }
}
