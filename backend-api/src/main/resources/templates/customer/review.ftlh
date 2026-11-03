<#assign pageTitle="Review Session - FitMatch">
<#include "/shared/head.ftlh">
<#include "/shared/nav.ftlh">
<main class="section">
  <div class="form-container">
    <h1>Review Your Session</h1>
    <p>${trainingSession.trainingServiceName()} with ${trainingSession.trainerName()}</p>
    <form action="/customers/sessions/${trainingSession.id()}/review" method="post">
      <label for="rating">Rating</label>
      <select id="rating" name="rating" required><option value="5">5 - Excellent</option><option value="4">4 - Great</option><option value="3">3 - Good</option><option value="2">2 - Fair</option><option value="1">1 - Poor</option></select>
      <label for="comments">Comments</label><textarea id="comments" name="comments" maxlength="2000"></textarea>
      <button class="btn btn-primary" type="submit">Submit Review</button>
      <a class="btn btn-outline" href="/customers/my-sessions?status=completed">Cancel</a>
    </form>
  </div>
</main>
</div>
</body>
</html>
