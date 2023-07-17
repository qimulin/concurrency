package lin.xi.chun.concurrency.juc.unsafe;

import lin.xi.chun.concurrency.juc.lock_free.Account;

/**
 * @author zhou.wu
 * @description unsafe示例2
 * @date 2023/7/17
 **/
public class UnsafeDemo2 {
    public static void main(String[] args) {
        Account.demo(new Account() {
            AtomicData atomicData = new AtomicData(10000);
            @Override
            public Integer getBalance() {
                return atomicData.getData();
            }
            @Override
            public void withdraw(Integer amount) {
                atomicData.decrease(amount);
            }
        });
    }
}
