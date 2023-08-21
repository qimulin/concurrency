package lin.xi.chun.concurrency.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author zhou.wu
 * @description 尝试invokeAny方法：找到一个最先执行的任务
 * @date 2023/8/21
 **/
@Slf4j
public class TestInvokeAny {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        // 执行任一一个返回结果即可
        String result = pool.invokeAny(Arrays.asList(
                () -> {
                    log.debug("begin-1");
                    Thread.sleep(1000);
                    log.debug("end-1");
                    return "1";
                },
                () -> {
                    log.debug("begin-2");
                    Thread.sleep(500);
                    log.debug("end-2");
                    return "2";
                },
                () -> {
                    log.debug("begin-3");
                    Thread.sleep(2000);
                    log.debug("end-3");
                    return "3";
                }
        ));
        log.debug("{}", result);
    }
}
/**
 * 【后记】：
 * invokeAny：所有被安排到Thread中的任务都会执行，哪个任务先成功执行完毕，就返回此任务执行结果，其它任务取消。
 * 自注：不是代表其他任务执行都失效，主要是看其他任务执行的效率，执行时间短的容易执行的很多代码逻辑。
 * 当只有一个Thread做任务时，那就只会返回这个任务的结果，因为此时只有它在执行。
 * */