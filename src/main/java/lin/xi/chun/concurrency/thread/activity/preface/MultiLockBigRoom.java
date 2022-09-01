package lin.xi.chun.concurrency.thread.activity.preface;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author zhou.wu
 * @description: 多把锁，睡觉行为锁卧室；学习行为锁书房
 * @date 2022/8/15
 **/
@Slf4j
public class MultiLockBigRoom {

    private final Object studyRoom = new Object();
    private final Object bedRoom = new Object();

    public void sleep() {
        synchronized (bedRoom) {
            log.debug("sleeping 2 小时");
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void study() {
        synchronized (studyRoom) {
            log.debug("study 1 小时");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
