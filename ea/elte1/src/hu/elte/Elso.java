package hu.elte;

import java.util.ArrayList;
import java.util.List;

public class Elso {
    public static void main(String[] args) throws InterruptedException {
        int[] a = {0};
        int max = 100; //_000;
        Thread th1 = new Thread(()->{
            System.out.println("My name is: "+ Thread.currentThread().getName());
            for(int i=0; i<max; ++i) {
                synchronized (a) {
                    try {
                        a.wait(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    int temp = a[0];
                    System.out.println(Thread.currentThread().getName()+" "+temp);
                    temp = temp + 1;
                    System.out.println(Thread.currentThread().getName()+" "+temp);
                    a[0] = temp;
                    a.notify();
                }
            }
        }, "Th1");
        Thread th2 = new Thread(()->{
            System.out.println("My name is: "+ Thread.currentThread().getName());
            for(int i=0; i<max; ++i){
                synchronized (a) {
                    int temp = a[0];
                    System.out.println(Thread.currentThread().getName()+" "+temp);
                    temp = temp - 1;
                    System.out.println(Thread.currentThread().getName()+" "+temp);
                    a[0] = temp;
                    a.notify();
                    try {
                        a.wait(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, "Th2");
        th1.start();
        //Thread.sleep(1_000);
        th2.start();
        th1.join();
        th2.join();
        System.out.println("finaly: "+a[0]);
    }
}
