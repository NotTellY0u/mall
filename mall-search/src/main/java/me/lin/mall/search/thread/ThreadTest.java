package me.lin.mall.search.thread;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author Fibonacci
 * @Date 2021/3/17 10:41 上午
 * @Version 1.0
 */
public class ThreadTest {

    public static ExecutorService executorService = Executors.newFixedThreadPool(10);

    public static void main(String[] args) {
        System.out.println("start");
        Thread01 thread01=  new Thread01();
        CompletableFuture.runAsync(thread01,executorService);
        System.out.println("end");
        String a = "10.00";
        BigDecimal decimal = new BigDecimal(a);
        System.out.println(decimal);
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
