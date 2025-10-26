// ========================================
// DELIVERY TEAMS MANAGEMENT JAVASCRIPT
// ========================================

console.log('Delivery Teams Management JS loaded successfully!');

class DeliveryTeamsManagement {
    constructor() {
        this.apiEndpoint = '/admin/delivery-teams/api';
        this.teams = [];
        this.zones = [];
        this.availableUsers = [];
        
        this.init();
    }
    
    init() {
        this.loadData();
    }
    
    /**
     * Load all data (teams, zones, users)
     */
    async loadData() {
        try {
            // Load teams
            const teamsResponse = await fetch(`${this.apiEndpoint}/teams`);
            if (!teamsResponse.ok) throw new Error('Failed to load teams');
            this.teams = await teamsResponse.json();
            
            // Load zones
            const zonesResponse = await fetch(`${this.apiEndpoint}/zones`);
            if (!zonesResponse.ok) throw new Error('Failed to load zones');
            this.zones = await zonesResponse.json();
            
            // Load available users
            const usersResponse = await fetch(`${this.apiEndpoint}/users`);
            if (!usersResponse.ok) throw new Error('Failed to load users');
            this.availableUsers = await usersResponse.json();
            
            this.renderTeams();
            this.updateStats();
            this.populateUserDropdowns();
        } catch (error) {
            console.error('Error loading data:', error);
            this.showNotification('Không thể tải dữ liệu đội giao hàng', 'danger');
        }
    }
    
    /**
     * Render teams
     */
    renderTeams() {
        const container = document.getElementById('teamsContainer');
        if (!container) return;
        
        if (this.teams.length === 0) {
            container.innerHTML = `
                <div class="text-center py-5">
                    <i class="bi bi-people text-muted" style="font-size: 3rem;"></i>
                    <p class="text-muted mt-3">Chưa có đội giao hàng nào</p>
                </div>
            `;
            return;
        }
        
        container.innerHTML = this.teams.map(team => this.renderTeamCard(team)).join('');
    }
    
    /**
     * Render individual team card
     */
    renderTeamCard(team) {
        const statusBadge = team.isActive 
            ? '<span class="badge bg-success">Đang hoạt động</span>'
            : '<span class="badge bg-secondary">Đã tạm dừng</span>';
        
        const phoneDisplay = team.phone || '<span class="text-muted">Chưa cập nhật</span>';
        const userNameDisplay = team.userName || '<span class="text-muted">Chưa gán</span>';
        const userEmailDisplay = team.userEmail || '';
        const zonesCount = team.zones ? team.zones.length : 0;
        
        return `
            <div class="col-md-6 col-lg-4 mb-4">
                <div class="card h-100 shadow-sm border-0">
                    <div class="card-header bg-light d-flex justify-content-between align-items-center">
                        <h6 class="mb-0">
                            <i class="bi bi-truck text-primary me-2"></i>
                            ${this.escapeHtml(team.name)}
                        </h6>
                        ${statusBadge}
                    </div>
                    <div class="card-body">
                        <div class="mb-3">
                            <div class="d-flex align-items-center mb-2">
                                <i class="bi bi-telephone-fill me-2 text-primary"></i>
                                <span class="small">${phoneDisplay}</span>
                            </div>
                            <div class="d-flex align-items-center mb-2">
                                <i class="bi bi-person-circle me-2 text-info"></i>
                                <div>
                                    <div class="small fw-bold">${userNameDisplay}</div>
                                    ${userEmailDisplay ? `<div class="small text-muted">${this.escapeHtml(userEmailDisplay)}</div>` : ''}
                                </div>
                            </div>
                            <div class="d-flex align-items-center">
                                <i class="bi bi-geo-alt-fill me-2 text-success"></i>
                                <span class="small">${zonesCount} khu vực phục vụ</span>
                            </div>
                        </div>
                        
                        ${team.zones && team.zones.length > 0 ? this.renderZonesPreview(team.zones) : this.renderNoZones()}
                    </div>
                    <div class="card-footer bg-white border-0 d-flex justify-content-between align-items-center">
                        <button class="btn btn-sm btn-outline-info" onclick="manageZones(${team.id})">
                            <i class="bi bi-geo-alt me-1"></i>Zones
                        </button>
                        <div>
                            <button class="btn btn-sm btn-outline-primary me-1" onclick="editTeam(${team.id})">
                                <i class="bi bi-pencil"></i>
                            </button>
                            <button class="btn btn-sm btn-outline-secondary" onclick="toggleTeamStatus(${team.id})">
                                <i class="bi bi-toggle-${team.isActive ? 'on' : 'off'}"></i>
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        `;
    }
    
