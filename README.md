# Game_Collection_App
Game Catalog Desktop Application
This project is a fully functional desktop application developed with JavaFX that allows users to create, manage, and track their personal video game libraries. Originally started as an academic group project, it has been individually re-written and developed from the ground up into an industry-standard portfolio project.

Key Features
For Users
Personal Catalog Management: Add, edit, and delete games with comprehensive details (genre, developer, platform, playtime, rating, etc.).
Advanced Login Screen: Modern UX features such as show/hide password, login with the Enter key, inline error feedback, and a loading indicator while waiting for the database response.
Dynamic Search and Filtering: Instantly search the library for games by title, genre, platform, or tags.
Data Backup: Export the entire game library to a JSON file and import it back later.

For Admins
Role-Based Authorization: Two distinct roles, "Admin" and "User."
User Management Panel: List all users, view their registration dates, and permanently delete users (along with all their associated data via CASCADE).
User Auditing: View any user's game library or activity logs in a separate window.
System-Wide Game List: View every game in the database, including information on which user added each entry.

Tech Stack
Language: Java
UI Framework: JavaFX, FXML, Scene Builder
Database: MySQL
Database Connectivity: JDBC (Java Database Connectivity)
JSON Processing: Google Gson
Design Pattern: MVC (Model-View-Controller)


Project Status
This project is a living work and is under active development. Planned features for the future include:
Integration with external APIs (e.g., Steam, RAWG.io) for game cover art.
A statistics and reporting dashboard.
Advanced filtering and sorting options.
