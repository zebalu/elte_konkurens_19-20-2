# Feladatok szinkronizációra

1. Szimulálj dead-lockot

   Készíts egy programot, ami alkalmas az éhező-filozófusok probléma szimulálására. 
   Győződj meg róla, hogy a program tényleg dead-lockba tud kerülni.
   
   a. Oldd fel a problémát wait()-notify() használatával 
   
   b. Oldd fel a problémát közponotsított lockkal
   
   c. Oldd fel a problémát tryLockkal

2. Készíts egyszerű cache-t.

   A cache tartalmazza számokhoz azok négyzetét. Ha a cache-ből kérnek egy elemet, 
   ami még nincs benne, akkor azt számold ki egy lassú módszerrel. (Például egy n*n-es 
   inkrementáló ciklussal.)
   
   a. oldd meg, hogy egyszerre csak egy szál használhassa a cache-t
   
   b. wait-notify használatával oldd meg, hogyha egy szál csak olvas, akkor ne blokkolja 
      a többi szálat
   
   c. oldd meg a fenti problémát [ReadWrite lockkal](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/locks/ReentrantReadWriteLock.WriteLock.html)
   
   d. oldd meg a fenti problémát optimista lockolással ([StampedLock](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/locks/StampedLock.html))
   
