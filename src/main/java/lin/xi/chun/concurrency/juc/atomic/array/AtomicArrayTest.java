package lin.xi.chun.concurrency.juc.atomic.array;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * juc提供的原子数组有
 * - AtomicIntegerArray
 * - AtomicLongArray
 * - AtomicReferenceArray
 * @author zhou.wu
 * @description: 原子数组不安全的实现
 * @date 2022/9/1
 **/
public class AtomicArrayTest {

    public static void main(String[] args) {
        // 不安全的数组
        demo(
                ()->new int[10],
                (array)->array.length,
                (array, index) -> array[index]++,
                array-> System.out.println(Arrays.toString(array))
        );

        // 安全的数组
        demo(
                ()-> new AtomicIntegerArray(10),
                (array) -> array.length(),
                (array, index) -> array.getAndIncrement(index),
                array -> System.out.println(array)
        );
    }

    /**
     本方法借助以下几个参数，比较灵活的可以用同一个方法，支持普通的Array和AtomicIntegerArray的处理
     参数1，提供数组、可以是线程不安全数组或线程安全数组
     参数2，获取数组长度的方法
     参数3，自增方法，回传 array, index
     参数4，打印数组的方法
     */
    // supplier 提供者 无中生有 ()->结果 没有参数，但是需要你提供返回结果
    // function 函数 一个参数一个结果 (参数)->结果 ；BiFunction (参数1,参数2)->结果 两个参数一个结果
    // consumer 消费者 一个参数没结果 (参数)->void, BiConsumer (参数1,参数2)->无结果 两个参数
    private static <T> void demo(
            Supplier<T> arraySupplier,
            Function<T, Integer> lengthFun,
            BiConsumer<T, Integer> putConsumer,
            Consumer<T> printConsumer
    ) {
        List<Thread> ts = new ArrayList<>();
        T array = arraySupplier.get();  // 由supplier提供数组，这样可以支持不同类型
        int length = lengthFun.apply(array);    // lengthFun获取数组长度
        for (int i = 0; i < length; i++) {
            // 每个线程对数组作 10000 次操作，理论上最终打印的数组元素应该都为10000
            ts.add(new Thread(() -> {
                for (int j = 0; j < 10000; j++) {
                    // 处理参数array和“取余得到的元素下标”
                    putConsumer.accept(array, j%length);
                }
            }));
        }

        // 启动所有线程
        ts.forEach(t -> t.start());
        ts.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        // 等所有线程结束，打印即可
        printConsumer.accept(array);
    }
}
