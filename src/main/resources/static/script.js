const API_BASE = '/api';
let currentUser = null;

document.addEventListener("DOMContentLoaded", () => {
    checkAuthStatus();

    document.getElementById('login-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        const email = document.getElementById('login-email').value;
        const password = document.getElementById('login-password').value;
        await login(email, password);
    });

    document.getElementById('register-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        const name = document.getElementById('reg-name').value;
        const email = document.getElementById('reg-email').value;
        const password = document.getElementById('reg-password').value;
        const role = document.getElementById('reg-role').value;
        await register(name, email, password, role);
    });
    document.getElementById('report-lost-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        const data = {
            itemName: document.getElementById('lost-itemName').value,
            category: document.getElementById('lost-category').value,
            description: document.getElementById('lost-description').value,
            locationLost: document.getElementById('lost-location').value,
            dateLost: document.getElementById('lost-date').value,
            status: 'REPORTED'
        };
        try {
            await apiFetch('/api/lost-reports', {
                method: 'POST',
                body: JSON.stringify(data)
            });
            showMessage('Lost item reported successfully!', 'success');
            document.getElementById('report-lost-form').reset();
            fetchLostItems();
        } catch (err) {
            showMessage(err.message, 'error');
        }
    });
});

function checkAuthStatus() {
    const token = localStorage.getItem('jwt');
    const userStr = localStorage.getItem('user');
    
    if (token && userStr) {
        currentUser = JSON.parse(userStr);
        showApp();
    } else {
        showAuth();
    }
}

function showAuth() {
    document.getElementById('auth-section').classList.remove('hidden');
    document.getElementById('app-section').classList.add('hidden');
    document.getElementById('user-info').classList.add('hidden');
    currentUser = null;
}

function showApp() {
    document.getElementById('auth-section').classList.add('hidden');
    document.getElementById('app-section').classList.remove('hidden');
    
    const userInfo = document.getElementById('user-info');
    userInfo.classList.remove('hidden');
    document.getElementById('user-greeting').textContent = `Welcome, ${currentUser.name || currentUser.email} (${currentUser.role})`;
    
    document.getElementById('content-area').innerHTML = '<h3>Select an option above to view data.</h3>';
    
    if (currentUser.role === 'ADMIN') {
        document.getElementById('admin-dashboard').classList.remove('hidden');
        fetchDashboardStats();
    } else {
        document.getElementById('admin-dashboard').classList.add('hidden');
    }
    
    if (currentUser.role === 'STUDENT') {
        document.getElementById('report-lost-btn').classList.remove('hidden');
    } else {
        document.getElementById('report-lost-btn').classList.add('hidden');
    }
}

async function apiFetch(url, options = {}) {
    const token = localStorage.getItem('jwt');
    if (!options.headers) {
        options.headers = {};
    }
    if (token) {
        options.headers['Authorization'] = 'Bearer ' + token;
    }
    if (!options.headers['Content-Type'] && !(options.body instanceof FormData)) {
        options.headers['Content-Type'] = 'application/json';
    }
    
    try {
        const response = await fetch(url, options);
        if (!response.ok) {
            let errorMsg = 'An error occurred';
            if (response.status === 401) {
                errorMsg = "Please login first.";
                localStorage.removeItem('jwt');
                localStorage.removeItem('user');
                showAuth();
            } else if (response.status === 403) {
                errorMsg = "You do not have permission to access this feature.";
            } else if (response.status === 404) {
                errorMsg = "Requested resource was not found.";
            } else if (response.status >= 500) {
                errorMsg = "Server error. Check the Spring Boot console.";
            } else {
                try {
                    const errorData = await response.json();
                    errorMsg = errorData.message || `Error: ${response.status} ${response.statusText}`;
                } catch(e) {
                    errorMsg = `Error: ${response.status} ${response.statusText}`;
                }
            }
            throw new Error(errorMsg);
        }
        
        if (response.status !== 204) {
            const text = await response.text();
            return text ? JSON.parse(text) : null;
        }
        return null;
    } catch (error) {
        throw error;
    }
}

