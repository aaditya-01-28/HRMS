document.addEventListener("DOMContentLoaded", () => {
    const pageId = document.body.dataset.pageId;

    if (pageId === "dashboard") {
        fetchDashboardData();
    } else if (pageId === "explore-courses") {
        fetchExploreCourses();
    } else if (pageId === "course-details") {
        fetchCourseDetails();
    } else if (pageId === "leaderboard") {
        fetchLeaderboard();
    } else if (pageId === "my-learnings") {
        fetchMyLearnings();
    }
});

// GLOBAL VIDEO PLAYER ROUTING
window.playVideo = function() {
    window.location.href = '/learning/course-player';
};

function fetchDashboardData() {
    fetch("/api/learning/dashboard")
        .then(res => res.json())
        .then(data => {
            // Combine Events and Training, limit to 3 items
            const eventsContainer = document.getElementById("eventsContainer");
            eventsContainer.innerHTML = "";
            
            const combinedEvents = [
                ...data.upcomingEvents.map(e => ({...e, isTraining: false})),
                ...data.upcomingTraining.map(t => ({...t, isTraining: true}))
            ].slice(0, 3); // Limit to 3 to prevent squishing other columns

            combinedEvents.forEach(item => {
                if (!item.isTraining) {
                    const badgeClass = item.type === 'LIVE' ? 'live' : 'upcoming';
                    eventsContainer.innerHTML += `
                        <div class="event-card">
                            <div class="event-icon-wrapper">
                                <i class="fa-solid fa-bullhorn fa-2x" style="color:var(--primary-blue)"></i>
                                <span class="badge ${badgeClass}">${item.type}</span>
                            </div>
                            <h4 class="event-title">${item.title}</h4>
                            <p class="event-date">${item.date}</p>
                            <button class="btn-outline">Register Now</button>
                        </div>
                    `;
                } else {
                    eventsContainer.innerHTML += `
                        <div class="event-card">
                            <div class="event-icon-wrapper">
                                <i class="fa-solid fa-chalkboard-user fa-2x" style="color:var(--primary-blue)"></i>
                                <span class="badge upcoming">TRAINING</span>
                            </div>
                            <h4 class="event-title">${item.title}</h4>
                            <p class="event-date">${item.date}</p>
                            <button class="btn-outline">Remind Me</button>
                        </div>
                    `;
                }
            });

            // Populate Recommended
            const recommendedContainer = document.getElementById("recommendedContainer");
            recommendedContainer.innerHTML = "";
            data.recommended.forEach(item => {
                recommendedContainer.innerHTML += `
                    <li>
                        <div class="item-name">
                            <i class="fa-brands fa-java"></i>
                            <span>${item}</span>
                        </div>
                        <i class="fa-solid fa-chevron-right" style="color:var(--text-muted); cursor:pointer;"></i>
                    </li>
                `;
            });

            // Populate Continue Learning
            const continueContainer = document.getElementById("continueContainer");
            continueContainer.innerHTML = "";
            data.continueLearning.forEach(item => {
                const colorClass = item.progress > 50 ? 'green' : '';
                continueContainer.innerHTML += `
                    <div class="progress-item">
                        <div class="progress-info">
                            <img src="https://img.youtube.com/vi/vtPkZShrvXQ/maxresdefault.jpg" style="width: 50px; height: 35px; border-radius: 6px; object-fit: cover;" alt="Thumbnail">
                            <span>${item.courseName}</span>
                        </div>
                        <div class="progress-bar-wrapper">
                            <div class="progress-track">
                                <div class="progress-fill ${colorClass}" style="width: ${item.progress}%"></div>
                            </div>
                            <span class="progress-text">${item.progress}%</span>
                        </div>
                        <button class="btn-outline" onclick="window.playVideo()">Continue</button>
                    </div>
                `;
            });

            // Populate Explore preview
            const explorePreview = document.getElementById("explorePreview");
            explorePreview.innerHTML = "";
            data.exploreCourses.forEach(course => {
                explorePreview.innerHTML += `
                    <div class="course-card">
                        <div class="course-card-header">
                            <div class="course-icon"><i class="fa-solid fa-code"></i></div>
                            <div class="course-info">
                                <h4>${course.title}</h4>
                            </div>
                        </div>
                        <div class="course-meta">
                            <i class="fa-solid fa-star"></i>
                            <span>${course.rating}</span>
                        </div>
                        <div class="course-meta" style="margin-bottom:0;">
                            <span>${course.level}</span>
                        </div>
                        <div style="margin-top:15px; text-align:center;">
                            <button class="btn-primary" style="width:100%; border-radius:20px;" onclick="location.href='/learning/course-details'">View Details</button>
                        </div>
                    </div>
                `;
            });

            // Populate Leaderboard preview
            const leaderboardPreview = document.getElementById("leaderboardPreview");
            leaderboardPreview.innerHTML = "";
            data.leaderboard.forEach(user => {
                leaderboardPreview.innerHTML += `
                    <div class="leaderboard-item">
                        <div class="rank-badge rank-${user.rank}">${user.rank}</div>
                        <div class="user-snippet">
                            <img src="/images/avatar.png" alt="Avatar" onerror="this.src='https://ui-avatars.com/api/?name=${user.name}'">
                            <div>
                                <h4 style="margin:0; font-size:14px;">${user.name}</h4>
                                <span style="font-size:12px; color:var(--text-muted);">${user.points} PTS</span>
                            </div>
                        </div>
                        <img src="/images/avatar.png" style="width:24px;height:24px;border-radius:50%;" onerror="this.src='https://ui-avatars.com/api/?name=${user.name}'">
                    </div>
                `;
            });
        });
}

