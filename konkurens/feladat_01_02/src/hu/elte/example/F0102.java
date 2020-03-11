package hu.elte.example;

//Futtass három szálat, a harmadik other 1 stb. szövegeket írjon ki.

public class F0102 {

    public static void main(String[] args) {
        start("hello");
        start("world");
        start("other");
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
