package lin.xi.chun.concurrency.juc.lock_free;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhou.wu
 * @description: 账户接口线程安全的实现
 * @date 2022/8/29
 **/
public class AccountSafe implements Account {

    private AtomicInteger balance;    // 共享资源

    /** 构造传入余额 */
    public AccountSafe(Integer balance) {
        this.balance = new AtomicInteger(balance);
    }

    @Override
    public Integer getBalance() {
        return balance.get();
    }

    @Override
    public void withdraw(Integer amount) {
        // 麻烦的一种写法（能体验AtomicInteger内部的原理）
        while (true) {
            // 获取余额的最新值
            /*
             点进get方法，就可以看到该方法返回的变量value是由volatile修饰的，保证该变量的可见性，
             它可以用来修饰成员变量和静态成员变量，他可以避免线程从自己的工作缓存中查找变量的值，必须到主存中获取它的值，
             线程操作 volatile 变量都是直接操作主存。即一个线程对 volatile 变量的修改，对另一个线程可见。
             CAS 必须借助 volatile 才能读取到共享变量的最新值来实现【比较并交换】的效果（自解：拿到最新概率才会大）
             【注意】 volatile 仅仅保证了共享变量的可见性，让其它线程能够看到最新值，但不能解决指令交错问题（不能保证原子性）
             */
            int prev = balance.get();
            // 余额减去取款金额=剩余余额（=要修改的余额），这个值还是在工作内存上，没有存到主存中去
            int next = prev - amount;
            /*
             compareAndSet 正是做这个检查，在 set 前，先比较 prev 与当前值
             - 不一致了，next 作废，返回 false 表示失败
             - 一致，以 next 设置为新值，返回 true 表示成功
             */
            // compareAndSet（也有Compare And Swap的说法，简称CAS 类似乐观锁的实现）: 参数1-当前余额最新值；参数2-要修改的值，
            // 底层是CPU指令级别的，可以看做是原子的"比较"和"设置"是原子的。核心思想
            if (balance.compareAndSet(prev, next)) {
                // 当参数1的值等于balance当前的值，则返回true；反之则返回false，就会在while循环做下一次尝试
                break;
            }
        }
        // 可以简化为下面的方法
        // balance.addAndGet(-1 * amount); // 负数则为减法
    }
}
/**
 * 后记：
 * 其实 CAS 的底层是 lock cmpxchg 指令（X86 架构），在单核 CPU 和多核 CPU 下都能够保证【比较-交换】的原子性。
 * 在多核状态下，某个核执行到带 lock 的指令时，CPU 会让总线锁住，当这个核把此指令执行完毕，再开启总线。这个过程中不会被线程的调度机制所打断，保证了多个线程对内存操作的准确性，是原子的。
 *
 * 通过执行时间可以得出，通常这种有锁无锁都支持实现的情况下，使用“无锁”方式的性能会强于"有锁"。那为什么无锁的效率高呢？
 * - 无锁情况下，即使重试失败，线程始终在高速运行，没有停歇，而 synchronized 会让线程在没有获得锁的时候，发生上下文切换，进入阻塞。打个比喻
 * - 线程就好像高速跑道上的赛车，高速运行时，速度超快，一旦发生上下文切换，就好比赛车要减速、熄火，等被唤醒又得重新打火、启动、加速... 恢复到高速运行，代价比较大
 * - 但无锁情况下，因为线程要保持运行，需要额外 CPU 的支持，CPU 在这里就好比高速跑道，没有额外的跑道，线程想高速运行也无从谈起，虽然不会进入阻塞，但由于没有分到时间片，仍然会进入可运行状态，还是会导致上下文切换。
 *
 * （自解：阻塞、唤醒的代价更大些；CAS在多核CPU的环境下更能发挥优势；而且线程数最好别超过CPU的核心数
 * - 有锁：本来一直在运行的很有可能就得阻塞下来，存储临时数据，后面唤醒的时候还得恢复，这些比较耗性能
 * - 无锁：跑道足够，就不会发生上下文切换，也不会需要阻塞和唤醒之类的
 * ）
 *
 * CAS 的特点
 * 结合 CAS 和 volatile 可以实现无锁并发，适用于线程数少、多核 CPU 的场景下。
 * - CAS 是基于乐观锁的思想：最乐观的估计，不怕别的线程来修改共享变量，就算改了也没关系，我吃亏点再重试呗。
 * - synchronized 是基于悲观锁的思想：最悲观的估计，得防着其它线程来修改共享变量，我上了锁你们都别想改，我改完了解开锁，你们才有机会。
 * - CAS 体现的是无锁并发、无阻塞并发，请仔细体会这两句话的意思
 * -- 因为没有使用 synchronized，所以线程不会陷入阻塞，这是效率提升的因素之一
 * -- 但如果竞争激烈，可以想到重试必然频繁发生，反而效率会受影响
 *
 * */
