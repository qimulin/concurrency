package lin.xi.chun.concurrency.juc.atomic.reference;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * @author zhou.wu
 * @description: 利用AtomicStampedReference（可以加版本号）解决ABA问题 {@link ABAProblemTest}
 * @date 2022/9/1
 **/
@Slf4j
public class ABAAtomicStampedReferenceTest {

    static AtomicStampedReference<String> ref = new AtomicStampedReference<>("A", 0);

    /**
     * 主线程仅能判断出共享变量的值与最初值 A 是否相同，不能感知到这种从 A 改为 B 又 改回 A 的情况，如果主线程
     * 希望：
     * 只要有其它线程【动过了】共享变量，那么自己的 cas 就算失败，这时，仅比较值是不够的，需要再加一个版本号
     * */
    public static void main(String[] args) throws InterruptedException {
        log.debug("main start...");
        // 获取值 A
        String prev = ref.getReference();
        // 获取版本号
        int stamp = ref.getStamp(); //  stamp [stæmp] 翻译：戳;印记;戳记;
        log.debug("版本 {}", stamp);
        // 如果中间有其它线程干扰，发生了 ABA 现象
        other();
        TimeUnit.SECONDS.sleep(1);
        // 尝试改为 C，版本号也得配合更新
        log.debug("change A->C {}", ref.compareAndSet(prev, "C", stamp, stamp + 1));
    }

    private static void other() {
        new Thread(() -> {
            log.debug("change A->B {}", ref.compareAndSet(ref.getReference(), "B",
                    ref.getStamp(), ref.getStamp() + 1));
            log.debug("更新版本为 {}", ref.getStamp());
        }, "t1").start();

        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            log.debug("change B->A {}", ref.compareAndSet(ref.getReference(), "A",
                    ref.getStamp(), ref.getStamp() + 1));
            log.debug("更新版本为 {}", ref.getStamp());
        }, "t2").start();
    }
}

/**
 * 后记：
 * AtomicStampedReference 可以给原子引用加上版本号，追踪原子引用整个的变化过程，
 * 如： A -> B -> A -> C ，通过AtomicStampedReference，我们可以知道，引用变量中途被更改了几次。
 *
 * 但是有时候，并不关心引用变量更改了几次，只是单纯的关心是否更改过，所以就有了AtomicMarkableReference
 * 例子可参考 {@link lin.xi.chun.concurrency.juc.atomic.reference.ABAAtomicMarkableReferenceTest}
 * */
