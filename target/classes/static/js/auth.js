// Check if user is logged in
async function checkAuth() {
    try {
        const response = await fetch('/api/auth/me', {
            method: 'GET',
            credentials: 'include'
        });
        if (response.ok) {
            return await response.json();
        } else {
            return null;
        }
    } catch (error) {
        console.error('Auth check failed:', error);
        return null;
    }
}

// Redirect to login if not authenticated
async function requireAuth(expectedRole = null) {
    const user = await checkAuth();

    if (!user) {
        window.location.href = '/index.html';
        return null;
    }

    if (expectedRole && user.role !== expectedRole) {
        showAlert('Access denied: You do not have permission to access this page', 'error');
        setTimeout(() => { logout(); }, 2000);
        return null;
    }

    return user;
}

// Redirect to dashboard if already authenticated
async function requireGuest() {
    const user = await checkAuth();
    if (user) {
        redirectToDashboard(user.role);
    }
}

// Redirect to appropriate dashboard based on role
function redirectToDashboard(role) {
    if (role === 'HR') {
        window.location.href = '/hr-dashboard.html';
    } else {
        window.location.href = '/employee-dashboard.html';
    }
}

// Logout function
async function logout() {
    try {
        await fetch('/api/auth/logout', {
            method: 'POST',
            credentials: 'include'
        });
        window.location.href = '/index.html';
    } catch (error) {
        console.error('Logout failed:', error);
        window.location.href = '/index.html';
    }
}

// Show alert message — supports both auth pages (alertContainer) and app pages
function showAlert(message, type = 'error') {
    // Remove existing alerts
    document.querySelectorAll('.alert').forEach(a => a.remove());

    const alert = document.createElement('div');
    alert.className = `alert alert-${type}`;
    alert.textContent = message;

    // Try alertContainer first (auth pages)
    const container = document.getElementById('alertContainer');
    if (container) {
        container.appendChild(alert);
    } else {
        // App pages: insert before main content or form
        const main = document.querySelector('.main-content');
        const form = document.querySelector('form');
        if (main) {
            main.insertBefore(alert, main.firstChild);
        } else if (form) {
            form.parentElement.insertBefore(alert, form);
        } else {
            const fallback = document.querySelector('.card') || document.body;
            fallback.insertBefore(alert, fallback.firstChild);
        }
    }

    // Auto-dismiss after 5 seconds
    setTimeout(() => { alert.remove(); }, 5000);
}