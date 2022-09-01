package lin.xi.chun.concurrency.thread.locksupport;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * @author zhou.wu
 * @description:
 * // 暂停当前线程
 * LockSupport.park();
 * // 恢复某个线程的运行
 * LockSupport.unpark(暂停线程对象)
 * 先 park 再 unpark
 *
 * 与 Object 的 wait & notify 相比
 * - wait，notify 和 notifyAll 必须配合 Object Monitor 一起使用，而 park，unpark 不必
 * - park & unpark 是以线程为单位来【阻塞】和【唤醒】线程，而 notify 只能随机唤醒一个等待线程，notifyAll是唤醒所有等待线程，就不那么【精确】
 * - park & unpark 可以先 unpark，而 wait & notify 不能先 notify
 * @date 2022/8/13
 **/
@Slf4j
public class ParkUnparkTest {
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            log.debug("start...");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            try {
//                Downloader.download();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            log.debug("park...");
            LockSupport.park(); // 这里调用park()方法后会使本线程的状态变为“WAIT"
            log.debug("resume...");
        },"t1");
        t1.start();
//        TimeUnit.MILLISECONDS.sleep(5);
//        log.debug("interrupt...");
//        t1.interrupt();
        TimeUnit.SECONDS.sleep(2);
        log.debug("unpark...");
        LockSupport.unpark(t1);
    }
}
/**
 * 后记：
 * unpark即可以在park之前调用，也可以在park之后调用。如果把上述两个时间长短交换，即可以模拟unpark在park前还是后执行了。
 * 如果先执行unpark方法，则后续调用park()方法第一次将不会起作用，第二次的park()和之之前的未调用unpark先调用park的效果就一样了
 * 自解：另外，看源码可知，如果线程此时打断标记为true
 * （具体原因参见“LockSupport park/unpark原理”）
 * */