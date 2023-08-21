package lin.xi.chun.concurrency.cp_pattern.termination.two_phase_termination;

import lombok.extern.slf4j.Slf4j;

/**
 * 停止标记用 volatile 是为了保证该变量在多个线程之间的可见性
 * 我们的例子中，即主线程把它修改为 true 对 t1 线程可见
 * @author zhou.wu
 * @description: 利用停止标记
 * @date 2022/8/20
 **/
@Slf4j
public class TPTVolatile {

    private Thread thread;
    private volatile boolean stop = false;

    public void start(){
        thread = new Thread(() -> {
            while(true) {
                Thread current = Thread.currentThread();
                if(stop) {
                    log.debug("料理后事");
                    break;
                }
                try {
                    Thread.sleep(1000);
                    log.debug("将结果保存");
                } catch (InterruptedException e) {
                }
                // 执行监控操作
            }
        },"监控线程");
        thread.start();
    }

    public void stop() {
        stop = true;
        thread.interrupt();
    }
}
