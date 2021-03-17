package me.lin.mall.search.thread;

/**
 * @Author Fibonacci
 * @Date 2021/3/17 10:41 上午
 * @Version 1.0
 */
public class ThreadTest {
    public static void main(String[] args) {
        Thread01 thread01=  new Thread01();
        thread01.start();
    }

    public static class Thread01 extends Thread{
        @Override
        public void run(){
            System.out.println("当前线程："+Thread.currentThread().getId());
            int i = 10/2;
            System.out.println("运行结果："+i);
        }
    }
}
