package lin.xi.chun.concurrency.thread.interrupt;

/**
 * @author lin.xc
 * @date 2021/6/2
 **/
public class MyThread1 extends Thread {

    @Override
    public void run() {
        super.run();
        try{
            for (int i = 0; i < 500000; i++) {
                if (this.interrupted()) {
                    System.out.println("should be stopped and exit");
                    throw new InterruptedException();
                }
                System.out.println("i=" + (i + 1));
            }
            System.out.println("this line cannot be executed. cause thread throws exception"); //这行语句不会被执行!!!
        }catch(InterruptedException e){
            System.out.println("catch interrupted exception");
            e.printStackTrace();
        }
    }
}
