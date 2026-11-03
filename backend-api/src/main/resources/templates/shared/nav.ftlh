<nav>
  <div class="navbar">
    <a href="/" class="navbar-brand"><img src="/images/logo.png" width="120" height="92" alt="FitMatch"></a>
    <ul class="nav-links">
      <#if userRole?? && userRole == "customer">
        <li><a href="/customers/browse-trainers">Browse Trainers</a></li>
        <li><a href="/customers/my-sessions">My Sessions</a></li>
        <li><a href="/customers/profile">My Profile</a></li>
      <#elseif userRole?? && userRole == "trainer">
        <li><a href="/trainers/dashboard">Dashboard</a></li>
        <li><a href="/trainers/manage-services">My Services</a></li>
        <li><a href="/trainers/view-clients">My Clients</a></li>
        <li><a href="/trainers/profile">Profile</a></li>
      <#else>
        <li><a href="/#features">Features</a></li>
        <li><a href="/#how-it-works">How It Works</a></li>
        <li><a href="/login" class="btn btn-outline">Login</a></li>
        <li><a href="/register" class="btn btn-outline">Get Started</a></li>
      </#if>
      <#if userRole??>
        <li>
          <form action="/logout" method="post">
            <button type="submit" class="btn btn-outline">Logout</button>
          </form>
        </li>
      </#if>
    </ul>
  </div>
</nav>
