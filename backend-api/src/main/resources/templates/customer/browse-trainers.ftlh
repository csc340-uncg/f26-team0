<#assign pageTitle="Browse Trainers - FitMatch">
  <#include "/shared/head.ftlh">
    <#include "/shared/nav.ftlh">
      <section class="section" style="background: var(--primary-color); color: white;">
        <h1 style="font-size: 2rem; font-weight: 700; color: white;">Find Your Perfect Trainer</h1>
        <p style="font-size: 1.1rem; color: white;">Browse certified fitness professionals matched to your goals.</p>
      </section>
      <main class="section">
        <form action="/customers/browse-trainers" method="get" class="card"
          style="padding: 1.5rem; margin-bottom: 2rem;">
          <h2>Filter Trainers</h2>
          <div class="grid grid-2">
            <div><label for="category">Specialization</label><input id="category" name="category" value="${category}"
                placeholder="Strength, yoga, weight loss"></div>
            <div style="display: flex; align-items: flex-end;"><button class="btn btn-primary" type="submit">Apply
                Filters</button></div>
          </div>
        </form>
        <div class="grid grid-3">
          <#list trainers as card>
            <div class="trainer-card">
              <div class="trainer-image"><img src="/trainers/${card.trainer().id()}/picture"
                  alt="${card.trainer().name()}" style="width: 200px; height: 200px; object-fit: cover;"></div>
              <div class="trainer-info">
                <h2 class="trainer-name">${card.trainer().name()}</h2>
                <p class="trainer-specialty">${card.trainer().specialties()!"Personal training"}</p>
                <p class="trainer-bio">${card.trainer().biography()!"Certified fitness professional."}</p>
                <p>${card.trainer().yearsOfExperience()!0} years experience</p>
                <#list card.services() as service>
                  <div class="trainer-rate">${service.name()} - $${service.price()}/session</div>
                  <#else>
                    <p class="text-muted">No published services yet.</p>
                </#list>
                <a class="btn btn-outline" href="/customers/trainers/${card.trainer().id()}">View Profile</a>
              </div>
            </div>
            <#else>
              <div class="card">
                <div class="card-content">No trainers found. Try another specialization.</div>
              </div>
          </#list>
        </div>
      </main>
      </div>
      </body>

      </html>