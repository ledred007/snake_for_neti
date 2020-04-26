# snake_for_neti
A népszerű kígyós játék megvalósítása Java nyelven.
-----------------------------
A forrás könyvtár szerkezete:
-----------------------------
- .classpath és .project eclipse paraméter fájlok
  - fejlesztés során eclipse-et használtam 
- src/main/snake - java források
- src/test/snake - írtam néhány unit tesztet Junit 5-öt használnak
- src/log4j.xml - logger beállításai
- readme.txt - program információk (ez a fájl)
- sources.txt - a fordításhoz szükséges forrás lista fájl
- lib - külső library-k (log4j és derby)

--------------------
A program fordítása:
--------------------
javac  -d ./build -classpath "./lib/log4j-1.2.17.jar;./lib/derby-10.13.1.1.jar" @sources.txt

java -cp snake.jar snake.Main 50 50

------------------
Egyéb információk:
------------------
A kígyó színe zöld - az alma piros, a banán sárga, ha
elindult a kígyó tartja az irányt.

A program indítása:

snake.Main <width> <height>

10 X 10 - es négyzetnél kisebb játékteret nem fogad el,
a felső  határ a képernyő mérete paraméterként definiál
keret meghagyásával.

A Játék paraméterei fordítás előtt a 
sneak.game.configuration.GameParameters - osztályban állíthatóak be:
- cella és játéktér paraméterek
- színek
- időzítés értékek
- adatbázis név.

Az AWT panelek tulajdonságai a
sneak.game.configuration.PanelParameters - osztályban vannak definiálva.

A szöveges üzeneteket a 
TextParameters
sneak.game.configuration.PanelParameters - osztály tartalmazza.
Kivéve a Main üzeneteit hagytam a helyükön, hogy az alap működés
értehetőbb legyen.

A játék az egyes játékosok adatait lokális Derby adatbázisba menti.

