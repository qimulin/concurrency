# 共享模型之不可变
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
结果发现也没有，构造新字符串对象时，会生成新的 char[] value，对内容进行复制 。这种通过创建副本对象来避免共享的手段称之为【保护性拷贝（defensive copy）】

### 模式之享元
<B>定义</B>\
英文名称: Flyweight patem.当需要重用数量有限的同一类对象时
> wikipedia:
A flyweight is an object that minimizes memory usage by sharing as much data as possible with other similarobjects

<B>出自</B>\
"Gang of Four" design patterns

<B>归类</B>\
Structual patterns
