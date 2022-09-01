package lin.xi.chun.concurrency.thread.activity.preface;

/**
 * @author zhou.wu
 * @description: 测试
 * @date 2022/8/15
 **/
public class BigRoomTest {
    public static void main(String[] args) {
//        BigRoom bigRoom = new BigRoom();  // 本不相干的动作，因为同一把锁，不能并发执行了
        MultiLockBigRoom bigRoom = new MultiLockBigRoom();  // 可以并发执行
        new Thread(() -> {
            bigRoom.study();
        },"小南").start();
        new Thread(() -> {
            bigRoom.sleep();
        },"小女").start();
    }
}
