package lin.xi.chun.concurrency.thread.interrupt;

/**
 * @author lin.xc
 * @date 2021/6/2
 **/
public class MainTest {
    public static void main(String[] args) {
        try {
            MyThread thread = new MyThread();
            thread.start();
            Thread.sleep(20);//modify 2000 to 20
            // 请求中断MyThread线程
            thread.interrupt();
        } catch (InterruptedException e) {
            System.out.println("main catch");
            e.printStackTrace();
        }
        System.out.println("end!");
    }

}
