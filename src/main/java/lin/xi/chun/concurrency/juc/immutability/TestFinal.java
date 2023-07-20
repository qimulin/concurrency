package lin.xi.chun.concurrency.juc.immutability;

/**
 * @author zhou.wu
 * @description 测试Final
 * @date 2023/7/20
 **/
public class TestFinal {

    /** 静态final成员变量 */
    final static int A = 10;
    final static int B = Short.MAX_VALUE+1;

    /** 普通final成员变量 */
    final int a = 20;
    final int b = Integer.MAX_VALUE;

    final void test1() {
    }
}

class UseFinal1 {
    public void test() {
        System.out.println(TestFinal.A);
        System.out.println(TestFinal.B);
        System.out.println(new TestFinal().a);
        System.out.println(new TestFinal().b);
        new TestFinal().test1();
    }
}

class UseFinal2 {
  public void test(){
      System.out.println(TestFinal.A);
  }
}