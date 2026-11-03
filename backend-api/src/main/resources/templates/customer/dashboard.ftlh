<#assign pageTitle="Customer Dashboard - FitMatch">
<#include "/shared/head.ftlh">
<#include "/shared/nav.ftlh">
<main class="section">
  <div class="dashboard-header">
    <div class="dashboard-title">Welcome back, ${customer.name()}!</div>
    <div class="dashboard-subtitle">Your fitness journey is ready for its next step.</div>
  </div>
  <h2 style="font-size: 1.8rem; font-weight: 700; margin: 1.5rem 0;">Upcoming Sessions</h2>
  <#list sessions as trainingSession>
    <#if trainingSession.status() == "BOOKED">
      <div class="session-item">
        <div class="session-header"><div class="session-title">${trainingSession.trainingServiceName()} with ${trainingSession.trainerName()}</div><span class="session-status status-upcoming">${trainingSession.status()}</span></div>
        <div class="session-details">
          <div class="detail-item"><span class="detail-label">Date &amp; Time:</span> ${trainingSession.startTime()!"To be scheduled"}</div>
          <div class="detail-item"><span class="detail-label">Location:</span> ${trainingSession.location()!"Not specified"}</div>
        </div>
        <div style="display: flex; gap: 1rem; margin-top: 1rem;">
          <a class="btn btn-primary" href="/customers/sessions/${trainingSession.id()}">View Details</a>
          <form action="/customers/sessions/${trainingSession.id()}/cancel" method="post"><button class="btn btn-outline" type="submit">Cancel Session</button></form>
        </div>
      </div>
    </#if>
  <#else>
    <div class="card"><div class="card-content">No sessions booked yet. Browse trainers to find your match.</div></div>
  </#list>
  <div class="grid grid-3" style="margin-top: 2rem;">
    <a href="/customers/browse-trainers" class="card" style="text-decoration: none; color: inherit; text-align: center;"><div class="card-content"><h3>Browse Trainers</h3><p>Find a coach for your goals.</p></div></a>
    <a href="/customers/profile" class="card" style="text-decoration: none; color: inherit; text-align: center;"><div class="card-content"><h3>My Profile</h3><p>Keep your health and fitness details current.</p></div></a>
    <a href="/customers/my-sessions" class="card" style="text-decoration: none; color: inherit; text-align: center;"><div class="card-content"><h3>My Sessions</h3><p>Review your bookings and completed sessions.</p></div></a>
  </div>
</main>
</div>
</body>
</html>
