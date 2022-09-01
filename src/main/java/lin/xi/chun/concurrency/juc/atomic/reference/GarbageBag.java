package lin.xi.chun.concurrency.juc.atomic.reference;

/**
 * @author zhou.wu
 * @description: 垃圾袋
 * @date 2022/9/1
 **/
public class GarbageBag {
    String desc;

    public GarbageBag(String desc) {
        this.desc = desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return super.toString() + " " + desc;
    }
}
