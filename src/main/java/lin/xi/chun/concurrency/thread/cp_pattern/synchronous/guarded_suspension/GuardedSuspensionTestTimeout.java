package lin.xi.chun.concurrency.thread.cp_pattern.synchronous.guarded_suspension;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author zhou.wu
 * @description: 测试1
 * @date 2022/8/13
 **/
@Slf4j
public class GuardedSuspensionTestTimeout {
    public static void main(String[] args) {
        GuardedObjectWithTimeout v2 = new GuardedObjectWithTimeout();
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            v2.complete(null);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            v2.complete(Arrays.asList("a", "b", "c"));
        }).start();

        // 主线程设置了超时时间
        Object response = v2.get(2500);
        if (response != null) {
            log.debug("get response: [{}] lines", ((List<String>) response).size());
        } else {
            log.debug("can't get response");
        }
    }
}
/**
 * 后记：
 * join原理：其实就是对调用join方法的那个对象进行“保护性暂停”
 * public final synchronized void join(long millis)
 *     throws InterruptedException {
 *         long base = System.currentTimeMillis();
 *         long now = 0;
 *
 *         if (millis < 0) {
 *             throw new IllegalArgumentException("timeout value is negative");
 *         }
 *
 *         if (millis == 0) {
 *             while (isAlive()) {  // 线程未结束，反复调用wait方法，自解：这时候相当于外面调用的线程要进行一直等待了，因为此时本线程是锁，调用本线程的线程要被放入waitSet
 *                 wait(0);
 *             }
 *         } else {
 *             while (isAlive()) {
 *                 long delay = millis - now;   // 规定剩余的时间
 *                 if (delay <= 0) {
 *                     break;
 *                 }
 *                 wait(delay); // 等待不超过最大剩余的时间
 *                 now = System.currentTimeMillis() - base; // 经历的时间
 *             }
 *         }
 *     }
 * */
