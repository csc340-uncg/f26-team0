<#assign pageTitle="My Clients - FitMatch">
  <#include "/shared/head.ftlh">
    <#include "/shared/nav.ftlh">
      <main class="section">
        <h1>My Clients</h1>
        <div class="grid grid-3">
          <#list customers as customer>
            <div class="card">
              <div class="card-content">
                <h2>${customer.customerName()}</h2>
                <p>${customer.totalSessions()} session(s) | ${customer.completedSessions()} completed</p>
                <p><strong>Goals:</strong> ${customer.fitnessGoals()!"Not provided"}</p>
                <a class="btn btn-primary" href="/trainers/clients/${customer.customerId()}">View Profile</a>
              </div>
            </div>
            <#else>
              <div class="card">
                <div class="card-content">Clients will appear after a session is booked.</div>
              </div>
          </#list>
        </div>
      </main>
      </div>
      </body>

      </html>