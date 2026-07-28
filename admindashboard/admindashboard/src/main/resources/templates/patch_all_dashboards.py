import os
import glob
import re

base_dir = r"D:\WCG\HRMS_WCG\admindashboard\admindashboard\src\main\resources\templates"
dashboard_files = [
    "accounts-dashboard.html",
    "facility-dashboard.html",
    "hr-dashboard.html",
    "learninghead-dashboard.html",
    "manager-dashboard.html",
    "rewards-dashboard.html",
    "senior_accounts-dashboard.html",
    "senior_facility-dashboard.html",
    "senior_hr-dashboard.html",
    "senior_it-dashboard.html",
    "senior_rewards-dashboard.html",
    "senior_transport-dashboard.html",
    "transport-dashboard.html"
]

def slugify(text):
    text = text.lower()
    text = re.sub(r'[^a-z0-9]+', '-', text)
    return text.strip('-')

for filename in dashboard_files:
    filepath = os.path.join(base_dir, filename)
    if not os.path.exists(filepath):
        continue
        
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    original_content = content
    
    # 1. Add Edit button to Self Services
    # Look for the Self Services header and View All button.
    # The current layout has: <a href="#expandedSelfServices" ...>View All</a>
    if '<a href="#" class="small fw-bold text-white text-decoration-none me-3" onclick="openPrefModal(\'services\', event)">' not in content:
        content = content.replace(
            '<a href="#expandedSelfServices"',
            '<div>\n                        <a href="#" class="small fw-bold text-white text-decoration-none me-3" onclick="openPrefModal(\'services\', event)"><i class="fas fa-edit"></i> Edit</a>\n                        <a href="#expandedSelfServices"'
        )
        # Also need to close the div after the View All button
        content = content.replace(
            'id="viewAllSelfServicesBtn">View All</a>',
            'id="viewAllSelfServicesBtn">View All</a>\n                    </div>'
        )

    # 2. Add Edit button to Applications
    if '<a href="#" class="small fw-bold text-white text-decoration-none me-3" onclick="openPrefModal(\'apps\', event)">' not in content:
        content = content.replace(
            '<a href="#expandedApps"',
            '<div>\n                        <a href="#" class="small fw-bold text-white text-decoration-none me-3" onclick="openPrefModal(\'apps\', event)"><i class="fas fa-edit"></i> Edit</a>\n                        <a href="#expandedApps"'
        )
        content = content.replace(
            'id="viewAllAppsBtn">View All</a>',
            'id="viewAllAppsBtn">View All</a>\n                    </div>'
        )

    # 3. Add container IDs
    content = content.replace('<div class="row g-2">', '<div class="row g-2" id="pinnedServicesContainer">', 1)
    content = content.replace('<div class="row g-2">', '<div class="row g-2" id="collapsedServicesContainer">', 1)
    content = content.replace('<div class="row g-2">', '<div class="row g-2" id="pinnedAppsContainer">', 1)
    content = content.replace('<div class="row g-2">', '<div class="row g-2" id="collapsedAppsContainer">', 1)

    # 4. Add data-item-name to spans
    # We find `<span class="app-text[^>]*>Text</span>`
    def repl_span(m):
        classes = m.group(1)
        text = m.group(2)
        if 'data-item-name' in classes:
            return m.group(0)
        slug = slugify(text)
        return f'<span {classes} data-item-name="{slug}">{text}</span>'
    
    content = re.sub(r'<span (class="app-text[^"]*")>(.*?)</span>', repl_span, content)

    # 5. Append fragment import
    if "fragments/dashboard-preferences" not in content:
        content = content.replace('</body>', '    <div th:replace="~{fragments/dashboard-preferences :: preferences}"></div>\n</body>')

    if content != original_content:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f"Patched {filename}")
    else:
        print(f"Skipped {filename} (no changes needed)")

# Finally, clean up employee-dashboard.html to use the fragment instead of inline script
emp_file = os.path.join(base_dir, "employee-dashboard.html")
if os.path.exists(emp_file):
    with open(emp_file, 'r', encoding='utf-8') as f:
        emp_content = f.read()
    
    # We remove the huge modal and script block.
    # It starts at <!-- Dashboard Preference Modal --> and ends at </script>\n</body>
    match = re.search(r'<!-- Dashboard Preference Modal -->.*?</script>', emp_content, re.DOTALL)
    if match:
        emp_content = emp_content.replace(match.group(0), '<div th:replace="~{fragments/dashboard-preferences :: preferences}"></div>')
        with open(emp_file, 'w', encoding='utf-8') as f:
            f.write(emp_content)
        print("Cleaned up employee-dashboard.html")

