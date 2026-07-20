$files = Get-ChildItem -Path "d:\WCG\HRMS_WCG\admindashboard\admindashboard\src\main\resources\templates\learning\*.html"

$cssToAdd = @"
    /* Sidebar Toggle */
    .sidebar { transition: width 0.3s; }
    .sidebar.collapsed { width: 80px; }
    .sidebar.collapsed .company-details, .sidebar.collapsed .menu-item span, .sidebar.collapsed .logout-text { display: none; }
    .sidebar.collapsed .sidebar-header { justify-content: center; padding: 20px 0; }
    .sidebar.collapsed .toggle-sidebar-btn { display: none; } /* Hide the close button when collapsed, or keep it */
    .sidebar-header { padding:20px; display:flex; align-items:center; justify-content:space-between; border-bottom:1px solid #eaeaea; }
    .toggle-sidebar-btn { background: none; border: none; font-size: 18px; color: #555; cursor: pointer; }
    .expand-sidebar-btn { display: none; background: none; border: none; font-size: 20px; color: #333; cursor: pointer; margin-right: 15px; margin-top: 2px;}
    .sidebar.collapsed ~ .dashboard-content .expand-sidebar-btn { display: block; }
"@

$sidebarHeaderRegex = '(?s)<div class="sidebar-header">.*?</div>\s*</div>'
$sidebarHeaderNew = @"
        <div class="sidebar-header">
            <div style="display:flex; align-items:center; gap:12px;" class="logo-wrapper">
                <img src="/images/wcg-logo.jpg" alt="WCG Logo" class="company-logo" onerror="this.src='https://ui-avatars.com/api/?name=WCG&background=0D8ABC&color=fff'">
                <div class="company-details">
                    <h3>WhiteCircle</h3>
                    <span>Learning Admin L3</span>
                </div>
            </div>
            <button class="toggle-sidebar-btn" onclick="toggleSidebar()" title="Collapse Sidebar"><i class="fa-solid fa-angle-left"></i></button>
        </div>
"@

$logoutNew = @"
        </nav>
        <div style="margin-top: auto; padding: 20px; border-top: 1px solid #eaeaea;">
            <a href="/logout" style="display:flex; align-items:center; gap:12px; text-decoration:none; color:#de350b; font-weight:600; font-size:14px;">
                <i class="fa-solid fa-arrow-right-from-bracket" style="width:18px; text-align:center; font-size:16px;"></i><span class="logout-text">Logout</span>
            </a>
        </div>
"@

$jsToAdd = @"
<script>
    function toggleSidebar() {
        document.querySelector('.sidebar').classList.toggle('collapsed');
    }
</script>
</body>
"@

foreach ($file in $files) {
    $content = Get-Content -Raw $file.FullName

    # 1. Add CSS
    if ($content -notmatch "\.sidebar\.collapsed") {
        $content = $content -replace '</style>', "$cssToAdd`n</style>"
    }

    # 2. Replace Sidebar Header
    $content = [regex]::Replace($content, '(?s)<div class="sidebar-header">.*?</div>\s*</div>', $sidebarHeaderNew, 1)

    # 3. Add Logout
    if ($content -notmatch 'href="/logout"') {
        $content = $content -replace '</nav>', $logoutNew
    }

    # 4. Remove Profile
    $content = [regex]::Replace($content, '(?s)<div class="header-profile">.*?onerror=.*?>\s*</div>', '')

    # 5. Add Hamburger to header-left (to expand sidebar when collapsed)
    $headerLeftNew = '<div class="header-left" style="display:flex; align-items:flex-start;">' + "`n" + '                <button class="expand-sidebar-btn" onclick="toggleSidebar()"><i class="fa-solid fa-bars"></i></button>' + "`n" + '                <div>'
    
    # We'll replace <div class="header-left"> and its inner contents up to the </div>
    # Actually, simpler: replace `<div class="header-left">` with `<div class="header-left" style="display:flex; align-items:flex-start;"> <button class="expand-sidebar-btn" onclick="toggleSidebar()"><i class="fa-solid fa-bars"></i></button> <div>`
    # and add `</div>` before `<div class="header-right">`
    if ($content -notmatch "expand-sidebar-btn") {
        $content = $content -replace '<div class="header-left">', $headerLeftNew
        $content = $content -replace '(?s)(<div class="header-right">)', "</div>`n            `$1"
    }

    # 6. Add JS
    if ($content -notmatch "function toggleSidebar") {
        $content = $content -replace '</body>', $jsToAdd
    }

    Set-Content -Path $file.FullName -Value $content -Encoding UTF8
}
echo "Modifications Complete"
