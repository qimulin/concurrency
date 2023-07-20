# 共享模型之不可变
自解：不可变类是线程安全的一种实现方式，就是在使用不可变类生成的对象某些情况下由于它的不可变性是可以达到线程安全的要求（具体还是要看业务）。
## 不可变类的使用
先来看使用了SimpleDateFormat这个可变类的[示例代码](../../../../src/main/java/lin/xi/chun/concurrency/juc/immutability/MutabilityTest.java)

如果一个对象在不能够修改其内部状态（属性），那么它就是线程安全的，因为不存在并发修改啊！这样的对象在Java中有很多，例如在Java 8后，提供了一个新的日期格式化类：
```java
package java.time.format;

/**
 * Formatter for printing and parsing date-time objects.
 * 省略其他……
 *
 * @implSpec
 * This class is immutable and thread-safe. 这个类是不可变且线程安全的
 *
 * @since 1.8 从jdk1.8之后开始提供
 */
public final class DateTimeFormatter {
    /* 省略其他 */
}
```
使用不可变类[示例代码](../../../../src/main/java/lin/xi/chun/concurrency/juc/immutability/ImmutabilityTest.java)，
不可变对象，实际是另一种避免竞争的方式。我打从一开始就注定这样不变，谁也影响不了我，自然而然就不会被任何线程所改变。

