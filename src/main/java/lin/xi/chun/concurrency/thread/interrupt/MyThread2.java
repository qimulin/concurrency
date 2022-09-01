package lin.xi.chun.concurrency.thread.interrupt;

import lombok.extern.slf4j.Slf4j;

/**
 * @author lin.xc
 * @date 2021/6/2
 **/
@Slf4j
public class MyThread2 extends Thread {

    @Override
    public void run() {
        super.run();
        try {
            synchronized (this){
                for (int i = 0; i < 50000; i++) {
                    if (i==9999) {
                        System.out.println("线程内wait");
                        wait(2000);
                    }
//                    if(i==15000){
//                        System.out.println("线程内interrupt");
//                        interrupt();
//                    }
                    log.info("test");
                    System.out.println("i=" + (i + 1));
                }
            }
        } catch (InterruptedException e) {
            System.out.println("线程内InterruptedException");
            e.printStackTrace();
        }
        System.out.println("this line cannot be executed. cause thread throws exception"); //这行语句不会被执行!!!
    }
}
