package lin.xi.chun.concurrency.thread.singleton;

/**
 * @author zhou.wu
 * @description: 枚举单例
 * @date 2022/8/20
 **/
// 问题1：枚举单例是如何限制实例个数的
// 答：枚举定义的枚举对象实际上定义的有几个，以后就有几个，它相当于是枚举类的静态成员变量
// 问题2：枚举单例在创建时是否有并发问题
// 答：没有，也是静态成员变量，在类加载阶段创建完成，由JVM控制
// 问题3：枚举单例能否被反射破坏单例
// 答：不能
// 问题4：枚举单例能否被反序列化破坏单例
// 答：枚举类本身都是实现Serializable可序列化功能，这点看Enum的代码可知
// public abstract class Enum<E extends Enum<E>>
//        implements Comparable<E>, Serializable {
// 但是枚举在设计的时候已经考虑到反序列化的问题，所以不需要再额外的操作保证
// 问题5：枚举单例属于懒汉式还是饿汉式
// 答：饿汉
// 问题6：枚举单例如果希望加入一些单例创建时的初始化逻辑该如何做
// 答：加构造方法，把初始化方法逻辑加到构造方法中
public enum EnumSingleton {
    INSTANCE;
}

/**
 * 利用jad工具反编译后的样子
 public final class EnumSingleton extends Enum
 {

     public static EnumSingleton[] values()
     {
        return (EnumSingleton[])$VALUES.clone();
     }

     public static EnumSingleton valueOf(String name)
     {
        return (EnumSingleton)Enum.valueOf(zhou/wu/mytest/thread/singleton/EnumSingleton, name);
     }

     private EnumSingleton(String s, int i)
     {
        super(s, i);
     }

     public static final EnumSingleton INSTANCE;    // 静态成员变量
     private static final EnumSingleton $VALUES[];

     static
     {
        INSTANCE = new EnumSingleton("INSTANCE", 0);
        $VALUES = (new EnumSingleton[] {
            INSTANCE
        });
     }
 }
 *
 * */
