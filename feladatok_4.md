# 1. Készíts saját 1 szálas threadpool-t

A Threadpool-nak 1 szála van. Runnable-öket fogad és hajt futtat le.
a) végtelen sok runnable-t kaphat
b) a job-ok sora betellhet, ilyenkor dobjon exception-t ha új jobot kap

# 2. Készíts saját több szálas threadpool-t

A Threadpool-nak töb szála van, ha nincs szabad szál, egy véges sorban gyűjti a taskokat. (mint az 1/b)

# 3. Írd át a feladatot a bepített ThreadPool-okra. (Executor)

Mikor melyik threadpool-t kell hazsnálni?

# 4. Használj ExecutorService-eket.

Készíts egy programot, ami egy ExecutorService segítségével más szálakon számoltat ki Fibonacci értékeket. 
(A számításhoz egy nem hatékony -- pl rekurzív -- Fibonacci számítási megoldást használj.)

# 5. Szimulálj processor-t ExecutorService-szel

Készíts olyan Fibonacci számítást, ami a lépeseket egy ExecutorServcie segítségével szimulálja.
