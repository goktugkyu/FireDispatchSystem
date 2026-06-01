# Fire Dispatch System

A web and Android-based emergency dispatch prototype for creating fire reports, notifying firefighters, and managing dispatch-related information.

This project was developed as a full-stack/mobile system connecting a React web application, an Android firefighter application, Supabase database services, and Firebase Cloud Messaging notifications.

> **Note:** The original Supabase database is no longer active, so this repository is provided as a portfolio/code showcase rather than a fully deployable live system.

---

## Repository Structure

```text
FireDispatchSystem/
├── fire-reporting-system/   # React web application for creating fire reports
├── android-app/             # Android firefighter application
├── .gitignore
├── package.json
└── README.md
```

---

## Project Overview

The system was designed around a simple emergency dispatch workflow:

1. A dispatcher submits a fire/emergency report through the web application.
2. The report is stored as a dispatch record in Supabase.
3. Firefighters using the Android app can view active dispatches.
4. Firebase Cloud Messaging is used to notify firefighters about new interventions.
5. Firefighters can open a dispatch popup and apply/respond to an intervention.
6. Related information such as firefighters, trucks, dispatch details, and application counts can be displayed in the mobile app.

---

## Web Application

The web application is located in:

```text
fire-reporting-system/
```

It provides a reporting interface for creating dispatch/fire reports and sending the relevant information to the backend database.

### Main responsibilities

* Create new emergency/fire reports
* Submit dispatch information to Supabase
* Provide a web-facing interface for dispatch creation
* Serve as the starting point of the notification flow

### Technologies

* React
* JavaScript / TypeScript
* Supabase
* Firebase-related integration

---

## Android Application

The Android application is located in:

```text
android-app/
```

It is designed for firefighters to log in, view dispatches, inspect details, receive notifications, and respond to interventions.

### Main features

* Firefighter login
* Dispatch list and dispatch detail screens
* Firefighter overview
* Truck overview
* User profile page
* Firebase notification handling
* Intervention popup screen
* Apply-to-dispatch/intervention workflow
* Password change and registration-related screens

### Technologies

* Java
* Android Studio
* XML layouts
* Firebase Cloud Messaging
* Supabase REST/database integration

---

## Backend and Services

The original system used:

* **Supabase** for authentication-related logic, database storage, and dispatch data
* **Firebase Cloud Messaging** for push notifications
* **Android SharedPreferences** for storing logged-in user/session-related information
* Optional hardware-related code for triggering a buzzer/ESP-style notification flow

The database is currently inactive, so credentials and live service configuration files are intentionally not included.

---

## Security and Credentials

Sensitive configuration files are excluded from this repository, including:

* `.env`
* Firebase service account files
* `google-services.json`
* local Android configuration files such as `local.properties`
* generated build files and dependency folders

This is intentional. The repository is meant to demonstrate the implementation and project structure without exposing private credentials or backend access.

---

## Current Status

This repository is kept as a portfolio version of the project.

Because the original Supabase backend is no longer active, the Android app may not progress beyond login/home-related screens without recreating the backend database and Firebase configuration.

The code still demonstrates the main implementation logic for:

* Web-based dispatch creation
* Android UI and navigation
* Supabase data retrieval/insertion
* Firebase notification handling
* Dispatch/intervention response flow

---


## Project Context

This project was developed as an emergency dispatch system prototype, combining web development, Android development, backend integration, database operations, and real-time notification workflows.

It demonstrates practical experience with full-stack/mobile system design, especially in connecting a web-based reporting interface with a mobile responder application.
