package lin.xi.chun.concurrency.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.SynchronousQueue;

import static java.lang.Thread.sleep;

/**
 * @author zhou.wu
 * @description SynchronousQueue使用示例
 * 特点介绍: 它没有容量，没有线程来取是放不进去的（一手交钱、一手交货）
 * @date 2023/8/18
 **/
@Slf4j(topic = "my.SynchronousQueueMain")
public class TestSynchronousQueue {

    public static void main(String[] args) throws InterruptedException {
        SynchronousQueue<Integer> integers = new SynchronousQueue<>();

        // t1将1和2放入队列
        new Thread(() -> {
            try {
                log.debug("putting {} ", 1);
                integers.put(1);
                log.debug("{} putted...", 1);
                log.debug("putting...{} ", 2);
                integers.put(2);
                log.debug("{} putted...", 2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"t1").start();

        // 1s后，t2取出元素1
        sleep(1);
        new Thread(() -> {
            try {
                log.debug("taking");
                Integer take = integers.take();
                log.debug("took {}", take);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"t2").start();

        // 再1s（相当于2s）后，t3取出元素2
        sleep(1);
        new Thread(() -> {
            try {
                log.debug("t3 taking");
                Integer take = integers.take();
                log.debug("took {}", take);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"t3").start();
    }
}

/**
 * 后记：
 * 【打印结果】
 * 11:25:36.058 [t3] DEBUG my.SynchronousQueueMain - t3 taking
 * 11:25:36.057 [t1] DEBUG my.SynchronousQueueMain - putting 1
 * 11:25:36.057 [t2] DEBUG my.SynchronousQueueMain - taking
 * 11:25:36.068 [t2] DEBUG my.SynchronousQueueMain - took 1
 * 11:25:36.068 [t1] DEBUG my.SynchronousQueueMain - 1 putted...
 * 11:25:36.068 [t1] DEBUG my.SynchronousQueueMain - putting...2
 * 11:25:36.068 [t1] DEBUG my.SynchronousQueueMain - 2 putted...
 * 11:25:36.068 [t3] DEBUG my.SynchronousQueueMain - took 2
 *
 * 【结果分析】
 * 从上述结果可以看出，基本上put操作和take操作是有同步限制的，自解：即put完，会锁住不会新的任务put，且只有让take取了之后，才通知让新的可以put
 * 想要详细了解可参考内部类方法代码：
 * 1、E transfer(E e, boolean timed, long nanos)
 * 2、Object awaitFulfill(QNode s, E e, boolean timed, long nanos)
 * */
