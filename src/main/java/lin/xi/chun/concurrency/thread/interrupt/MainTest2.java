package lin.xi.chun.concurrency.thread.interrupt;

/**
 * @author lin.xc
 * @date 2021/6/2
 **/
public class MainTest2 {
    public static void main(String[] args) {
        try {
            MyThread2 t2 = new MyThread2();
            t2.start();
            Thread.sleep(20); // modify 2000 to 20
            // 请求中断MyThread线程
//            System.out.println("wait");
//            thread.sleep(3000);
            // 调用线程中断方法
            t2.interrupt();
//            System.out.println("当前线程状态为："+thread.getState());
//            System.out.println("wait后到这里");

        } catch (InterruptedException e) {
            System.out.println("main catch");
            e.printStackTrace();
        }
        System.out.println("end!");
    }

}
