<#assign pageTitle="Edit Trainer Profile - FitMatch">
  <#include "/shared/head.ftlh">
    <#include "/shared/nav.ftlh">
      <main class="section">
        <div class="form-container">
          <h1>Edit Trainer Profile</h1>
          <form action="/trainers/edit-details" method="post" enctype="multipart/form-data">
            <label for="name">Full Name</label><input id="name" name="name" value="${trainer.name()}" required
              maxlength="100">
            <label for="email">Email</label><input id="email" name="email" type="email" value="${trainer.email()}"
              required maxlength="254">
            <label for="biography">Biography</label><textarea id="biography" name="biography"
              maxlength="2000">${trainer.biography()!''}</textarea>
            <label for="certifications">Certifications</label><textarea id="certifications" name="certifications"
              maxlength="2000">${trainer.certifications()!''}</textarea>
            <label for="yearsOfExperience">Years of Experience</label><input id="yearsOfExperience"
              name="yearsOfExperience" type="number" min="0" value="${trainer.yearsOfExperience()!0}" required>
            <label for="specialties">Specialties</label><input id="specialties" name="specialties"
              value="${trainer.specialties()!''}" maxlength="1000">
            <label for="profilePicture">Change Profile Picture (optional)</label><input id="profilePicture"
              name="profilePicture" type="file" accept="image/jpeg,image/png,image/gif,image/webp">
            <button class="btn btn-primary" type="submit">Save Changes</button>
            <a class="btn btn-outline" href="/trainers/profile">Cancel</a>
          </form>
        </div>
      </main>
      </div>
      </body>

      </html>