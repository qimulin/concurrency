package lin.xi.chun.concurrency.juc.unsafe;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @author zhou.wu
 * @description Unsafe访问
 * @date 2023/7/17
 **/
public class UnsafeAccessor {

    static Unsafe unsafe;

    static {
        try {
            // 共有的属性，可用getField(String name)获取属性
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            // 私有的域，调用前需要设置accessible为true
            theUnsafe.setAccessible(true);
            // theUnsafe是静态static修饰的，从属于类，不从属于对象，所以不需要传递对象，get方法参数传null
            unsafe = (Unsafe) theUnsafe.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new Error(e);
        }
    }

    static Unsafe getUnsafe() {
        return unsafe;
    }
}
