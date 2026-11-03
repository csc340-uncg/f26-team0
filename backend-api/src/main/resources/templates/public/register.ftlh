<#assign pageTitle="Sign Up - FitMatch">
<#include "/shared/head.ftlh">
<#include "/shared/nav.ftlh">
<main class="section">
  <div class="form-container">
    <h1 class="text-center mb-2" style="font-weight: 700; font-size: 1.8rem;">Join FitMatch</h1>
    <p class="text-center text-muted">Create your customer profile</p>
    <form action="/register" method="post">
      <label for="name">Full Name</label><input id="name" name="name" required maxlength="100">
      <label for="email">Email Address</label><input id="email" name="email" type="email" required maxlength="254">
      <label for="password">Password</label><input id="password" name="password" type="password" required minlength="8" maxlength="72">
      <label for="phoneNumber">Phone Number</label><input id="phoneNumber" name="phoneNumber" type="tel" maxlength="20">
      <div class="grid grid-2">
        <div><label for="currentWeight">Current Weight</label><input id="currentWeight" name="currentWeight" type="number" step="0.1" min="0.1" required></div>
        <div><label for="goalWeight">Goal Weight</label><input id="goalWeight" name="goalWeight" type="number" step="0.1" min="0.1" required></div>
      </div>
      <label for="fitnessLevel">Fitness Level</label>
      <select id="fitnessLevel" name="fitnessLevel"><option>Beginner</option><option>Intermediate</option><option>Advanced</option></select>
      <label for="fitnessGoals">Fitness Goals</label><textarea id="fitnessGoals" name="fitnessGoals" maxlength="2000"></textarea>
      <label for="injuriesOrHealthConcerns">Injuries or Health Concerns</label><textarea id="injuriesOrHealthConcerns" name="injuriesOrHealthConcerns" maxlength="2000"></textarea>
      <button type="submit" class="btn btn-primary btn-large" style="width: 100%; margin-top: 1.5rem;">Create Account</button>
    </form>
    <p class="text-center" style="margin-top: 1rem;">Are you a trainer? <a href="/provider/register">Sign up as a trainer</a></p>
    <p class="text-center" style="margin-top: 1.5rem;">Already have an account? <a href="/login">Login here</a></p>
  </div>
</main>
</div>
</body>
</html>
