package hu.elte.example;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/*
Ellenőrizd, hogy egy feladat végrehajtása több szálon jelentős gyorsulást okoz(hat).
Add össze az 1..1_000_000_000 intervallum számait egy szálon.
Mérd ki a System.nanoTime() felhasználásával, ez mennyi időbe telik.
Ezután 10 szálon add össze az 1..100_000_000, 100_000_001..200_000_000 stb. intervallumok számait, és az adódó összegek értékeit add össze egy static változóba.
Mérd ki a System.nanoTime() felhasználásával, ez mennyi időbe telik.
Írd ki a program végén a gyorsulás mértékét.
 */

public class F0205 {

    private static volatile long lotThreadSum = 0L;

    public static void main(String[] args) {
        long oneThreadSum = 0L;
        Instant start = Instant.now();
        for (int i = 0; i < 1_000_000_000; ++i) {
            oneThreadSum += i;
        }
        Instant end = Instant.now();
        Duration oneThreadDuration = Duration.between(start, end);
        System.out.println("osszeadas 1 szalon, ennyi ideig tart: " + oneThreadDuration.toMillis() + " ms");
        List<Thread> threads = getSummingThreads();
        Instant start2 = Instant.now();
        threads.forEach(Thread::start);
        threads.forEach(F0205::waitThread);
        Instant end2 = Instant.now();
        Duration multiThreadDuration = Duration.between(start2, end2);
        System.out.println("osszeadas 10 szalon, ennyi ideig tart: " + multiThreadDuration.toMillis() + " ms");
        System.out.println(Runtime.getRuntime().availableProcessors() + " db (virtualis)processor magon a gyorsulas merteke: " +
                ((double) oneThreadDuration.toMillis() / (double) multiThreadDuration.toMillis()));
    }

    private static List<Thread> getSummingThreads() {
        List<Thread> threads = new ArrayList<>(10);
        int min = 0;
        int max = 100_000_000;
        for (int t = 0; t < 10; ++t) {
            final int localMin = min;
            final int localMax = max;
            threads.add(new Thread(() -> {
                long sum = 0L;
                for (int i = localMin; i < localMax; ++i) {
                    sum += i;
                }
                synchronized (F0205.class) {
                    lotThreadSum += sum;
                }
            }));
            min = max;
            max += 100_000_000;
        }
        return threads;
    }

    private static void waitThread(Thread t) {
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
