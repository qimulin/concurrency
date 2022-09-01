package lin.xi.chun.concurrency.thread.singleton;

/**
 * @author zhou.wu
 * @description: 双锁检查（double-checked locking）的懒汉模式
 * @date 2022/8/20
 **/
public class DCLLazySingleton {

    private DCLLazySingleton() { }

    // 用volatile修饰静态共享变量
    // 问题1：解释为什么要加 volatile ?
    /**
     * 答：字节码层面看，防止构造方法赋值质量重排序，具体{@link DCLLazySingletonP1}最下面的内容讲解
     * */
    private static volatile DCLLazySingleton INSTANCE = null;   // 很遗憾不能从字节码角度看不出加与不加volatile指令的区别

    // 问题2：对比懒汉式, 说出这样做的意义。
    // 答：不用每次调用都加synchronized锁，性能更优越些
    public static DCLLazySingleton getInstance() {
        if(INSTANCE == null) {
            synchronized(DCLLazySingleton.class) {
                // 问题3：为什么还要在这里加为空判断, 之前不是判断过了吗
                // 答：防止首次创建INSTANCE对象的时候多个线程并发的问题，不要被重复创建
                if (INSTANCE == null) {
                    INSTANCE = new DCLLazySingleton();
                }
            }
        }
        return INSTANCE;
    }
}
/**
 * 很遗憾不能从字节码角度看不出加与不加volatile指令的区别，所以还是结合其读屏障和写屏障的知识去看
 * // -------------------------------------> 加入对 INSTANCE 变量的读屏障
 * 0: getstatic #2 // Field INSTANCE:Lcn/itcast/n5/Singleton;   // 获取静态变量INSTANCE，拿到的也肯定是最新的结果
 * 3: ifnonnull 37  // 判断值是否非null，非null的话就会跳转到37行
 * 6: ldc #3 // class cn/itcast/n5/Singleton    // 获取类对象
 * 8: dup   // 复制类对象的引用地址，为了后来的解锁用
 * 9: astore_0  // 将引用地址存入到本地变量表的0位置
 * 10: monitorenter // 同步代码块    -----------------------> 保证原子性、可见性
 * 11: getstatic #2 // Field INSTANCE:Lcn/itcast/n5/Singleton;  // 拿到静态变量
 * 14: ifnonnull 27 // 判断非空，非空到27行
 * 17: new #3 // class cn/itcast/n5/Singleton   // 为空，则创建实例
 * 20: dup  // 复制类实例的引用
 * 21: invokespecial #4 // Method "<init>":()V  // 用当前栈顶，复制的应用来调用其构造方法
 * 24: putstatic #2 // Field INSTANCE:Lcn/itcast/n5/Singleton;  // 剩下的一份引用地址，拿来复制给静态变量INSTANCE
 * // -------------------------------------> 加入对 INSTANCE 变量的写屏障，这样21行的代码就不能排在24后面了，这样就保证INSTANCE不为null的话，就一定执行过构造方法
 * 27: aload_0  // 类对象取出来放到栈顶
 * 28: monitorexit  // 解锁    -----------------------> 保证原子性、可见性
 * 29: goto 37  // 又是到37行
 * 32: astore_1
 * 33: aload_0
 * 34: monitorexit
 * 35: aload_1
 * 36: athrow
 * 37: getstatic #2 // Field INSTANCE:Lcn/itcast/n5/Singleton;  // 获取静态变量INSTANCE
 * 40: areturn
 * */

