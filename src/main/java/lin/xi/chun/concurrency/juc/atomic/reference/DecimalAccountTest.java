package lin.xi.chun.concurrency.juc.atomic.reference;

import java.math.BigDecimal;

/**
 * @author zhou.wu
 * @description: 测试类
 * @date 2022/9/1
 **/
public class DecimalAccountTest {
    public static void main(String[] args) {
        DecimalAccount.demo(new DecimalAccountUnsafe(new BigDecimal("10000")));
        DecimalAccount.demo(new DecimalAccountSafeLock(new BigDecimal("10000")));
        DecimalAccount.demo(new DecimalAccountSafeCas(new BigDecimal("10000")));
    }
}
