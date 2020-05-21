# A feladatrészekről

A feladatokat sorban kell megoldani.

A ZH végeredménye az elméleti rész (`20` pont) és a gyakorlati rész összpontszámából (mindegyik feladatrészre maximum `10` pont, összesen `40` pont) számítódik.

A megoldás elkészítésének részletes feltételei a kiküldött kurzusmailben szerepelnek.

- A feltöltés során **ZIP** fájlt kell készíteni, más tömörítési formátumokat nem fogad el a rendszer.
- Célszerű az elkészült teljes projektet feltölteni (a fordítással előállítható `.jar`, `.class` fájlok ízlés szerint eltávolíthatók belőle). A **forrásfájlok** mindenféleképpen kerüljenek bele a zipbe!

# A feladatról általában: MultipleMap

Készítsd el a `MultipleMap<K,V>` osztályt. A MultipleMap olyan osztály, mely több `Map<K,V>` objektumot fog össze. Műveletei:

* `void newMap(String name)` létrehoz egy  `Map` objektumot, amit a `name` név azonosít
* `List<String> getMaps()` visszaadja a `MultipleMap` -ben tárolt map-ek nevét.
* `void deleteMap(String name)` töröl egy map-et a `MultipleMap`-ből.
* `void put(K key, V value, String... maps)` a felsorolt nevű map-ekbe beírka K kulcsal V értéket
* `void remove(K key, String... maps)` törli a `K` kulcsot a felsorolt nevű map-ekből.
* `List<V> get(K key, String... maps)` kiveszi a K kulccsal jelölt értékeket a felsolort map-ekből
* `List<Integer> size(String... maps)` lekérdezi a felsorolt map-ek méretét.

## 1. feladat

Biztosítsd, hogy a MultiMap szálbiztos legyen. (Egyszerre csak 1 módosító művelet futhat az objektumon. Olvasóművelet nem futhat módosítás közben.)

## 2. feladat

Írj `main` függvényt, mely paraméterül kapja, hány szálon dolgozzon,  valamint map neveket.

* Hozzon létre egy `MultipleMap<Long, Long>` objektumot, és abban hozzon létre minden névnek egy-egy map-et.
* Hozzon létre annyi szálat, amennyit a paraméter megkövetel, a szálak osztozzanak a `MultipleMap` objektumon, és `1000` millisecundumonként válasszon véletlenszerűen valahány Map nevet, véletlenszerűen egy `k` számot a `[0..100)` intervallumból, és vagy 
    * lekérdezi a `k` értékhez tartozó értékeket, vagy 
    * választ egy véletlen számot ugyanabból az intervallumból, és beírja azokat a map-ekbe. 
* Indíts még egy szálat, ami másodpercenként kiírja, melyik map-ben hány elem található.

## 3. feladat

Egészítsd ki a tárolt értékeket statisztikákkal. Minden belső map minden kulcs-érték párjához tartsd nyilván, mikor nyúltak utoljára hozzá (`java.time.Instant.now()`), és hogy hányszor nyúltak hozzá összesen.

Egészítsd ki a `MultipleMap` objektumot egy `n` másodpercenként lefutó szállal, (Az `n` legyen konstruktor paraméter, ha nem adnak meg semmit, értéke legyen `10`.) ami összegyűjti:

* A legrégebben érintett `kulcs-mapNév` párokat
* A legkevesebbszer érintett `kulcs-mapNév` párokat

Kiválasztja a nagyobb elemszámú listát, és törli az adott map-ekből az adott kulcsokat. Eközben más művelet ne futhasson a MultipleMap-en.

## 4. feladat

Tedd lehetővé, hogy ha a MultipleMap `A1...An` mapjain író műveleteket végzünk, aközben a többi `B1...Bn` map-en lehessen más műveleteket végezni (amennyiben `A1...An` és `B1...Bn` diszjunkt halmazok), és két párhuzamos művelet ne okozhasson holtpontot.
