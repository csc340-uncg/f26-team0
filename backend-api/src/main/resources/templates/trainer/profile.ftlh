<#assign pageTitle="Trainer Profile - FitMatch">
  <#include "/shared/head.ftlh">
    <#include "/shared/nav.ftlh">
      <main class="section">
        <div class="trainer-card">
          <div class="trainer-image"><img src="/trainers/${trainer.id()}/picture" alt="${trainer.name()}"
              style="width: 200px; height: 200px; object-fit: cover;"></div>
          <div class="trainer-info">
            <h1 class="trainer-name">${trainer.name()}</h1>
            <p class="trainer-specialty">${trainer.specialties()!"Personal training"}</p>
            <p class="trainer-bio">${trainer.biography()!"Add a biography to help customers get to know you."}</p>
            <p><strong>Email:</strong> ${trainer.email()}</p>
            <p><strong>Certifications:</strong> ${trainer.certifications()!"Not listed"}</p>
            <p><strong>Experience:</strong> ${trainer.yearsOfExperience()!0} years</p>
            <a class="btn btn-primary" href="/trainers/edit-details">Edit Profile</a>
          </div>
        </div>
        <div class="grid grid-3" style="margin-top: 2rem;">
          <div class="stat-card">
            <div class="stat-number">${statistics.customerCount()}</div>
            <div class="stat-label">Clients</div>
          </div>
          <div class="stat-card">
            <div class="stat-number">${statistics.completedSessions()}</div>
            <div class="stat-label">Sessions Completed</div>
          </div>
          <div class="stat-card">
            <div class="stat-number">
              <#if statistics.averageRating()??>${statistics.averageRating()}<#else>New</#if>
            </div>
            <div class="stat-label">Average Rating</div>
          </div>
        </div>
      </main>
      </div>
      </body>

      </html>