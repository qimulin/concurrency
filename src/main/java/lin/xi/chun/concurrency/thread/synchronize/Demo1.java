package lin.xi.chun.concurrency.thread.synchronize;

import lombok.extern.slf4j.Slf4j;

/**
 * @author lin.xc
 * @date 2020/11/27
 **/
@Slf4j
public class Demo1 {
    public static void main(String[] args) throws InterruptedException {
        Demo1 demo1 = new Demo1();
        new Thread(() -> {
            demo1.syncMethod(Thread.currentThread().getName(), 1);
            log.debug(Thread.currentThread().getName()+"再次执行！");
            demo1.syncMethod(Thread.currentThread().getName(), 1);
        }).start();

        Thread t1 = new Thread(() -> demo1.syncMethod(Thread.currentThread().getName(), 2), "t1");
        Thread t2 = new Thread(() -> demo1.syncMethod(Thread.currentThread().getName(), 1), "t2");
        Thread t3 = new Thread(() -> demo1.syncMethod(Thread.currentThread().getName(), 1), "t3");

        t1.start();
        t2.start();
        t3.start();
    }

    /**
     * 同步方法
     * */
    public void syncMethod(String threadName, Integer flagId){
        System.out.println("进入方法，参数："+threadName+"，"+flagId);
        synchronized(flagId){
            try {
                log.debug(threadName+" 执行同步方法需要5s");
                Thread.sleep(5000);
                log.debug(threadName+" 执行同步方法完毕");
            } catch (InterruptedException e) {
                log.error(e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
