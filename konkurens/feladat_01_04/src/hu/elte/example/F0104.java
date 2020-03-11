package hu.elte.example;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/*
Írd fájlba a kimenetet (PrintWriter).
Ügyelj arra, hogy a fájlt zárd is be a kiírások végén.
 */

public class F0104 {

    private static final String[] DEFAULT_STRINGS = ("alma korte banan dio mogyoro jacint ibolya nefelejts kutya macska " +
            "pek postas rendor villanyszerelo kemenysepro").split(" ");

    private static Consumer<String> println = System.out::println;

    public static void main(String[] args) throws IOException {
        try (PrintWriter pw = new PrintWriter(new File("output.txt"))) {
            println = pw::println;
            if (args == null || args.length == 0) {
                startAndWaitAll(DEFAULT_STRINGS);
            } else {
                startAndWaitAll(args);
            }
            pw.flush();
        }
    }

    private static void startAndWaitAll(String[] strings) {
        List<Thread> threads = Arrays.stream(strings).map(F0104::startAndWaitAll).collect(Collectors.toList());
        threads.forEach(t ->{
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private static void print(String text) {
        for (int i = 0; i < 100_000; ++i) {
            F0104.println.accept(String.format("%15s \t %05d", text, i));
        }
    }

    private static Thread startAndWaitAll(String text) {
        Thread t = new Thread(() -> print(text));
        t.start();
        return t;
    }
}
