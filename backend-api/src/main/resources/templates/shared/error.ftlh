<#assign pageTitle="Something went wrong - FitMatch">
<#include "/shared/head.ftlh">
<#include "/shared/nav.ftlh">
<main class="section">
  <div class="card">
    <div class="card-content">
      <h1>We could not complete that request</h1>
      <p>${errorMessage!"Please try again."}</p>
      <a class="btn btn-primary" href="/">Return home</a>
      <#if userRole?? && userRole == "customer">
        <a class="btn btn-outline" href="/customers/dashboard">Customer dashboard</a>
      <#elseif userRole?? && userRole == "trainer">
        <a class="btn btn-outline" href="/trainers/dashboard">Trainer dashboard</a>
      </#if>
    </div>
  </div>
</main>
</div>
</body>
</html>
