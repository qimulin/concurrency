# J.U.C
"juc" 的翻译是 "Java Util Concurrency"，即 "Java工具类库并发"。

需要掌握的点：
- Lock（锁）：Lock是Java并发编程中提供的一种同步机制，用于控制对共享资源的访问。它提供了与传统的synchronized关键字不同的灵活性和功能。
- Semaphore（信号量）：Semaphore是一种计数信号量，用于控制同时访问某个资源的线程数量。它可以用来限制并发访问的线程数量，或者在多个线程之间进行信号的传递。
- CountDownLatch（倒计时门闩）：CountDownLatch是一种同步工具，它允许一个或多个线程等待其他线程完成操作后再继续执行。它通过一个计数器实现，当计数器的值达到零时，等待的线程将被释放。
- CyclicBarrier（循环屏障）：CyclicBarrier是一种同步辅助类，它允许一组线程在某个点上相互等待，直到所有线程都到达该点。一旦所有线程都到达，它们可以选择执行某个共同任务。
- ConcurrentHashMap（并发哈希表）：ConcurrentHashMap是Java中线程安全的哈希表实现，它支持高度并发的读写操作。它通过分段锁（Segment）的方式实现并发控制，从而提供了更好的性能。
- BlockingQueue（阻塞队列）：BlockingQueue是一种特殊的队列，它支持在队列为空时阻塞等待元素的添加，或在队列已满时阻塞等待元素的移除。它常用于实现生产者-消费者模式。
- CopyOnWriteArrayList（写时复制数组列表）：CopyOnWriteArrayList是一种线程安全的动态数组实现，它通过在修改操作时创建数组的副本来实现并发安全。它适用于读多写少的场景，读操作无锁，写操作通过复制数组来保证线程安全。

## AQS原理
AQS为什么要先讲？因为其他的并发工具都是依赖了这个AQS实现的。

是个抽象类，全称是 AbstractQueuedSynchronizer（直译过来就是：抽象的基于队列的同步器，其他的同步工具都是基于它的），是阻塞式锁和相关的同步器工具的框架。
它是阻塞式的锁，不同于CAS，更类似Synchronized。

特点：
- 用 state 属性来表示资源的状态（分独占模式和共享模式），<font color="red">子类需要定义如何维护这个状态（子类可以决定什么数字表示什么状态）</font>，
控制如何获取锁和释放锁
  - getState - 获取 state 状态
  - setState - 设置 state 状态
  - compareAndSetState - cas 机制设置 state 状态（防止多个线程来修改的时候，state的线程安全）
  - <font color="red">独占模式是只有一个线程能够访问资源，而共享模式可以允许多个线程访问资源</font>（但是一般会提供一个访问上限）
- 提供了基于<font color="red">FIFO（先进先出）的等待队列</font>，类似于 Monitor 的 EntryList（synchronized的Monitor是基于C++实现的，而我们这个是基于纯Java）
- 条件变量来实现等待、唤醒机制，支持多个条件变量，类似于 Monitor 的 WaitSet（比synchronized强的地方是它支持多个条件变量）

子类主要实现这样一些方法（默认抛出 UnsupportedOperationException）
- tryAcquire
- tryRelease
- tryAcquireShared
- tryReleaseShared
- isHeldExclusively

获取锁的姿势
```java
// 如果获取锁失败
if (!tryAcquire(arg)) {
 // 入队, 可以选择阻塞当前线程 park unpark
}
```

释放锁的姿势
```java
// 如果释放锁成功
if (tryRelease(arg)) {
 // 让阻塞线程恢复运行
}
```