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
});
