package hu.elte.konkurens.zh1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Main {
	
	private static final int BOUND = 100;

	public static void main(String[] args) throws Exception {
		
		if(args.length<2) {
			throw new IllegalArgumentException("we need at least 2 arguments! the thread count and the map names");
		}
		
		Random random = new Random(System.currentTimeMillis());
		int threadCount = Integer.parseInt(args[0]);
		List<String> mapNames = new ArrayList<>(new HashSet<>(Arrays.asList(Arrays.copyOfRange(args, 1, args.length))));
		
		MultipleMaps<Long, Long> mMaps = new MultipleMaps<>();
		mapNames.forEach(mMaps::newMap);
		
		for(int i=0; i<threadCount; ++i) {
			new Thread(()-> repeateEverySecond(()->{
				modifyMaps(random, mMaps, selectRandomMaps(random, mapNames));
			})).start();
		}
		
		new Thread(() -> repeateEverySecond(() -> {
			mapNames.forEach(name -> {
				System.out.println(name + " has size: " + mMaps.size(name).get(0));
			});
			System.out.println();
		})).start();
		
	}

	private static void modifyMaps(Random random, MultipleMaps<Long, Long> mMaps, String[] maps) {
		if(random.nextBoolean()) {
			mMaps.put(Long.valueOf(random.nextInt(BOUND)), Long.valueOf(random.nextInt(BOUND)), maps);
		} else {
			mMaps.remove(Long.valueOf(random.nextInt(BOUND)), maps);
		}
	}

	private static String[] selectRandomMaps(Random random, List<String> mapNames) {
		int mapCount = random.nextInt(mapNames.size()) + 1;
		Set<String> names = new LinkedHashSet<>();
		while(names.size()<mapCount) {
			names.add(mapNames.get(random.nextInt(mapNames.size())));
		}
		String[] maps = names.toArray(new String[] {});
		return maps;
	}
	
	private static void repeateEverySecond(Runnable runnable) {
		while(true) {
			try {
				runnable.run();
				Thread.sleep(1_000);
			} catch(InterruptedException ie) {
				ie.printStackTrace();
				return;
			}
		}
	}

}
