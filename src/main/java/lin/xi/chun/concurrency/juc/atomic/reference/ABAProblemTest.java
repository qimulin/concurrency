package lin.xi.chun.concurrency.juc.atomic.reference;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 自解：这个CAS只认共享变量当下的值，不会感知其他线程是否对共享变量的值做了修改，A->B—>A的饶了一圈，还是回来，我就CAS还是成功
 * 那怎么样才能做到能感知其他线程是否动过共享变量呢？参考 {@link ABAAtomicStampedReferenceTest}
 * @author zhou.wu
 * @description: ABA问题
 * @date 2022/9/1
 **/
@Slf4j
public class ABAProblemTest {
    // 初始值为A
    static AtomicReference<String> ref = new AtomicReference<>("A");

    public static void main(String[] args) throws InterruptedException {
        log.debug("main start...");
        // 获取值 A
        // 这个共享变量被它线程修改过？
        String prev = ref.get();
        other();
        // sleep让线程修改错开
        TimeUnit.SECONDS.sleep(1);
        // 尝试改为 C
        log.debug("change A->C {}", ref.compareAndSet(prev, "C"));
    }

    /**
     * 其他线程对共享变量做修改
     * */
    private static void other() {
        // t1 将A改为B
        new Thread(() -> {
            log.debug("change A->B {}", ref.compareAndSet(ref.get(), "B"));
        }, "t1").start();

        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // t2 将B改为A
        new Thread(() -> {
            log.debug("change B->A {}", ref.compareAndSet(ref.get(), "A"));
        }, "t2").start();
    }
}
