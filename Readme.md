# YogaBuddy – Backend

**YogaBuddy** is a personalized, AI‑powered yoga planning service built with Spring Boot. It generates 30‑day daily yoga routines tailored to each user’s goals and health issues, and lets users track their progress.

## Features

- **AI‑Generated Plans:** Generate personalized 30‑day yoga plans via Google Gemini (Vertex AI) using Spring AI.
- **User Management:** Register, log in, and manage users with JWT‑based authentication.
- **Refresh Tokens:** Secure long‑term sessions with refresh tokens.
- **Plan Persistence:** Save and retrieve generated plans in PostgreSQL.
- **Daily Tracking:** Mark individual days complete or incomplete and view progress history.
- **Role‑Based Access:** `ROLE_USER` and `ROLE_ADMIN` for fine‑grained authorization.

## Tech Stack

- **Backend:** Spring Boot 3.5.x, Java 21
- **AI Integration:** Spring AI (`ChatClient`) + Google Gemini (Vertex AI)
- **Security:** Spring Security, JWT (jjwt)
- **Database:** PostgreSQL, Spring Data JPA
- **Build & Packaging:** Maven
- **JSON Processing:** Jackson

## Requirements

- Java 21 or above
- Maven 3.6+
- PostgreSQL instance
- Google Cloud project with Vertex AI + Gemini enabled
- Service Account JSON key with **Vertex AI User** role

## Getting Started

### Clone the repository

```bash
git clone https://github.com/GouravBakshi/YogaBuddy-AI.git
cd YogaBuddy-AI
