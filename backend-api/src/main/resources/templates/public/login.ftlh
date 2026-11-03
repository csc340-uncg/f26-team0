<#assign pageTitle="Login - FitMatch">
  <#include "/shared/head.ftlh">
    <#include "/shared/nav.ftlh">
      <main class="section">
        <div class="form-container">
          <h1 class="text-center mb-4" style="font-weight: 700; font-size: 1.8rem;">Welcome Back to FitMatch</h1>
          <form action="/login" method="post">
            <label for="role">I am a</label>
            <select id="role" name="role" required>
              <option value="customer">Customer</option>
              <option value="trainer">Trainer</option>
            </select>
            <label for="email">Email Address</label>
            <input id="email" name="email" type="email" placeholder="you@example.com" required>
            <label for="password">Password</label>
            <input id="password" name="password" type="password" required>
            <button type="submit" class="btn btn-primary btn-large"
              style="width: 100%; margin-top: 1.5rem;">Login</button>
          </form>
          <p class="text-center" style="margin-top: 1.5rem;">Don't have an account? <a href="/register">Sign up</a></p>
        </div>
      </main>
      </div>
      </body>

      </html>