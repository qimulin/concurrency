# 自定义线程池

## 自定义线程池

![自定义线程池组件](../images/20230809001.png)

手写一个自定义的线程池，有助于以后理解JDK中线程池的使用。详见[自定义线程代码](../../../../src/main/java/lin/xi/chun/concurrency/threadpool/custom/TestPool.java)

## JDK提供的线程池

### ThreadPoolExecutor

![线程池接口实现类](../images/20230817001.png)

- ExecutorService：定义线程池基本的方法
- ScheduleExecutorService：扩展ExecutorService，新增任务调度功能，用来定时执行任务

#### 线程池状态

ThreadPoolExecutor 使用 int 的高 3 位来表示线程池状态，低 29 位表示线程数量（共32位）

那为什么不用两个int数，一个存储“状态”，一个存储“数量”呢？原因后面补充……

<table>
    <tr>
        <th>状态名</th>
        <th>高3位</th>
        <th>接收新任务</th>
        <th>处理阻塞队列任务</th>
        <th>说明</th>
    </tr>
    <tr>
        <td>RUNNING</td>
        <td>111</td>
        <td>Y</td>
        <td>Y</td>
        <td>当线程池对象被创建出来，初始的状态就是RUNNING</td>
    </tr>
     <tr>
        <td>SHUTDOWN</td>
        <td>000</td>
        <td>N</td>
        <td>Y</td>
        <td>当调用了线程池的shutdown()方法，意图就是要停止线程池。调用shutdown()方法的那一刻就不会接收新任务，但它相对温和，会让运行中的任务处理完，也会处理阻塞队列剩余任务</td>
    </tr>
    <tr>
        <td>STOP</td>
        <td>001</td>
        <td>N</td>
        <td>N</td>
        <td>当调用了线程池的shutdownNow()方法，会中断正在执行的任务，并抛弃阻塞队列任务。STOP相对SHUTDOWN比较暴力</td>
    </tr>
    <tr>
        <td>TIDYING</td>
        <td>010</td>
        <td>-</td>
        <td>-</td>
        <td>任务全执行完毕，活动线程为 0 即将进入终结</td>
    </tr>
    <tr>
        <td>TERMINATED</td>
        <td>011</td>
        <td>-</td>
        <td>-</td>
        <td>终结状态</td>
    </tr>
</table>

<font color="red">从数字上比较，TERMINATED > TIDYING > STOP > SHUTDOWN > RUNNING，RUNNING是“111”，为什么是最小的？这是因为最高位表示符号位，因此它是个负数。</font>
自解： 在计算机中，使用最高位作为符号位的情况下，通常使用二进制补码表示有符号整数。在这种表示方式下，最高位为0表示正数，最高位为1表示负数。
补码为"111"的二进制数在十进制中表示的值是-1

这些信息存储在一个原子变量 ctl 中，目的是将线程池状态与线程个数合二为一，这样就可以用<font color="red">一次 cas 原子操作</font>进行赋值

```java
// c 为旧值， ctlOf方法返回结果为新值(计算新的状态和新的线程数量)
ctl.compareAndSet(c, ctlOf(targetState, workerCountOf(c))));

// rs（Running States）为高 3 位代表线程池状态， wc（Worker Count）为低 29 位代表线程个数，ctl 是合并它们
// 由于两个属性值对应的不同位，所以运算中做个位或就可以了
private static int ctlOf(int rs, int wc) { return rs | wc; }
```

#### 构造方法
```java
public ThreadPoolExecutor(
        int corePoolSize,
        int maximumPoolSize,
        long keepAliveTime,
        TimeUnit unit,
        BlockingQueue<Runnable> workQueue,
        ThreadFactory threadFactory,
        RejectedExecutionHandler handler
)
```

- corePoolSize 核心线程数目 (最多保留的线程数)
- maximumPoolSize 最大线程数目（maximumPoolSize = 核心线程数corePoolSize + 救急线程数）救急线程任务执行完，没有新任务了，就会销毁掉。
- keepAliveTime 生存时间 - 针对救急线程（没活可干的时候能生存多久）
- unit 时间单位 - 针对救急线程
- workQueue 阻塞队列
- threadFactory 线程工厂 - 可以为线程创建时起个好名字。方便把线程池中的线程和其他线程区分开来
- handler 拒绝策略。当等待队列满员，最大线程数耗光（即核心线程都派上了，救急线程也都派上了），仍有新任务进来需要执行，这时候就会执行拒绝策略。

![线程池接口实现类](../images/20230817002.jpg)

- 线程池中刚开始没有线程，当一个任务提交给线程池后，线程池会创建一个新线程来执行任务。
- 当线程数达到 corePoolSize 并没有线程空闲，这时再加入任务，新加的任务会被加入workQueue 队列排队，直到有空闲的线程。
- 如果队列选择了有界队列，那么任务超过了队列大小时，会创建 maximumPoolSize - corePoolSize 数目的线程来救急。
  <font color="red">如果用的是无界队列，就没有救急线程的概念了，都是核心线程执行完一个就去等待队列里面拿一个执行。</font>
