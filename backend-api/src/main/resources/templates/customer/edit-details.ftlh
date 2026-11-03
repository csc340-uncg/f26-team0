<#assign pageTitle="Edit Profile - FitMatch">
  <#include "/shared/head.ftlh">
    <#include "/shared/nav.ftlh">
      <main class="section">
        <div class="form-container">
          <h1>Edit Personal Information</h1>
          <form action="/customers/edit-details" method="post">
            <label for="name">Full Name</label><input id="name" name="name" value="${customer.name()}" required
              maxlength="100">
            <label for="email">Email</label><input id="email" name="email" type="email" value="${customer.email()}"
              required maxlength="254">
            <label for="phoneNumber">Phone</label><input id="phoneNumber" name="phoneNumber"
              value="${customer.phoneNumber()!''}" maxlength="20">
            <div class="grid grid-2">
              <div><label for="currentWeight">Current Weight</label><input id="currentWeight" name="currentWeight"
                  type="number" step="0.1" min="0.1" value="${customer.currentWeight()!''}" required></div>
              <div><label for="goalWeight">Goal Weight</label><input id="goalWeight" name="goalWeight" type="number"
                  step="0.1" min="0.1" value="${customer.goalWeight()!''}" required></div>
            </div>
            <label for="fitnessLevel">Fitness Level</label><input id="fitnessLevel" name="fitnessLevel"
              value="${customer.fitnessLevel()!''}" maxlength="50">
            <label for="fitnessGoals">Fitness Goals</label><textarea id="fitnessGoals" name="fitnessGoals"
              maxlength="2000">${customer.fitnessGoals()!''}</textarea>
            <label for="injuriesOrHealthConcerns">Injuries or Health Concerns</label><textarea
              id="injuriesOrHealthConcerns" name="injuriesOrHealthConcerns"
              maxlength="2000">${customer.injuriesOrHealthConcerns()!''}</textarea>
            <button class="btn btn-primary" type="submit">Save Changes</button>
            <a class="btn btn-outline" href="/customers/profile">Cancel</a>
          </form>
        </div>
      </main>
      </div>
      </body>

      </html>