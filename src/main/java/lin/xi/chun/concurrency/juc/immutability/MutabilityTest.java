package lin.xi.chun.concurrency.juc.immutability;

import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;

/**
 * @author zhou.wu
 * @description 可变类测试
 * @date 2023/7/17
 **/
@Slf4j
public class MutabilityTest {
    public static void main(String[] args) {
        // 本例用SimpleDateFormat来举例
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // 起10个线程，都用sdf进行日期的转换
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    log.debug("{}", sdf.parse("1951-04-21"));
                } catch (Exception e) {
                    log.error("parse error", e);
                }
            }).start();
        }
    }
}
/**
 * 后记：上述运行后，有很大几率出现 java.lang.NumberFormatException 或者出现不正确的日期解析结果
 * 解决思路1：可用同步锁，例如给下面这一段代码外面包上synchronized(sdf)
 * try {
 *  log.debug("{}", sdf.parse("1951-04-21"));
 * } catch (Exception e) {
 *  log.error("{}", e);
 * }
 * 这样虽能解决问题，但带来的是性能上的损失，并不算很好.
 * 那换一个更好的实现：{@link lin.xi.chun.concurrency.juc.immutability.ImmutabilityTest}
 * */
