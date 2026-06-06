# Game Collection App

A fully functional desktop application built with **JavaFX** that lets users create, manage, and
track their personal video game libraries. Originally started as an academic group project, it has
been individually re-written from the ground up into a portfolio project.

## Features

### For Users
- **Personal catalog management** — add, edit, and delete games with rich details (genre, developer,
  publisher, platform, playtime, rating, status, tags, and more).
- **Modern login screen** — show/hide password, login with the Enter key, inline error feedback, and
  a loading indicator while the database responds.
- **Search & filter** — instantly search the library by title, genre, platform, or tags.
- **Data backup** — export the entire library to a JSON file and import it back later.

### For Admins
- **Role-based authorization** — two roles: *Admin* and *User*.
- **User management** — list all users, view registration dates, and delete users along with all
  their data (via `ON DELETE CASCADE`).
- **Auditing** — view any user's game library or activity logs in a separate window.
- **System-wide game list** — see every game in the database, including who added it.

## Tech Stack
- **Language:** Java 21
- **UI:** JavaFX 21 + FXML (Scene Builder)
- **Database:** MySQL 8
- **Connectivity:** JDBC
- **JSON:** Google Gson
- **Build:** Apache Maven
- **Pattern:** MVC (Model–View–Controller)

## Getting Started

### Prerequisites
- **JDK 21+** (the build targets Java 21)
- **Apache Maven 3.9+**
- A reachable **MySQL 8** server

> JavaFX, the MySQL connector, and Gson are resolved automatically by Maven — no manual SDK
> downloads or IDE path configuration required.

### 1. Clone
```bash
git clone https://github.com/utkubilir/Game_Collection_App.git
cd Game_Collection_App
```

### 2. Create the database
Create a schema in MySQL and load the tables:
```bash
mysql -u <user> -p -e "CREATE DATABASE game_collection CHARACTER SET utf8mb4;"
mysql -u <user> -p game_collection < database/schema.sql
```
Optionally create an admin account by uncommenting the seed `INSERT` at the bottom of
[`database/schema.sql`](database/schema.sql).

### 3. Configure the connection
Copy the example config and fill in your credentials:
```bash
cp config.properties.example config.properties
```
Then edit `config.properties`:
```properties
DB_HOST=localhost
DB_PORT=3306
DB_NAME=game_collection
DB_USER=your_username
DB_PASSWORD=your_password
```
`config.properties` is git-ignored and never committed. You can also provide these as the
environment variables `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD` (which take
precedence over the file).

### 4. Run
```bash
mvn javafx:run
```

To build a compiled artifact:
```bash
mvn clean package
```

## Project Structure
```
src/main/java/        Application code (App, Controller, Model, Util)
src/main/resources/   FXML views
database/schema.sql   Database schema
pom.xml               Maven build & dependencies
```

## Roadmap
- Replace plain-text passwords with BCrypt hashing.
- Run database operations off the UI thread (currently only login does).
- Integration with external APIs (Steam, RAWG.io) for cover art.
- A statistics and reporting dashboard.
- Advanced filtering and sorting options.
- Unit tests.

## License
Released under the [MIT License](LICENSE).