async function login(email, password) {
    try {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });
        
        const data = await response.json();
        if (response.ok && data.success) {
            localStorage.setItem('jwt', data.token);
            if (data.user) {
                localStorage.setItem('user', JSON.stringify({ 
                    email: data.user.email, 
                    role: data.user.role,
                    name: data.user.name
                }));
            } else {
                const payload = JSON.parse(atob(data.token.split('.')[1]));
                localStorage.setItem('user', JSON.stringify({ 
                    email: payload.sub, 
                    role: payload.role || 'USER',
                    name: payload.sub
                }));
            }
            
            showMessage('Login successful', 'success');
            checkAuthStatus();
        } else {
            showMessage(data.message || 'Login failed', 'error');
        }
    } catch (err) {
        showMessage('Error during login: ' + err.message, 'error');
    }
}

async function register(name, email, password, role) {
    try {
        const response = await fetch('/api/auth/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, email, password, role })
        });
        
        const data = await response.json();
        if (response.ok && data.success) {
            showMessage('Registration successful. Please login.', 'success');
            document.getElementById('register-form').reset();
        } else {
            showMessage(data.message || 'Registration failed', 'error');
        }
    } catch (err) {
        showMessage('Error during registration: ' + err.message, 'error');
    }
}

function logout() {
    localStorage.removeItem('jwt');
    localStorage.removeItem('user');
    checkAuthStatus();
}

function fillLogin(email, password) {
    document.getElementById('login-email').value = email;
    document.getElementById('login-password').value = password;
}

function showMessage(msg, type) {
    const msgDiv = document.getElementById('auth-message');
    msgDiv.textContent = msg;
    msgDiv.className = type === 'error' ? 'error-msg' : 'success-msg';
    setTimeout(() => { msgDiv.textContent = ''; }, 3000);
}

function showAppMessage(msg, type) {
    const area = document.getElementById('content-area');
    area.innerHTML = `<p class="${type === 'error' ? 'error-msg' : 'success-msg'}">${msg}</p>`;
}

async function fetchDashboardStats() {
    try {
        const data = await apiFetch('/api/admin/dashboard');
        if (data) {
            const statsDiv = document.getElementById('dashboard-stats');
            statsDiv.innerHTML = `
                <div class="stat-card"><h3>${data.totalUsers || 0}</h3><p>Total Users</p></div>
                <div class="stat-card"><h3>${data.totalStudents || 0}</h3><p>Students</p></div>
                <div class="stat-card"><h3>${data.totalStaff || 0}</h3><p>Staff</p></div>
                <div class="stat-card"><h3>${data.totalAdmins || 0}</h3><p>Admins</p></div>
                <div class="stat-card"><h3>${data.totalLostReports || 0}</h3><p>Lost Reports</p></div>
                <div class="stat-card"><h3>${data.totalFoundItems || 0}</h3><p>Found Items</p></div>
                <div class="stat-card"><h3>${data.availableItems || 0}</h3><p>Available Items</p></div>
                <div class="stat-card"><h3>${data.claimedItems || 0}</h3><p>Claimed Items</p></div>
                <div class="stat-card"><h3>${data.returnedItems || 0}</h3><p>Returned Items</p></div>
                <div class="stat-card"><h3>${data.totalMatches || 0}</h3><p>Total Matches</p></div>
            `;
        }
    } catch (err) {
        document.getElementById('dashboard-stats').innerHTML = `<p class="error-msg">${err.message}</p>`;
    }
}

function hideAllSections() {
    document.getElementById('content-area').innerHTML = '';
    document.getElementById('content-area').classList.remove('hidden');
    document.getElementById('report-lost-section').classList.add('hidden');
    if (document.getElementById('admin-dashboard')) {
        document.getElementById('admin-dashboard').classList.add('hidden');
    }
}

