# Game Collection App

![build](https://github.com/utkubilir/Game_Collection_App/actions/workflows/build.yml/badge.svg)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A fully functional desktop application built with **JavaFX** that lets users create, manage, and
track their personal video game libraries. Originally started as an academic group project, it has
been individually re-written from the ground up into a portfolio project.

## Features

### For Users
- **Personal catalog management** — add, edit, and delete games with rich details (genre, developer,
  publisher, platform, playtime, rating, status, tags, and more).
- **Search & advanced filtering** — free-text search plus dedicated filters by **status** and **genre**.
- **Cover art** — when a Steam app id is provided, the game's Steam header image is shown in the
  details panel.
- **Statistics dashboard** — a charts window (status distribution pie chart + games-per-genre bar
  chart) on top of the always-visible summary bar.
- **Data backup** — export the entire library to JSON and import it back (re-importing updates
  existing entries instead of creating duplicates).
- **Account** — change password and log out (return to the login screen) from the *Account* menu.
- **Quality-of-life** — double-click a row to edit, press <kbd>Delete</kbd> to remove, login with
  <kbd>Enter</kbd>, show/hide password, empty-state hints, input validation, and an app icon.

### For Admins
- **Role-based authorization** — *Admin* and *User* roles.
- **User management** — list all users with their role and registration date, promote/demote
  admins, and delete users along with all their data (`ON DELETE CASCADE`). You cannot demote or
  delete your own account.
- **Auditing** — view any user's game library or activity logs in a separate window. Logins,
  registrations, game add/edit/delete, role changes, imports/exports, and logouts are all logged.
- **System-wide game list** — see every game in the database, including who added it.

### Internationalization
The login and registration screens are localized (Turkish by default, English included). Switch with
a JVM flag: `mvn javafx:run -Dapp.locale=en`.

## Tech Stack
- **Language:** Java 21
- **UI:** JavaFX 21 + FXML (Scene Builder), JavaFX Charts
- **Database:** MySQL 8 with **HikariCP** connection pooling
- **Connectivity:** JDBC, accessed through a dedicated **DAO layer**
- **JSON:** Google Gson
- **Logging:** SLF4J + Logback (rolling file under `logs/`)
- **Build:** Apache Maven (with `javafx-maven-plugin` and `maven-shade-plugin`)
- **Tests:** JUnit 5
- **Pattern:** MVC (Model–View–Controller) with a DAO data-access layer

## Getting Started

### Prerequisites
- **JDK 21+**
- **Apache Maven 3.9+**
- A reachable **MySQL 8** server

> JavaFX, the MySQL connector, Gson, and HikariCP are resolved automatically by Maven — no manual
> SDK downloads or IDE path configuration required.

### 1. Clone
```bash
git clone https://github.com/utkubilir/Game_Collection_App.git
cd Game_Collection_App
```

### 2. Create the database
```bash
mysql -u <user> -p -e "CREATE DATABASE game_collection CHARACTER SET utf8mb4;"
mysql -u <user> -p game_collection < database/schema.sql
```
Optionally create an admin account by uncommenting the seed `INSERT` at the bottom of
[`database/schema.sql`](database/schema.sql).

### 3. Configure the connection
```bash
cp config.properties.example config.properties
```
Edit `config.properties` with your credentials (the file is git-ignored and never committed):
```properties
DB_HOST=localhost
DB_PORT=3306
DB_NAME=game_collection
DB_USER=your_username
DB_PASSWORD=your_password
```
You can also provide these as the environment variables `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`,
`DB_PASSWORD`, which take precedence over the file.

### 4. Run
```bash
mvn javafx:run
```

### Build a distributable
```bash
mvn clean package
```
This produces a self-contained runnable jar at `target/game-collection-app.jar` (it bundles JavaFX
for the build platform). Run it with:
```bash
java -jar target/game-collection-app.jar
```
To create a native installer (`.dmg` / `.msi` / `.deb`), use the JDK's
[`jpackage`](https://docs.oracle.com/en/java/javase/21/jpackage/) tool against that jar.

## Screenshots
_Add screenshots to `docs/screenshots/` and reference them here, e.g.:_

```markdown
![Login](docs/screenshots/login.png)
![Library](docs/screenshots/library.png)
![Statistics](docs/screenshots/statistics.png)
```

## Project Structure
```
src/main/java/
  App, Launcher          Entry points
  Controller/            JavaFX controllers (UI only)
  Dao/                   Data-access layer (all SQL + typed errors)
  Model/                 Plain data classes
  Util/                  Pooling, dialogs, i18n, theming, validation, logging facade
src/main/resources/
  Fxml/                  FXML views
  css/styles.css         Shared theme
  i18n/                  Message bundles (tr, en)
  images/                App icon
  logback.xml            Logging configuration
src/test/java/           JUnit tests
database/schema.sql      Database schema
.github/workflows/       CI (build) + release automation
pom.xml                  Maven build & dependencies
```

## Roadmap
- Replace plain-text passwords with BCrypt hashing.
- Move the remaining database operations off the UI thread (login already does).
- Cover art via the RAWG.io API (in addition to Steam header images).
- Fuller internationalization across every screen.

## License
Released under the [MIT License](LICENSE).
