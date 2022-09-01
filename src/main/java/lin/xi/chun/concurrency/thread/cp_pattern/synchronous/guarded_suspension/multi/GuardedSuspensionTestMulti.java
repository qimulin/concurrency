package lin.xi.chun.concurrency.thread.cp_pattern.synchronous.guarded_suspension.multi;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author zhou.wu
 * @description: 如果需要在多个类之间使用 GuardedObject 对象，作为参数传递不是很方便，因此设计一个用来解耦的中间类，
 * 这样不仅能够解耦【结果等待者】和【结果生产者】，还能够同时支持多个任务的管理
 * @date 2022/8/13
 **/
@Slf4j
public class GuardedSuspensionTestMulti {
    public static void main(String[] args) throws InterruptedException {
        // 三个居民等着收信
        for (int i = 0; i < 3; i++) {
            new People().start();
        }
        TimeUnit.SECONDS.sleep(1);
        // 邮递员根据信箱格编号，进行送信
        for (Integer id : Mailboxes.getIds()) {
            new Postman(id, "内容" + id).start();
        }
    }
}
/**
 * 后记：
 * 但本例子写的人和邮递员是一一对应的，也不大符合现实生活
 * */

