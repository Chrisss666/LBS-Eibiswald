**Entwicklungsprotokoll: Hangman-Spiel in Java**

**Projektname:** Hangman-Spiel<br>
**Made by:** Christian Reitbauer-Rieger

### **1. Projektstart und Planung**
- **Zielsetzung:** Erstellung eines Hangman-Spiels in Java mit einer grafischen Benutzeroberfläche (GUI), das sowohl statische als auch dynamisch geladene Wörter verwenden kann.
- **Anforderungen definiert:** Ein Spiel mit Menü zur Anpassung der Spielparameter, grafische Ausgabe der Hangman-Teile.
- **Werkzeuge:** Java Swing für die GUI, Dateihandling für das dynamische Laden von Wörtern, Verwendung von JMenu und JDialog für Benutzerinteraktionen.
- **To-Do-Liste erstellt:**
    - Erstellen der Grundstruktur des Programms
    - Implementieren der GUI-Komponenten
    - Einfügen von Logik zur Verwaltung der Spielregeln

### **2. Grundlegende Struktur und Klassenaufteilung**
- **Erstellung der Klassenstruktur:**
    - **MainFrame** für das Hauptfenster und die Benutzeroberfläche.
    - **GamePanel** für die Darstellung der Hangman-Grafiken.
    - **InputPanel** für die Eingabe der Buchstaben.
    - **GameState** zur Verwaltung des aktuellen Spielstatus (z.B. Fehlversuche, erratene Buchstaben).
    - **WordManager** zur Verwaltung der Lösungswörter (statisch und dynamisch).
- **Überlegung:** Eine klare Trennung der Aufgaben in verschiedene Klassen könnte helfen, den Code übersichtlicher und wartbarer zu gestalten.
- **Implementierung der Grundfunktionen:** Die Basisklassen und ihre Schnittstellen wurden definiert, und erste GUI-Komponenten wurden hinzugefügt.
- **Probleme:** Schwierigkeiten beim Initialisieren der GUI-Komponenten im richtigen Layout.
- **Lösung:** Verwenden eines BorderLayout für die Aufteilung der Panels, um mehr Flexibilität bei der Anordnung zu erhalten.

### **3. Implementierung der Spielmechanik**
- **Spielregeln integriert:**
    - Implementierung der Logik zur Buchstabenüberprüfung.
    - Verwaltung der Fehlversuche und Anzeige des aktuellen Status.
- **Überlegung:** Die Spielregeln sollten so gestaltet sein, dass der Spielfluss intuitiv bleibt und Benutzerfeedback direkt sichtbar ist.
- **GameState-Klasse erweitert:** Zählt die Fehlversuche und verfolgt die bereits eingegebenen Buchstaben (History).
- **Wörterverwaltung:**
    - Zunächst statische Liste von Wörtern in einem Array eingefügt.
    - Später Datei-Handling mit der **Scanner**-Klasse hinzugefügt, um Wörter dynamisch aus einer Datei zu lesen.
- **Probleme:** Beim dynamischen Laden der Wörter traten FileNotFoundException-Probleme auf.
- **Lösung:** Prüfen, ob die Datei existiert, und die entsprechenden Fehler abfangen.

### **4. Grafische Darstellung der Fehlversuche**
- **GamePanel-Klasse implementiert:**
    - Laden von 9 Bildern, die den Fortschritt des Hangman zeigen.
    - Verwendung von **ImageIcon** zum Darstellen der Bilder, je nach Anzahl der Fehlversuche.
- **Überlegung:** Die Bilder sollten so gestaltet sein, dass der Spieler den Fortschritt leicht nachvollziehen kann.
- **Probleme:** Schwierigkeiten beim richtigen Pfad für die Bilddateien.
- **Lösung:** Relative Pfade verwendet und sichergestellt, dass der Ordner "images" korrekt in das Projekt eingebunden wurde.

### **5. Menü und Einstellungen**
- **Menü hinzugefügt:** Verwendung von **JMenuBar** zur Erstellung des Menüs:
    - **Set Number of Attempts** ermöglicht es, die Anzahl der Fehlversuche vor Spielbeginn anzupassen.
    - **Show Letter History** als **JCheckBoxMenuItem** hinzugefügt, um die History der eingegebenen Buchstaben anzuzeigen oder zu verbergen.
- **Überlegung:** Das Menü sollte dem Benutzer ermöglichen, das Spiel seinen Vorlieben entsprechend anzupassen, ohne die Übersichtlichkeit zu verlieren.
- **SettingsDialog implementiert:** Vor dem Spielstart wird ein Dialog angezeigt, in dem die Anzahl der Fehlversuche festgelegt werden kann.

### **6. Testen und Bugfixing**
- **Testen der Spielabläufe:**
    - Mehrere Tests mit unterschiedlichen Wörtern und Anzahlen von Fehlversuchen.
    - Prüfung der Benutzeroberfläche und der Logik zur Verwaltung der Eingaben.
- **Bugfixes:**
    - **Fehlende Eingabevalidierung**: Sicherstellen, dass nur einzelne Buchstaben akzeptiert werden und keine Sonderzeichen oder Zahlen.
    - **Neustart des Spiels**: Sicherstellen, dass alle Spielzustände (Fehlversuche, Lösungswort, History) zurückgesetzt werden.
- **Überlegung:** Die Benutzerführung sollte so gestaltet sein, dass Fehler leicht zu verstehen sind und das Spiel jederzeit nachvollziehbar bleibt.
- **Probleme:** Sobald ein Buchstabe eingegeben wurde, welcher kleingeschrieben war, aber im Wort groß steht, wurde er als falsch gewertet.
- **Lösungen:** Implementierung einer Überprüfung in **WordManager**, sodass alle Buchstaben klein gesetzt werden.

### **7. Zusätzliche Features nach der eigentlichen Abgabe**
- **Features:**
    - Wörter werden per OpenAI API Anbindung generiert.