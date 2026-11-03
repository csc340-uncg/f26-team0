<#assign pageTitle="Trainer Dashboard - FitMatch">
<#include "/shared/head.ftlh">
<#include "/shared/nav.ftlh">
<main class="section">
  <div class="dashboard-header">
    <div class="dashboard-title">Welcome back, ${trainer.name()}!</div>
    <div class="dashboard-subtitle">Manage your services and upcoming training sessions.</div>
  </div>
  <div class="dashboard-grid">
    <div class="stat-card"><div class="stat-number">${statistics.customerCount()}</div><div class="stat-label">Total Clients</div></div>
    <div class="stat-card"><div class="stat-number">${statistics.completedSessions()}</div><div class="stat-label">Sessions Completed</div></div>
    <div class="stat-card"><div class="stat-number">${statistics.upcomingSessions()}</div><div class="stat-label">Upcoming Sessions</div></div>
    <div class="stat-card"><div class="stat-number"><#if statistics.averageRating()??>${statistics.averageRating()}/5<#else>New</#if></div><div class="stat-label">Your Rating</div></div>
  </div>
  <h2 style="font-size: 1.8rem; font-weight: 700; margin: 2rem 0 1.5rem;">Upcoming Sessions</h2>
  <#list sessions as trainingSession>
    <#if trainingSession.status() == "BOOKED">
      <div class="session-item">
        <div class="session-header"><div class="session-title">${trainingSession.trainingServiceName()} with ${trainingSession.customerName()}</div><span class="session-status status-upcoming">Confirmed</span></div>
        <div class="session-details">
          <div class="detail-item"><span class="detail-label">Date &amp; Time:</span> ${trainingSession.startTime()!"Not scheduled"}</div>
          <div class="detail-item"><span class="detail-label">Location:</span> ${trainingSession.location()!"Not specified"}</div>
          <div class="detail-item"><span class="detail-label">Client notes:</span> ${trainingSession.notes()!"None"}</div>
        </div>
        <div style="display: flex; gap: 1rem; margin-top: 1rem;">
          <a class="btn btn-primary" href="/trainers/clients/${trainingSession.customerId()}">View Client</a>
          <form action="/trainers/sessions/${trainingSession.id()}/notes" method="post">
            <input name="notes" value="${trainingSession.notes()!''}" maxlength="2000" aria-label="Session notes">
            <button class="btn btn-outline" type="submit">Save Notes</button>
          </form>
        </div>
      </div>
    </#if>
  <#else><div class="card"><div class="card-content">No upcoming sessions yet.</div></div></#list>
  <section class="section section-light">
    <h2 style="font-size: 1.8rem; font-weight: 700;">Recent Client Reviews</h2>
    <#list reviews as review>
      <div class="card" style="margin: 1rem 0;"><div class="card-content">
        <strong>${review.customerName()} - ${review.rating()}/5</strong><p>${review.comments()!"No comment."}</p>
        <#if review.replyText()??><p><strong>Your reply:</strong> ${review.replyText()}</p><#else>
          <form action="/trainers/reviews/${review.id()}/reply" method="post"><label for="reply-${review.id()}">Reply</label><textarea id="reply-${review.id()}" name="replyText" required maxlength="2000"></textarea><button class="btn btn-primary" type="submit">Reply</button></form>
        </#if>
      </div></div>
    <#else><p>No reviews yet.</p></#list>
  </section>
  <div class="grid grid-3" style="margin-top: 2rem;">
    <a class="card" href="/trainers/manage-services" style="text-decoration: none; color: inherit;"><div class="card-content"><h3>Manage Services</h3><p>${services?size} service(s)</p></div></a>
    <a class="card" href="/trainers/view-clients" style="text-decoration: none; color: inherit;"><div class="card-content"><h3>My Clients</h3><p>${statistics.customerCount()} active client(s)</p></div></a>
    <a class="card" href="/trainers/profile" style="text-decoration: none; color: inherit;"><div class="card-content"><h3>My Profile</h3><p>Update your public profile.</p></div></a>
  </div>
</main>
</div>
</body>
</html>