function fetchExploreCourses() {
    fetch("/api/learning/courses")
        .then(res => res.json())
        .then(data => {
            const list = document.getElementById("exploreList");
            list.innerHTML = "";
            document.getElementById("resultsCount").innerText = `Showing ${data.length} results`;

            data.forEach(course => {
                list.innerHTML += `
                    <div class="explore-card">
                        <div class="course-header-info">
                            <div class="course-icon" style="width:60px; height:60px; font-size:30px;"><i class="fa-brands fa-react"></i></div>
                            <div>
                                <h3 style="margin:0 0 5px 0;">${course.title}</h3>
                                <p style="margin:0; color:var(--text-muted);">By ${course.author}</p>
                                <div class="course-meta" style="margin-top:10px; margin-bottom:0;">
                                    <i class="fa-solid fa-star"></i>
                                    <span>${course.rating} • ${course.level} • ${course.duration}</span>
                                </div>
                            </div>
                        </div>
                        <div class="course-actions">
                            <button class="btn-outline" style="padding: 10px 30px;" onclick="location.href='/learning/course-details'">View Details</button>
                            <button class="btn-secondary"><i class="fa-regular fa-bookmark"></i> Save Course</button>
                        </div>
                    </div>
                `;
            });
        });
}

function fetchCourseDetails() {
    fetch("/api/learning/courses/1")
        .then(res => res.json())
        .then(data => {
            document.getElementById("courseTitle").innerText = data.title;
            document.getElementById("courseAuthor").innerText = "By " + data.author;
            document.getElementById("courseRating").innerHTML = `<i class="fa-solid fa-star"></i> ${data.rating} • ${data.duration}`;
            document.getElementById("courseLevel").innerText = data.level;
            document.getElementById("courseCategory").innerText = data.category;
            document.getElementById("courseDesc").innerText = data.description;

            const modulesList = document.getElementById("modulesList");
            modulesList.innerHTML = "";
            data.modules.forEach((mod, index) => {
                modulesList.innerHTML += `
                    <div class="module-item">
                        <span>${index + 1}. ${mod}</span>
                        <i class="fa-solid fa-chevron-right"></i>
                    </div>
                `;
            });
        });
}

