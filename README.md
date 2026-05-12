*README for JavaFX Desktop App*

# MindNest JavaFX Application

MindNest Desktop is a mental wellness desktop application built with **Java** and **JavaFX**.  
It connects to a shared MySQL database and works alongside the Symfony web application.

---

## Overview

The JavaFX application provides a desktop interface for core MindNest features such as therapy sessions, coaching plans, journals, quizzes, content browsing, and profile management.

It is designed to work with the same data source as the web platform so that actions performed in JavaFX can be reflected in Symfony and vice versa.

---

## Main Features

### Therapy Module
- View therapy sessions
- Book sessions
- Reschedule sessions
- Cancel sessions
- Load therapists from the database
- Display upcoming and past sessions
- Session reminder support

### Coaching Module
- Explore coaching plans
- Favorite and unfavorite plans
- Display top favorite plans
- Coach-specific and patient-specific behavior
- Role-based coaching dashboard

### Journal and Quiz Module
- Create journals
- Manage quizzes
- Add and manage quiz questions
- Take quizzes
- Navigate between journal and quiz modules

### Content Module
- Browse wellness content
- View guided materials and resources

### Profile Module
- Display user information
- Update profile data
- Role-aware profile interface
- Light and dark mode compatibility

### About Us
- Dedicated About Us page
- Sidebar navigation entry
- Module shortcuts
- Improved landing experience

---

## Roles

The application supports:
- PATIENT
- THERAPIST
- COACH
- ADMIN

Role handling is important because the interface changes depending on the logged-in user.

### Patient
- Browse content
- Manage therapy sessions
- Use journal and quiz
- Explore and favorite coaching plans
- View profile

### Therapist
- Appears in therapy booking data
- Participates in therapy-related workflows

### Coach
- Create and manage coaching plans
- View top liked plan behavior

### Admin
- Access dashboard and administrative features

---

## Tech Stack

- **Language:** Java
- **UI Framework:** JavaFX
- **FXML:** UI layout structure
- **CSS:** theme and visual styling
- **Database:** MySQL
- **Architecture Style:** controller/service/model based structure

---

## Project Structure

Important folders typically include:

- `src/main/java/controllers` → JavaFX controllers
- `src/main/java/services` → business logic and DB services
- `src/main/java/models` → domain models
- `src/main/java/utils` → shared utilities
- `src/main/resources/fxml` → FXML views
- `src/main/resources/css` → stylesheets
- `src/main/resources/images` → image assets

---

## Database Integration

The JavaFX app uses a **shared MySQL database** with the Symfony web application.

This required:
- role synchronization
- fixing schema mismatches
- aligning table and column usage
- ensuring both applications read/write compatible data

Shared entities include:
- users
- therapy sessions
- journals
- quizzes
- quiz questions
- coaching plans
- favorites
- notifications

---

## Features Improved During Integration

- added support for `COACH`
- removed outdated login CAPTCHA
- fixed role mismatch issues
- fixed therapy therapist loading from database
- fixed coaching favorites and top-liked logic
- synchronized journal and quiz behavior with Symfony
- improved UI consistency
- made About Us a standalone module
- improved theme behavior in light and dark mode

---

## Setup

### Requirements
- Java
- JavaFX
- MySQL
- Maven or IDE support for JavaFX project execution

### Run
1. Open the project in your IDE
2. Make sure the database connection is configured correctly
3. Ensure MySQL is running
4. Run the JavaFX application entry point

If using Maven:
bash
mvn clean javafx:run
