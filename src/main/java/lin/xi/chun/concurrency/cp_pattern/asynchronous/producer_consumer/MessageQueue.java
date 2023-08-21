package lin.xi.chun.concurrency.cp_pattern.asynchronous.producer_consumer;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;

/**
 * @author zhou.wu
 * @description: 线程之间通信的消息队列（消息不会被立刻消费）
 * 要点
 * - 与前面的保护性暂停中的 GuardObject 不同，不需要产生结果和消费结果的线程一一对应
 * - 消费队列可以用来平衡生产和消费的线程资源
 * - 生产者仅负责产生结果数据，不关心数据该如何处理，而消费者专心处理结果数据
 * - 消息队列是有容量限制的，满时不会再加入数据，空时不会再消耗数据
 * - JDK 中各种阻塞队列，采用的就是这种模式
 * @date 2022/8/13
 **/
@Slf4j
public class MessageQueue {

    /** 集合：作为消息的存储的容器，数据解构采用双向队列，一头存；一头取 */
    private LinkedList<Message> queue;
    /** 容量限制 */
    private int capacity;

    public MessageQueue(int capacity) {
        this.capacity = capacity;
        queue = new LinkedList<>();
    }

    /**
     * 获取消息方法
     * */
    public Message take() {
        synchronized (queue) {  // 共享的是queue
            while (queue.isEmpty()) {
                log.debug("队列为空, 消费者等待");
                try {
                    queue.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 返回排在头部的消息
            Message message = queue.removeFirst();
            // 获取消息以后，通知生产者线程，可能有空位了
            queue.notifyAll();
            return message;
        }
    }

    /**
     * 存入消息的方法
     * */
    public void put(Message message) {
        synchronized (queue) {
            // 暂时没有空的位置被放入，待有空位再往里放
            while (queue.size() == capacity) {
                log.debug("队列已满, 生产者等待");
                try {
                    queue.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 空出空位，从队列尾部放入消息
            queue.addLast(message);
            // 不是空队列了，唤醒消费线程可以消费了
            queue.notifyAll();
        }
    }
}
/**
 * 后记：
 * 自解：利用消息队列这个中间属性解耦了【结果等待者】和【结果生产者】之间的同步关系，生产者和消费者之间不需要去控制同步，
 * 但是生产者和消息队列、消息队列和消费者可以各自维护同步关系
 * */
