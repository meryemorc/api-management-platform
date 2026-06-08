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

// Analytics
export const getAnalyticsDaily = (orgId) =>
    api.get(`/api/v1/analytics/${orgId}/daily`)

export const getAnalyticsMonthly = (orgId) =>
    api.get(`/api/v1/analytics/${orgId}/monthly`)

export const getRecentRequests = (orgId) =>
    api.get(`/api/v1/analytics/${orgId}/recent`)

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

// API Keys
export const getApiKeys = (orgId) =>
    api.get(`/api/v1/organizations/${orgId}/keys`)

export const deleteApiKey = (keyId) =>
    api.delete(`/api/v1/keys/${keyId}`)
export const getMyOrganizations = () =>
    api.get('/api/v1/organizations/my')