import axios from 'axios'

const GATEWAY_URL = 'http://localhost:8080'

const headers = {
    'X-User-Id': 'admin-user',
    'X-User-Email': 'admin@apimanagement.com',
}

const api = axios.create({
    baseURL: GATEWAY_URL,
    headers,
})
// Auth
export const login = (email, password) =>
    api.post('/api/v1/auth/login', { email, password })

export const register = (username, email, password) =>
    api.post('/api/v1/auth/register', { username, email, password })

// Analytics
export const getAnalyticsDaily = (orgId) =>
    api.get(`/api/v1/analytics/${orgId}/daily`)

export const getAnalyticsMonthly = (orgId) =>
    api.get(`/api/v1/analytics/${orgId}/monthly`)

export const getRecentRequests = (orgId) =>
    api.get(`/api/v1/analytics/${orgId}/recent`)

export const getTopEndpoints = (orgId) =>
    api.get(`/api/v1/analytics/${orgId}/endpoints`)

export const getResponseTime = (orgId) =>
    api.get(`/api/v1/analytics/${orgId}/response-time`)

// Billing
export const getBillingPlans = () =>
    api.get('/api/v1/billing/plans')

export const getSubscription = (orgId) =>
    api.get(`/api/v1/billing/subscriptions/${orgId}`)

export const getInvoices = (orgId) =>
    api.get(`/api/v1/billing/invoices/${orgId}`)

export const getCurrentUsage = (orgId) =>
    api.get(`/api/v1/billing/usage/${orgId}/current`)

// Notifications
export const getNotificationLogs = (orgId) =>
    api.get(`/api/v1/notifications/${orgId}/logs`)

export const getPreferences = (orgId) =>
    api.get(`/api/v1/notifications/${orgId}/preferences`)

export const getWebhooks = (orgId) =>
    api.get(`/api/v1/notifications/${orgId}/webhooks`)

// API Keys
export const getApiKeys = (orgId) =>
    api.get(`/api/v1/keys/organization/${orgId}`)

export const deleteApiKey = (keyId) =>
    api.delete(`/api/v1/keys/${keyId}`)

// Organizations
export const getMyOrganizations = () =>
    api.get('/api/v1/organizations/my')

export const getOrgMembers = (slug) =>
    api.get(`/api/v1/organizations/${slug}/members`)