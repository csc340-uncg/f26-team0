package com.csc340.fitmatch.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DocumentationController {

  @GetMapping(value = "/api/doc", produces = MediaType.TEXT_MARKDOWN_VALUE)
  public String getMarkdownDoc() {
    return """

        # FitMatch backend API

        The API base path is `/api`. JSON request bodies are validated; validation errors
        return `400`, missing resources `404`, and duplicate or invalid state transitions
        `409`.

        ## Profile and discovery

        | Method | Path | Purpose |
        |---|---|---|
        | `POST` | `/api/customers` | Register a customer profile |
        | `GET` | `/api/customers/{customerId}` | Read a customer profile |
        | `PUT` | `/api/customers/{customerId}` | Update a customer profile |
        | `POST` | `/api/trainers` | Register a trainer profile |
        | `GET` | `/api/trainers?category={goal}` | Browse trainers by specialty or published service category |
        | `GET` | `/api/trainers/{trainerId}` | Read a trainer profile |
        | `PUT` | `/api/trainers/{trainerId}` | Update a trainer profile |
        | `GET` | `/api/services?category={goal}` | Browse published training services by category |

        Passwords are accepted only by registration endpoints, hashed with BCrypt, and
        never included in API responses. Authentication and authorization are not yet
        implemented; IDs in paths are not proof of identity and these endpoints must not
        be exposed to untrusted clients until access control is added.

        ## Services and availability

        | Method | Path | Purpose |
        |---|---|---|
        | `GET` | `/api/trainers/{trainerId}/services` | List that trainer's published services |
        | `POST` | `/api/trainers/{trainerId}/services` | Add a service; it is published by default |
        | `PUT` | `/api/trainers/{trainerId}/services/{serviceId}` | Update service details or status (`DRAFT`, `PUBLISHED`, `ARCHIVED`) |
        | `GET` | `/api/trainers/{trainerId}/timeslots?availableOnly=true` | Browse available time slots |
        | `POST` | `/api/trainers/{trainerId}/timeslots` | Add a future availability slot |
        | `PUT` | `/api/trainers/{trainerId}/timeslots/{timeslotId}` | Update an unused slot |
        | `DELETE` | `/api/trainers/{trainerId}/timeslots/{timeslotId}` | Delete an unused slot |

        Timeslot timestamps use ISO-8601 local date-time values, for example
        `"2030-05-10T14:30:00"`. The end time must be later than the start time.

        ## Sessions and reviews

        | Method | Path | Purpose |
        |---|---|---|
        | `POST` | `/api/customers/{customerId}/sessions` | Book a published service into an available slot |
        | `GET` | `/api/customers/{customerId}/sessions` | View the customer's sessions/dashboard |
        | `PATCH` | `/api/customers/{customerId}/sessions/{sessionId}/cancel` | Cancel a booked session |
        | `PATCH` | `/api/customers/{customerId}/sessions/{sessionId}/reschedule` | Move a booked session to an available slot |
        | `PATCH` | `/api/customers/{customerId}/sessions/{sessionId}/notes` | Update progress notes |
        | `GET` | `/api/trainers/{trainerId}/sessions` | View sessions for a trainer |
        | `PATCH` | `/api/trainers/{trainerId}/sessions/{sessionId}/complete` | Mark a booked session completed |
        | `POST` | `/api/customers/{customerId}/sessions/{sessionId}/review` | Review a completed session (one review per session) |
        | `GET` | `/api/trainers/{trainerId}/reviews` | View reviews received by a trainer |
        | `PATCH` | `/api/trainers/{trainerId}/reviews/{reviewId}/reply` | Reply to a review |
        | `GET` | `/api/trainers/{trainerId}/statistics` | View session, rating, and customer progress summaries |

        Booking and rescheduling lock the selected slot while checking and updating
        availability, preventing simultaneous requests from booking the same slot.

                """;
  }

}