function showReportLostForm() {
    hideAllSections();
    document.getElementById('report-lost-section').classList.remove('hidden');
}

async function fetchLostItems() {
    hideAllSections();
    if (currentUser && currentUser.role === 'ADMIN') {
        document.getElementById('admin-dashboard').classList.remove('hidden');
        fetchDashboardStats();
    }
    try {
        const data = await apiFetch('/api/lost-reports');
        if (data) {
            let html = '<h3>Lost Items</h3><table><tr><th>ID</th><th>Item</th><th>Category</th><th>Description</th><th>Location</th><th>Date</th><th>Status</th><th>Actions</th></tr>';
            if (data.length === 0) {
                html += '<tr><td colspan="8">No lost reports found.</td></tr>';
            } else {
                data.forEach(item => {
                    let actions = '-';
                    if (item.status === 'CLAIMED') {
                        actions = 'Pending Return';
                    } else if (item.status === 'RESOLVED') {
                        actions = 'Returned';
                    } else if (currentUser && (currentUser.role === 'STAFF' || currentUser.role === 'ADMIN')) {
                        actions = `<button class="action-btn" onclick="updateLostStatus(${item.id}, 'RESOLVED')">Mark Resolved</button>`;
                    }
                    html += `<tr>
                        <td>${item.id}</td>
                        <td>${item.itemName}</td>
                        <td>${item.category}</td>
                        <td>${item.description || 'N/A'}</td>
                        <td>${item.locationLost || 'N/A'}</td>
                        <td>${item.dateLost || 'N/A'}</td>
                        <td>${item.status}</td>
                        <td>${actions}</td>
                    </tr>`;
                });
            }
            html += '</table>';
            document.getElementById('content-area').innerHTML = html;
        }
    } catch (err) {
        showAppMessage(err.message, 'error');
    }
}

async function updateLostStatus(id, newStatus) {
    try {
        const itemData = await apiFetch(`/api/lost-reports/${id}`);
        itemData.status = newStatus;
        await apiFetch(`/api/lost-reports/${id}`, {
            method: 'PUT',
            body: JSON.stringify(itemData)
        });
        fetchLostItems();
    } catch (err) {
        alert(err.message);
    }
}

async function fetchFoundItems() {
    hideAllSections();
    if (currentUser && currentUser.role === 'ADMIN') {
        document.getElementById('admin-dashboard').classList.remove('hidden');
        fetchDashboardStats();
    }
    try {
        const data = await apiFetch('/api/found-items');
        if (data) {
            let html = '<h3>Found Items</h3>';
            if (currentUser && (currentUser.role === 'STAFF' || currentUser.role === 'ADMIN')) {
                html += '<button onclick="showAddFoundItemFromLost()" class="action-btn" style="margin-bottom: 10px;">Add from Lost Reports</button>';
            }
            html += '<div id="add-from-lost-container" class="hidden" style="margin-bottom: 15px; background: #f9f9f9; padding: 10px; border-radius: 5px;"></div>';
            html += '<table><tr><th>ID</th><th>Item</th><th>Category</th><th>Description</th><th>Location</th><th>Date</th><th>Status</th><th>Actions</th></tr>';
            if (data.length === 0) {
                html += '<tr><td colspan="8">No found items found.</td></tr>';
            } else {
                data.forEach(item => {
                    if (item.status === 'RETURNED') return;
                    let actions = 'None';
                    if (currentUser && (currentUser.role === 'STAFF' || currentUser.role === 'ADMIN')) {
                        if (item.status === 'AVAILABLE') {
                            actions = `<button class="action-btn" onclick="updateItemStatus(${item.id}, 'claim')">Claim</button>`;
                        } else if (item.status === 'CLAIMED') {
                            actions = `<button class="action-btn" onclick="updateItemStatus(${item.id}, 'return')">Return</button>`;
                        }
                    }
                    html += `<tr>
                        <td>${item.id}</td>
                        <td>${item.itemName}</td>
                        <td>${item.category}</td>
                        <td>${item.description || 'N/A'}</td>
                        <td>${item.locationFound || 'N/A'}</td>
                        <td>${item.dateFound || 'N/A'}</td>
                        <td>${item.status}</td>
                        <td>${actions}</td>
                    </tr>`;
                });
            }
            html += '</table>';
            document.getElementById('content-area').innerHTML = html;
        }
    } catch (err) {
        showAppMessage(err.message, 'error');
    }
}

