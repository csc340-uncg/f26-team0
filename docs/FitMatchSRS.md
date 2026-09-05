
# Requirements – FitMatch

**Project Name:** FitMatch \
**Team:** Alice Beback - Provider, Joany Cache - Customer \
**Course:** CSC 340\
**Version:** 1.0\
**Date:** 2026-08-25

---

## 1. Overview
**Vision.** FitMatch is a personalized trainer‑matching platform designed to help adults find certified fitness professionals who align with their goals, schedule, and coaching preferences. The system supports customers seeking structured fitness or rehabilitation guidance, as well as providers (trainers and wellness coaches) who want to offer tailored training services.

**Glossary** Terms used in the project
- **Trainer:** The fitness professional who provides training services to customers.
- **Customer:** A person seeking fitness or rehabilitation guidance.
- **Profile:** A collection of information about a user, including personal details, fitness goals, and preferences.
- **Services:** The specific training or coaching offerings provided by a trainer.
- **Session:** A scheduled appointment between a customer and a trainer for training or coaching.

**Primary Users / Roles.**
- **Customer** - Find trainers aligned with goals and constraints.
- **Trainer** — Attract clients and manage services.
- **SysAdmin** — Maintain platform quality and security.

**Scope (this semester).**
- User profiles (customers & trainers)
- Search/browse trainers by goals
- Booking training sessions
- Basic progress tracking (text notes)
- Reviews and ratings

**Out of scope (deferred).**
- Nutrition plans
- Group classes or multi client sessions

> This document is **requirements‑level** and solution‑neutral; design decisions (UI layouts, API endpoints, schemas) are documented separately.

---

## 2. Functional Requirements (User Stories)

### 2.1 Customer Stories
- **US‑1 - Register & manage profile**

  _Story:_ As a customer, I want to create a fitness profile, so that trainers can understand my goals and constraints.

  _Acceptance:_
  ```gherkin
  Scenario: Register with valid credentials
    Given I am not registered
    When I provide valid registration details
    Then I should be successfully registered and logged in
  ```

- **US‑2 - Browse trainers by goal category**

  _Story:_ As a customer, I want to browse trainers by goal category so that I can quickly find relevant matches.

  _Acceptance:_
  ```gherkin
  Scenario: Browse trainers by goal category
    Given I am logged in as a customer
    When I select a goal category
    Then I should see a list of trainers who specialize in that category
  ```

- **US-3 - Book a training session**

  _Story:_ As a customer, I want to book a training session with a trainer so that I can receive personalized guidance.

  _Acceptance:_
  ```gherkin
  Scenario: Book a training session
    Given I am logged in as a customer
    When I select a trainer and choose an available time slot
    Then I should receive a confirmation of the booked session
  ```

- **US-4 - Write a review after a session**

    _Story:_ As a customer, I want to write a review after a session so that others can benefit from my experience.

    _Acceptance:_
  ```gherkin
    Scenario: Write a review after a session
      Given I have completed a training session with a trainer
      When I submit a review for that session
      Then the review should be saved and visible to other customers
  ```
