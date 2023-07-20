package lin.xi.chun.concurrency.juc.immutability;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * @author zhou.wu
 * @description 享元模式的体现-连接池
 * @date 2023/7/20
 **/
@Slf4j
public class Pool {

    public static final int CONNECTION_STATE_IDLE = 0;
    public static final int CONNECTION_STATE_BUSY = 1;

    /** 1. 连接池大小（真正的连接池可以进行扩容和收缩，这里只是简单实现） */
    private final int poolSize;
    /** 2. 连接对象数组 */
    private Connection[] connections;
    /** 3. 连接状态数组 0 表示空闲， 1 表示繁忙  */
    private AtomicIntegerArray states;
    /** 4. 构造方法初始化   */
    public Pool(int poolSize) {
        this.poolSize = poolSize;
        this.connections = new Connection[poolSize];
        // int数组，每个元素默认值都是0，所以初始化的池里的元素状态都是CONNECTION_STATE_IDLE
        this.states = new AtomicIntegerArray(new int[poolSize]);
        // 将设置的池大小的连接对象放入池中
        for (int i = 0; i < poolSize; i++) {
            connections[i] = new MockConnection("连接" + (i+1));
        }
    }

    /** 5. 借连接 */
    public Connection borrow() {
        // 乐观锁的实现
        while(true) {
            for (int i = 0; i < poolSize; i++) {
                // 循环，获取空闲连接
                if(states.get(i) == CONNECTION_STATE_IDLE) {
                    // 状态空闲，CAS设置成繁忙
                    if (states.compareAndSet(i, CONNECTION_STATE_IDLE, CONNECTION_STATE_BUSY)) {
                        log.debug("borrow {}", connections[i]);
                        return connections[i];
                    }
                }
            }
            // 如果没有空闲连接，当前线程进入等待。防止空转，浪费CPU资源
            synchronized (this) {
                try {
                    log.debug("wait...");
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**  6. 归还连接 */
    public void free(Connection conn) {
        for (int i = 0; i < poolSize; i++) {
            // 找到对应的连接，设置状态为空闲
            if (connections[i] == conn) {
                // 归还连接本身就是这个连接的持有线程，不会有其他线程和你共享一个连接，所以不用CAS去设置更改
                states.set(i, CONNECTION_STATE_IDLE);
                synchronized (this) {
                    // synchronized包围，对应wait
                    log.debug("free {}", conn);
                    // 唤醒
                    this.notifyAll();
                }
                break;
            }
        }
    }
}
