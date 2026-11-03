<#assign pageTitle="Session Details - FitMatch">
<#include "/shared/head.ftlh">
<#include "/shared/nav.ftlh">
<main class="section">
  <h1>Session Details</h1>
  <div class="session-item">
    <div class="session-header"><div class="session-title">${trainingSession.trainingServiceName()} with ${trainingSession.trainerName()}</div><span class="session-status">${trainingSession.status()}</span></div>
    <div class="session-details">
      <div class="detail-item"><span class="detail-label">Date &amp; Time:</span> ${trainingSession.startTime()!"Not scheduled"}</div>
      <div class="detail-item"><span class="detail-label">Location:</span> ${trainingSession.location()!"Not specified"}</div>
      <div class="detail-item"><span class="detail-label">Level:</span> ${trainingSession.level()!"Not specified"}</div>
      <div class="detail-item"><span class="detail-label">Notes:</span> ${trainingSession.notes()!"None"}</div>
    </div>
  </div>
  <#if trainingSession.status() == "BOOKED">
    <section class="card" style="margin-top: 1.5rem;"><div class="card-content">
      <h2>Reschedule Session</h2>
      <#if timeslots?size gt 0>
        <form action="/customers/sessions/${trainingSession.id()}/reschedule" method="post">
          <label for="timeslotId">Choose an available time</label>
          <select id="timeslotId" name="timeslotId" required>
            <#list timeslots as timeslot><option value="${timeslot.id()}">${timeslot.startTime()} - ${timeslot.endTime()}</option></#list>
          </select>
          <button class="btn btn-primary" type="submit">Reschedule</button>
        </form>
      <#else><p>No alternate times are available right now.</p></#if>
      <form action="/customers/sessions/${trainingSession.id()}/cancel" method="post" style="margin-top: 1rem;"><button class="btn btn-outline" type="submit">Cancel Session</button></form>
    </div></section>
  </#if>
  <a class="btn btn-outline" href="/customers/my-sessions">Back to My Sessions</a>
</main>
</div>
</body>
</html>