    /**
     * Render zones preview
     */
    renderZonesPreview(zones) {
        return `
            <div class="d-flex flex-wrap gap-1">
                ${zones.slice(0, 3).map(zone => `
                    <span class="badge bg-light text-dark border">${this.escapeHtml(zone.name)}</span>
                `).join('')}
                ${zones.length > 3 ? `<span class="badge bg-secondary">+${zones.length - 3}</span>` : ''}
            </div>
        `;
    }
    
    /**
     * Render no zones state
     */
    renderNoZones() {
        return `
            <div class="text-center py-2 text-muted">
                <i class="bi bi-inbox me-1"></i>
                <small>Chưa có khu vực</small>
            </div>
        `;
    }
    
    /**
     * Update stats
     */
    updateStats() {
        const totalTeams = this.teams.length;
        const activeTeams = this.teams.filter(t => t.isActive).length;
        const totalZones = this.teams.reduce((sum, team) => sum + (team.zones ? team.zones.length : 0), 0);
        
        document.getElementById('totalTeams').textContent = totalTeams;
        document.getElementById('activeTeams').textContent = activeTeams;
        document.getElementById('totalZones').textContent = totalZones;
    }
    
    /**
     * Populate user dropdowns
     */
    populateUserDropdowns() {
        const addDropdown = document.getElementById('addTeamUserId');
        const editDropdown = document.getElementById('editTeamUserId');
        
        if (addDropdown) {
            addDropdown.innerHTML = '<option value="">Chọn user phụ trách</option>' +
                this.availableUsers.map(user => 
                    `<option value="${user.id}">${this.escapeHtml(user.name)} (${this.escapeHtml(user.email)})</option>`
                ).join('');
        }
        
        if (editDropdown) {
            editDropdown.innerHTML = '<option value="">Chọn user phụ trách</option>' +
                this.availableUsers.map(user => 
                    `<option value="${user.id}">${this.escapeHtml(user.name)} (${this.escapeHtml(user.email)})</option>`
                ).join('');
        }
    }
    
