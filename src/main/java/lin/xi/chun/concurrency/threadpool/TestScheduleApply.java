package lin.xi.chun.concurrency.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.*;

/**
* 测试定时调度的应用：如何让每周四 18:00:00 定时执行任务？
* @author zhou.wu
* @date 2023/9/13
*/
@Slf4j
public class TestScheduleApply {

    public static void main(String[] args) throws Exception{
        /*
        * LocalDateTime是jdk8新增的日期操作类，不仅是线程安全的，而且还比较方便做时间运算。比date更好
        * */
        // 获得当前时间
        LocalDateTime now = LocalDateTime.now();
        // 获取本周四 18:00:00.000 修改now的一些时间；.with(DayOfWeek.THURSDAY)只能获取本周周四的时间
        LocalDateTime thursday =
                now.with(DayOfWeek.THURSDAY).withHour(18).withMinute(0).withSecond(0).withNano(0);
        // 如果当前时间已经超过 本周四 18:00:00.000， 那么找下周四 18:00:00.000
        if(now.compareTo(thursday) >= 0) {
            // 加上一周的时间
            thursday = thursday.plusWeeks(1);
        }
        // 计算时间差，即延时执行时间
        long initialDelay = Duration.between(now, thursday).toMillis();
        // 计算间隔时间，即 1 周的毫秒值
        long oneWeek = 7 * 24 * 3600 * 1000;

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
        System.out.println("开始时间：" + new Date());
        executor.scheduleAtFixedRate(() -> {
            System.out.println("执行时间：" + new Date());
        }, initialDelay, oneWeek, TimeUnit.MILLISECONDS);
    }

}
