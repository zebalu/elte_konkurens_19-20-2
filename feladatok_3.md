# Feladatok konkurens adatszerkezetekkel

## 1. Randevú

Készíts termelő-fogyasztó implementációt. A termelő gyártson 5 ms-onként 1 véletlen számot a fogyasztó 12 ms-onként fogyasszony el 1-et, 
és ha az 7-tel osztható, írja ki. A szálak között a kommunikáció egy BlockingQueue-val történjen. 
https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/BlockingQueue.html

a) valósítsd meg a feladatot ArrayBlockingQueue -val https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ArrayBlockingQueue.html

b) valósítsd meg a feladatot LinkedBlockingDeque -val https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/LinkedBlockingDeque.html

c) valósítsd meg a feladatot SynchronousQueue -val https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/SynchronousQueue.html

figyeld meg, mikor hogy viselkednek a proramok

## 2. Megosztott lista

Valósíts meg egy olyan programot, amiben egy központi ArrayList-et véletlenszerűen olvasnak / írnak / iterálnak végi a szálak.

a) figyeld meg mindenféle szinkronizáció nélkül a működést

b) írj saját szinkronizációt a problémák megoldására

c) használj szinkronizált listát: https://docs.oracle.com/javase/8/docs/api/java/util/Collections.html#synchronizedList-java.util.List-

d) használj CopyOnWriteArrayList -et: https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CopyOnWriteArrayList.html

figyeld meg, mikor hogy viselkedik a program

## 3. Szinkronizált asszociatív tároló

Készíts "MultiMap"-et (egy kucslhoz több érték is felvehető.) Ez objektum legyen szálbiztos. (Használd hozzá a Map interface 
speciális metódusait és a ConcurrentHashMap-et. https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ConcurrentHashMap.html )
