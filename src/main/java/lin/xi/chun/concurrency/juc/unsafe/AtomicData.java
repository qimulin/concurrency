package lin.xi.chun.concurrency.juc.unsafe;

import sun.misc.Unsafe;

/**
 * @author zhou.wu
 * @description 使用自定义的 AtomicData 实现之前线程安全的原子整数 Account 实现
 * @date 2023/7/17
 **/
public class AtomicData {

    /**
     * 对于 CAS 操作来说，必须对变量进行 volatile 修饰。
     * 在不使用 volatile 修饰变量的情况下，由于线程间的数据不可见性，可能会导致 CAS 操作失败。因为 CAS 操作需要比较当前内存中的值和期望值是否相等，
     * 如果不相等就更新，但是如果值在更新前被其他线程修改了，那么 CAS 操作就会失败，因为它比较的是内存中的旧值。
     * 而对变量进行 volatile 修饰，可以确保每个线程访问变量时都能读取到最新的值，从而避免了数据不一致的问题，并保证了 CAS 操作的正确性。
     * 因此，对于需要使用 CAS 操作的变量，应该将其声明为 volatile 类型。
     * */
    private volatile int data;
    static final Unsafe unsafe;
    static final long DATA_OFFSET;
    static {
        unsafe = UnsafeAccessor.getUnsafe();
        try {
            // data 属性在 DataContainer 对象中的偏移量，用于 Unsafe 直接访问该属性
            DATA_OFFSET = unsafe.objectFieldOffset(AtomicData.class.getDeclaredField("data"));
        } catch (NoSuchFieldException e) {
            throw new Error(e);
        }
    }

    public AtomicData(int data) {
        this.data = data;
    }

    public void decrease(int amount) {
        int oldValue;
        while(true) {
            // 获取共享变量旧值，可以在这一行加入断点，修改 data 调试来加深理解
            oldValue = data;
            // cas 尝试修改 data 为 旧值 + amount，如果期间旧值被别的线程改了，返回 false
            if (unsafe.compareAndSwapInt(this, DATA_OFFSET, oldValue, oldValue - amount)) {
                return;
            }
        }
    }

    public int getData() {
        return data;
    }
}
