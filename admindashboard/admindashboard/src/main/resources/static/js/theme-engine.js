// === UNIVERSAL THEME ENGINE (State-Based Fix) ===

// 1. Apply theme immediately on load
const savedTheme = localStorage.getItem('whitecircle-theme') || 'light';
document.documentElement.setAttribute('data-bs-theme', savedTheme);

// 2. Updated Function: Now it's "Double-Trigger Proof"
function toggleDarkMode() {
    const toggleCheckbox = document.getElementById('darkModeToggle');
    let newTheme;

    if (toggleCheckbox) {
        // IMPORTANT: We look at the checkbox state, not the current theme.
        // If it's checked, it's dark. If not, it's light.
        // Even if this runs 10 times, the result is the same!
        newTheme = toggleCheckbox.checked ? 'dark' : 'light';
    } else {
        // Fallback for pages without a checkbox (e.g., a simple button)
        const currentTheme = document.documentElement.getAttribute('data-bs-theme');
        newTheme = currentTheme === 'dark' ? 'light' : 'dark';
    }

    document.documentElement.setAttribute('data-bs-theme', newTheme);
    localStorage.setItem('whitecircle-theme', newTheme);

    // Sync any other UI elements if needed
    updateToggleUI(newTheme);
}

function updateToggleUI(theme) {
    const toggleCheckbox = document.getElementById('darkModeToggle');
    if (toggleCheckbox) {
        toggleCheckbox.checked = (theme === 'dark');
    }
}

// 3. Re-enable this block! It handles the Admin Dashboard.
document.addEventListener('DOMContentLoaded', () => {
    const currentTheme = document.documentElement.getAttribute('data-bs-theme');
    updateToggleUI(currentTheme);

    const toggleCheckbox = document.getElementById('darkModeToggle');
    if (toggleCheckbox) {
        // We add the listener for pages like Admin that don't have 'onchange'
        toggleCheckbox.addEventListener('change', toggleDarkMode);
    }
});

// === GLOBAL EMPTY STATE HANDLER ===
// Automatically detects empty tables and list containers to show a "No data found" message
document.addEventListener('DOMContentLoaded', function() {
    // 1. Handle Empty Tables
    const tables = document.querySelectorAll('table');
    tables.forEach(table => {
        const tbody = table.querySelector('tbody');
        if (tbody) {
            const rowCount = tbody.querySelectorAll('tr:not(.d-none):not(.empty-state-row)').length;
            if (rowCount === 0) {
                const headRow = table.querySelector('thead tr');
                const colCount = headRow ? headRow.children.length : 10;
                const emptyRow = document.createElement('tr');
                emptyRow.className = 'empty-state-row';
                emptyRow.innerHTML = `<td colspan="${colCount}" style="padding: 0; border: none;">
                    <div class="global-empty-state" style="border: none; background: transparent;">
                        <i class="fas fa-inbox"></i> No data found in this list
                    </div>
                </td>`;
                tbody.appendChild(emptyRow);
            }
        }
    });

    // 2. Handle Empty Flex/Div Lists (Card Bodies, Widget Bodies, List Groups)
    // We target containers that often hold Thymeleaf th:each loops
    const listContainers = document.querySelectorAll('.card-body, .widget-body, .list-group, .tab-pane');
    listContainers.forEach(container => {
        // If the container has absolutely no element children and no significant text, 
        // it means the th:each loop returned 0 items and didn't render anything.
        if (container.children.length === 0 && container.textContent.trim() === '') {
            container.innerHTML = `
                <div class="global-empty-state">
                    <i class="fas fa-folder-open"></i> No data found
                </div>
            `;
            // Ensure container has some padding/display if it was collapsed
            container.style.display = 'block';
            container.style.padding = '20px';
        }
    });
});