package lin.xi.chun.concurrency.juc.atomic.reference;

import java.math.BigDecimal;

/**
 * @author zhou.wu
 * @description: 配合锁的安全实现
 * @date 2022/9/1
 **/
public class DecimalAccountSafeLock implements DecimalAccount {

    private final Object lock = new Object();
    BigDecimal balance;

    public DecimalAccountSafeLock(BigDecimal balance) {
        this.balance = balance;
    }
    @Override
    public BigDecimal getBalance() {
        return balance;
    }

    @Override
    public void withdraw(BigDecimal amount) {
        synchronized (lock) {
            BigDecimal balance = this.getBalance();
            this.balance = balance.subtract(amount);
        }
    }

}
