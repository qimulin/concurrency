package lin.xi.chun.concurrency.thread.singleton;

/**
 * @author zhou.wu
 * @description: 静态内部类懒汉单例
 * @date 2022/8/29
 **/
public final class StaticInnerClazzSingleton {

    private StaticInnerClazzSingleton() { }

    // 问题1：属于懒汉式还是饿汉式
    // 答：懒汉式的，类加载本来就是懒惰式的，用到再加载
    private static class LazyHolder {
        static final StaticInnerClazzSingleton INSTANCE = new StaticInnerClazzSingleton();
    }

    // 问题2：在创建时是否有并发问题
    // 答：类加载时对静态变量是JVM控制的，不用担心线程安全
    public static StaticInnerClazzSingleton getInstance() {
        return LazyHolder.INSTANCE;
    }
}
