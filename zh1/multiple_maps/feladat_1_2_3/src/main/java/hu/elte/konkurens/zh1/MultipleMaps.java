package hu.elte.konkurens.zh1;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;


/**
 * <h2>3. feladat</h2>

Egészítsd ki a tárolt értékeket statisztikákkal. Minden belső map minden kulcs-érték párjához tartsd nyilván, mikor nyúltak utoljára hozzá (java.time.Instant.now()), 
és hogy hányszor nyúltak hozzá összesen.

Egészítsd ki a MultipleMap objektumot egy n másodpercenként lefutó szállal, (Az n legyen konstruktor paraméter, ha nem adnak meg semmit, értéke legyen 10.) ami összegyűjti:

<ul>
  <li> A legrégebben érintett kulcs-mapNév párokat
  <li> A legkevesebbszer érintett kulcs-mapNév párokat
</ul>

Kiválasztja a nagyobb elemszámú listát, és törli az adott map-ekből az adott kulcsokat. Eközben más művelet ne futhasson a MultipleMap-en.
 */
public class MultipleMaps<K, V> {
	
	private final ReadWriteLock centralLock = new ReentrantReadWriteLock();
	
	private final Map<String, Map<K, ValueWitStatistics<V>>> maps = new ConcurrentHashMap<>();
	
	private final int clearUpSeconds;
	
	public MultipleMaps() {
		this(10);
	}
	
	public MultipleMaps(int clearUpSeconds) {
		this.clearUpSeconds=clearUpSeconds;
		startClearUpThread();
	}
	
	public void newMap(String name) {
		doInLock(centralLock.writeLock(), ()-> {
			if(maps.containsKey(name)) {
				throw new IllegalArgumentException("Map with name: "+name+" already exists!");
			}
			maps.put(name, new HashMap<>());
		});
	}
	
	public List<String> getMaps() {
		return new ArrayList<String>(maps.keySet());
	}
	
	public void deleteMap(String name) {
		doInLock(centralLock.writeLock(), () -> maps.remove(name));
	}
	
	public void put(K key, V value, String... maps) {
		doInLock(centralLock.writeLock(), () -> {
			validateMapsExists(maps);
			applyToMaps(map -> {
				ValueWitStatistics<V> stored = map.get(key);
				if (stored != null) {
					stored.setValue(value);
				} else {
					stored = new ValueWitStatistics<V>(value);
					map.put(key, stored);
				}
			}, maps);
		});
	}

	public void remove(K key, String... maps) {
		doInLock(centralLock.writeLock(), () -> {
			validateMapsExists(maps);
			applyToMaps(map -> map.remove(key), maps);
		});
	}
	
	public List<V> get(K key, String... maps) {
		return queryInLock(centralLock.readLock(), () -> {
			validateMapsExists(maps);
			return queryOnMaps(map -> map.get(key).getValue(), maps);
		});
	}
	
	public List<Integer> size(String... maps) {
		return queryInLock(centralLock.readLock(), () -> {
			validateMapsExists(maps);
			return queryOnMaps(map -> map.size(), maps);
		});
	}

	private void validateMapsExists(String... mapNames) {
		Arrays.stream(mapNames).filter(name -> !maps.containsKey(name)).findAny().ifPresent(missingName -> {
			throw new IllegalArgumentException("Map with name: " + missingName + "does not exisit!");
		});
	}
	
	private void applyToMaps(Consumer<Map<K, ValueWitStatistics<V>>> actionOnMap, String... mapNames) {
		Arrays.stream(mapNames).map(maps::get).forEach(actionOnMap);
	}
	
	private<T> List<T> queryOnMaps(Function<Map<K, ValueWitStatistics<V>>, T> queryOnMap, String... mapNames) {
		return Arrays.stream(mapNames).map(maps::get).map(queryOnMap).collect(Collectors.toList());
	}
	
	private void doInLock(Lock lock, Runnable action) {
		lock.lock();
		try {
			action.run();
		} finally {
			lock.unlock();
		}
	}
	
	private <T> T queryInLock(Lock lock, Supplier<T> query) {
		lock.lock();
		try {
			return query.get();
		} finally {
			lock.unlock();
		}
	}
	
