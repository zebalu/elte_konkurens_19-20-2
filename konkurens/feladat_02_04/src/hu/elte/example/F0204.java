package hu.elte.example;

// Készítsd el a szálakat több különböző módon: Runnable típusú lambdából készített Thread példányként, Thread leszármazott osztály példányaként, Runnable interfészt implementáló osztály példányaként.

public class F0204 {
    public static void main(String[] args) {
        Thread runnablebol = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Ez a thread egy nevtelen runnable-t kap parameterul");
            }
        });
        Thread lambdabol = new Thread(()->{
            System.out.println("Ez a thread egy lambdat kapott parameterul");
        });
        Thread referenciabol = new Thread(F0204::referalhatoMetodus);
        Thread runnablePeldannyal = new Thread(new MyRunnable());
        Thread threadLeszarmazott = new MyThread();

        runnablebol.start();
        lambdabol.start();
        referenciabol.start();
        runnablePeldannyal.start();
        threadLeszarmazott.start();
    }

    private static void referalhatoMetodus() {
        System.out.println("Ez a thread egy megfelelo method referenciaval jott letre");
    }

    private static class MyRunnable implements Runnable {
        @Override
        public void run() {
            System.out.println("Ez a thread egy runnable peldanyt kap");
        }
    }

    private static class MyThread extends Thread {
        @Override
        public void run() {
            System.out.println("Ez egy thread leszarmazott");
        }
    }
}
