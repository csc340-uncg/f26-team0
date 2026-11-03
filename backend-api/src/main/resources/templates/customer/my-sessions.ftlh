<#assign pageTitle="My Training Sessions - FitMatch">
  <#include "/shared/head.ftlh">
    <#include "/shared/nav.ftlh">
      <main class="section">
        <h1>My Training Sessions</h1>
        <div style="display: flex; gap: 1rem; margin: 1.5rem 0;">
          <a class="btn <#if status == 'upcoming'>btn-primary<#else>btn-outline</#if>"
            href="/customers/my-sessions?status=upcoming">Upcoming</a>
          <a class="btn <#if status == 'completed'>btn-primary<#else>btn-outline</#if>"
            href="/customers/my-sessions?status=completed">Completed</a>
          <a class="btn <#if status == 'cancelled'>btn-primary<#else>btn-outline</#if>"
            href="/customers/my-sessions?status=cancelled">Cancelled</a>
        </div>
        <#assign shown=0>
          <#list sessions as trainingSession>
            <#if (status=="upcoming" && trainingSession.status()=="BOOKED" ) || (status=="completed" &&
              trainingSession.status()=="COMPLETED" ) || (status=="cancelled" && trainingSession.status()=="CANCELLED"
              )>
              <#assign shown=shown + 1>
                <div class="session-item">
                  <div class="session-header">
                    <div class="session-title">${trainingSession.trainingServiceName()} with
                      ${trainingSession.trainerName()}</div><span
                      class="session-status">${trainingSession.status()}</span>
                  </div>
                  <div class="session-details">
                    <div class="detail-item"><span class="detail-label">Date &amp; Time:</span>
                      ${trainingSession.startTime()!"Not scheduled"}</div>
                    <div class="detail-item"><span class="detail-label">Location:</span>
                      ${trainingSession.location()!"Not specified"}</div>
                  </div>
                  <div style="display: flex; gap: 1rem; margin-top: 1rem;">
                    <a class="btn btn-primary" href="/customers/sessions/${trainingSession.id()}">View Details</a>
                    <#if status=="upcoming">
                      <form action="/customers/sessions/${trainingSession.id()}/cancel" method="post"><button
                          class="btn btn-outline" type="submit">Cancel</button></form>
                      <#elseif status=="completed">
                        <#assign reviewed=false>
                          <#list reviews as review>
                            <#if review.trainingSessionId()==trainingSession.id()>
                              <#assign reviewed=true>
                            </#if>
                          </#list>
                          <#if reviewed><span class="btn btn-outline">Review submitted</span>
                            <#else><a class="btn btn-outline"
                                href="/customers/sessions/${trainingSession.id()}/review">Leave a Review</a>
                          </#if>
                    </#if>
                  </div>
                </div>
            </#if>
          </#list>
          <#if shown==0>
            <div class="card">
              <div class="card-content">No ${status} sessions to show.</div>
            </div>
          </#if>
      </main>
      </div>
      </body>

      </html>