	private void startClearUpThread() {
		ScheduledExecutorService clearupThreadExecutor = Executors.newSingleThreadScheduledExecutor(MultipleMaps::makeDaemonThreadFor);
		ExecutorService collectorService = Executors.newFixedThreadPool(2, MultipleMaps::makeDaemonThreadFor);
		clearupThreadExecutor.scheduleAtFixedRate(()->{
			clearUp(collectorService);
		}, 0, clearUpSeconds, TimeUnit.SECONDS);
	}

	private void clearUp(ExecutorService collectorService) {
		doInLock(centralLock.writeLock(), ()->{
			Future<List<Pair<String, K>>> leastUsedFuture = collectorService.submit(this::findLeastUsedInAllMaps);
			Future<List<Pair<String, K>>> leastFrequentlyUsedFuture = collectorService.submit(this::findLeastFrequentlyUsedInAllMaps);
			try {
				if(leastFrequentlyUsedFuture.get().size()<leastUsedFuture.get().size()) {
					deleteAll(leastFrequentlyUsedFuture.get());
				} else {
					deleteAll(leastUsedFuture.get());
				}
			} catch (InterruptedException ie) {
				ie.printStackTrace();
				return;
			} catch (ExecutionException ee) {
				ee.printStackTrace();
				throw new IllegalStateException("could not run clearup", ee);
			}
		});
	}
	
	private void deleteAll(List<Pair<String, K>> data) {
		data.forEach(pair -> {
			maps.get(pair.getFirst()).remove(pair.getSecond());
		});
	}
	
	private static Thread makeDaemonThreadFor(Runnable r) {
		Thread t = new Thread(r);
		t.setDaemon(true);
		return t;
	}
	
	private List<Pair<String, K>> findLeastUsedInAllMaps() {
		List<Pair<String, K>> result = new ArrayList<>();
		for(String name: maps.keySet()) {
			findLeastUsedKeys(maps.get(name)).stream().forEach(key -> {
				result.add(new Pair<>(name, key));
			});
		}
		return result;
	}
	
	private List<Pair<String, K>> findLeastFrequentlyUsedInAllMaps() {
		List<Pair<String, K>> result = new ArrayList<>();
		for(String name: maps.keySet()) {
			findLeastFreequentlyUsedKeys(maps.get(name)).stream().forEach(key -> {
				result.add(new Pair<>(name, key));
			});
		}
		return result;
	}
	
	private List<K> findLeastUsedKeys(Map<K, ValueWitStatistics<V>> map) {
		List<K> result = new ArrayList<K>();
		if(map.isEmpty()) {
			return result;
		}
		int smallestToucCount = Integer.MAX_VALUE;
		for(Entry<K, ValueWitStatistics<V>> entry: map.entrySet()) {
			if(entry.getValue().getTouchCount() < smallestToucCount) {
				smallestToucCount = entry.getValue().getTouchCount();
				result.clear();
				result.add(entry.getKey());
			} else if (entry.getValue().getTouchCount() == smallestToucCount) {
				result.add(entry.getKey());
			}
		}
		return result;
	}
	
	private List<K> findLeastFreequentlyUsedKeys(Map<K, ValueWitStatistics<V>> map) {
		List<K> result = new ArrayList<K>();
		if(map.isEmpty()) {
			return result;
		}
		Instant lastUsed = null;
		for(Entry<K, ValueWitStatistics<V>> entry: map.entrySet()) {
			if(lastUsed == null || entry.getValue().getTimeStamp().isBefore(lastUsed)) {
				lastUsed = entry.getValue().getTimeStamp();
				result.clear();
				result.add(entry.getKey());
			} else if (entry.getValue().getTimeStamp().equals(lastUsed)) {
				result.add(entry.getKey());
			}
		}
		return result;
	}
	
	private static class ValueWitStatistics<V> {
		private Instant timeStamp = Instant.now();
		private int touchCount = 1;
		private V value;
		
		ValueWitStatistics(V value) {
			this.value=value;
		}

		public Instant getTimeStamp() {
			return timeStamp;
		}
		
		public int getTouchCount() {
			return touchCount;
		}

		public synchronized V getValue() {
			++touchCount;
			timeStamp=Instant.now();
			return value;
		}

		public synchronized void setValue(V value) {
			++touchCount;
			timeStamp=Instant.now();
			this.value = value;
		}
	}
}
