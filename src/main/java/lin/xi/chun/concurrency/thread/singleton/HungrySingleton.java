package lin.xi.chun.concurrency.thread.singleton;

import java.io.Serializable;

/**
 * @author zhou.wu
 * @description: 饿汉单例 饿汉式：类加载就会导致该单实例对象被创建
 * @date 2022/8/20
 **/
// 问题1：为什么加 final
// 答：怕有子类，子类中重写方法破坏单例
// 问题2：如果实现了序列化接口, 还要做什么来防止反序列化破坏单例
// 答：使用readResolve方法，反序列化过程中一旦发现readResolve返回了一个对象，就会用这个对象
public final class HungrySingleton implements Serializable {

    // 问题3：为什么设置为私有? 是否能防止反射创建新的实例?
    // 答：非私有，其他类就可以无限创建了；反射很强大，不能防止反射创建新实例
    private HungrySingleton() {}

    // 问题4：这样初始化是否能保证单例对象创建时的线程安全?
    // 答：没有，这是在静态成员变量初始化的时候创建单例对象，这是类加载阶段完成的，JVM会保证对这些变量的赋值线程安全
    private static final HungrySingleton INSTANCE = new HungrySingleton();

    // 问题5：为什么提供静态方法而不是直接将 INSTANCE 设置为 public, 说出你知道的理由
    // 答：用方法可以提供更好的封装性，直接提供静态成员变量，以后就不好改成其他例如懒惰式加载的形式了；
    //  还可以对创建的单例对象有更多的一些控制；还可以提供泛型的支持
    public static HungrySingleton getInstance() {
        return INSTANCE;
    }

    public Object readResolve() {
        return INSTANCE;
    }
}
