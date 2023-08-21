package lin.xi.chun.concurrency.cp_pattern.synchronous.balking;

import lombok.extern.slf4j.Slf4j;

/**
 * 定义：Balking （回避、阻碍、犹豫）模式用在一个线程发现另一个线程或本线程已经做了某一件相同的事，那么本线程就无需再做了，直接结束返回
 * （自解：为啥叫犹豫模式？多个人都想做一件事情，都打算开始做了，结果发现彼此都想做这件事，但都做这件事浪费效率，结果就在做之前需要犹豫一下，看看是否已经有人在做这件事了）
 * @author zhou.wu
 * @description: 锁服务对象
 * @date 2022/8/20
 **/
@Slf4j
public class MonitorService {

    // 用来表示是否已经有线程已经在执行启动了
    private volatile boolean starting;

    public void start() {
        log.info("尝试启动监控线程...");
        synchronized (this) {
            if (starting) { // 有其他线程在进行，我就不进行了
                return;
            }
            starting = true;    // 设置标志
        }

        // 真正启动监控线程...
    }
}
/**
 * 后记：
 * 犹豫模式还常用于单例模式，见 {@link zhou.wu.mytest.thread.singleton.LazySingleton}
 * */
