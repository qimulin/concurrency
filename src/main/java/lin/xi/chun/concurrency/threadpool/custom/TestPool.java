package lin.xi.chun.concurrency.threadpool.custom;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author zhou.wu
 * @description 测试线程池
 * @date 2023/8/9
 **/
@Slf4j(topic = "my.TestPool")
public class TestPool {

    public static void main(String[] args) {
        RejectPolicy rejectPolicy = null;
        // 1、死等
        rejectPolicy = (RejectPolicy<Runnable>) (queue, task) -> queue.put(task);
        // 2、带超时等待
        rejectPolicy = (RejectPolicy<Runnable>) (queue, task) -> queue.offer(task, 500, TimeUnit.MILLISECONDS);
        // 3、让调用者放弃任务执行
        rejectPolicy = (RejectPolicy<Runnable>) (queue, task) -> log.info("放弃任务{}", task);
        // 4、让调用者抛出异常，跟放弃执行的方式区别：抛出异常，下面循环中后面的任务根本不会执行
        rejectPolicy = (RejectPolicy<Runnable>) (queue, task) -> {throw new RuntimeException("任务执行失败"+task);};
        // 4、让调用者自己执行任务，跟放弃执行的方式区别：抛出异常，下面循环中后面的任务根本不会执行
        rejectPolicy = (RejectPolicy<Runnable>) (queue, task) -> {log.info("main执行{}", task); task.run();};

        ThreadPool threadPool = new ThreadPool(
                1,
                1000,
                TimeUnit.MILLISECONDS,
                1,
                rejectPolicy
        );
        for (int i = 0; i < 4; i++) {
            int j = i;
            threadPool.execute(() -> {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.info("{}", j);
            });
        }
    }
}

/**
 * 拒绝策略接口
 * */
@FunctionalInterface
interface RejectPolicy<T> {

    void reject(BlockingQueue<T> queue, T task);
}

@Slf4j(topic = "my.ThreadPool")
class ThreadPool {

    /** 等待的任务队列 */
    private BlockingQueue<Runnable> taskQueue;

    /** 线程集合 */
    private HashSet<Worker> workers = new HashSet<>();

    /** 核心线程数 */
    private int coreSize;

    /** 获取任务时的超时时间 */
    private long timeout;

    /** 时间单位 */
    private TimeUnit timeUnit;

    /** 拒绝策略 */
    private RejectPolicy<Runnable> rejectPolicy;

    /** 构造方法 */
    public ThreadPool(int coreSize, long timeout, TimeUnit timeUnit, int queueCapcity, RejectPolicy<Runnable> rejectPolicy) {
        this.coreSize = coreSize;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        this.taskQueue = new BlockingQueue<>(queueCapcity);
        this.rejectPolicy = rejectPolicy;
    }

    /**
     * 执行任务
     * */
    public void execute(Runnable task) {
        // 当任务数没有超过 coreSize 时，直接交给 worker 对象执行
        // 如果任务数超过 coreSize 时，加入任务队列暂存
        // synchronized保证workers的线程安全
        synchronized (workers) {
            if(workers.size() < coreSize) {
                Worker worker = new Worker(task);
                log.debug("新增 worker{}, {}", worker, task);
                // 加入线程集合
                workers.add(worker);
                // 线程执行
                worker.start();
            } else {
                // 加入任务队列，put方法里面，如果等待队列的任务数等于容量，则等待，但这样对主线程非常不友好。所以可以引入拒绝策略
//                taskQueue.put(task);
                // 队列满了可以做出的选择有如下：
                // 1) 死等
                // 2) 带超时等待
                // 3) 让调用者放弃任务执行
                // 4) 让调用者抛出异常
                // 5) 让调用者自己执行任务
                taskQueue.tryPut(rejectPolicy, task);
            }
        }
    }

    /**
     * Thread包含的信息有限，所以要继续包装一个Worker类
     * */
    class Worker extends Thread{

        private Runnable task;

        public Worker(Runnable task) {
            this.task = task;
        }

