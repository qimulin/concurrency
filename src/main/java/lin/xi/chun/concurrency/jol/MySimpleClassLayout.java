package lin.xi.chun.concurrency.jol;

import org.openjdk.jol.info.ClassLayout;

/**
 * @author zhou.wu
 * @description: 自定义简单的输出
 * @date 2022/8/8
 **/
public class MySimpleClassLayout {

    public static String printMarkDown(Object instance){
        String[] split = ClassLayout.parseInstance(instance).toPrintable().split("\\r\\n");
        String s = split[2];
        return s.substring(40);
    }
}
