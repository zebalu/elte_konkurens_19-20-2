package hu.elte.example;

import java.util.Timer;
import java.util.TimerTask;

/*
 * Készíts egy másik megoldást is, ebben a Timer osztály scheduleAtFixedRate metódusát használd.
 */

public class F0402 {

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
		Timer timer = new Timer("waiter timer");
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				try {
					Thread.sleep(1000);
					if (myGroup.activeCount() == 0) {
						System.out.println(
								"This is the last active thread. We are leavnig application, that is goint to stop");
						cancel();
					} else {
						System.out.println("Waiting for other threads to stop");
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
					Thread.currentThread().interrupt();
				}
			}
		}, 0L, 1000L);
	}

}