        @Override
        public void run() {
            // 执行任务
            // 1) 当 task 不为空，执行任务
            // 2) 当 task 执行完毕，再接着从等待的任务队列获取任务并执行
            // while(task != null || (task = taskQueue.take()) != null) {
            while(task != null || (task = taskQueue.poll(timeout, timeUnit)) != null) {
                try {
                    log.debug("正在执行...{}", task);
                    task.run();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    // 任务执行完，task置为null
                    task = null;
                }
            }
            synchronized (workers) {
                log.debug("worker被worker集合移除 {}", this);
                workers.remove(this);
            }
        }
    }
}

@Slf4j(topic = "my.BlockingQueue")
class BlockingQueue<T> {

    /** 任务队列：双向链表 */
    private Deque<T> queue = new ArrayDeque<>();

    /** 锁：线程取任务的时候加锁 */
    private ReentrantLock lock = new ReentrantLock();

    /** 生产者的条件变量：阻塞队列有容量限制，不可能无限的加新的任务 */
    private Condition fullWaitSet = lock.newCondition();

    /** 消费者的条件变量：在队列没有元素的时候，进行等待 */
    private Condition emptyWaitSet = lock.newCondition();

    /** 容量：阻塞队列的容量上限 */
    private int capcity;

    /** 构造方法 */
    public BlockingQueue(int capcity) {
        this.capcity = capcity;
    }

    /**
     * 阻塞获取：从阻塞队列里取出元素-相当于在消费
     * */
    public T take() {
        lock.lock();
        try {
            // 当没有元素，则进行等待
            while (queue.isEmpty()) {
                try {
                    emptyWaitSet.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 获取队列头部的元素，并移除
            T t = queue.removeFirst();
            // 通知可以继续生产了
            fullWaitSet.signal();
            // 头部元素返回
            return t;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 带超时阻塞获取：丰富“阻塞获取”功能
     * */
    public T poll(long timeout, TimeUnit unit) {
        lock.lock();
        try {
            // 将 timeout 统一转换为 纳秒
            long nanos = unit.toNanos(timeout);
            while (queue.isEmpty()) {
                try {
                    // 返回值是剩余时间，超时了就无需等待，也是返回null
                    if (nanos <= 0) {
                        return null;
                    }
                    /*
                     * 当被唤醒后，到while循环的判断条件，queue.isEmpty()成立，那么需要继续等待，可是已经等待了timeout的时间，下次还是要继续等这么久的时间？
                     * 为解决上述这个问题，awaitNanos方法的返回值是：剩余时间=等待时间-经过时间，
                     * 而剩余时间又再次赋值给了nanos，这样nanos在循环的过程中，值是逐渐减小的，就避免了过久的等待。
                     * */
                    nanos = emptyWaitSet.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            T t = queue.removeFirst();
            fullWaitSet.signal();
            return t;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 阻塞添加：往阻塞队列里添加元素-相当于在生产
     * */
    public void put(T task) {
        lock.lock();
        try {
            // 当队列满了，阻塞，不要再往队列放元素
            while (queue.size() == capcity) {
                try {
                    log.debug("等待加入任务队列 {} ...", task);
                    fullWaitSet.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.debug("加入任务队列 {}", task);
            queue.addLast(task);
            // 通知可以来消费了
            emptyWaitSet.signal();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 带超时时间阻塞添加：往阻塞队列里添加元素-相当于在生产
     * */
    public boolean offer(T task, long timeout, TimeUnit timeUnit) {
        lock.lock();
        try {
            long nanos = timeUnit.toNanos(timeout);
            while (queue.size() == capcity) {
                try {
                    if(nanos <= 0) {
                        return false;
                    }
                    log.debug("等待加入任务队列 {} ...", task);
                    nanos = fullWaitSet.awaitNanos(nanos);} catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.debug("加入任务队列 {}", task);
            queue.addLast(task);
            emptyWaitSet.signal();
            return true;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取阻塞队列大小
     * */
    public int size() {
        lock.lock();
        try {
            return queue.size();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 带拒绝策略的添加任务
     * */
    public void tryPut(RejectPolicy<T> rejectPolicy, T task) {
        lock.lock();
        try {
            // 判断队列是否满
            if(queue.size() == capcity) {
                rejectPolicy.reject(this, task);
            } else {
                // 有空闲
                log.debug("加入任务队列 {}", task);
                queue.addLast(task);
                emptyWaitSet.signal();
            }
        } finally {
            lock.unlock();
        }
    }

}
