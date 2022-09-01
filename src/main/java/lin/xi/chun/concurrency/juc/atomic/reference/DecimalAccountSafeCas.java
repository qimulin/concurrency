package lin.xi.chun.concurrency.juc.atomic.reference;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 原子引用示例
 * @author zhou.wu
 * @description: 配合无锁CAS的安全实现
 * @date 2022/9/1
 **/
public class DecimalAccountSafeCas implements DecimalAccount {

    AtomicReference<BigDecimal> ref;

    public DecimalAccountSafeCas(BigDecimal balance) {
        ref = new AtomicReference<>(balance);
    }

    @Override
    public BigDecimal getBalance() {
        return ref.get();
    }

    @Override
    public void withdraw(BigDecimal amount) {
        while (true) {
            BigDecimal prev = ref.get();
            BigDecimal next = prev.subtract(amount);
            // CAS操作
            if (ref.compareAndSet(prev, next)) {
                // 操作成功，退出循环
                break;
            }
        }
    }
}
