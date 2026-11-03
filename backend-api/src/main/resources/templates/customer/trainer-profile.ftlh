<#assign pageTitle="${trainer.name()} - FitMatch">
  <#include "/shared/head.ftlh">
    <#include "/shared/nav.ftlh">
      <main class="section">
        <div class="trainer-card">
          <div class="trainer-image"><img src="/trainers/${trainer.id()}/picture" alt="${trainer.name()}"
              style="width: 200px; height: 200px; object-fit: cover;"></div>
          <div class="trainer-info">
            <h1 class="trainer-name">${trainer.name()}</h1>
            <p class="trainer-specialty">${trainer.specialties()!"Personal training"}</p>
            <p class="trainer-bio">${trainer.biography()!"No biography added."}</p>
            <p><strong>Experience:</strong> ${trainer.yearsOfExperience()!0} years</p>
            <p><strong>Certifications:</strong> ${trainer.certifications()!"Not listed"}</p>
          </div>
        </div>
        <section class="section">
          <h2>Services &amp; Availability</h2>
          <#if userRole?? && userRole=="customer">
            <#if services?size gt 0 && timeslots?size gt 0>
              <form action="/customers/book" method="post" class="form-container" id="booking">
                <input type="hidden" name="trainerId" value="${trainer.id()}">
                <label for="trainingServiceId">Choose a service</label>
                <select id="trainingServiceId" name="trainingServiceId" required>
                  <#list services as service>
                    <option value="${service.id()}">${service.name()} - $${service.price()}</option>
                  </#list>
                </select>
                <label for="timeslotId">Choose a time</label>
                <select id="timeslotId" name="timeslotId" required>
                  <#list timeslots as timeslot>
                    <option value="${timeslot.id()}">${timeslot.startTime()} - ${timeslot.endTime()}</option>
                  </#list>
                </select>
                <label for="level">Fitness Level</label><input id="level" name="level" maxlength="50">
                <label for="location">Location</label><input id="location" name="location" maxlength="255">
                <label for="notes">Goals or notes for the trainer</label><textarea id="notes" name="notes"
                  maxlength="2000"></textarea>
                <button class="btn btn-primary" type="submit">Book Session</button>
              </form>
              <#else>
                <p>This trainer has no published services or open times yet.</p>
            </#if>
            <#else>
              <p><a href="/login">Log in as a customer</a> to book a session.</p>
          </#if>
          <div class="grid grid-3">
            <#list services as service>
              <div class="card">
                <div class="card-content">
                  <h3>${service.name()}</h3>
                  <p>${service.description()!"No description provided."}</p>
                  <p class="trainer-rate">$${service.price()} / session</p>
                </div>
              </div>
            </#list>
          </div>
        </section>
        <section class="section section-light">
          <h2>Client Reviews</h2>
          <#list reviews as review>
            <div class="card" style="margin-bottom: 1rem;">
              <div class="card-content"><strong>${review.customerName()}</strong> - ${review.rating()}/5<p>
                  ${review.comments()!"No comment."}</p>
                <#if review.replyText()??>
                  <p><strong>Trainer reply:</strong> ${review.replyText()}</p>
                </#if>
              </div>
            </div>
            <#else>
              <p>No reviews yet.</p>
          </#list>
        </section>
      </main>
      </div>
      </body>

      </html>