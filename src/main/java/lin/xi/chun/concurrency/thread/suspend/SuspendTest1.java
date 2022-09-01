package lin.xi.chun.concurrency.thread.suspend;

/**
 * @author zhou.wu
 * @description: 测试Demo
 * @date 2022/8/9
 **/
public class SuspendTest1 {

    private static String message = null;

    private static int i = 0;

    public static void main(String[] args) {

    }

    /**
     * 测试Suspend Resume 死锁
     * <p>
     */
    public static void testSuspendAndResumeDeadLockOfSync() {
        Thread consumer = new Thread(() -> {
            while (true) {
                synchronized (SuspendTest1.class) {
                    while (message == null) {
                        System.out.println("等待接受消息");
                        Thread.currentThread().suspend();
                    }
                    System.out.println("接受消息 => " + message);
                    message = null;
                }
            }
        });
        consumer.start();

        Thread producer = new Thread(() -> {
            while (true) {
                synchronized (SuspendTest1.class) {
                    try {
                        Thread.sleep(3000);
                        message = "Hello , this is " + i++;
                        consumer.resume();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        producer.start();
    }

}
