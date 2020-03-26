package hu.elte;

import java.util.ArrayList;
import java.util.List;

public class Ea2 {
    private static boolean hasElement = false;
    private static boolean finished = false;
    public static void main(String[] args) throws InterruptedException {
        List<Integer> list = new ArrayList<>();
        int max = 30;
        for(int j=0; j<max; ++j) {
            Thread th1 = new Thread(() -> {
                System.out.println("My name is: " + Thread.currentThread().getName());
                for (int i = 0; i < max; ++i) {
                    synchronized (list) {
                        try {
                            Thread.sleep(20);
                            if(list.size()>=10) {
                                list.wait();
                            }
                            list.add(i * i);
                            hasElement = true;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                finished = true;
            }, "Th1");
            th1.start();
        }
        Thread th2 = new Thread(()->{
            System.out.println("My name is: "+ Thread.currentThread().getName());
            while(!finished) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(hasElement) {
                    synchronized (list) {
                        System.out.println(list.remove(0));
                        hasElement = !list.isEmpty();
                        if (list.size() < 10) {
                            list.notify();
                        }
                    }
                }
            }
        }, "Th2");
        //th1.start();
        th2.start();
        //th1.join();
        th2.join();
        System.out.println("finaly: "+list.size());
    }
}
