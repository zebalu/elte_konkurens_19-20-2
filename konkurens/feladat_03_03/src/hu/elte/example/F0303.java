package hu.elte.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/*
 * A fájl sorait kiíró programot bővítsd úgy, hogy lehessen megadni neki néhány számpárt is.
N,M,N2 jelentse azt, hogy az N-edik fájlt beolvasó szál, miután M kiírást megtett, be kell, hogy várja az N2-edik fájlt beolvasó szál futását, és csak utána folytathatja a fájlja sorainak kiírását.
Tipp: ki kell találni, hogy az N-es szál hogyan érheti el az N2-es szálat.
 */
public class F0303 {

	private static final String[] FILE_NAMES = { "f1.txt", "f2.txt", "f3.txt", "f4.txt", "f5.txt", "f6.txt", "f7.txt",
			"f8.txt", "f9.txt", "f10.txt" };

	private static final Thread[] THREADS = new Thread[FILE_NAMES.length];

	private static final Random random = new Random(System.currentTimeMillis());

	private static int waitingIndex = -1;
	private static int waitedIndex = -1;
	private static int waitAfterLines = -1;

	public static void main(String[] args) throws Exception {
		if (args.length < 3) {
			System.err.println("This program must have 3 integers as input");
			throw new IllegalArgumentException("Provide 3 integers as input parm!");
		}
		waitingIndex = Integer.parseInt(args[0]);
		waitAfterLines = Integer.parseInt(args[1]);
		waitedIndex = Integer.parseInt(args[2]);
		checkIndex(waitingIndex);
		checkIndex(waitedIndex);
		for (int i = 0; i < FILE_NAMES.length; ++i) {
			String name = FILE_NAMES[i];
			int j = i;
			THREADS[i] = new Thread(() -> readAndPrintFileLines(name, j));
			THREADS[i].start();
		}
	}

	private static void printLineAndWait(String line) {
		System.out.println(line);
		try {
			Thread.sleep(random.nextInt(100) + 100);
		} catch (InterruptedException ie) {
			ie.printStackTrace();
			Thread.currentThread().interrupt();
		}
	}

	private static void readAndPrintFileLines(String name, int index) {
		try (BufferedReader br = new BufferedReader(new FileReader(name))) {
			List<String> allLines = br.lines().collect(Collectors.toList());
			for (int i = 0; i < allLines.size(); ++i) {
				printLineAndWait(allLines.get(i));
				waitOtherThreadIfNeeded(index, i);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	private static void waitOtherThreadIfNeeded(int index, int lineCount) {
		if (index == waitingIndex && lineCount >= waitAfterLines) {
			try {
				THREADS[waitedIndex].join();
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
	}

	private static void checkIndex(int index) {
		if (index >= FILE_NAMES.length) {
			throw new IllegalArgumentException(index + " is too high! it must be less than " + FILE_NAMES.length);
		}
		if (index < 0) {
			throw new IllegalArgumentException(index + " is too low! it must be greter than o equal to 0");
		}
	}

}
