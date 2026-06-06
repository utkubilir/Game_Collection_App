# Contributing

Thanks for your interest in improving Game Collection App!

## Getting set up
1. Install **JDK 21+** and **Maven 3.9+**.
2. Create the database from [`database/schema.sql`](database/schema.sql).
3. Copy `config.properties.example` to `config.properties` and fill in your credentials.
4. Run the app: `mvn javafx:run`

## Before opening a pull request
- Keep the existing code style (4-space indent; see [`.editorconfig`](.editorconfig)).
- Make sure the build is green: `mvn clean verify`.
- Add or update tests for any logic you change (`src/test/java`).
- Keep commits focused and write clear messages.
- Never commit secrets — `config.properties` is git-ignored for a reason.

## Reporting bugs / requesting features
Open an issue using the templates under `.github/ISSUE_TEMPLATE`. Include steps to
reproduce, expected vs. actual behavior, and your OS / Java version where relevant.
