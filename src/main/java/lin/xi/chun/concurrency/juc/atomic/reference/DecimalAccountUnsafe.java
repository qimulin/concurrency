package lin.xi.chun.concurrency.juc.atomic.reference;

import java.math.BigDecimal;

/**
 * @author zhou.wu
 * @description: 不安全实现
 * @date 2022/9/1
 **/
public class DecimalAccountUnsafe implements DecimalAccount {

    BigDecimal balance;

    public DecimalAccountUnsafe(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public BigDecimal getBalance() {
        return balance;
    }

    @Override
    public void withdraw(BigDecimal amount) {
        BigDecimal balance = this.getBalance();
        this.balance = balance.subtract(amount);
    }

}
