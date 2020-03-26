package hu.elte;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyLocks {

    private static boolean hasElement = false;
    private static boolean finished = false;
    private static Lock lock = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {
        List<Integer> list = new ArrayList<>();
        int max = 10;
        for (int j = 0; j < max; ++j) {
            Thread th1 = new Thread(() -> {
                System.out.println("My name is: " + Thread.currentThread().getName());
                for (int i = 0; i < max; ++i) {
                    lock.lock();
                    try {
                        Thread.sleep(20);
                        list.add(i * i);
                        hasElement = true;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    lock.unlock();
                }
                finished = true;
            }, "Th"+j);
            th1.start();
        }
        Thread th2 = new Thread(() -> {
            System.out.println("My name is: " + Thread.currentThread().getName());
            while (!finished) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (hasElement) {
                    lock.lock();
                    try {
                        System.out.println(list.remove(0));
                        hasElement = !list.isEmpty();
                    } finally {
                        lock.unlock();
                    }
                }
            }
        }, "Th2");
        //th1.start();
        th2.start();
        //th1.join();
        th2.join();
        System.out.println("finaly: " + list.size());
    }

}
