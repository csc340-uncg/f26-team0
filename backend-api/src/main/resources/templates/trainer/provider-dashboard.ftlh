<#assign pageTitle="Provider Dashboard - FitMatch">
<#include "/shared/head.ftlh">
<#include "/shared/nav.ftlh">
<main class="section">
  <div class="dashboard-header">
    <div class="dashboard-title">Your Training Business</div>
    <div class="dashboard-subtitle">${trainer.name()} | ${trainer.specialties()!"Fitness Trainer"}</div>
  </div>
  <div class="dashboard-grid">
    <div class="stat-card"><div class="stat-number">${statistics.customerCount()}</div><div class="stat-label">Clients</div></div>
    <div class="stat-card"><div class="stat-number">${statistics.totalSessions()}</div><div class="stat-label">Total Sessions</div></div>
    <div class="stat-card"><div class="stat-number">${services?size}</div><div class="stat-label">Services</div></div>
    <div class="stat-card"><div class="stat-number"><#if statistics.averageRating()??>${statistics.averageRating()}/5<#else>New</#if></div><div class="stat-label">Rating</div></div>
  </div>
  <section class="section">
    <h2>Manage Services</h2>
    <p>Publish services and add open times so customers can book a session.</p>
    <a class="btn btn-primary" href="/trainers/manage-services">Open Service Manager</a>
    <a class="btn btn-outline" href="/trainers/new-service">Add a Service</a>
  </section>
  <section class="section section-light">
    <h2>Upcoming Sessions</h2>
    <#list sessions as trainingSession>
      <#if trainingSession.status() == "BOOKED">
        <div class="session-item"><div class="session-title">${trainingSession.trainingServiceName()} with ${trainingSession.customerName()}</div><div class="session-details">${trainingSession.startTime()!"Not scheduled"} | ${trainingSession.location()!"Not specified"}</div><a href="/trainers/clients/${trainingSession.customerId()}">View Client</a></div>
      </#if>
    <#else><p>No upcoming sessions.</p></#list>
  </section>
</main>
</div>
</body>
</html>