    /**
     * Create new team
     */
    async createTeam() {
        const name = document.getElementById('addTeamName').value.trim();
        const phone = document.getElementById('addTeamPhone').value.trim();
        const userId = parseInt(document.getElementById('addTeamUserId').value);
        const isActive = document.getElementById('addTeamActive').checked;
        
        if (!name || !userId) {
            this.showNotification('Vui lòng điền đầy đủ thông tin bắt buộc', 'warning');
            return;
        }
        
        try {
            const response = await fetch(`${this.apiEndpoint}/teams`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    name,
                    phone,
                    userId,
                    isActive
                })
            });
            
            const result = await response.json();
            
            if (response.ok && result.success !== false) {
                this.showNotification('Tạo đội giao hàng thành công', 'success');
                const modalElement = document.getElementById('addTeamModal');
                const modal = bootstrap.Modal.getInstance(modalElement);
                if (modal) modal.hide();
                document.getElementById('addTeamForm').reset();
                this.loadData();
            } else {
                let errorMsg = result.message || 'Tạo đội giao hàng thất bại';
                this.showNotification(errorMsg, 'danger');
            }
        } catch (error) {
            console.error('Error creating team:', error);
            this.showNotification('Không thể tạo đội giao hàng', 'danger');
        }
    }
    
    /**
     * Edit team
     */
    async editTeam(teamId) {
        const team = this.teams.find(t => t.id === teamId);
        if (!team) {
            this.showNotification('Không tìm thấy đội', 'danger');
            return;
        }
        
        // Fill form
        document.getElementById('editTeamId').value = team.id;
        document.getElementById('editTeamName').value = team.name;
        document.getElementById('editTeamPhone').value = team.phone || '';
        document.getElementById('editTeamUserId').value = team.userId;
        document.getElementById('editTeamActive').checked = team.isActive;
        
        // Show modal
        const modal = new bootstrap.Modal(document.getElementById('editTeamModal'));
        modal.show();
    }
    
    /**
     * Update team
     */
    async updateTeam() {
        const teamId = parseInt(document.getElementById('editTeamId').value);
        const name = document.getElementById('editTeamName').value.trim();
        const phone = document.getElementById('editTeamPhone').value.trim();
        const userId = parseInt(document.getElementById('editTeamUserId').value);
        const isActive = document.getElementById('editTeamActive').checked;
        
        if (!name || !userId) {
            this.showNotification('Vui lòng điền đầy đủ thông tin bắt buộc', 'warning');
            return;
        }
        
        try {
            const response = await fetch(`${this.apiEndpoint}/teams/${teamId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    name,
                    phone,
                    userId,
                    isActive
                })
            });
            
            const result = await response.json();
            
            if (response.ok && result.success !== false) {
                this.showNotification('Cập nhật đội giao hàng thành công', 'success');
                const modalElement = document.getElementById('editTeamModal');
                const modal = bootstrap.Modal.getInstance(modalElement);
                if (modal) modal.hide();
                this.loadData();
            } else {
                let errorMsg = result.message || 'Cập nhật đội giao hàng thất bại';
                this.showNotification(errorMsg, 'danger');
            }
        } catch (error) {
            console.error('Error updating team:', error);
            this.showNotification('Không thể cập nhật đội giao hàng', 'danger');
        }
    }
    
    /**
     * Toggle team status
     */
    async toggleTeamStatus(teamId) {
        const team = this.teams.find(t => t.id === teamId);
        if (!team) {
            this.showNotification('Không tìm thấy đội', 'danger');
            return;
        }
        
        try {
            const response = await fetch(`${this.apiEndpoint}/teams/${teamId}/toggle-status`, {
                method: 'PUT'
            });
            
            const result = await response.json();
            
            if (response.ok && result.success !== false) {
                this.showNotification('Cập nhật trạng thái thành công', 'success');
                this.loadData();
            } else {
                this.showNotification(result.message || 'Cập nhật trạng thái thất bại', 'danger');
            }
        } catch (error) {
            console.error('Error toggling team status:', error);
            this.showNotification('Không thể cập nhật trạng thái', 'danger');
        }
    }
    
    /**
     * Manage zones for a team
     */
    async manageZones(teamId) {
        const team = this.teams.find(t => t.id === teamId);
        if (!team) {
            this.showNotification('Không tìm thấy đội', 'danger');
            return;
        }
        
        // Set team info
        document.getElementById('manageZonesTeamId').value = team.id;
        document.getElementById('manageZonesTeamName').textContent = team.name;
        
        // Populate available zones
        const teamZoneIds = team.zones ? team.zones.map(z => z.id) : [];
        const availableZones = this.zones.filter(z => !teamZoneIds.includes(z.id));
        
        const dropdown = document.getElementById('addZoneSelect');
        dropdown.innerHTML = '<option value="">Chọn khu vực</option>' +
            availableZones.map(zone => 
                `<option value="${zone.id}">${this.escapeHtml(zone.name)}</option>`
            ).join('');
        
        // Render current zones
        this.renderCurrentZones(team.zones || []);
        
        // Show modal
        const modal = new bootstrap.Modal(document.getElementById('manageZonesModal'));
        modal.show();
    }
    
    /**
     * Render current zones
     */
    renderCurrentZones(zones) {
        const container = document.getElementById('currentZonesList');
        if (!container) return;
        
        if (zones.length === 0) {
            container.innerHTML = '<div class="text-muted small">Chưa có khu vực nào</div>';
            return;
        }
        
        container.innerHTML = zones.map(zone => `
            <div class="col-md-6 col-lg-4">
                <div class="d-flex align-items-center bg-light rounded p-2">
                    <i class="bi bi-geo-alt-fill me-2 text-primary"></i>
                    <span class="flex-grow-1 small">${this.escapeHtml(zone.name)}</span>
                    <button class="btn btn-sm btn-link text-danger p-0 ms-2" onclick="removeZoneFromTeam(${zone.mappingId}, '${this.escapeHtml(zone.name)}')">
                        <i class="bi bi-x-circle"></i>
                    </button>
                </div>
            </div>
        `).join('');
    }
    
    /**
     * Add zone to team
     */
    async addZoneToTeam() {
        const teamId = parseInt(document.getElementById('manageZonesTeamId').value);
        const zoneId = parseInt(document.getElementById('addZoneSelect').value);
        
        if (!zoneId) {
            this.showNotification('Vui lòng chọn khu vực', 'warning');
            return;
        }
        
        try {
            const response = await fetch(`${this.apiEndpoint}/teams/${teamId}/zones`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ zoneId })
            });
            
            const result = await response.json();
            
            if (response.ok && result.success !== false) {
                this.showNotification('Thêm khu vực thành công', 'success');
                this.loadData();
                this.manageZones(teamId); // Refresh modal
            } else {
                this.showNotification(result.message || 'Thêm khu vực thất bại', 'danger');
            }
        } catch (error) {
            console.error('Error adding zone:', error);
            this.showNotification('Không thể thêm khu vực', 'danger');
        }
    }
    
    /**
     * Remove zone from team
     */
    async removeZoneFromTeam(mappingId, zoneName) {
        if (!confirm(`Xóa khu vực "${zoneName}" khỏi đội?`)) return;
        
        try {
            const response = await fetch(`${this.apiEndpoint}/teams/zones/${mappingId}`, {
                method: 'DELETE'
            });
            
            const result = await response.json();
            
            if (response.ok && result.success !== false) {
                this.showNotification('Xóa khu vực thành công', 'success');
                const teamId = parseInt(document.getElementById('manageZonesTeamId').value);
                this.loadData();
                this.manageZones(teamId); // Refresh modal
            } else {
                this.showNotification(result.message || 'Xóa khu vực thất bại', 'danger');
            }
        } catch (error) {
            console.error('Error removing zone:', error);
            this.showNotification('Không thể xóa khu vực', 'danger');
        }
    }
    
    /**
     * Helper: Show notification
     */
    showNotification(message, type = 'info') {
        if (window.notificationSystem) {
            window.notificationSystem.show(message, type);
        } else {
            console.log(`[${type.toUpperCase()}]: ${message}`);
        }
    }
    
    /**
     * Helper: Escape HTML
     */
    escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }
}

// Global instance
let deliveryTeamsManagement;

// Initialize Delivery Teams Management
function initializeDeliveryTeamsManagement() {
    if (deliveryTeamsManagement) {
        delete deliveryTeamsManagement;
    }
    deliveryTeamsManagement = new DeliveryTeamsManagement();
}

// Global functions for onclick handlers
function createTeam() {
    if (deliveryTeamsManagement) {
        deliveryTeamsManagement.createTeam();
    }
}

function editTeam(teamId) {
    if (deliveryTeamsManagement) {
        deliveryTeamsManagement.editTeam(teamId);
    }
}

function updateTeam() {
    if (deliveryTeamsManagement) {
        deliveryTeamsManagement.updateTeam();
    }
}

function toggleTeamStatus(teamId) {
    if (deliveryTeamsManagement) {
        deliveryTeamsManagement.toggleTeamStatus(teamId);
    }
}

function manageZones(teamId) {
    if (deliveryTeamsManagement) {
        deliveryTeamsManagement.manageZones(teamId);
    }
}

function addZoneToTeam() {
    if (deliveryTeamsManagement) {
        deliveryTeamsManagement.addZoneToTeam();
    }
}

function removeZoneFromTeam(mappingId, zoneName) {
    if (deliveryTeamsManagement) {
        deliveryTeamsManagement.removeZoneFromTeam(mappingId, zoneName);
    }
}

// Export for admin.js
window.initializeDeliveryTeamsManagement = initializeDeliveryTeamsManagement;

