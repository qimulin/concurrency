package lin.xi.chun.concurrency.juc.atomic.reference;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicMarkableReference;

/**
 * @author zhou.wu
 * @description: 利用AtomicMarkableReference（可以加版本号）解决ABA问题
 * 有时候，并不关心引用变量更改了几次，只是单纯的关心是否更改过，所以就有了AtomicMarkableReference，只要知道更改过即可
 * @date 2022/9/1
 **/
@Slf4j
public class ABAAtomicMarkableReferenceTest {

    /**
     * 场景：
     * - 主人：检查垃圾袋，如果满了，就换新垃圾袋；如果没满，就装垃圾
     * - 保洁：阿姨负责倒空垃圾袋，不换垃圾袋
     * - 垃圾袋：共享资源
     * */
    public static void main(String[] args) throws InterruptedException {
        GarbageBag bag = new GarbageBag("装满了垃圾");
        // 参数2 mark 可以看作一个标记，true表示垃圾袋满了
        AtomicMarkableReference<GarbageBag> ref = new AtomicMarkableReference<>(bag, true);
        log.debug("主线程 start...");
        GarbageBag prev = ref.getReference();
        log.debug(prev.toString());

        new Thread(() -> {
            log.debug("打扫卫生的线程 start...");
            // 清空垃圾袋
            bag.setDesc("空垃圾袋");
            while (!ref.compareAndSet(bag, bag, true, false)) {}
            log.debug(bag.toString());
        }).start();

        Thread.sleep(1000);
        log.debug("主线程想换一只新垃圾袋？");
        boolean success = ref.compareAndSet(prev, new GarbageBag("空垃圾袋"), true, false);
        log.debug("换了么？" + success);
        log.debug(ref.getReference().toString());
    }
}