function fetchLeaderboard() {
    fetch("/api/learning/leaderboard")
        .then(res => res.json())
        .then(data => {
            const list = document.getElementById("leaderboardList");
            list.innerHTML = "";
            data.forEach(user => {
                list.innerHTML += `
                    <div class="leaderboard-item ${user.isCurrentUser ? 'current-user' : ''}" style="margin-bottom: 10px; padding: 15px;">
                        <div style="display:flex; align-items:center; width:20%;">
                            <div class="rank-badge rank-${user.rank}" style="margin-right:20px;">${user.rank}</div>
                        </div>
                        <div class="user-snippet" style="width:60%;">
                            <img src="/images/avatar.png" alt="Avatar" onerror="this.src='https://ui-avatars.com/api/?name=${user.name}'">
                            <h4 style="margin:0; font-size:15px; ${user.isCurrentUser ? 'text-decoration: underline; color:var(--primary-blue);' : ''}">${user.name}</h4>
                        </div>
                        <div class="points" style="width:20%; text-align:right;">
                            ${user.points} PTS
                        </div>
                    </div>
                `;
            });
        });
}

function fetchMyLearnings() {
    fetch("/api/learning/my-learnings")
        .then(res => res.json())
        .then(data => {
            const ongoingContainer = document.getElementById("ongoingContainer");
            ongoingContainer.innerHTML = "";
            document.getElementById("ongoingCount").innerText = `Ongoing Courses (${data.ongoing.length})`;

            data.ongoing.forEach((course, i) => {
                const gradientClass = i === 1 ? 'blue-gradient' : '';
                ongoingContainer.innerHTML += `
                    <div class="ongoing-card">
                        <div class="ongoing-left">
                            <div class="ongoing-banner" style="background-image: url('https://img.youtube.com/vi/vtPkZShrvXQ/maxresdefault.jpg'); background-size: cover; background-position: center; border: none;">
                                <span class="banner-tag" style="background: rgba(0,0,0,0.6); padding: 4px 8px; border-radius: 4px; font-size: 12px;"><i class="fa-solid fa-fire"></i> ${course.tags}</span>
                            </div>
                            <div class="ongoing-details">
                                <h3>${course.title}</h3>
                                <p>${course.level} • By ${course.author}</p>
                                <div class="progress-track" style="margin-top:15px;">
                                    <div class="progress-fill green" style="width: ${course.progress}%"></div>
                                </div>
                                <div class="ongoing-progress-text">
                                    <span>${course.completedModules} out of ${course.totalModules} Modules Completed</span>
                                    <span style="font-weight:bold; color:var(--success)">${course.progress}%</span>
                                </div>
                            </div>
                        </div>
                        <button class="btn-outline" style="padding: 10px 30px;" onclick="window.playVideo()">Continue</button>
                    </div>
                `;
            });

            const completedContainer = document.getElementById("completedContainer");
            completedContainer.innerHTML = "";
            document.getElementById("completedCount").innerText = `Completed Courses (${data.completed.length})`;

            data.completed.forEach((course, i) => {
                const highlightClass = i === 2 ? 'highlight' : '';
                completedContainer.innerHTML += `
                    <div class="completed-card ${highlightClass}">
                        <div class="completed-info">
                            <div class="completed-icon">
                                <i class="fa-brands fa-react"></i>
                            </div>
                            <div>
                                <h3 style="margin:0 0 5px 0; font-size:16px;">${course.title}</h3>
                                <p style="margin:0; font-size:13px; color:var(--text-muted);">Completed on ${course.completionDate}</p>
                            </div>
                        </div>
                        <button class="btn-outline" style="padding: 8px 20px;">View Certificate</button>
                    </div>
                `;
            });
        });
}

function switchTab(tabId) {
    document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
    document.querySelectorAll('.tab-content').forEach(tc => tc.style.display = 'none');
    
    event.target.classList.add('active');
    document.getElementById(tabId).style.display = 'block';
}

function toggleFaq(headerElement) {
    const item = headerElement.parentElement;
    item.classList.toggle('active');
}
