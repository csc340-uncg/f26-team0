<#assign pageTitle="Manage Services - FitMatch">
<#include "/shared/head.ftlh">
<#include "/shared/nav.ftlh">
<main class="section">
  <div style="display: flex; justify-content: space-between; align-items: center;">
    <h1>Manage Your Services</h1><a class="btn btn-primary" href="/trainers/new-service">+ Add New Service</a>
  </div>
  <div class="grid grid-3">
    <#list services as service>
      <div class="card"><div class="card-content">
        <h2>${service.name()}</h2><p>${service.description()!"No description"}</p>
        <p><strong>Category:</strong> ${service.category()}</p><p><strong>Price:</strong> $${service.price()}</p>
        <p><strong>Status:</strong> ${service.status()}</p>
        <a class="btn btn-outline" href="/trainers/edit-service/${service.id()}">Edit</a>
      </div></div>
    <#else><div class="card"><div class="card-content">You have not created any services yet.</div></div></#list>
  </div>
  <section class="section section-light">
    <h2>Availability</h2>
    <p>Add future timeslots to let customers book your published services.</p>
    <form action="/trainers/timeslots" method="post" class="form-container">
      <label for="startTime">Start</label><input id="startTime" name="startTime" type="datetime-local" required>
      <label for="endTime">End</label><input id="endTime" name="endTime" type="datetime-local" required>
      <input type="hidden" name="isAvailable" value="true">
      <button class="btn btn-primary" type="submit">Add Availability</button>
    </form>
    <div class="grid grid-3" style="margin-top: 1rem;">
      <#list timeslots as timeslot>
        <div class="card"><div class="card-content">
          <p>${timeslot.startTime()} - ${timeslot.endTime()}</p>
          <p>${timeslot.isAvailable()?string("Available","Booked")}</p>
          <#if timeslot.isAvailable()>
            <form action="/trainers/timeslots/${timeslot.id()}/delete" method="post"><button class="btn btn-outline" type="submit">Remove</button></form>
          </#if>
        </div></div>
      <#else><p>No availability added.</p></#list>
    </div>
  </section>
</main>
</div>
</body>
</html>
