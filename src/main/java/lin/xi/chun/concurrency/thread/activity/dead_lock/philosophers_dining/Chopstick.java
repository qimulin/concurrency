package lin.xi.chun.concurrency.thread.activity.dead_lock.philosophers_dining;

/**
 * @author zhou.wu
 * @description: 筷子
 * @date 2022/8/15
 **/
public class Chopstick {
    String name;
    public Chopstick(String name) {
        this.name = name;
    }
    @Override
    public String toString() {
        return "筷子{" + name + '}';
    }
}