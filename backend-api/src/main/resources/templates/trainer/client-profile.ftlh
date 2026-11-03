<#assign pageTitle="Client Profile - FitMatch">
<#include "/shared/head.ftlh">
<#include "/shared/nav.ftlh">
<main class="section">
  <div class="dashboard-header">
    <h1>${customer.name()}</h1><p>${customer.email()} | ${customer.phoneNumber()!"No phone provided"}</p>
  </div>
  <div class="grid grid-2">
    <section class="card"><div class="card-content">
      <h2>Health &amp; Constraints</h2>
      <p><strong>Fitness level:</strong> ${customer.fitnessLevel()!"Not provided"}</p>
      <p><strong>Current weight:</strong> ${customer.currentWeight()!"Not provided"}</p>
      <p><strong>Goal weight:</strong> ${customer.goalWeight()!"Not provided"}</p>
      <p><strong>Goals:</strong> ${customer.fitnessGoals()!"Not provided"}</p>
      <p><strong>Health concerns:</strong> ${customer.injuriesOrHealthConcerns()!"None provided"}</p>
    </div></section>
    <section class="card"><div class="card-content">
      <h2>Recent Sessions</h2>
      <#list sessions as trainingSession>
        <div class="session-item"><strong>${trainingSession.trainingServiceName()}</strong> - ${trainingSession.status()}<p>${trainingSession.startTime()!"Not scheduled"}</p>
          <form action="/trainers/sessions/${trainingSession.id()}/notes" method="post"><label for="notes-${trainingSession.id()}">Training Notes</label><textarea id="notes-${trainingSession.id()}" name="notes" maxlength="2000">${trainingSession.notes()!''}</textarea><button class="btn btn-primary" type="submit">Save Notes</button></form>
        </div>
      </#list>
    </div></section>
  </div>
  <a class="btn btn-outline" href="/trainers/view-clients">Back to Clients</a>
</main>
</div>
</body>
</html>
