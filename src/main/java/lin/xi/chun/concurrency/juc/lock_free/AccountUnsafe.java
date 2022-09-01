package lin.xi.chun.concurrency.juc.lock_free;

/**
 * @author zhou.wu
 * @description: 账户接口线程不安全的实现
 * 当然用synchronized肯定也能保证线程安全性，不过本节主要是为了用无锁的解决方案{@link AccountSafe}
 * @date 2022/8/29
 **/
public class AccountUnsafe implements Account {

    private Integer balance;    // 共享资源

    /** 构造传入余额 */
    public AccountUnsafe(Integer balance) {
        this.balance = balance;
    }

    @Override
    public Integer getBalance() {
        return balance;
    }

    @Override
    public void withdraw(Integer amount) {
        balance -= amount;
    }
}
