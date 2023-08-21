package lin.xi.chun.concurrency.cp_pattern.asynchronous.worker_thread;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author zhou.wu
 * @description 学完线程池后再看这个饥饿问题。两个全能员工针对来的1个/2个客人导致死等的问题
 * @date 2023/8/21
 **/
@Slf4j
public class TestDeadWait {

    static final List<String> MENU = Arrays.asList("地三鲜", "宫保鸡丁", "辣子鸡丁", "烤鸡翅");

    static Random RANDOM = new Random();

    /** 随机挑个菜做 */
    static String cooking() {
        return MENU.get(RANDOM.nextInt(MENU.size()));
    }

    public static void main(String[] args) {
        // 模拟两个工人
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        // 点餐任务和做菜任务
        executorService.execute(() -> {
            log.debug("处理点餐...");
            Future<String> f = executorService.submit(() -> {
                log.debug("做菜");
                return cooking();
            });
            try {
                log.debug("上菜: {}", f.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

        // 加上这一段好比来了两个客人了
        executorService.execute(() -> {
             log.debug("处理点餐...");
             Future<String> f = executorService.submit(() -> {
                 log.debug("做菜");
                 return cooking();
             });
             try {
                 log.debug("上菜: {}", f.get());
             } catch (InterruptedException | ExecutionException e) {
                 e.printStackTrace();
             }
        });
    }
}
