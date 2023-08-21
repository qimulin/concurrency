package lin.xi.chun.concurrency.cp_pattern.asynchronous.producer_consumer;

/**
 * @author zhou.wu
 * @description: 消息
 * @date 2022/8/13
 **/
public final class Message {
    /** 必须要有id属性，好标识 */
    private int id;
    private Object message;
    public Message(int id, Object message) {
        this.id = id;
        this.message = message;
    }
    public int getId() {
        return id;
    }

    /** 只有get方法，构造函数设置后，不可被修改 */
    public Object getMessage() {
        return message;
    }
}
