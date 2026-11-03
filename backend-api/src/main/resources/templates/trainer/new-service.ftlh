<#assign pageTitle="New Service - FitMatch">
<#include "/shared/head.ftlh">
<#include "/shared/nav.ftlh">
<main class="section">
  <div class="form-container">
    <h1>Create a Training Service</h1>
    <form action="/trainers/services" method="post">
      <label for="name">Service Name</label><input id="name" name="name" required maxlength="150">
      <label for="description">Description</label><textarea id="description" name="description" maxlength="2000"></textarea>
      <label for="category">Category</label><input id="category" name="category" required maxlength="100">
      <label for="price">Price per Session</label><input id="price" name="price" type="number" min="0" step="0.01" required>
      <label for="status">Status</label><select id="status" name="status"><option value="PUBLISHED">Published</option><option value="DRAFT">Draft</option></select>
      <button class="btn btn-primary" type="submit">Create Service</button>
      <a class="btn btn-outline" href="/trainers/manage-services">Cancel</a>
    </form>
  </div>
</main>
</div>
</body>
</html>
