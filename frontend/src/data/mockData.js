export const stats = {
    totalRequests: 124853,
    totalRevenue: 8420.50,
    activeSubscriptions: 47,
    errorRate: 2.3,
    requestsToday: 3421,
    requestsThisMonth: 89234,
}

export const dailyRequests = [
    { date: '01 Jun', requests: 4200, errors: 120 },
    { date: '02 Jun', requests: 3800, errors: 95 },
    { date: '03 Jun', requests: 5100, errors: 180 },
    { date: '04 Jun', requests: 4700, errors: 110 },
    { date: '05 Jun', requests: 6200, errors: 200 },
    { date: '06 Jun', requests: 5800, errors: 145 },
    { date: '07 Jun', requests: 3421, errors: 89 },
]

export const revenueData = [
    { month: 'Jan', revenue: 5200 },
    { month: 'Feb', revenue: 6100 },
    { month: 'Mar', revenue: 5800 },
    { month: 'Apr', revenue: 7200 },
    { month: 'May', revenue: 8100 },
    { month: 'Jun', revenue: 8420 },
]

export const recentNotifications = [
    { id: 1, type: 'RATE_LIMIT_WARNING', org: 'Acme Corp', time: '2 min ago', status: 'SENT' },
    { id: 2, type: 'HIGH_ERROR_RATE', org: 'TechStart', time: '15 min ago', status: 'SENT' },
    { id: 3, type: 'API_KEY_EXPIRING', org: 'DevCo', time: '1 hr ago', status: 'SENT' },
    { id: 4, type: 'RATE_LIMIT_EXCEEDED', org: 'BigData Inc', time: '2 hr ago', status: 'FAILED' },
]

export const invoices = [
    { id: 'INV-202606-1000', org: 'Acme Corp', plan: 'PRO', amount: 116.82, status: 'PAID', date: '2026-06-01' },
    { id: 'INV-202606-1001', org: 'TechStart', plan: 'STARTER', amount: 34.22, status: 'OPEN', date: '2026-06-01' },
    { id: 'INV-202606-1002', org: 'DevCo', plan: 'ENTERPRISE', amount: 353.42, status: 'PAID', date: '2026-06-01' },
    { id: 'INV-202606-1003', org: 'BigData Inc', plan: 'PRO', amount: 116.82, status: 'OPEN', date: '2026-06-01' },
]

export const plans = [
    { name: 'FREE', subscribers: 12, revenue: 0, limit: 1000 },
    { name: 'STARTER', subscribers: 18, revenue: 522, limit: 10000 },
    { name: 'PRO', subscribers: 14, revenue: 1386, limit: 100000 },
    { name: 'ENTERPRISE', subscribers: 3, revenue: 897, limit: 1000000 },
]