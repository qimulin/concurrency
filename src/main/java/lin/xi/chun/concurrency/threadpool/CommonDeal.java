package lin.xi.chun.concurrency.threadpool;

/**
 * Created by lin.xc on 2019/8/14
 */
public abstract class CommonDeal {
    public String doDeal(int i){
        i=add(i);
        System.out.println("i="+i);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int j = i*10;
        System.out.println("j="+j);
        return "j="+j;
    }

    abstract Integer add(int i);
}
