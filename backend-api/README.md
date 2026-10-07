# FitMatch backend API

The API base path is `/api`. JSON request bodies are validated; validation errors
return `400`, missing resources `404`, and duplicate or invalid state transitions
`409`.

## Profile and discovery

| Method | Path | Purpose | User story |
|---|---|---|---|
| `POST` | `/api/customers` | Register a customer profile | [US-1](../docs/FitMatchSRS.md#us-1-register-and-manage-profile) |
| `GET` | `/api/customers/{customerId}` | Read a customer profile | [US-1](../docs/FitMatchSRS.md#us-1-register-and-manage-profile) |
| `PUT` | `/api/customers/{customerId}` | Update a customer profile | [US-1](../docs/FitMatchSRS.md#us-1-register-and-manage-profile) |
| `POST` | `/api/trainers` | Register a trainer profile | [US-5](../docs/FitMatchSRS.md#us-5-create-and-update-trainer-profile) |
| `GET` | `/api/trainers?category={goal}` | Browse trainers by specialty or published service category | [US-2](../docs/FitMatchSRS.md#us-2-browse-trainers-by-goal-category) |
| `GET` | `/api/trainers/{trainerId}` | Read a trainer profile | [US-5](../docs/FitMatchSRS.md#us-5-create-and-update-trainer-profile) |
| `PUT` | `/api/trainers/{trainerId}` | Update a trainer profile | [US-5](../docs/FitMatchSRS.md#us-5-create-and-update-trainer-profile) |
| `GET` | `/api/services?category={goal}` | Browse published training services by category | [US-2](../docs/FitMatchSRS.md#us-2-browse-trainers-by-goal-category), [US-6](../docs/FitMatchSRS.md#us-6-define-services-and-pricing) |

Passwords are accepted only by registration endpoints, hashed with BCrypt, and
never included in API responses. Authentication and authorization are not yet
implemented; IDs in paths are not proof of identity and these endpoints must not
be exposed to untrusted clients until access control is added.

## Services and availability

| Method | Path | Purpose | User story |
|---|---|---|---|
| `GET` | `/api/trainers/{trainerId}/services` | List that trainer's published services | [US-6](../docs/FitMatchSRS.md#us-6-define-services-and-pricing) |
| `POST` | `/api/trainers/{trainerId}/services` | Add a service; it is published by default | [US-6](../docs/FitMatchSRS.md#us-6-define-services-and-pricing) |
| `PUT` | `/api/trainers/{trainerId}/services/{serviceId}` | Update service details or status (`DRAFT`, `PUBLISHED`, `ARCHIVED`) | [US-6](../docs/FitMatchSRS.md#us-6-define-services-and-pricing) |
| `GET` | `/api/trainers/{trainerId}/timeslots?availableOnly=true` | Browse available time slots | [US-3](../docs/FitMatchSRS.md#us-3-book-a-training-session) |
| `POST` | `/api/trainers/{trainerId}/timeslots` | Add a future availability slot | [US-3](../docs/FitMatchSRS.md#us-3-book-a-training-session) |
| `PUT` | `/api/trainers/{trainerId}/timeslots/{timeslotId}` | Update an unused slot | [US-3](../docs/FitMatchSRS.md#us-3-book-a-training-session) |
| `DELETE` | `/api/trainers/{trainerId}/timeslots/{timeslotId}` | Delete an unused slot | [US-3](../docs/FitMatchSRS.md#us-3-book-a-training-session) |

Timeslot timestamps use ISO-8601 local date-time values, for example
`"2030-05-10T14:30:00"`. The end time must be later than the start time.

## Sessions and reviews

| Method | Path | Purpose | User story |
|---|---|---|---|
| `POST` | `/api/customers/{customerId}/sessions` | Book a published service into an available slot | [US-3](../docs/FitMatchSRS.md#us-3-book-a-training-session) |
| `GET` | `/api/customers/{customerId}/sessions` | View the customer's sessions/dashboard | [US-3](../docs/FitMatchSRS.md#us-3-book-a-training-session) |
| `PATCH` | `/api/customers/{customerId}/sessions/{sessionId}/cancel` | Cancel a booked session | [US-3](../docs/FitMatchSRS.md#us-3-book-a-training-session) |
| `PATCH` | `/api/customers/{customerId}/sessions/{sessionId}/reschedule` | Move a booked session to an available slot | [US-3](../docs/FitMatchSRS.md#us-3-book-a-training-session) |
| `PATCH` | `/api/customers/{customerId}/sessions/{sessionId}/notes` | Update progress notes | [US-3](../docs/FitMatchSRS.md#us-3-book-a-training-session), [US-8](../docs/FitMatchSRS.md#us-8-view-customer-statistics) |
| `GET` | `/api/trainers/{trainerId}/sessions` | View sessions for a trainer | [US-3](../docs/FitMatchSRS.md#us-3-book-a-training-session) |
| `PATCH` | `/api/trainers/{trainerId}/sessions/{sessionId}/complete` | Mark a booked session completed | [US-4](../docs/FitMatchSRS.md#us-4-write-a-review-after-a-session) |
| `POST` | `/api/customers/{customerId}/sessions/{sessionId}/review` | Review a completed session (one review per session) | [US-4](../docs/FitMatchSRS.md#us-4-write-a-review-after-a-session) |
| `GET` | `/api/trainers/{trainerId}/reviews` | View reviews received by a trainer | [US-7](../docs/FitMatchSRS.md#us-7-respond-to-reviews) |
| `PATCH` | `/api/trainers/{trainerId}/reviews/{reviewId}/reply` | Reply to a review | [US-7](../docs/FitMatchSRS.md#us-7-respond-to-reviews) |
| `GET` | `/api/trainers/{trainerId}/statistics` | View session, rating, and customer progress summaries | [US-8](../docs/FitMatchSRS.md#us-8-view-customer-statistics) |

Booking and rescheduling lock the selected slot while checking and updating
availability, preventing simultaneous requests from booking the same slot.