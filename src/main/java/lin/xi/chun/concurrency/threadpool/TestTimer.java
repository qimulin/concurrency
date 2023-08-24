package lin.xi.chun.concurrency.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.util.Timer;
import java.util.TimerTask;


/**
 * @author zhou.wu
 * @description Timer测试，借Timer的缺点引入Schedule功能的线程池
 * @date 2023/8/24
 **/
@Slf4j
public class TestTimer {

    public static void main(String[] args) throws Exception{
        // 创建Timer对象
        Timer timer = new Timer();

        // 配合Timer使用的任务，只能是TimerTask类型
        // 任务1
        TimerTask task1 = new TimerTask() {
            @Override
            public void run() {
                log.debug("task 1");
                // 情况1-任务1执行的时间比较久
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                // 情况1-任务1执行出异常
                int i = 1/0;
            }
        };

        // 任务2
        TimerTask task2 = new TimerTask() {
            @Override
            public void run() {
                log.debug("task 2");
            }
        };

        // 使用 timer 添加两个任务，希望它们都在 1s 后执行
        // 但由于 timer 内只有一个线程来顺序执行队列中的任务，因此『任务1』的延时，影响了『任务2』的执行
        // 当第一个任务出异常，第二个任务便没被执行
        timer.schedule(task1, 1000);
        timer.schedule(task2, 1000);
    }
}
