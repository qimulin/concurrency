package lin.xi.chun.concurrency.threadpool.use;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.concurrent.Callable;

/**
 * @author lin.xc
 * @date 2021/5/28
 **/
@Slf4j
public class CallTask1 implements Callable<String> {

    /** 随机数 */
    private Random random= new Random();

    @Override
    public String call() throws Exception {
        long start = System.currentTimeMillis();
        Thread currThread = Thread.currentThread();
        String currThreadName = currThread.getName();
        if("default-pool-23".equals(currThreadName)){
            throw new RuntimeException("特意制造的运行时异常！");
        }
        try {
            for(int i=0; i<10; i++){
//                int ms = (random.nextInt(4)+1)*100;
                int ms =100;
                currThread.sleep(ms);
                log.debug("间隔{}ms 线程{}第{}次操作！", ms, currThreadName, i);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long time = System.currentTimeMillis()-start;
        log.info("***** 线程{}本次耗时{}ms *****", currThreadName, time);
        return currThreadName;
    }
}
