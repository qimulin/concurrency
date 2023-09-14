package lin.xi.chun.concurrency.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * ForkJoinPool线程池的使用。要求：对 1~n 之间的整数求和
 * @author zhou.wu
 * @date 2023/9/14
 **/
@Slf4j(topic = "my.TestForkJoin")
public class TestForkJoin {
    public static void main(String[] args) {
        // 如果不设置parallelism值，那默认就等于默认CPU的核数了
        ForkJoinPool pool = new ForkJoinPool(4);
        // 采用AddTask1的形式
        System.out.println(pool.invoke(new AddTask1(5)));
        // 采用AddTask3的形式
        System.out.println(pool.invoke(new AddTask3(1, 10)));
    }
}

/**
 * AddTask1
 * @author zhou.wu
 * @date 2023/9/14
 *
 * - 任务有返回结果，则继承RecursiveTask
 * - 任务无返回结果，则可继承RecursiveAction
 **/
@Slf4j(topic = "my.AddTask1")
class AddTask1 extends RecursiveTask<Integer> {

    int n;

    public AddTask1(int n) {
        this.n = n;
    }

    @Override
    public String toString() {
        return "(" + n + ") result";
    }

    @Override
    protected Integer compute() {
        // 如果 n 已经为 1，可以求得结果了
        if (n == 1) {
            log.debug("join() {}", n);
            return n;
        }

        // 将任务进行拆分(fork)
        AddTask1 t1 = new AddTask1(n - 1);
        t1.fork();
        log.debug("fork() {} + {}", n, t1);

        // 合并(join)结果，递归获得分出下面一层的结果，到n为1的时候即无法再进行拆分的时候
        int result = n + t1.join();
        log.debug("join() {} + {} = {}", n, t1, result);
        return result;
    }
}

/**
 * AddTask3 【自解】：跟归并排序的思想好像
 * AddTask1的拆分形式有个缺点，任务相互依赖，AddTask3则可以做到并行执行
 * @author zhou.wu
 * @date 2023/9/14
 **/
@Slf4j(topic = "my.AddTask3")
class AddTask3 extends RecursiveTask<Integer> {

    int begin;
    int end;

    public AddTask3(int begin, int end) {
        this.begin = begin;
        this.end = end;
    }

    @Override
    public String toString() {
        return "(" + begin + "," + end + ") result";
    }

    @Override
    protected Integer compute() {
        // 5, 5
        if (begin == end) {
            log.debug("join() {}", begin);
            return begin;
        }
        // 4, 5
        if (end - begin == 1) {
            log.debug("join() {} + {} = {}", begin, end, end + begin);
            return end + begin;
        }

        // 1 5
        int mid = (end + begin) / 2; // 3
        AddTask3 t1 = new AddTask3(begin, mid); // 1,3
        t1.fork();
        AddTask3 t2 = new AddTask3(mid + 1, end); // 4,5
        t2.fork();
        log.debug("fork() {} + {} = ?", t1, t2);
        int result = t1.join() + t2.join();
        log.debug("join() {} + {} = {}", t1, t2, result);
        return result;
    }
}