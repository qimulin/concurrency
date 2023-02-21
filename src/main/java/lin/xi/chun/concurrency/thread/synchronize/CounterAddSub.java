package lin.xi.chun.concurrency.thread.synchronize;

/**
 * @author zhou.wu
 * @description: 计数加减
 * @date 2023/2/21
 **/
public class CounterAddSub {
    static int counter = 0;
    static final Object room = new Object();
    public static void main(String[] args) throws InterruptedException {
        // t1进行counter--
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 5000; i++) {
                synchronized (room) {   // 注意：这个synchronized是放在循环内的
                    System.out.println("counter="+counter+", counter++");
                    counter++;
                }
            }
        }, "t1");
        // t2进行counter--
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 5000; i++) {
                synchronized (room) {
                    System.out.println("counter="+counter+", counter--");
                    counter--;
                }
            }
        }, "t2");

        t1.start();
        t2.start();
        // 主线程等待t1和t2的完成
        t1.join();
        t2.join();
        System.out.println(counter);
    }
}
