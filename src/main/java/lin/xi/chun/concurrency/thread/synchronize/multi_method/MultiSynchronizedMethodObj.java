package lin.xi.chun.concurrency.thread.synchronize.multi_method;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author zhou.wu
 * @date 2023/12/20
 **/
@Slf4j
public class MultiSynchronizedMethodObj {

    public synchronized void method1(){
        try {
            log.info("method1 start");
            TimeUnit.SECONDS.sleep(3);
            log.info("method1 end");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void method2(){
        try {
            log.info("method2 start");
            TimeUnit.SECONDS.sleep(1);
            log.info("method2 end");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void method3(){
        try {
            log.info("method3 start");
            TimeUnit.SECONDS.sleep(1);
            log.info("method3 end");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