async function updateItemStatus(id, action) {
    try {
        await apiFetch(`/api/found-items/${id}/${action}`, {
            method: 'PUT'
        });
        fetchFoundItems();
    } catch (err) {
        alert(err.message);
    }
}

async function fetchMatches() {
    hideAllSections();
    if (currentUser && currentUser.role === 'ADMIN') {
        document.getElementById('admin-dashboard').classList.remove('hidden');
        fetchDashboardStats();
    }
    try {
        const data = await apiFetch('/api/matches');
        if (data) {
            let html = '<h3>Possible Matches</h3><table><tr><th>Match ID</th><th>Lost Item</th><th>Found Item</th><th>Score</th></tr>';
            if(data.length === 0) {
                html += '<tr><td colspan="4">No matches found.</td></tr>';
            } else {
                data.forEach(match => {
                    html += `<tr>
                        <td>${match.id}</td>
                        <td>${match.lostItemName || 'N/A'} (ID: ${match.lostReportId})</td>
                        <td>${match.foundItemName || 'N/A'} (ID: ${match.foundItemId})</td>
                        <td>${match.matchScore || 'N/A'}</td>
                    </tr>`;
                });
            }
            html += '</table>';
            document.getElementById('content-area').innerHTML = html;
        }
    } catch (err) {
        showAppMessage(err.message, 'error');
    }
}

async function showAddFoundItemFromLost() {
    const container = document.getElementById('add-from-lost-container');
    if (!container.classList.contains('hidden')) {
        container.classList.add('hidden');
        return;
    }
    
    try {
        const data = await apiFetch('/api/lost-reports');
        if (data) {
            let html = '<h4 style="margin-top:0;">Select a Lost Report to mark as Found:</h4><ul style="list-style-type: none; padding: 0; margin: 0;">';
            let hasItems = false;
            data.forEach(item => {
                if (item.status !== 'RESOLVED') {
                    hasItems = true;
                    html += `<li style="padding: 5px 0; border-bottom: 1px solid #ccc;">${item.itemName} (${item.category}) - Lost on ${item.dateLost || 'N/A'} <button class="action-btn" onclick="createFoundItemFromLost(${item.id})" style="padding: 2px 5px; font-size: 0.8em; margin-left: 10px;">Add to Found</button></li>`;
                }
            });
            if (!hasItems) html += '<li>No active lost reports available.</li>';
            html += '</ul>';
            container.innerHTML = html;
            container.classList.remove('hidden');
        }
    } catch (err) {
        alert('Error fetching lost reports: ' + err.message);
    }
}

async function createFoundItemFromLost(lostId) {
    try {
        const lostData = await apiFetch(`/api/lost-reports/${lostId}`);
        const foundData = {
            itemName: lostData.itemName,
            category: lostData.category,
            description: lostData.description,
            locationFound: lostData.locationLost || 'Unknown',
            dateFound: new Date().toISOString().split('T')[0],
            status: 'AVAILABLE',
            linkedLostReportId: lostId
        };
        await apiFetch('/api/found-items', {
            method: 'POST',
            body: JSON.stringify(foundData)
        });
        showMessage('Item added to Found Items!', 'success');
        fetchFoundItems();
    } catch (err) {
        alert('Error adding item: ' + err.message);
    }
}
