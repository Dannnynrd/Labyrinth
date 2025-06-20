# LABYRINTH

1. Entwicklungsumgebung:
    - IntelliJ IDEA 2025.1.1.1 (Ultimate Edition)
    - Java-Version "24.0.1" (2025-04-15, JDK 24)
    - macOS Sequoia 15.5

2. Vor dem Start

    2.1 Schwierigkeitsgrad (Difficulty)
        - Es gibt 3 Schwierigkeitsgrade: Easy, Medium, Hard.
        - Unterschiede zwischen den Schwierigkeitsgraden:
            - Größe des Spielfelds: (25x25, 35x35, 45x45)
            - Zufällige Größenvarianz: (2, 3, 4), wird zur Basisgröße addiert
            - Wandanteil in Prozent: (0.6, 0.5, 0.4)
              → Nach der Maze-Generierung werden Wände entfernt. Höherer Wert = offeneres Spiel.
            - Gegneranteil in Prozent: (0, 0.2, 0.3)
            - Gegnerbewegung (Intervall in ms): (1500, 1000, 750)

        - Diese Werte ändern sich pro Level leicht, sind aber gedeckelt ("capped").
        - Dadurch wird Easy im späteren Spielverlauf genauso schwer wie Hard – nur später.

    2.2 Steuerung
        - Mit den Pfeiltasten bewegt man sich.
        - Mit "ESC" öffnet man das Ingame-Menü (Pause).
        - Im Menü kann man:
            - Spiel neustarten
            - Schwierigkeitsgrad ändern
            - Spiel beenden
            - Spiel fortsetzen
        - Oben links wird die aktuelle Richtung des Spielers angezeigt (Nord, Ost, Süd, West).

    2.3 Labyrinth-Generierung
        - Das Labyrinth wird pro Level neu generiert.
        - Algorithmus: Depth-First-Search (rekursiver Backtracker).
        - Es ist immer mindestens ein Weg zum Ziel vorhanden.

    2.4 Spielergesundheit
        - Der Spieler startet mit einer festen maximalen Gesundheit (aktuell: 5).

    2.5 Power-Ups
        - Health: +1 Leben
        - Unverwundbarkeit: Immun gegen Gegner für eine begrenzte Zeit
        - Gegner einfrieren: Gegner bewegen sich nicht, verursachen aber weiterhin Schaden

3. Spiel starten und spielen

    3.1 Spielstart
        - Um das Spiel zu starten, muss die Datei "Labyrinth.java"
          im Paket "src/controller" ausgeführt werden.

    3.2 Hauptmenü
        - Nach dem Start erscheint ein Hauptmenü mit 3 auswählbaren Schwierigkeitsgraden.
        - Standardmäßig ist "Medium" ausgewählt.
        - Mit dem Button "Start Game" beginnt das Spiel basierend auf der gewählten Schwierigkeit.

    3.3 Während des Spiels
        - Gegner verfolgen den Spieler.
        - Ziel: Den Ausgang erreichen.
        - Auf dem Weg können Power-Ups eingesammelt werden, die dem Spieler helfen.
    3.4 Nicht geschafft zum Ziel zu kommen
        - Dann Startet man wieder bei Level 1
