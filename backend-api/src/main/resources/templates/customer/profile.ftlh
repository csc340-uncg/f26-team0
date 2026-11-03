<#assign pageTitle="My Profile - FitMatch">
<#include "/shared/head.ftlh">
<#include "/shared/nav.ftlh">
<main class="section">
  <div class="dashboard-header">
    <h1>My Profile</h1>
    <p>${customer.name()} | ${customer.email()}</p>
    <a class="btn btn-primary" href="/customers/edit-details">Edit Personal Info</a>
  </div>
  <div class="grid grid-2">
    <section class="card"><div class="card-content">
      <h2>Personal Information</h2>
      <p><strong>Phone:</strong> ${customer.phoneNumber()!"Not provided"}</p>
      <p><strong>Account:</strong> ${customer.accountStatus()}</p>
      <p><strong>Fitness level:</strong> ${customer.fitnessLevel()!"Not set"}</p>
    </div></section>
    <section class="card"><div class="card-content">
      <h2>Health Constraints</h2>
      <p><strong>Current weight:</strong> ${customer.currentWeight()!"Not set"}</p>
      <p><strong>Goal weight:</strong> ${customer.goalWeight()!"Not set"}</p>
      <p><strong>Goals:</strong> ${customer.fitnessGoals()!"Not set"}</p>
      <p><strong>Injuries or health concerns:</strong> ${customer.injuriesOrHealthConcerns()!"None provided"}</p>
    </div></section>
  </div>
</main>
</div>
</body>
</html>
