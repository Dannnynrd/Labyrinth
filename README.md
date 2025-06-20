# 🧩 LABYRINTH

## 1. Entwicklungsumgebung

- IntelliJ IDEA 2025.1.1.1 (Ultimate Edition)
- Java-Version: `24.0.1` (JDK 24, Release: 2025-04-15)
- macOS Sequoia 15.5

---

## 2. Vor dem Start

### 2.1 Schwierigkeitsgrad (Difficulty)

Es gibt drei Schwierigkeitsgrade: **Easy**, **Medium**, **Hard**.  
Diese beeinflussen:

- **Spielfeldgröße**: `25x25`, `35x35`, `45x45`
- **Zufalls-Varianz der Größe**: `+2`, `+3`, `+4`
- **Wandanteil** *(nach Maze-Generierung entfernt)*:
    - Easy: `0.6` → viele Wände
    - Medium: `0.5`
    - Hard: `0.4` → offeneres Spielfeld
- **Gegneranteil in %**: `0`, `0.2`, `0.3`
- **Gegnerbewegungsintervall** *(ms)*: `1500`, `1000`, `750`

> ⚠️ Mit jedem Level werden diese Werte dynamisch angepasst (bis zu einem Limit).  
> Dadurch wird **Easy** später genauso schwer wie **Hard**, nur später im Spielverlauf.

---

### 2.2 Steuerung

- **Bewegung**: Pfeiltasten
- **Pause-Menü öffnen**: `ESC`
- **Im Menü verfügbar**:
    - Spiel neu starten
    - Schwierigkeit ändern
    - Spiel beenden
    - Spiel fortsetzen

> 🔍 Oben links im Spiel wird die aktuelle Spieler-Richtung angezeigt:  
> **Nord**, **Ost**, **Süd**, **West**

---

### 2.3 Labyrinth-Generierung

- Pro Level wird das Labyrinth **neu generiert**
- Verwendeter Algorithmus: **Depth-First Search (rekursiver Backtracker)**
- 🔐 **Mindestens ein Pfad** zum Ziel ist immer garantiert

---

### 2.4 Spielergesundheit

- Startgesundheit: **5 Leben**
- Keine Regeneration ohne Power-Ups

---

### 2.5 Power-Ups

| Power-Up             | Effekt                                      |
|----------------------|---------------------------------------------|
| 🩹 **Health**         | +1 Leben                                    |
| 🛡️ **Unverwundbarkeit** | Gegner verursachen für kurze Zeit keinen Schaden |
| ❄️ **Gegner einfrieren** | Gegner stoppen, fügen aber weiterhin Schaden zu   |

---

## 3. Spiel starten und spielen

### 3.1 Starten

- Datei `Labyrinth.java` im Paket `src/controller` ausführen

---

### 3.2 Hauptmenü

- Auswahl zwischen **Easy**, **Medium**, **Hard**
- Standardmäßig ist **Medium** ausgewählt
- Mit **"Start Game"** beginnt das Spiel mit gewähltem Schwierigkeitsgrad

---

### 3.3 Während des Spiels

- Ziel: Den Ausgang des Labyrinths erreichen
- Gegner verfolgen den Spieler
- Sammle Power-Ups für Vorteile

---
s
