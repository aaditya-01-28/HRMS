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

for filename in dashboard_files:
    filepath = os.path.join(base_dir, filename)
    if not os.path.exists(filepath):
        continue

    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    original_content = content

    # Regex to find 7 closing divs before Applications col-lg-6
    pattern = r'(\s*</div>\s*</div>\s*</div>\s*</div>\s*</div>\s*</div>\s*</div>\s*)(<div class="col-lg-6">\s*<div class="widget-card">\s*<div class="widget-header navy-header">\s*<div class="d-flex align-items-center">\s*<i class="fas fa-th me-2"></i>Applications)'

    # Replace 7 closing divs with 6 closing divs
    replacement = r'\n                        </div>\n                    </div>\n                </div>\n            </div>\n        </div>\n    </div>\n\n        \2'

    new_content = re.sub(pattern, replacement, content)

    if new_content != original_content:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(new_content)
        print(f"Successfully fixed layout for {filename}")
    else:
        print(f"No match found for {filename}")
