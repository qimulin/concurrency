package lin.xi.chun.concurrency.juc.lock_free.volatile_demo;

/**
 * @author zhou.wu
 * @description 计数器
 * @date 2023/7/27
 **/
public class MyCounter {

    private volatile int counter = 0;

    public void increment() {
        counter++;
    }

    public int getCount() {
        return counter;
    }
}
