package lin.xi.chun.concurrency.thread.sleep;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author zhou.wu
 * @description: Sleep方法测试
 * @date 2022/7/30
 * sleep和wait的不同：
 * - sleep是Thread的静态方法。wait是Object的方法
 * - sleep想啥时候用就啥时候用。wait需要先获得对象锁，配合synchronized一起使用（wait方法必须执行在同步方法中执行，而sleep不需要）
 * - sleep会让出CPU的使用权，但是不会释放对象锁。wait也会让出CPU的使用权，会释放对象锁（线程在同步方法中执行sleep方法时，不会释放monitor的锁。而wait方法则会释放monitor的锁）
 * 相同点：
 * - 它们的状态都是TIMED_WAITING
 * - wait和sleep方法都可以使线程进入阻塞状态。
 * - wait和sleep方法都是可中断方法，被中断后都会收到中断异常。
 **/
@Slf4j
public class SleepTest1 {
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread("t1") {
            @Override
            public void run() {
                log.info("enter sleep");
                try {
                    // 内部其实也是调用Thread.sleep的方法，只是可读性更好
                    TimeUnit.SECONDS.sleep(3);
                    log.info("continue execute……current state:{}", getState());
                } catch (InterruptedException e) {
                    // 中断之后，是Runnable状态，自解：好像和sleep结束后的样子不一样，sleep结束后还可以继续运行后续代码的，所以被中断后是不能继续了？
                    log.info("wake up!current state:{}", getState());
                    e.printStackTrace();
                    // 自解：我觉得此时只能算打算睡眠，不能算唤醒，因为它不会继续做后面的事，但可以重新执行run方法
//                    log.info("rerun");
//                    run();
                }
            }
        };

        t1.start();
        Thread.sleep(1000);

        log.info("interrupt t1");
        t1.interrupt();
        // sleep、wait、join 方法都会让线程进入阻塞状态，打断线程会清空打断状态
        log.info("打断标记：{}", t1.isInterrupted());

        Thread.sleep(5000);
    }
}
