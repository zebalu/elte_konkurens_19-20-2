package hu.elte.example;

//Készíts két szálat, amelyek készítsenek 10-10 szálat. Ezek mind írjanak ki százezer szöveget az első feladathoz hasonlóan, hello1, hello2 stb. és world1, world2 stb. prefixekkel.

public class F0301 {

	public static void main(String[] args) {
		new Thread(()->threadMakerAndStarter("hello")).start();
		new Thread(()->threadMakerAndStarter("world")).start();
	}

	private static void threadMakerAndStarter(String word) {
		for(int i=0; i<10; ++i) {
			final int finalizedI = i;
			new Thread(()->{
				for(int j=0; j<100_000; ++j) {
					System.out.println(String.format("%02d \t %04d \t %s", finalizedI, j, word));
				}
			}).start();
		}
	}
}
