package lin.xi.chun.concurrency.juc.immutability;

import java.sql.Connection;
import java.util.Random;

/**
 * @author zhou.wu
 * @description 享元Demo测试类
 * @date 2023/7/20
 **/
public class FlyweightDemoTest {
    public static void main(String[] args) {
        // 设置的连接池大小小于测试线程数
        Pool pool = new Pool(2);
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                Connection conn = pool.borrow();
                try {
                    // 模拟使用耗时（1s之内随机随便）
                    Thread.sleep(new Random().nextInt(1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    pool.free(conn);
                }
            }).start();
        }
    }
}
