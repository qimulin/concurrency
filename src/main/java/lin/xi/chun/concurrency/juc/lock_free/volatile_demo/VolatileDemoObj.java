package lin.xi.chun.concurrency.juc.lock_free.volatile_demo;

import lombok.extern.slf4j.Slf4j;
import sun.misc.Unsafe;

import java.util.concurrent.TimeUnit;

/**
 * @author zhou.wu
 * @description volatile demo 测试对象
 * @date 2023/7/27
 **/
@Slf4j
public class VolatileDemoObj {

    private volatile String name;

    public String getName(String param){
        if(this.name!=null){
            log.info("name not null, name:{}", name);
            return name;
        }else{
            log.info("name is null");
        }
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        synchronized (this){
//            if(null==this.name){
//                this.name = buildName(param);
//                log.info("CAS modify name: {}", this.name);
//            }
//        }
        this.name = buildName(param);
        return this.name;
    }

    private String buildName(String param){
        log.info("build name: {}", param);
        return "your name is "+param;
    }
}
