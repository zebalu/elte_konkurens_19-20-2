package hu.elte.example;

/*IDE segítségével szüneteltesd a szálak futását (suspend).
Vizsgáld meg a változóik tartalmát. Írd át a változók értékét, és folytasd a futtatást.
Nevezd el a szálakat (setName).
Csoportosítsd a szálakat (ThreadGroup).*/

public class F0202 {

    public static void main(String[] args) {
        ThreadGroup group1 = new ThreadGroup("egyik group");
        ThreadGroup group2 = new ThreadGroup(group1, "masik group");

        //nevtelen thread 1:
        new Thread(F0202::threadMessager).start();
        //nectelen thread 2:
        new Thread(F0202::threadMessager).start();

        new Thread(F0202::threadMessager, "Kvazi grouptalan szal").start();

        new Thread(group1, F0202::threadMessager, "Elso group thread-je").start();
        new Thread(group1, F0202::threadMessager, "Elso group thread-je 2").start();

        new Thread(group2, F0202::threadMessager, "Masodik group thread-je").start();
        new Thread(group2, F0202::threadMessager, "Masodik group thread-je 2").start();

        new Thread(F0202::emptyLine).start();
    }

    private static void threadMessager() {
        printEverySecond("Ez a szal: " + Thread.currentThread().getName() + " ebben a groupban van: " + Thread.currentThread().getThreadGroup().getName() + " aminek ez a parentje: " + Thread.currentThread().getThreadGroup().getParent());
    }

    private static void emptyLine() {
        printEverySecond("");
    }

    private static void printEverySecond(String message) {
        try {
            for (int i = 1; i < 100; ++i) {
                System.out.println(message);
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
