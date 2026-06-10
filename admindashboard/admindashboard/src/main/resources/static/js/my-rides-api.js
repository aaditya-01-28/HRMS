document.addEventListener("DOMContentLoaded", function() {
    // Sidebar Toggle
    const sidebar = document.getElementById("mr-sidebar");
    const toggleBtn = document.getElementById("mr-sidebar-toggle");

    if (toggleBtn && sidebar) {
        toggleBtn.addEventListener("click", function() {
            sidebar.classList.toggle("collapsed");
        });
    }

    // Modal logic
    const modal = document.getElementById("mr-map-modal");
    const openModalBtns = document.querySelectorAll(".open-map-modal");
    const closeModalBtn = document.querySelector(".close-modal");

    if (modal) {
        openModalBtns.forEach(btn => {
            btn.addEventListener("click", () => {
                modal.style.display = "flex";
            });
        });

        if (closeModalBtn) {
            closeModalBtn.addEventListener("click", () => {
                modal.style.display = "none";
            });
        }
    }
    
    // Helpdesk Accordion
    const accordions = document.querySelectorAll(".mr-accordion");
    accordions.forEach(acc => {
        acc.addEventListener("click", function() {
            this.classList.toggle("active");
            const panel = this.nextElementSibling;
            if (panel.style.maxHeight) {
                panel.style.maxHeight = null;
            } else {
                panel.style.maxHeight = panel.scrollHeight + "px";
            }
        });
    });
    
    // Dashboard Logic
    const dashDateElem = document.getElementById('dashboardTodayDate');
    if (dashDateElem) {
        const today = new Date();
        const fullDaysMap = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];
        const daysMap = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
        const monthsMap = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'];
        
        const dateNum = String(today.getDate()).padStart(2, '0');
        const monthName = monthsMap[today.getMonth()];
        const year = today.getFullYear();
        
        const fullDateStr = `${fullDaysMap[today.getDay()]}, ${dateNum} ${monthName} ${year}`;
        const shortDateStr = `${daysMap[today.getDay()]}, ${dateNum} ${monthName} ${year}`;
        
        const modalDateElem = document.getElementById('modalTodayDate');
        
        dashDateElem.innerHTML = `<i class="fa-regular fa-calendar"></i> ${fullDateStr}`;
        if (modalDateElem) modalDateElem.textContent = shortDateStr;
        
        fetch('/api/my-rides/bookings')
            .then(res => res.json())
            .then(data => {
                let todayBooking = null;
                if(Array.isArray(data)) {
                    todayBooking = data.find(b => b.fullDate === fullDateStr && b.status !== 'CANCELLED');
                }
                
                const loginElem = document.getElementById('dashLoginTime');
                const logoutElem = document.getElementById('dashLogoutTime');
                
                if(todayBooking) {
                    if(loginElem) loginElem.textContent = todayBooking.login || '10:00 AM';
                    if(logoutElem) logoutElem.textContent = todayBooking.logout || '07:00 PM';
                } else {
                    if(loginElem) loginElem.textContent = 'No Ride';
                    if(logoutElem) logoutElem.textContent = 'No Ride';
                }
            })
            .catch(err => console.error("Error fetching today's booking:", err));
    }
});
