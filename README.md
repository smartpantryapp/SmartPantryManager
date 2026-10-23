Smart Pantry Manager

Overview

Smart Pantry Manager is an Android application developed in Java to help users manage ingredients they already have at home and discover recipes they can prepare with those ingredients.

The application aims to reduce food waste and make meal planning easier by matching recipes against the user's available pantry ingredients.

Features

- Add, view, edit, and delete pantry ingredients.
- Store pantry information locally using SQLite.
- Validate ingredient names and quantities.
- Suggest recipes only when all required ingredients and quantities are available.
- View recipe ingredients and preparation instructions.
- Seed the database with sample recipes.
- Configure expiry alerts for ingredients approaching their expiry dates.
- Save pantry information between application sessions.

Technologies Used

- Java
- Android Studio
- Android XML layouts
- SQLite
- AndroidX libraries
- WorkManager for scheduled expiry checks
- Git and GitHub for version control

Project Structure

- "app/src/main/java/" - Java application code.
- "app/src/main/res/layout/" - XML screen layouts.
- "app/src/main/AndroidManifest.xml" - Android application configuration.
- "gradle/" - Gradle wrapper and related configuration.

Main Business Rule

A recipe is suggested only when the user has every required ingredient in sufficient quantities. Recipes that require missing ingredients or quantities the user does not have should not be suggested.

Setup

1. Install Android Studio.
2. Clone or download this repository.
3. Open the project in Android Studio.
4. Allow Gradle to sync and download the required dependencies.
5. Run the application on a compatible Android emulator or physical device.

Testing

Manual testing has covered ingredient creation, editing and deletion, input validation, local data persistence, recipe matching, recipe details, and expiry-alert settings.

Project Status

This project was developed as part of a Mobile App Development assessment. Features and documentation may be updated as development continues.