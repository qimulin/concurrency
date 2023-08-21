package lin.xi.chun.concurrency.cp_pattern.synchronous.guarded_suspension.multi;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

/**
 * @author zhou.wu
 * @description: 邮箱
 * @date 2022/8/13
 **/
public class Mailboxes {

    private static Map<Integer, GuardedObjectSupportMulti> boxes = new Hashtable<>();
    private static int id = 1;

    // 产生唯一 id
    private static synchronized int generateId() {  // synchronized修饰静态方法，相当于对.class对象加了锁
        return id++;
    }
    public static GuardedObjectSupportMulti getGuardedObject(int id) {
        return boxes.remove(id);
    }
    public static GuardedObjectSupportMulti createGuardedObject() {
        GuardedObjectSupportMulti go = new GuardedObjectSupportMulti(generateId());
        boxes.put(go.getId(), go);
        return go;
    }
    public static Set<Integer> getIds() {
        return boxes.keySet();
    }
}