## 不可变类设计
另一个大家更为熟悉的 String 类也是不可变的，以它为例，说明一下不可变设计的要素
```java
/**
 * 类上加final修饰符，就不会被子类破坏不可变性
 * */
public final class String
    implements java.io.Serializable, Comparable<String>, CharSequence {
    /** The value is used for character storage. 构造的时候赋值 */
    private final char value[];
    /** Cache the hash code for the string，缓存字符串的hash码，私有也保证了不可变 */
    private int hash; // Default to 0
 
    // ...
    
    /**
     * Initializes a newly created {@code String} object so that it represents
     * the same sequence of characters as the argument; in other words, the
     * newly created string is a copy of the argument string. Unless an
     * explicit copy of {@code original} is needed, use of this constructor is
     * unnecessary since Strings are immutable.
     *
     * @param  original
     *         A {@code String}
     */
    public String(String original) {
        // 传字符串过来，会公用原始数组和hash码
        this.value = original.value;
        this.hash = original.hash;
    }

    /**
     * Allocates a new {@code String} so that it represents the sequence of
     * characters currently contained in the character array argument. The
     * contents of the character array are copied; subsequent modification of
     * the character array does not affect the newly created string.
     *
     * @param  value
     *         The initial value of the string
     */
    public String(char value[]) {
        // 传字符串数组过来，则会拷贝字符串数组，新数组作为String的value，如果不这样做，外部一改变，那就无法保证我这个对象的value不可变了。
        // 这种思想叫“保护性拷贝”
        this.value = Arrays.copyOf(value, value.length);
    }
    
}
```
### 保护性拷贝
但有同学会说，使用字符串时，也有一些跟修改相关的方法啊，比如 substring 等，那么下面就看一看这些方法是
如何实现的，就以 substring 为例：
```java
public final class String{
    
    /* 省略其他 */
    
    /**
     * Returns a string that is a substring of this string. The
     * substring begins with the character at the specified index and
     * extends to the end of this string. <p>
     * Examples:
     * <blockquote><pre>
     * "unhappy".substring(2) returns "happy"
     * "Harbison".substring(3) returns "bison"
     * "emptiness".substring(9) returns "" (an empty string)
     * </pre></blockquote>
     *
     * @param      beginIndex   the beginning index, inclusive.
     * @return     the specified substring.
     * @exception  IndexOutOfBoundsException  if
     *             {@code beginIndex} is negative or larger than the
     *             length of this {@code String} object.
     */
    public String substring(int beginIndex) {
        // 检查下标
        if (beginIndex < 0) {
            throw new StringIndexOutOfBoundsException(beginIndex);
        }
        int subLen = value.length - beginIndex;
        if (subLen < 0) {
            throw new StringIndexOutOfBoundsException(subLen);
        }
        // 当下标为0，即还是自己
        // 当下标>0，则会创建新的对象，也是进行数组拷贝
        return (beginIndex == 0) ? this : new String(value, beginIndex, subLen);
    }

    /**
     * Returns a string that is a substring of this string. The
     * substring begins at the specified {@code beginIndex} and
     * extends to the character at index {@code endIndex - 1}.
     * Thus the length of the substring is {@code endIndex-beginIndex}.
     * <p>
     * Examples:
     * <blockquote><pre>
     * "hamburger".substring(4, 8) returns "urge"
     * "smiles".substring(1, 5) returns "mile"
     * </pre></blockquote>
     *
     * @param      beginIndex   the beginning index, inclusive.
     * @param      endIndex     the ending index, exclusive.
     * @return     the specified substring.
     * @exception  IndexOutOfBoundsException  if the
     *             {@code beginIndex} is negative, or
     *             {@code endIndex} is larger than the length of
     *             this {@code String} object, or
     *             {@code beginIndex} is larger than
     *             {@code endIndex}.
     */
    public String substring(int beginIndex, int endIndex) {
        if (beginIndex < 0) {
            throw new StringIndexOutOfBoundsException(beginIndex);
        }
        if (endIndex > value.length) {
            throw new StringIndexOutOfBoundsException(endIndex);
        }
        int subLen = endIndex - beginIndex;
        if (subLen < 0) {
            throw new StringIndexOutOfBoundsException(subLen);
        }
        return ((beginIndex == 0) && (endIndex == value.length)) ? this
                : new String(value, beginIndex, subLen);
    }

    /**
     * Allocates a new {@code String} that contains characters from a subarray
     * of the character array argument. The {@code offset} argument is the
     * index of the first character of the subarray and the {@code count}
     * argument specifies the length of the subarray. The contents of the
     * subarray are copied; subsequent modification of the character array does
     * not affect the newly created string.
     *
     * @param  value
     *         Array that is the source of characters
     *
     * @param  offset
     *         The initial offset
     *
     * @param  count
     *         The length
     *
     * @throws  IndexOutOfBoundsException
     *          If the {@code offset} and {@code count} arguments index
     *          characters outside the bounds of the {@code value} array
     */
    public String(char value[], int offset, int count) {
        if (offset < 0) {
            throw new StringIndexOutOfBoundsException(offset);
        }
        if (count <= 0) {
            if (count < 0) {
                throw new StringIndexOutOfBoundsException(count);
            }
            if (offset <= value.length) {
                this.value = "".value;
                return;
            }
        }
        // Note: offset or count might be near -1>>>1.
        if (offset > value.length - count) {
            throw new StringIndexOutOfBoundsException(offset + count);
        }
        this.value = Arrays.copyOfRange(value, offset, offset+count);
    }
}
```
发现其内部是调用 String 的构造方法创建了一个新字符串，再进入这个构造看看，是否对 final char[] value 做出了修改。
结果发现也没有，构造新字符串对象时，会生成新的 char[] value，对内容进行复制 。这种通过创建副本对象来避免共享的手段称之为【保护性拷贝（defensive copy）】\
”保护性拷贝“带来的问题就是对象创建得太频繁，容易个数比较多，影响性能。（自解：注意，倒不是说String的设计不好，String肯定是在设计使用它的时候用
保护性拷贝是比较好的形式，但不能为了不可变类都采用“保护性拷贝”的方式，所以就可以往下看有没有其他的设计）

### 模式之享元
#### 简介
##### 定义

英文名称: Flyweight pattern.当需要<mark>重用</mark>数量有限的同一类对象时。（自解：如果取值相同的对象已经有了，那就可以重用这个对象，而不是每次都要创建）

> wikipedia:
> A flyweight is an object that minimizes memory usage by sharing as much data as possible with other similar objects\
> 翻译：享元就是尽可能让相同的对象最小化内存的使用

##### 出自
"Gang of Four"(GOF) design patterns

##### 归类

Structual patterns 结构型模式

