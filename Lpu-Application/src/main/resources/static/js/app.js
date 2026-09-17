/* ===================================================
   Task Manager - Application JavaScript
   =================================================== */

// ---- Notification badge polling ----
(function initNotificationPolling() {
  const badge = document.getElementById('notif-badge');
  if (!badge) return;

  async function fetchUnread() {
    try {
      const res = await fetch('/notifications/unread-count', {
        credentials: 'include'
      });
      if (res.ok) {
        const data = await res.json();
        const count = data.count || 0;
        if (count > 0) {
          badge.textContent = count > 99 ? '99+' : count;
          badge.style.display = 'inline-flex';
        } else {
          badge.style.display = 'none';
        }
      }
    } catch (e) {
      // Silent fail
    }
  }

  fetchUnread();
  setInterval(fetchUnread, 30000); // Poll every 30s
})();

// ---- Flash message auto-dismiss ----
(function initAlertDismiss() {
  const alerts = document.querySelectorAll('.alert[data-auto-dismiss]');
  alerts.forEach(alert => {
    setTimeout(() => {
      alert.style.transition = 'opacity 0.5s ease, transform 0.5s ease';
      alert.style.opacity = '0';
      alert.style.transform = 'translateY(-10px)';
      setTimeout(() => alert.remove(), 500);
    }, 4000);
  });
})();

// ---- Active sidebar link ----
(function markActiveSidebarLink() {
  const currentPath = window.location.pathname;
  const navLinks = document.querySelectorAll('.nav-item');
  navLinks.forEach(link => {
    const href = link.getAttribute('href');
    if (href && currentPath.startsWith(href) && href !== '/') {
      link.classList.add('active');
    } else if (href === '/' && currentPath === '/') {
      link.classList.add('active');
    }
  });
})();

// ---- Confirm before delete ----
(function initDeleteConfirm() {
  const deleteForms = document.querySelectorAll('form[data-confirm]');
  deleteForms.forEach(form => {
    form.addEventListener('submit', function(e) {
      const msg = this.getAttribute('data-confirm') || 'Are you sure?';
      if (!confirm(msg)) {
        e.preventDefault();
      }
    });
  });
})();

// ---- Tag input ----
(function initTagInput() {
  const tagInput = document.getElementById('tag-input');
  const tagsContainer = document.getElementById('tags-container');
  if (!tagInput || !tagsContainer) return;

  const hiddenTagsField = document.getElementById('tags-hidden');
  let tags = hiddenTagsField ? hiddenTagsField.value.split(',').filter(Boolean) : [];

  function renderTags() {
    const existing = tagsContainer.querySelectorAll('.tag-pill');
    existing.forEach(t => t.remove());

    tags.forEach((tag, i) => {
      const pill = document.createElement('span');
      pill.className = 'tag-chip';
      pill.style.display = 'inline-flex';
      pill.style.alignItems = 'center';
      pill.style.gap = '6px';
      pill.innerHTML = `${tag} <button type="button" onclick="removeTag(${i})" style="background:none;border:none;cursor:pointer;color:inherit;font-size:12px;padding:0;">&times;</button>`;
      tagsContainer.insertBefore(pill, tagInput);
    });

    if (hiddenTagsField) {
      hiddenTagsField.value = tags.join(',');
    }
  }

  window.removeTag = function(index) {
    tags.splice(index, 1);
    renderTags();
  };

  tagInput.addEventListener('keydown', function(e) {
    if (e.key === 'Enter' || e.key === ',') {
      e.preventDefault();
      const val = this.value.trim().toLowerCase().replace(/[^a-z0-9-]/g, '');
      if (val && !tags.includes(val) && tags.length < 10) {
        tags.push(val);
        renderTags();
      }
      this.value = '';
    }
  });

  renderTags();
})();

// ---- Chart.js helpers ----
window.createStatusChart = function(canvasId, todo, inProgress, done) {
  const canvas = document.getElementById(canvasId);
  if (!canvas || typeof Chart === 'undefined') return;

  new Chart(canvas, {
    type: 'doughnut',
    data: {
      labels: ['To Do', 'In Progress', 'Done'],
      datasets: [{
        data: [todo, inProgress, done],
        backgroundColor: ['#64748b', '#6366f1', '#10b981'],
        borderColor: 'rgba(30, 30, 53, 0.8)',
        borderWidth: 3,
        hoverOffset: 8
      }]
    },
    options: {
      responsive: true,
      cutout: '68%',
      plugins: {
        legend: {
          position: 'bottom',
          labels: {
            color: '#94a3b8',
            padding: 16,
            font: { size: 12, family: 'Inter' }
          }
        }
      }
    }
  });
};

window.createPriorityChart = function(canvasId, low, medium, high, critical) {
  const canvas = document.getElementById(canvasId);
  if (!canvas || typeof Chart === 'undefined') return;

  new Chart(canvas, {
    type: 'bar',
    data: {
      labels: ['Low', 'Medium', 'High', 'Critical'],
      datasets: [{
        label: 'Tasks',
        data: [low, medium, high, critical],
        backgroundColor: ['#10b981', '#f59e0b', '#f97316', '#ef4444'],
        borderRadius: 8,
        borderSkipped: false
      }]
    },
    options: {
      responsive: true,
      plugins: {
        legend: { display: false }
      },
      scales: {
        x: {
          ticks: { color: '#94a3b8', font: { family: 'Inter' } },
          grid: { color: 'rgba(99,102,241,0.08)' }
        },
        y: {
          ticks: { color: '#94a3b8', stepSize: 1, font: { family: 'Inter' } },
          grid: { color: 'rgba(99,102,241,0.08)' },
          beginAtZero: true
        }
      }
    }
  });
};

// ---- Color input sync ----
(function initColorSync() {
  const colorInput = document.getElementById('colorPicker');
  const colorText = document.getElementById('colorText');
  if (!colorInput || !colorText) return;

  colorInput.addEventListener('input', () => { colorText.value = colorInput.value; });
  colorText.addEventListener('input', () => {
    if (/^#[0-9A-Fa-f]{6}$/.test(colorText.value)) {
      colorInput.value = colorText.value;
    }
  });
})();
