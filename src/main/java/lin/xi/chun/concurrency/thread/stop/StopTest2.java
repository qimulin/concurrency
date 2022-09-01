package lin.xi.chun.concurrency.thread.stop;

/**
 * @author zhou.wu
 * @description: stop方法测试
 *  本例子的的写线程有一直在用同步锁保护name的值和age的值相等，所以理论上读线程无论何时去读，都是应该相等的；
 *  但是由于用了stop方法，在age和name值设置上还没统一的时候，强制解锁监视器，就会导致两值不相等（即受这些监视器保护的任何对象处于不一致状态）
 * @date 2022/8/8
 **/
public class StopTest2 {
    private static User user=new User();

    public static void main(String[] args) throws InterruptedException {
        new ReadThread().start();
        //频繁创建线程
        while (true) {
            Thread thread=new WriteThread();
            thread.start();
            Thread.sleep(150);
            thread.stop();
        }
    }
    // 频繁写线程
    public static class WriteThread extends Thread{

        @Override
        public void run() {
            while (true) {
                synchronized (user) {   // 加锁
                    Integer age= (int) ((System.currentTimeMillis()/1000));
                    user.setAge(age);
                    // 休息100毫秒，更好能让stop在这期间解锁，让name的值还不等于age
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // 把名字和年龄赋同样的值：
                    user.setName(String.valueOf(age));
                }
                Thread.yield();
            }
        }
    }

    // 频繁读线程
    public static class ReadThread extends Thread{
        @Override
        public void run() {
            while (true) {  // 一直监听user的信息
                synchronized (user) {   // 和write线程一样对同个对象加锁
                    if (user.getName()!=null && user.getAge()!=Integer.parseInt(user.getName())) {  // write线程理论上会加锁控制这两个属性值相等
                        System.out.println("信息不正确了！！！！！");
                        System.out.println(user.toString());
                    }
                }
                Thread.yield();
            }
        }

    }

    // 实体类
    public static class User{
        private String name;
        private Integer age;
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public Integer getAge() {
            return age;
        }
        public void setAge(Integer age) {
            this.age = age;
        }
        @Override
        public String toString() {
            return "User [name=" + name + ", age=" + age + "]";
        }
    }

}

/**
 * 输出结果会类似如下：
 * ……
 * 信息不正确了！！！！！
 * User [name=1659935561, age=1659935562]
 * 信息不正确了！！！！！
 * User [name=1659935561, age=1659935562]
 * 信息不正确了！！！！！
 * User [name=1659935561, age=1659935562]
 * 信息不正确了！！！！！
 * User [name=1659935561, age=1659935562]
 * 信息不正确了！！！！！
 * User [name=1659935561, age=1659935562]
 * ……
 * */
