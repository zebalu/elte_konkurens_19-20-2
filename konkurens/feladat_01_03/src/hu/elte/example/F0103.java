package hu.elte.example;

//Egy tömb szövegeket tartalmaz, mindegyikhez készíts és futtass a fentiekhez hasonló szálakat.

import java.util.Arrays;

public class F0103 {

    private static final String[] DEFAULT_STRINGS = ("alma korte banan dio mogyoro jacint ibolya nefelejts kutya macska " +
            "pek postas rendor villanyszerelo kemenysepro").split(" ");

    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            start(DEFAULT_STRINGS);
        } else {
            start(args);
        }
    }

    private static void start(String[] strings) {
        Arrays.stream(strings).forEach(F0103::start);
    }

    private static void print(String text) {
        for (int i = 0; i < 100_000; ++i) {
            System.out.println(String.format("%15s \t %05d", text, i));
        }
    }

    private static void start(String text) {
        new Thread(() -> print(text)).start();
    }

}
