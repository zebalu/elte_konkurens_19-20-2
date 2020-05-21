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
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;


/**
 * <h2>4. feladat</h2>

Tedd lehetővé, hogy ha a MultipleMap `A1...An` mapjain író műveleteket végzünk, aközben a többi `B1...Bn` map-en lehessen más műveleteket végezni 
(amennyiben `A1...An` és `B1...Bn` diszjunkt halmazok), és két párhuzamos művelet ne okozhasson holtpontot.
 */
public class MultipleMaps<K, V> {
	
	private final Lock centralLock = new ReentrantLock();
	
	private final Map<String, Map<K, ValueWitStatistics<V>>> maps = new ConcurrentHashMap<>();
	private final Map<String, ReadWriteLock> mapLocks = new ConcurrentHashMap<>();
	
	private final int clearUpSeconds;
	
	public MultipleMaps() {
		this(10);
	}
	
	public MultipleMaps(int clearUpSeconds) {
		this.clearUpSeconds=clearUpSeconds;
		startClearUpThread();
	}
	
	public void newMap(String name) {
		doInLocks(Arrays.asList(centralLock), ()-> {
			if(maps.containsKey(name)) {
				throw new IllegalArgumentException("Map with name: "+name+" already exists!");
			}
			maps.put(name, new HashMap<>());
			mapLocks.put(name, new ReentrantReadWriteLock());
		});
	}
	
	public List<String> getMaps() {
		return new ArrayList<String>(maps.keySet());
	}
	
	public void deleteMap(String name) {
		doInLocks(Arrays.asList(centralLock), () -> {
			maps.remove(name);
			mapLocks.remove(name);
		});
	}
	
	public void put(K key, V value, String... maps) {
		doInLocks(collectWriteLocks(maps), () -> {
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
		doInLocks(collectWriteLocks(maps), () -> {
			validateMapsExists(maps);
			applyToMaps(map -> map.remove(key), maps);
		});
	}
	
	public List<V> get(K key, String... maps) {
		return queryInLocks(collectReadLocks(maps), () -> {
			validateMapsExists(maps);
			return queryOnMaps(map -> map.get(key).getValue(), maps);
		});
	}
	
	public List<Integer> size(String... maps) {
		return queryInLocks(collectReadLocks(maps), () -> {
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
	
	private void doInLocks(List<Lock> locks, Runnable action) {
		List<Lock> locked = new ArrayList<Lock>(locks.size());
		try {
			safeLockAllLocks(locks, locked);
			action.run();
		}finally {
			for(Lock lock: locked) {
				lock.unlock();
			}
		} 
	}
	
	private <T> T queryInLocks(List<Lock> locks, Supplier<T> query) {
		List<Lock> locked = new ArrayList<Lock>(locks.size());
		try {
			safeLockAllLocks(locks, locked);
			return query.get();
		} finally {
			for(Lock lock: locked) {
				lock.unlock();
			}
		}
	}
	
	private void safeLockAllLocks(List<Lock> locks, List<Lock> locked) {
		centralLock.lock();
		try {
			for(Lock lock: locks) {
				lock.lock();
				locked.add(lock);
			}
		} finally {
			centralLock.unlock();
		}
	}
	
	private List<Lock> collectReadLocks(String... maps) {
		return Arrays.stream(maps).map(name -> getLock(name, true)).collect(Collectors.toList());
	}
	
    private List<Lock> collectWriteLocks(String... maps) {
    	return Arrays.stream(maps).map(name -> getLock(name, false)).collect(Collectors.toList());
	}
    
    private Lock getLock(String name, boolean readLock) {
    	ReadWriteLock lock = mapLocks.get(name);
    	if(readLock) {
    		return lock.readLock();
    	} else {
    		return lock.writeLock();
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
		doInLocks(Arrays.asList(centralLock), ()->{
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
