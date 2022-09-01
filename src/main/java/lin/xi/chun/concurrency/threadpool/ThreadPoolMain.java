package lin.xi.chun.concurrency.threadpool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by lin.xc on 2019/8/14
 */
public class ThreadPoolMain {
    public static void main(String[] args) {
        ExecutorService pool = new ThreadPoolExecutor(
                2, 2, 0L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1)
        );
        CommonDeal a = new ADeal();
        CommonDeal b = new BDeal();
        List<Callable<String>> tasks = new ArrayList<>();
        tasks.add(() ->a.doDeal(1));
        tasks.add(() ->b.doDeal(2));
        try {
            List<Future<String>> futures = pool.invokeAll(tasks);
//            for(Future<String> f: futures){
//                f.get()
//            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
