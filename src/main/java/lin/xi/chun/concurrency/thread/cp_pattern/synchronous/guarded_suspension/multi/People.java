package lin.xi.chun.concurrency.thread.cp_pattern.synchronous.guarded_suspension.multi;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhou.wu
 * @description: 居民负责收信
 * @date 2022/8/13
 **/
@Slf4j
public class People extends Thread{
    @Override
    public void run() {
        // 收信
        GuardedObjectSupportMulti guardedObject = Mailboxes.createGuardedObject();
        log.debug("开始收信 id:{}", guardedObject.getId());
        Object mail = guardedObject.get(5000);
        log.debug("收到信 id:{}, 内容:{}", guardedObject.getId(), mail);
    }
}
