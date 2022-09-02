package lin.xi.chun.concurrency.juc.atomic.accumulator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 已经有AtomicInteger、AtomicLong了，为什么还要有LongAdder和LongAccumulator之类的呢？
 * 因为在jdk1.8以后专门新增了几个来做累加器，它们的性能会比AtomicInteger和AtomicLong好
 * @author zhou.wu
 * @description: 原子累加器测试
 * @date 2022/9/2
 **/
public class AtomicAdderTest {

    public static void main(String[] args) {
        // LongAdder和AtomicLong性能对比
        System.out.println("use LongAdder");
        // 各5次执行，执行1次往往jvm还不知道给它进行优化
        for (int i = 0; i < 5; i++) {
            demo(() -> new LongAdder(), adder -> adder.increment());
        }

        System.out.println("use AtomicLong");
        for (int i = 0; i < 5; i++) {
            demo(() -> new AtomicLong(), adder -> adder.getAndIncrement());
        }
    }

    private static <T> void demo(Supplier<T> adderSupplier, Consumer<T> action) {
        T adder = adderSupplier.get();
        long start = System.nanoTime();
        List<Thread> ts = new ArrayList<>();
        // 40 个线程，每人累加 50 万
        for (int i = 0; i < 40; i++) {
            ts.add(new Thread(() -> {
                for (int j = 0; j < 500000; j++) {
                    action.accept(adder);
                }
            }));
        }

        ts.forEach(t -> t.start());
        ts.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        long end = System.nanoTime();   // 纳秒
        System.out.println(adder + " cost:" + (end - start)/1000_000);
    }

}
/**
 * 后记：
 * 为什么用累加器会性能提升那么多呢？
 * 性能提升的原因很简单，就是在有竞争时，设置多个累加单元，Therad-0 累加 Cell[0]，而 Thread-1 累加Cell[1]... 最后将结果汇总。
 * 这样它们在累加时操作的不同的 Cell 变量，因此减少了 CAS 重试失败，从而提高性能。
 *（累加单元会和CPU核心数有关，一开始2个，若竞争激烈会增到4个，但总之不会超过CPU核心数）
 * 关于LongAdder的主要源码解析，请参考 LongAdderAnalysis.md
 * */

