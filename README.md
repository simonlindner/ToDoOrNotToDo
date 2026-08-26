# ☀️ ToDoOrNotToDo

Eine wetterabhängige Aktivitäten-Planer Webanwendung, gebaut mit **Java**, **Spring Boot**, **Thymeleaf** und **PostgreSQL**. 

Die App gleicht hinterlegte Aktivitäten (Outdoor/Indoor, Temperaturspannen, Sonnenbedarf) mit aktuellen Wetterdaten ab und schlägt vor, welche Aktivitäten heute sinnvoll sind.

---

## 🛠️ Tech Stack

* **Backend:** Java 17, Spring Boot 3, Spring Data JPA, Hibernate
* **Frontend:** Thymeleaf, HTML5, JavaScript, Tailwind CSS
* **Datenbank:** PostgreSQL (Persistenz mit Spring Data JPA)
* **Build Tool:** Maven

---

## ⚡ Features

* **Wetter-Matching:** Automatische Auswertung von Aktivitäten basierend auf Temperatur- und Wetter-Bedingungen.
* **Aktivitäten-Verwaltung (CRUD):** Erstellen, Anschauen und Löschen von benutzerdefinierten Aktivitäten.
* **Interactive UI:** Flip-Karten-Design für Details und Regeln mit In-Browser-Bestätigung beim Löschen.

---

## 🚀 Quick Start (Lokal ausführen)

### 1. Repository klonen
```bash
git clone [https://github.com/simonlindner/ToDoOrNotToDo.git](https://github.com/simonlindner/ToDoOrNotToDo.git)
cd ToDoOrNotToDo
```

### 2. Datenbank konfigurieren

#### Stelle sicher, dass eine PostgreSQL-Instanz läuft, und passe die Zugangsdaten in ```src/main/resources/application.properties``` an
