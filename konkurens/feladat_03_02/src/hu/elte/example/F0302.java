package hu.elte.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Random;

/*
Egy tömbben fájlnevek vannak megadva. Minden fájlhoz indíts egy szálat, ami soronként kiolvassa a tartalmát, és kiírja a sztenderd kimenetre.
A program működése lassítható a Thread.sleep művelet használatával.
A sleep paraméterét a program mindig véletlenszerűen válassza a 100..200 intervallumból.
*/

public class F0302 {

	private static final String[] FILE_NAMES = { "f1.txt", "f2.txt", "f3.txt", "f4.txt", "f5.txt", "f6.txt", "f7.txt",
			"f8.txt", "f9.txt", "f10.txt" };
	
	private static final Random random = new Random(System.currentTimeMillis());

	public static void main(String[] args) throws Exception {
		Arrays.stream(FILE_NAMES).forEach( name -> {
			new Thread(()->readAndPrintFileLines(name)).start();
		});
	}
	
	private static void printLineAndWait(String line) {
		System.out.println(line);
		try {
			Thread.sleep(random.nextInt(100)+100);
		} catch(InterruptedException ie) {
			ie.printStackTrace();
			Thread.currentThread().interrupt();
		}
	}
	
	private static void readAndPrintFileLines(String name) {
		try (BufferedReader br = new BufferedReader(new FileReader(name))){
			br.lines().forEach(F0302::printLineAndWait);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

}
