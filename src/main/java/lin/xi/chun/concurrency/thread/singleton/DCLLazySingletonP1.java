package lin.xi.chun.concurrency.thread.singleton;

/**
 * @author zhou.wu
 * @description: 双锁检查（double-checked locking）的懒汉模式
 * @date 2022/8/20
 **/
public class DCLLazySingletonP1 {

    private DCLLazySingletonP1() { }

    private static DCLLazySingletonP1 INSTANCE = null;

    public static DCLLazySingletonP1 getInstance() {
        if(INSTANCE == null) { // t2
            // 首次访问会同步，而之后的使用不会进到这了，就不会进到synchronized
            synchronized(DCLLazySingletonP1.class) {
                if (INSTANCE == null) { // t1
                    INSTANCE = new DCLLazySingletonP1();
                }
            }
        }
        return INSTANCE;
    }
}
/**
 * 以上的实现特点是：
 * - 懒惰实例化
 * - 首次使用 getInstance() 才使用 synchronized 加锁，后续使用时无需加锁
 * - 有隐含的，但很关键的一点：第一个 if 使用了 INSTANCE 变量，是在同步块之外
 * 但是这种方式，仍然会有问题，在代码：
 * if(INSTANCE == null)
 * 这里并没有同步代码块一起的保护，会因为指令重排的原因，导致线程不安全的问题，这点还是得从字节码上找问题（原课程的示例字节码，但是类似）
 * 0: getstatic #2 // Field INSTANCE:Lcn/itcast/n5/Singleton;   // 获取静态变量INSTANCE
 * 3: ifnonnull 37  // 判断值是否非null，非null的话就会跳转到37行
 * 6: ldc #3 // class cn/itcast/n5/Singleton    // 获取类对象
 * 8: dup   // 复制类对象的引用地址，为了后来的解锁用
 * 9: astore_0  // 将引用地址存入到本地变量表的0位置
 * 10: monitorenter // 同步代码块
 * 11: getstatic #2 // Field INSTANCE:Lcn/itcast/n5/Singleton;  // 拿到静态变量
 * 14: ifnonnull 27 // 判断非空，非空到27行
 * 17: new #3 // class cn/itcast/n5/Singleton   // 为空，则创建实例
 * 20: dup  // 复制类实例的引用
 * 21: invokespecial #4 // Method "<init>":()V  // 用当前栈顶，复制的应用来调用其构造方法
 * 24: putstatic #2 // Field INSTANCE:Lcn/itcast/n5/Singleton;  // 剩下的一份引用地址，拿来复制给静态变量INSTANCE
 * 27: aload_0  // 类对象取出来放到栈顶
 * 28: monitorexit  // 解锁
 * 29: goto 37  // 又是到37行
 * 32: astore_1
 * 33: aload_0
 * 34: monitorexit
 * 35: aload_1
 * 36: athrow
 * 37: getstatic #2 // Field INSTANCE:Lcn/itcast/n5/Singleton;  // 获取静态变量INSTANCE
 * 40: areturn
 *
 * 其中
 * 17 表示创建对象，将对象引用入栈 // new Singleton
 * 20 表示复制一份对象引用 // 引用地址
 * 21 表示利用一个对象引用，调用构造方法
 * 24 表示利用一个对象引用，赋值给 static INSTANCE
 * 也许 jvm 会优化为：先执行 24，再执行 21。先把引用地址赋值给静态变量，再去调用构造方法。
 * 比如t1线程先先把引用地址赋值给静态变量INSTANCE，还未调用构造方法，此时INSTANCE已有对象引用了，不为null，
 * 此时t2线程的if(INSTANCE == null)这一段，由于没有同步代码块的保护，所以也有可能在执行，
 * 这个时候如果执行到字节码行数3的时候，得到了静态变量INSTANCE确实不等于null，则就继续到37行，拿到一个还未调用new方法的对象实例（还未初始化完毕）
 *
 * 那以上问题要像解决，可以给共享变量INSTANCE加一个volatile修饰。见 {@link zhou.wu.mytest.thread.singleton.DCLLazySingleton}
 * */
