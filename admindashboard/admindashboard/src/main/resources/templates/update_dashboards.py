import os
import re

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

base_dir = r"D:\WCG\HRMS_WCG\admindashboard\admindashboard\src\main\resources\templates"

for filename in dashboard_files:
    filepath = os.path.join(base_dir, filename)
    if not os.path.exists(filepath):
        continue
        
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    original_content = content

    # 1. Self Services
    # Find View All link and replace
    content = re.sub(
        r'<a href="#" class="small fw-bold">View All</a>',
        '<a href="#expandedSelfServices" data-bs-toggle="collapse" role="button" aria-expanded="false" aria-controls="expandedSelfServices" class="small fw-bold text-white text-decoration-none" id="viewAllSelfServicesBtn">View All</a>',
        content
    )
    
    # Restructure Self Services
    # We find the block for Self Services items
    self_services_match = re.search(r'(<i class="fas fa-user-cog me-2"></i>Self Services.*?<div class="row g-2">)(.*?)(</div>\s*</div>\s*</div>)', content, re.DOTALL)
    if self_services_match:
        prefix = self_services_match.group(1)
        items_html = self_services_match.group(2)
        suffix = self_services_match.group(3)
        
        # Split items by <div class="col-md-6">
        # Note: we should look for col-md-6.
        items = re.split(r'<div class="col-md-6">', items_html)
        if len(items) > 1:
            new_items_html = items[0] # leading whitespace
            for i in range(1, len(items)):
                if i <= 6:
                    new_items_html += '<div class="col-md-4">' + items[i]
                elif i == 7:
                    new_items_html += '</div>\n                    <div class="collapse mt-2" id="expandedSelfServices">\n                        <div class="row g-2">\n                            <div class="col-md-4">' + items[i]
                else:
                    new_items_html += '<div class="col-md-4">' + items[i]
            
            if len(items) > 7:
                new_items_html += '                        </div>\n                    </div>\n                '
            
            content = content[:self_services_match.start(2)] + new_items_html + content[self_services_match.end(2):]

    # 2. Applications
    apps_match = re.search(r'(<div class="collapse mt-2" id="expandedApps">\s*<div class="row g-2">)(.*?)(</div>\s*</div>)', content, re.DOTALL)
    if apps_match:
        # Before we process the collapsed items, let's process the first block of items
        # Find the row g-2 before the collapse
        first_block_match = re.search(r'(<i class="fas fa-th me-2"></i>Applications.*?<div class="row g-2">)(.*?)(</div>\s*<div class="collapse mt-2" id="expandedApps">)', content, re.DOTALL)
        
        if first_block_match:
            prefix = first_block_match.group(1)
            items_html = first_block_match.group(2)
            suffix = first_block_match.group(3)
            
            items = re.split(r'<div class="col-md-6"(.*?)>', items_html)
            # items will be like: [ws, attr1, content1, attr2, content2]
            
            # Since regex split with groups behaves differently, let's just use simple split and replace
            # A safer approach for applications since the 7th item might have th:if
            # Just replace all col-md-6 with col-md-4 in both blocks
            
            # Let's just find the entire Applications widget
            apps_widget = re.search(r'(<i class="fas fa-th me-2"></i>Applications.*?)(</div>\s*</div>\s*</div>)', content, re.DOTALL)
            if apps_widget:
                apps_content = apps_widget.group(0)
                
                # change all col-md-6 to col-md-4
                apps_content = apps_content.replace('col-md-6', 'col-md-4')
                
                # Now we need to move the 7th item (My Space) into the collapse block if it exists
                # It's better to just extract My Space and put it in the collapse block
                my_space_match = re.search(r'(\s*<div class="col-md-4"[^>]*>\s*<a[^>]*href="[^"]*/space/login[^"]*"[^>]*>.*?</a>\s*</div>)', apps_content, re.DOTALL)
                
                if my_space_match:
                    my_space_html = my_space_match.group(1)
                    # Remove it from its current position
                    apps_content = apps_content.replace(my_space_html, '')
                    
                    # Add it to the beginning of the expandedApps row
                    expanded_apps_start = apps_content.find('<div class="collapse mt-2" id="expandedApps">\n                        <div class="row g-2">')
                    if expanded_apps_start != -1:
                        insert_pos = expanded_apps_start + len('<div class="collapse mt-2" id="expandedApps">\n                        <div class="row g-2">')
                        apps_content = apps_content[:insert_pos] + my_space_html + apps_content[insert_pos:]
                
                content = content[:apps_widget.start(0)] + apps_content + content[apps_widget.end(0):]

    if content != original_content:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f"Updated {filename}")
    else:
        print(f"No changes made to {filename}")

print("Done")
