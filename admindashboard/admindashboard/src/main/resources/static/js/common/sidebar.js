/* ==========================================
   Sidebar Active Link Detection
   ========================================== */
document.addEventListener('DOMContentLoaded', function () {

    var currentPath = window.location.pathname;
    var links = document.querySelectorAll('.sidebar-link[data-page]');

    links.forEach(function (link) {
        link.classList.remove('active');

        var pagePath = link.getAttribute('data-page');

        if (pagePath && currentPath === pagePath) {
            link.classList.add('active');
        }
    });

    /* ==========================================
       Sidebar Toggle Logic
       ========================================== */
    var toggleBtn = document.getElementById('sidebarToggle');
    var appLayout = document.querySelector('.app-layout');
    
    // Check localStorage for saved state
    if (localStorage.getItem('sidebar-collapsed') === 'true') {
        if (appLayout) appLayout.classList.add('sidebar-collapsed');
    }
    
    if (toggleBtn && appLayout) {
        toggleBtn.addEventListener('click', function() {
            appLayout.classList.toggle('sidebar-collapsed');
            localStorage.setItem('sidebar-collapsed', appLayout.classList.contains('sidebar-collapsed'));
        });
    }

});
