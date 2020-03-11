package hu.elte.example;

/*
 * Egy tetszőleges korábbi feladat megoldását bővítsd egy olyan szállal, amelyik másodpercenként kiírja, hány szál aktív még (Thread.currentThread().getThreadGroup().activeCount()). Ha már csak ez a szál maradt meg, lépjen ki.
A másodpercenkénti működést először a Thread.sleep művelet használatával oldd meg.
 */

public class F0401 {

	private static final String[] WORDS = "alma korte banan barack szilva dio mogyoro cseresznye meggy".split(" ");

	public static void main(String[] args) {
		ThreadGroup myGroup = new ThreadGroup("le Group");
		for (String word : WORDS) {
			new Thread(myGroup, () -> {
				for (int i = 0; i < 100_000; ++i) {
					System.out.println(String.format("%05d \t %15s", i, word));
				}
			}, word).start();
		}
		new Thread(myGroup, () -> {
			try {
				boolean shouldStop = false;
				while (!shouldStop) {
					Thread.sleep(1000);
					if (myGroup.activeCount() == 1) {
						System.out.println(
								"This is the last active thread. We are leavnig application, that is goint to stop");
						shouldStop = true;
					} else {
						System.out.println("Waiting for other threads to stop");
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}
		}, "waiter thread").start();
	}
}
