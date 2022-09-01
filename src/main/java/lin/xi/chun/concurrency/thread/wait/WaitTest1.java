package lin.xi.chun.concurrency.thread.wait;

/**
 * @author zhou.wu
 * @description wait与suspend方法的区别
 * @date 2022/8/9
 **/
public class WaitTest1 {
    public static void main(String[] args) throws InterruptedException {
        Object o1=new Object();
        Object o2=new Object();
        Thread t1=new Thread(()->{
            synchronized (o1) {
                System.out.println("t1获取到o1锁开始执行");
                try {
                    System.out.println("begin wait");
                    o1.wait();  // wait和suspend不同，wait作用在锁对象上，suspend作用在线程上；而且wait方法会可以暂时让出锁，不会造成出现异常未解锁的情况
                    // 【注意】！！！被notify之后本线程需要重新竞争锁，再继续执行
                    System.out.println("end wait，continue");
                    Thread.sleep(5000); // 模拟执行业务逻辑
                } catch (InterruptedException e) {
                    System.out.println("catch 1");
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
                    Thread.sleep(3000); // 执行耗时业务
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (o1) {
                    System.out.println("t2获取到o1锁开始继续执行");
                    o1.notify();
                }
                System.out.println("t2执行结束");
            }
        }, "t2");
        t2.start();

//        Thread.sleep(1000);
//        t1.suspend();

//        t1.resume();
    }
}
/**
 * 结果：t1一直无法执行结束；t2一直无法继续执行
 * 可以看到，整个程序卡的死死的，在调用resume恢复t1线程之前抛出了一个未知异常，导致t1一直挂起进而无法释放o1锁，
 * 而t2需要获取到o1锁后才能继续执行，但苦苦等待，奈何o1被t1拿捏的死死的，从此整个程序就陷入了无尽的等待中----死锁。
 * */
