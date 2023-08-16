package lin.xi.chun.concurrency.juc.atomic.updater;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * juc提供的字段更新器有
 * - AtomicReferenceFieldUpdater // 域 字段 引用类型即可
 * - AtomicIntegerFieldUpdater
 * - AtomicLongFieldUpdater
 * 利用字段更新器，可以针对对象的某个域（Field|属性|成员变量）进行原子操作，【注意】只能配合 volatile 修饰的字段使用，否则会出现异常
 * @author zhou.wu
 * @description 原子字段更新器测试
 * @date 2022/9/2
 **/
public class AtomicFieldUpdaterTest {

    /** 必须是volatile修饰，因为CAS本身就是必须结合volatile保证共享变量的可见性 */
    private volatile int field;

    public static void main(String[] args) {
        AtomicIntegerFieldUpdater fieldUpdater =AtomicIntegerFieldUpdater.newUpdater(AtomicFieldUpdaterTest.class, "field");
        AtomicFieldUpdaterTest test5 = new AtomicFieldUpdaterTest();

        // 参数1：修改的对象；参数2：原始值；参数3：更新值
        fieldUpdater.compareAndSet(test5, 0, 10);
        // 修改成功 field = 10
        System.out.println(test5.field);

        // 修改成功 field = 20
        fieldUpdater.compareAndSet(test5, 10, 20);
        System.out.println(test5.field);

        // 修改失败 field = 20
        fieldUpdater.compareAndSet(test5, 10, 30);
        System.out.println(test5.field);

        // 重新获取值，修改成功
        int prev = fieldUpdater.get(test5);
        fieldUpdater.compareAndSet(test5, prev, 30);
        System.out.println(test5.field);
    }
}
