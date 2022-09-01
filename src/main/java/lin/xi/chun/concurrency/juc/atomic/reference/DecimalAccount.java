package lin.xi.chun.concurrency.juc.atomic.reference;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 接口
 * @author zhou.wu
 * @description: 金额类型账户 参考例子 {@link lin.xi.chun.concurrency.juc.lock_free.Account}
 * 用到原子引用的实现类是 {@link DecimalAccountSafeCas}
 * @date 2022/9/1
 **/
public interface DecimalAccount {

    // 获取余额
    BigDecimal getBalance();

    // 取款
    void withdraw(BigDecimal amount);

    /**
     * 方法内会启动 1000 个线程，每个线程做 -10 元 的操作
     * 如果初始余额为 10000 那么正确的结果应当是 0
     */
    static void demo(DecimalAccount account) {
        List<Thread> ts = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            ts.add(new Thread(() -> {
                account.withdraw(BigDecimal.TEN);
            }));
        }
        ts.forEach(Thread::start);
        ts.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        System.out.println(account.getBalance());
    }
}
