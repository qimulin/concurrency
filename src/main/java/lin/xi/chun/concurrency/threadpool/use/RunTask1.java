package lin.xi.chun.concurrency.threadpool.use;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;

/**
 * @author lin.xc
 * @date 2021/5/28
 **/
@Slf4j
public class RunTask1 implements Runnable {

    /** 随机数 */
    private Random random= new Random();

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        Thread currThread = Thread.currentThread();
        String currThreadName = currThread.getName();
        try {
            for(int i=0; i<10; i++){
//                int ms = (random.nextInt(4)+1)*100;
                int ms =100;
                currThread.sleep(ms);
                log.debug("间隔{}ms 线程{}第{}次操作！", ms, currThreadName, i);
//                System.out.println("间隔"+ms+"ms 线程"+currThreadName+"第"+i+"次操作！");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long time = System.currentTimeMillis()-start;
        log.info("***** 线程{}本次耗时{}ms *****", currThreadName, time);
//        System.out.println("***** 线程"+currThreadName+"本次耗时"+time+"ms *****");
    }

    public void sRun(){
        long start = System.currentTimeMillis();
        Thread currThread = Thread.currentThread();
        String currThreadName = currThread.getName();
        try {
            for(int i=0; i<10; i++){
//                int ms = (random.nextInt(4)+1)*100;
                int ms =100;
                Thread.sleep(ms);
                log.info("间隔{}ms 线程{}第{}次操作！", ms, currThreadName, i);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long time = System.currentTimeMillis()-start;
        log.info("***** 线程{}本次耗时{}ms *****", currThreadName, time);
    }
}
