package hu.elte.example;

/*
Készíts két szálat és futtasd őket. A szálak írják ki a hello 1, hello 2, …, hello 100000 és world 1, …, world 100000 szövegeket.
Figyeld meg, hogy a kimenetek összefésülődnek.
 */

public class F0101 {
    public static void main(String[] args) {
        start("hello");
        start("world");
    }

    private static void print(String text) {
        for(int i=0; i<100_000; ++i) {
            System.out.println(String.format("%s %05d", text, i));
        }
    }
    private static void start(String text) {
        new Thread(()->print(text)).start();
    }
}