- 如果线程到达 maximumPoolSize 仍然有新任务这时会执行拒绝策略。拒绝策略 jdk 提供了 4 种实现(接口是叫RejectedExecutionHandler)，其它著名框架也提供了实现
  - AbortPolicy 让调用者抛出 RejectedExecutionException 异常，这是默认策略
  - CallerRunsPolicy 让调用者运行任务
  - DiscardPolicy 放弃本次任务
  - DiscardOldestPolicy 放弃队列中最早的任务，本任务取而代之
  - Dubbo 的实现，在抛出 RejectedExecutionException 异常之前会记录日志，并 dump 线程栈信息，方便定位问题
  - Netty 的实现，是创建一个新线程来执行任务
  - ActiveMQ 的实现，带超时等待（60s）尝试放入队列，类似我们之前自定义的拒绝策略
  - PinPoint 的实现，它使用了一个拒绝策略链，会逐一尝试策略链中每种拒绝策略
- 当高峰过去后，超过corePoolSize 的救急线程如果一段时间没有任务做，需要结束节省资源，这个时间由keepAliveTime和unit来控制。

![拒绝策略接口和实现](../images/20230817003.png)

根据这个构造方法，JDK Executors 类中提供了众多工厂方法来创建各种用途的线程池（其实就是定好了各种参数组合）

#### newFixedThreadPool
翻译过来：创建一个固定大小的线程池
```java
public static ExecutorService newFixedThreadPool(int nThreads) {
    return new ThreadPoolExecutor(nThreads, nThreads, 
        0L, TimeUnit.MILLISECONDS,
        new LinkedBlockingQueue<Runnable>());
}
```

特点
- 核心线程数=最大线程数（没有救急线程被创建），因此也无需超时时间
- 阻塞队列是无界的，可以放任意数量的任务（自解：队列构造函数中没有指定容量大小，但其实我看无参构造，也是有容量值的，只不过是Integer.MAX_VALUE，很大了，相当于无界）

> **评价** 适用于任务量已知，相对耗时的任务

#### newCachedThreadPool
翻译过来：带缓冲功能的线程池
```java
public static ExecutorService newCachedThreadPool() {
    return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
        60L, TimeUnit.SECONDS,
        new SynchronousQueue<Runnable>());
}
```

**特点**
- 核心线程数是 0， 最大线程数是 Integer.MAX_VALUE，救急线程的空闲生存时间是 60s，意味着
  - 全部都是救急线程（60s 后可以回收）
  - 救急线程可以无限创建
- 队列采用了 SynchronousQueue 实现特点是，它没有容量，没有线程来取是放不进去的（一手交钱、一手交货），可参考[代码](../../../../src/main/java/lin/xi/chun/concurrency/threadpool/TestSynchronousQueue.java)

> **评价** 整个线程池表现为线程数会根据任务量不断增长，没有上限，当任务执行完毕，空闲1分钟后释放线程。\
> 适合任务数比较密集，但每个任务执行时间较短的情况。
> 
> 自解：并不是同时只能执行一个，单个线程如果任务耗时时间长的话，还是会并发执行多个的。例如：来一个快递员取件给一个快递，那送快递的时间要是长，
> 会同时有多个快递员一起送快递的。

#### newSingleThreadExecutor
```java
public static ExecutorService newSingleThreadExecutor() {
    return new FinalizableDelegatedExecutorService(
            new ThreadPoolExecutor(1, 1, 
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>()));
}
```
**特点**
- 核心线程数是1， 最大线程数是1，意味着没有救急线程。
- 队列选择的也是阻塞的，没有边界的队列。

**使用场景：**

希望多个任务排队执行。线程数固定为 1，任务数多于 1 时，会放入无界队列排队。任务执行完毕，这唯一的线程也不会被释放。

1. 那和创建一个线程，执行一堆任务有什么区别呢？

- <font color="red">自己创建一个单线程串行执行任务，如果任务执行失败而终止那么没有任何补救措施，而线程池还会新建一个线程，保证池的正常工作</font>，可参考[代码](../../../../src/main/java/lin/xi/chun/concurrency/threadpool/TestSynchronousQueue.java)

2. 那和Executors.newFixedThreadPool(1) 初始值设为1有什么区别呢？

- Executors.newFixedThreadPool(1) 初始时为1，以后<font color="red">还可以修改</font>。而且new返回的是ThreadPoolExecutor对象，可以强转后调用 setCorePoolSize等方法进行修改
- 根据上一点，所以需要Executors.newSingleThreadExecutor() 线程个数始终为1，不能修改。因此它<font color="red">外面包装了FinalizableDelegatedExecutorService，应用的是装饰器模式</font>，只对外暴露了ExecutorService接口，因此不能调用ThreadPoolExecutor中特有的方法
