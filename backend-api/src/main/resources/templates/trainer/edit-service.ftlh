<#assign pageTitle="Edit Service - FitMatch">
  <#include "/shared/head.ftlh">
    <#include "/shared/nav.ftlh">
      <main class="section">
        <div class="form-container">
          <h1>Edit Training Service</h1>
          <form action="/trainers/edit-service/${service.id()}" method="post">
            <label for="name">Service Name</label><input id="name" name="name" value="${service.name()}" required
              maxlength="150">
            <label for="description">Description</label><textarea id="description" name="description"
              maxlength="2000">${service.description()!''}</textarea>
            <label for="category">Category</label><input id="category" name="category" value="${service.category()}"
              required maxlength="100">
            <label for="price">Price per Session</label><input id="price" name="price" type="number" min="0" step="0.01"
              value="${service.price()}" required>
            <label for="status">Status</label><select id="status" name="status">
              <option value="PUBLISHED" <#if service.status()=="PUBLISHED">selected</#if>>Published</option>
              <option value="DRAFT" <#if service.status()=="DRAFT">selected</#if>>Draft</option>
              <option value="ARCHIVED" <#if service.status()=="ARCHIVED">selected</#if>>Archived</option>
            </select>
            <button class="btn btn-primary" type="submit">Save Changes</button>
            <a class="btn btn-outline" href="/trainers/manage-services">Cancel</a>
          </form>
        </div>
      </main>
      </div>
      </body>

      </html>