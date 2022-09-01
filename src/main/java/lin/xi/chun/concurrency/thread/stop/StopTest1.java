package lin.xi.chun.concurrency.thread.stop;

/**
 * @author zhou.wu
 * @description: stop方法测试
 * @date 2022/8/4
 **/
public class StopTest1 {
    public static void main(String[] args) throws InterruptedException {
        Object o1=new Object();
        Object o2=new Object();
        Thread t1=new Thread(()->{
            synchronized (o1)
            {
                synchronized (o2)
                {
                    try {
                        System.out.println("t1获取到锁");
                        Thread.sleep(5000);
                        System.out.println("t1结束");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        System.out.println("t1抛出异常！");
                    }finally {
                        System.out.println("测试t1会到这里吗？");
                    }
                }
            }
        });
        t1.start();
        Thread.sleep(1000);
        Thread t2=new Thread(()->{
            synchronized (o1)
            {
                synchronized (o2)
                {
                    try {
                        System.out.println("t2获取到锁");
                        Thread.sleep(5000);
                        System.out.println("t2结束");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t2.start();
        // t1 stop
        /* 停止线程会导致它解锁已锁定的所有监视器.如果先前受这些监视器保护的任何对象处于不一致状态,则其他线程现在可以在不一致状态下查看这些对象.
        这些对象被认为已损坏.当线程对受损对象进行操作时,可能会导致任意行为 */
        t1.stop();
    }
}
