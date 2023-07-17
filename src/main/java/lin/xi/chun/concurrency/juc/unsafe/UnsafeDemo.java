package lin.xi.chun.concurrency.juc.unsafe;

import java.lang.reflect.Field;

/**
 * @author zhou.wu
 * @description Unsafe示例
 * @date 2023/7/17
 *
 * Unsafe 是 CAS 的核心类，由于 Java 无法直接访问底层系统，需要通过本地（Native）方法来访问
 * Unsafe 类存在 sun.misc 包，其中所有方法都是 native 修饰的，都是直接调用操作系统底层资源执行相应的任务，基于该类可以直接操作特定的内存数据，其内部方法操作类似 C 的指针
 **/
public class UnsafeDemo {

    public static void main(String[] args) throws NoSuchFieldException {
        /*
        * 用Unsafe对象线程安全地对Student成员变量进行修改，当然也可以用已经封装好的AtomicInteger和AtomicReferenceFieldUpdater进行修改
        *  */
        Field id = Student.class.getDeclaredField("id");
        Field name = Student.class.getDeclaredField("name");
        /* 更底层的写法 */
        // 获得成员变量的偏移量
        long idOffset = UnsafeAccessor.unsafe.objectFieldOffset(id);
        long nameOffset = UnsafeAccessor.unsafe.objectFieldOffset(name);
        Student student = new Student();
        // 使用 cas 方法替换成员变量的值
        // 返回 true
        UnsafeAccessor.unsafe.compareAndSwapInt(student, idOffset, 0, 20);
        // 返回 true
        UnsafeAccessor.unsafe.compareAndSwapObject(student, nameOffset, null, "张三");
        System.out.println(student);
    }

}
