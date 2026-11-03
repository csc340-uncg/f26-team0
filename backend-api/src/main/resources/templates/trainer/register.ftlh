<#assign pageTitle="Trainer Registration - FitMatch">
<#include "/shared/head.ftlh">
<#include "/shared/nav.ftlh">
<main class="section">
  <div class="form-container">
    <h1 class="text-center">Become a FitMatch Trainer</h1>
    <form action="/provider/register" method="post" enctype="multipart/form-data">
      <label for="name">Full Name</label><input id="name" name="name" required maxlength="100">
      <label for="email">Email</label><input id="email" name="email" type="email" required maxlength="254">
      <label for="password">Password</label><input id="password" name="password" type="password" minlength="8" maxlength="72" required>
      <label for="biography">Biography</label><textarea id="biography" name="biography" maxlength="2000"></textarea>
      <label for="certifications">Certifications</label><textarea id="certifications" name="certifications" maxlength="2000"></textarea>
      <label for="yearsOfExperience">Years of Experience</label><input id="yearsOfExperience" name="yearsOfExperience" type="number" min="0" value="0" required>
      <label for="specialties">Specialties</label><input id="specialties" name="specialties" maxlength="1000">
      <label for="profilePicture">Profile Picture (optional)</label><input id="profilePicture" name="profilePicture" type="file" accept="image/jpeg,image/png,image/gif,image/webp">
      <button class="btn btn-primary btn-large" type="submit" style="width: 100%; margin-top: 1rem;">Create Trainer Account</button>
    </form>
    <p class="text-center" style="margin-top: 1rem;">Already registered? <a href="/login">Log in</a></p>
  </div>
</main>
</div>
</body>
</html>
