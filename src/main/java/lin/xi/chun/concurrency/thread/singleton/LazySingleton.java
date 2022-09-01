package lin.xi.chun.concurrency.thread.singleton;

import java.io.Serializable;

/**
 * @author zhou.wu
 * @description: 懒汉单例 懒汉式：类加载不会导致该单实例对象被创建，而是首次使用该对象时才会创建
 * @date 2022/8/20
 **/
public final class LazySingleton implements Serializable {

    private LazySingleton() {}

    private static LazySingleton INSTANCE = null;

    // 分析这里的线程安全, 并说明有什么缺点？
    // 答：可以保证线程安全，锁的范围大，性能不佳，后续即使已经被创建了后，每次调用都还是要加锁
    /*    public static synchronized LazySingleton getInstance() {
        if( INSTANCE != null ){
            return INSTANCE;
        }
        INSTANCE = new LazySingleton();
        return INSTANCE;
    }*/

    /** 等同于修饰在静态方法上的getInstance方法 */
    public static LazySingleton getInstance() { // 不能锁INSTANCE，INSTANCE后续要赋值，会变化，而且INSTANCE可能为null，null不能用synchronized
        synchronized(LazySingleton.class){  // 这样每次有线程调用一次getInstance，就需要进入一次同步代码块，后续升级为重量级锁后，性能更不佳
            if (INSTANCE != null) {
                return INSTANCE;
            }

            INSTANCE = new LazySingleton();
        }
        return INSTANCE;
    }
}
/**
 * 实际上当INSTANCE只有在被创建以前才需要线程安全保护，创建后就不需要再被线程安全保护了。
 * 鉴于上述这种方式性能不佳，可以参考 {@link zhou.wu.mytest.thread.singleton.DCLLazySingletonP1}
 * */