#### 体现
##### 包装类
在JDK中 Boolean，Byte，Short，Integer，Long，Character 等包装类提供了valueOf方法，例如Long的valueOf会缓存-128~127之间的 Long 对象，
在这个范围之间会重用对象，大于这个范围，才会新建Long对象：
```java
public class Long{

    /**
     * 私有静态内部类
     * */
    private static class LongCache {
        private LongCache(){}

        static final Long cache[] = new Long[-(-128) + 127 + 1];

        static {
            // 添加数值-128~127的Long对象缓存到本类的cache数组中
            for(int i = 0; i < cache.length; i++) {
                cache[i] = new Long(i - 128);
            }
        }
    }
    
    /**
     * Returns a {@code Long} instance representing the specified
     * {@code long} value.
     * If a new {@code Long} instance is not required, this method
     * should generally be used in preference to the constructor
     * {@link #Long(long)}, as this method is likely to yield
     * significantly better space and time performance by caching
     * frequently requested values.
     *
     * Note that unlike the {@linkplain Integer#valueOf(int)
     * corresponding method} in the {@code Integer} class, this method
     * is <em>not</em> required to cache values within a particular
     * range.
     *
     * @param  l a long value.
     * @return a {@code Long} instance representing {@code l}.
     * @since  1.5
     */
    public static Long valueOf(long l) {
        final int offset = 128;
        if (l >= -128 && l <= 127) { // will cache 数字在这个范围，将会被缓存
            return LongCache.cache[(int)l + offset];
        }
        return new Long(l);
    }
}
```
这样常用的数字，就不会转包装类的时候被重复创建多个对象。看下面的例子，就可以知道这个区别了
```java
public class Test{
    public static void main(String[] args) {
        Long l1 = Long.valueOf(127);
        Long l2 = Long.valueOf(127);
        System.out.println(l1==l2); // 输出结果：true 同个对象

        Long l3 = Long.valueOf(128);
        Long l4 = Long.valueOf(128);
        System.out.println(l3==l4); // 输出结果：false 不同对象
    }
}
```
注意：
- Byte, Short, Long 缓存的范围都是 -128~127
- Character 缓存的范围是 0~127 
- Integer的默认范围是 -128~127
  - 最小值不能变
  - 但最大值可以通过调整虚拟机参数 `-Djava.lang.Integer.IntegerCache.high` 来改变
- Boolean 缓存了 TRUE 和 FALSE

##### String串池

注意说的是“串池”，在Java中，字符串池（String Pool）是一种特殊的对象池，用于存储字符串常量。字符串池中的字符串对象是被共享的，多个字符串变量可以引用同一个字符串对象，从而减少了内存使用量和垃圾回收的开销。

在JVM中，字符串池是存储在方法区（也称为永久代）中的。在JDK 8及之前的版本中，字符串池是存储在永久代的字符串常量池中的。而从JDK 8开始，永久代被移除了，字符串池被转移到了堆中，即在Java堆中的一块区域中存储。这个区域被称为“元空间”（Metaspace），它是堆的一部分，用于存储类的元数据信息。

需要注意的是，虽然字符串池本身并不是垃圾回收机制的一部分，但是其中的字符串对象是可以被垃圾回收的。当一个字符串对象不再被任何变量引用时，它就会变成垃圾，最终会被垃圾回收器回收。但是，如果这个字符串对象是字符串池中的对象，并且仍然有其他变量引用它，那么它就不会被回收，直到没有任何变量引用它为止。

##### BigDecimal和BigInteger

