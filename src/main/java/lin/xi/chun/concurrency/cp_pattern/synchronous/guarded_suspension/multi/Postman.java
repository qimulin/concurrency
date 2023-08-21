package lin.xi.chun.concurrency.cp_pattern.synchronous.guarded_suspension.multi;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhou.wu
 * @description: 邮递员负责送信
 * @date 2022/8/13
 **/
@Slf4j
public class Postman extends Thread {
    private int id;
    private String mail;
    public Postman(int id, String mail) {
        this.id = id;
        this.mail = mail;
    }
    @Override
    public void run() {
        GuardedObjectSupportMulti guardedObject = Mailboxes.getGuardedObject(id);
        log.debug("送信 id:{}, 内容:{}", id, mail);
        guardedObject.complete(mail);
    }
}
