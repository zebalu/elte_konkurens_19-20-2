package hu.elte.konkurens.zh1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MultipleMaps<K, V> {
	private final ReadWriteLock centralLock = new ReentrantReadWriteLock();
	
	private final Map<String, Map<K, V>> maps = new ConcurrentHashMap<>();
	
	public void newMap(String name) {
		doInLock(centralLock.writeLock(), ()-> {
			if(maps.containsKey(name)) {
				throw new IllegalArgumentException("Map with name: "+name+" already exists!");
			}
			maps.put(name, new HashMap<K,V>());
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
			applyToMaps(map -> map.put(key, value), maps);
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
			return queryOnMaps(map -> map.get(key), maps);
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
	
	private void applyToMaps(Consumer<Map<K, V>> actionOnMap, String... mapNames) {
		Arrays.stream(mapNames).map(maps::get).forEach(actionOnMap);
	}
	
	private<T> List<T> queryOnMaps(Function<Map<K, V>, T> queryOnMap, String... mapNames) {
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
}
