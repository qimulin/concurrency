# Unsafe
## 介绍
Unsafe 对象提供了非常底层的，操作内存、线程的方法，Unsafe 对象不能直接调用，只能通过反射获得。\
为什么不能直接调用？看下列部分源码可知
```java
package sun.misc;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;
/**
 * final修饰，不能被继承。
 * Unsafe名称虽然这么叫，但并不是线程的“安全不安全”，只是因为它会直接操作操作系统内存和线程，所以不建议编程人员直接使用，你如果误用就可能导致不安全的发生！
 * */
public final class Unsafe {
    // theUnsafe就是需要用到的Unsafe对象，是个私有的单例，因此不能直接获得，所以需要反射去获得
    private static final Unsafe theUnsafe;
    public static final int INVALID_FIELD_OFFSET = -1;
    
    /* 省略其他代码 */
}
```
具体使用，参考[示例代码](../../../main/java/lin/xi/chun/concurrency/juc/unsafe/UnsafeDemo.java)