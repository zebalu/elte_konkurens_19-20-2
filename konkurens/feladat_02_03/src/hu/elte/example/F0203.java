package hu.elte.example;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.stream.Collectors;

//Állítsd be a szálak prioritását. Változik-e a kimenet?

public class F0203 {

    private static final String[] DEFAULT_STRINGS = ("alma korte banan dio mogyoro jacint ibolya nefelejts kutya macska " +
            "pek postas rendor villanyszerelo kemenysepro").split(" ");

    private static final AtomicLong ALONG = new AtomicLong(0L);

    private static Consumer<String> println = System.out::println;

    public static void main(String[] args) throws IOException {
        if (args == null || args.length == 0) {
            startAndWaitAll(DEFAULT_STRINGS);
        } else {
            startAndWaitAll(args);
        }
    }

    private static void startAndWaitAll(String[] strings) {
        List<Thread> threads = Arrays.stream(strings).map(F0203::startAndWaitAll).collect(Collectors.toList());
        threads.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private static void print(String text) {
        for (int i = 0; i < 100_000; ++i) {
            F0203.println.accept(String.format("%2d \t %15s \t %05d", Thread.currentThread().getPriority(), text, i));
        }
    }

    private static Thread startAndWaitAll(String text) {
        Thread t = new Thread(() -> print(text));
        t.setPriority((int)(ALONG.incrementAndGet() % Thread.MAX_PRIORITY + Thread.MIN_PRIORITY));
        t.start();
        return t;
    }
}