有的人可能要问了，既然BigDecimal是不可变类，那为什么之前使用BigDecimal对账户进行取款操作的[例子](../../../../src/main/java/lin/xi/chun/concurrency/juc/atomic/reference/DecimalAccountSafeCas.java)，
它还需要靠AtomicReference去包装操作呢？来看下BigDecimal的部分源码：
```java
public class BigDecimal extends Number implements Comparable<BigDecimal> {

  /**
   * Sentinel value for {@link #intCompact} indicating the
   * significand information is only available from {@code intVal}.
   */
  static final long INFLATED = Long.MIN_VALUE;  // 为-9223372036854775808

  /* 省略其他代码 */

  /**
   * 减法方法
   * Returns a {@code BigDecimal} whose value is {@code (this -
   * subtrahend)}, and whose scale is {@code max(this.scale(),
   * subtrahend.scale())}.
   *
   * @param  subtrahend value to be subtracted from this {@code BigDecimal}.
   * @return {@code this - subtrahend}
   */
  public BigDecimal subtract(BigDecimal subtrahend) {
    if (this.intCompact != INFLATED) {
        // 当本值的小数点前数字非Long最小值
      if ((subtrahend.intCompact != INFLATED)) {
        // 当参数值的小数点前数字非Long最小值
        return add(this.intCompact, this.scale, -subtrahend.intCompact, subtrahend.scale);
      } else {
        return add(this.intCompact, this.scale, subtrahend.intVal.negate(), subtrahend.scale);
      }
    } else {
      if ((subtrahend.intCompact != INFLATED)) {
        // Pair of subtrahend values given before pair of
        // values from this BigDecimal to avoid need for
        // method overloading on the specialized add method
        return add(-subtrahend.intCompact, subtrahend.scale, this.intVal, this.scale);
      } else {
        return add(this.intVal, this.scale, subtrahend.intVal.negate(), subtrahend.scale);
      }
    }
  }

  private static BigDecimal add(final long xs, int scale1, BigInteger snd, int scale2) {
    int rscale = scale1;
    long sdiff = (long) rscale - scale2;
    boolean sameSigns = (Long.signum(xs) == snd.signum);
    BigInteger sum;
    if (sdiff < 0) {
      int raise = checkScale(xs, -sdiff);
      rscale = scale2;
      long scaledX = longMultiplyPowerTen(xs, raise);
      if (scaledX == INFLATED) {
        sum = snd.add(bigMultiplyPowerTen(xs, raise));
      } else {
        sum = snd.add(scaledX);
      }
    } else { //if (sdiff > 0) {
      int raise = checkScale(snd, sdiff);
      snd = bigMultiplyPowerTen(snd, raise);
      sum = snd.add(xs);
    }
    return (sameSigns) ?
            new BigDecimal(sum, INFLATED, rscale, 0) :
            valueOf(sum, rscale, 0);
  }
}
```
（我没有详细读懂源码的每一行）可以看到最终调用add方法，最后返回的是个新的BigDecimal对象，过程中不更改当前BigDecimal对象的数值，因此它也是不可变的。
那为什么上面示例中还要使用AtomicReference呢？可以看下示例的代码:
```java
public class DecimalAccountSafeCas implements DecimalAccount {

    AtomicReference<BigDecimal> ref;

    public DecimalAccountSafeCas(BigDecimal balance) {
        ref = new AtomicReference<>(balance);
    }

    @Override
    public BigDecimal getBalance() {
        return ref.get();
    }

    // 取款方法
    @Override
    public void withdraw(BigDecimal amount) {
        while (true) {
            BigDecimal prev = ref.get();
            BigDecimal next = prev.subtract(amount);
            // CAS操作
            if (ref.compareAndSet(prev, next)) {
                // 操作成功，退出循环
                break;
            }
        }
    }
}
```
可以看到取款方法while循环块中的代码，如果不用AtomicReference包裹，那么这个BigDecimal对象早就不是原来初始的BigDecimal对象，在业务层面上达不到线程安全的要求。

#### DIY连接池

例如：一个线上商城应用，QPS 达到数千，如果每次都重新创建和关闭数据库连接，性能会受到极大影响。 这时预先创建好一批连接，放入连接池。
一次请求到达后，从连接池获取连接，使用完毕后再还回连接池，这样既节约了连接的创建和关闭时间，也实现了连接的重用，不至于让庞大的连接数压垮数据库。

模拟连接池的简单实现代码见[示例](../../../../src/main/java/lin/xi/chun/concurrency/juc/immutability/Pool.java)

以上只是个模拟的示例，没有考虑：
- 连接的动态增长与收缩 —— 适当进行回收
- 连接保活（可用性检测）—— 网络原因可能会将连接断开
- 等待超时处理 —— 目前是死等获取连接
- 分布式 hash

对于关系型数据库，有比较成熟的连接池实现，例如c3p0, druid等 对于更通用的对象池，可以考虑使用apache commons pool，例如redis连接池可以参考jedis中关于连接池的实现

使用测试代码可见[示例](../../../../src/main/java/lin/xi/chun/concurrency/juc/immutability/FlyweightDemoTest.java)


