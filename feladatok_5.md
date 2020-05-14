# 1. Használj Atomic*-ot

Írj egz olyan programot, ami több szálon működik, mindegyik szál ugyanazzal az egész számot tartalamzó változóval dolgozik, és annak
értékőtől függően változtatja a változó értékét. Pl.: ha a 4-el vett osztási maradék:

* 0 --> növeli 1-el
* 1 --> csökkenti 2-vel
* 2 --> szorozza 3-mal
* 4 --> osztja 5-el

tegyél a műveletekhez lassító feladatokat (pl kiíratás), és csak akkor módosítsd a változó értékét, ha a műveletbe vett belépéstől számítva 
nem változott még meg.

# 2. Használj ThreadLocal-t:

Old meg egy nem szálbiztos, többszörösíthető objektum többszáló használatát ThreadLocal segítségével. 
(Jó példa: SimpleDateFormat oszály) Először próbáld megmutatni, hogy a ThreadLocal-ra szükség van.

# 3. Szimulálj processor-t ExecutorService-szel

Készíts olyan Fibonacci számítást, ami a lépeseket egy ExecutorServcie segítségével szimulálja.

# 4. Használj ForkJoinPool-t

Az előző feladatot írd át ForkJoinPool segítségével.
