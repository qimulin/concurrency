package lin.xi.chun.concurrency.juc.unsafe;

import lombok.Data;

/**
 * @author zhou.wu
 * @description 学生对象
 * @date 2023/7/17
 **/
@Data
public class Student {
    volatile int id;
    volatile String name;
}
