<#assign pageTitle="FitMatch - Personal Trainers Matched to Your Goals">
  <#include "/shared/head.ftlh">
    <#include "/shared/nav.ftlh">
      <section class="hero">
        <div class="hero-content">
          <h1>Find Your Perfect Fitness Match</h1>
          <p>Connect with certified trainers matched to your goals, constraints, and preferences. Your personalized
            fitness journey starts here.</p>
          <div class="hero-buttons">
            <a href="/register" class="btn btn-light btn-large">I'm a Customer</a>
            <a href="/provider/register" class="btn btn-primary btn-large"
              style="background-color: rgba(255,255,255,0.2); border: 2px solid white;">I'm a Trainer</a>
          </div>
        </div>
      </section>
      <section class="section" id="features">
        <h2 class="text-center" style="font-size: 2.5rem; margin-bottom: 3rem; font-weight: 700;">Expert Trainers</h2>
        <div class="grid grid-3">
          <#list trainers as trainer>
            <div class="trainer-card">
              <div class="trainer-image">
                <img src="/trainers/${trainer.id()}/picture" alt="${trainer.name()}"
                  style="width: 200px; height: 200px; object-fit: cover;">
              </div>
              <div class="trainer-info">
                <h3 class="trainer-name">${trainer.name()}</h3>
                <p class="trainer-specialty">${trainer.specialties()!"Personal training"}</p>
                <div class="trainer-bio">${trainer.biography()!"Browse this trainer's services and availability."}</div>
                <a class="btn btn-outline" href="/customers/trainers/${trainer.id()}">View Profile</a>
              </div>
            </div>
            <#else>
              <div class="card">
                <div class="card-content">Trainer profiles will appear here after a trainer registers.</div>
              </div>
          </#list>
        </div>
      </section>
      <section class="section section-light" id="how-it-works">
        <h2 class="text-center" style="font-size: 2.5rem; margin-bottom: 3rem; font-weight: 700;">How It Works</h2>
        <div class="grid grid-3">
          <div class="card">
            <div class="card-content">
              <div style="font-size: 3rem; color: var(--primary-color);">1</div>
              <h3>Create Your Profile</h3>
              <p>Share your fitness goals, constraints, and preferences.</p>
            </div>
          </div>
          <div class="card">
            <div class="card-content">
              <div style="font-size: 3rem; color: var(--secondary-color);">2</div>
              <h3>Browse Trainers</h3>
              <p>Explore trainer profiles, experience, services, and availability.</p>
            </div>
          </div>
          <div class="card">
            <div class="card-content">
              <div style="font-size: 3rem; color: var(--success-color);">3</div>
              <h3>Book a Session</h3>
              <p>Choose a service and a time that works for you.</p>
            </div>
          </div>
        </div>
      </section>
      </div>
      </body>

      </html>