package lin.xi.chun.concurrency.thread.suspend;

/**
 * @author zhou.wu
 * @description suspend方法
 * suspend 美[səˈspend] 悬; 挂; 吊; 暂停; 中止; 使暂停发挥作用(或使用等); 延缓; 暂缓; 推迟; 使暂时停职；
 * resume 美[rɪˈzuːm] 重新开始; (中断后)继续; 配合suspend使用
 * Suspend和Resume为什么被弃用？
 * - 因为suspend本质上容易出现死锁；
 * - 当线程A使用锁L，如果线程A处于suspend状态，而另一个线程B，需要L这把锁才能resume线程A，因为线程A处理suspend状态，是不会释放L锁，所以线程B线获取不到这个锁，
 *      而一直处于争夺L锁的状态，最终导致死锁。
 * @date 2022/8/9
 **/
public class SuspendTest {
    public static void main(String[] args) throws InterruptedException {
        Object o1=new Object();
        Object o2=new Object();
        Thread t1=new Thread(()->{
            synchronized (o1) {
                System.out.println("t1获取到o1锁开始执行");
                try {
                    Thread.sleep(5000); // 模拟执行业务逻辑
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("t1执行结束");
            }
        }, "t1");
        t1.start();

        Thread t2=new Thread(()->{
            synchronized (o2) {
                System.out.println("t2获取到o2开始执行");
                try {
                    Thread.sleep(2000); // 执行耗时业务
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (o1) {
                    System.out.println("t2获取到o1锁开始继续执行");
//                    t1.resume();
                }
                System.out.println("t2执行结束");
            }
        }, "t2");
        t2.start();

        Thread.sleep(1000);
        t1.suspend();
        //假设抛出了一个未知异常，t1还未resume
        int i=1/0;
        t1.resume();
    }
}
/**
 * 结果：t1一直无法执行结束；t2一直无法继续执行
 * 可以看到，整个程序卡的死死的，在调用resume恢复t1线程之前抛出了一个未知异常，导致t1一直挂起进而无法释放o1锁，
 * 而t2需要获取到o1锁后才能继续执行，但苦苦等待，奈何o1被t1拿捏的死死的，从此整个程序就陷入了无尽的等待中----死锁。
 * */
