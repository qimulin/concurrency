package lin.xi.chun.concurrency.juc.lock_free;

/**
 * @author zhou.wu
 * @description: 测试类
 * @date 2022/8/29
 **/
public class AccountTest {
    public static void main(String[] args) {
//        Account.demo(new AccountUnsafe(10000));
        Account.demo(new AccountSafe(10000));
    }
}
