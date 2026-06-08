import { useState, useEffect } from 'react'
import { ArrowUpRight, ArrowDownRight, Activity, DollarSign, Users, AlertTriangle } from 'lucide-react'
import { LineChart, Line, BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts'
import { stats, dailyRequests, revenueData } from '../data/mockData'
import { getNotificationLogs, getBillingPlans, getAnalyticsDaily } from '../services/api'

const ORG_ID = '7611c924-e3dd-4ce7-a933-b89efbe9959e'

const StatCard = ({ title, value, change, positive, icon: Icon }) => (
    <div className="rounded-xl p-5 transition-all cursor-default"
         style={{ background: 'rgba(255,255,255,0.02)', border: '1px solid rgba(255,255,255,0.06)' }}
         onMouseEnter={e => e.currentTarget.style.border = '1px solid rgba(255,255,255,0.12)'}
         onMouseLeave={e => e.currentTarget.style.border = '1px solid rgba(255,255,255,0.06)'}>
        <div className="flex items-center justify-between mb-4">
            <p className="text-xs font-medium" style={{ color: '#71717a' }}>{title}</p>
            <div className="w-8 h-8 rounded-lg flex items-center justify-center"
                 style={{ background: 'rgba(255,255,255,0.06)', border: '1px solid rgba(255,255,255,0.08)' }}>
                <Icon size={15} className="text-white" />
            </div>
        </div>
        <p className="text-white text-2xl font-semibold tracking-tight mb-2">{value}</p>
        <div className="flex items-center gap-1">
            {positive
                ? <ArrowUpRight size={13} style={{ color: '#22c55e' }} />
                : <ArrowDownRight size={13} style={{ color: '#ef4444' }} />
            }
            <span className="text-xs font-medium" style={{ color: positive ? '#22c55e' : '#ef4444' }}>{change}</span>
            <span className="text-xs" style={{ color: '#3f3f46' }}>&nbsp;vs last month</span>
        </div>
    </div>
)

const tooltipStyle = {
    contentStyle: { background: '#0a0a0a', border: '1px solid rgba(255,255,255,0.1)', borderRadius: '8px', fontSize: '12px', color: '#fff' },
    labelStyle: { color: '#a1a1aa' },
    cursor: { fill: 'rgba(255,255,255,0.03)' }
}

const notificationColors = {
    RATE_LIMIT_WARNING: { bg: 'rgba(234,179,8,0.08)', color: '#ca8a04', dot: '#ca8a04' },
    HIGH_ERROR_RATE: { bg: 'rgba(239,68,68,0.08)', color: '#ef4444', dot: '#ef4444' },
    API_KEY_EXPIRING: { bg: 'rgba(59,130,246,0.08)', color: '#3b82f6', dot: '#3b82f6' },
    RATE_LIMIT_EXCEEDED: { bg: 'rgba(249,115,22,0.08)', color: '#f97316', dot: '#f97316' },
}

export default function Dashboard() {
    const [notifications, setNotifications] = useState(null)
    const [billingPlans, setBillingPlans] = useState(null)
    const [dailyData, setDailyData] = useState(null)

    useEffect(() => {
        getNotificationLogs(ORG_ID)
            .then(res => setNotifications(Array.isArray(res.data) ? res.data : null))
            .catch(() => setNotifications(null))

        getBillingPlans()
            .then(res => setBillingPlans(Array.isArray(res.data) ? res.data : null))
            .catch(() => setBillingPlans(null))

        getAnalyticsDaily(ORG_ID)
            .then(res => setDailyData(res.data))
            .catch(() => setDailyData(null))
    }, [])

    const activeNotifications = notifications || []
    const totalRevenue = billingPlans
        ? billingPlans.reduce((acc, p) => acc + (p.monthlyPrice || 0), 0)
        : stats.totalRevenue

    const chartData = dailyData
        ? (Array.isArray(dailyData) ? dailyData : [dailyData]).map(d => ({
            date: d.date || 'N/A',
            requests: d.totalRequests || 0,
            errors: d.errorCount || 0,
        }))
        : dailyRequests

    const displayNotifications = activeNotifications.length > 0
        ? activeNotifications.slice(0, 5)
        : []

    return (
        <div className="p-6 space-y-5">
            <div className="grid grid-cols-4 gap-3">
                <StatCard title="Total Requests" value={stats.totalRequests.toLocaleString()} change="12.5%" positive icon={Activity} />
                <StatCard title="Monthly Revenue" value={`$${totalRevenue.toLocaleString()}`} change="8.2%" positive icon={DollarSign} />
                <StatCard title="Active Subscriptions" value={stats.activeSubscriptions} change="3 new" positive icon={Users} />
                <StatCard title="Error Rate" value={`${stats.errorRate}%`} change="0.5%" positive={false} icon={AlertTriangle} />
            </div>

            <div className="grid grid-cols-2 gap-4">
                <div className="rounded-xl p-5" style={{ background: 'rgba(255,255,255,0.02)', border: '1px solid rgba(255,255,255,0.06)' }}>
                    <p className="text-white text-sm font-medium mb-5">Daily API Requests</p>
                    <ResponsiveContainer width="100%" height={220}>
                        <BarChart data={chartData} barGap={2}>
                            <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.04)" vertical={false} />
                            <XAxis dataKey="date" stroke="transparent" tick={{ fill: '#3f3f46', fontSize: 11 }} />
                            <YAxis stroke="transparent" tick={{ fill: '#3f3f46', fontSize: 11 }} />
                            <Tooltip {...tooltipStyle} />
                            <Bar dataKey="requests" fill="rgba(255,255,255,0.15)" radius={[3, 3, 0, 0]} name="Requests" />
                            <Bar dataKey="errors" fill="rgba(239,68,68,0.5)" radius={[3, 3, 0, 0]} name="Errors" />
                        </BarChart>
                    </ResponsiveContainer>
                </div>

                <div className="rounded-xl p-5" style={{ background: 'rgba(255,255,255,0.02)', border: '1px solid rgba(255,255,255,0.06)' }}>
                    <p className="text-white text-sm font-medium mb-5">Monthly Revenue</p>
                    <ResponsiveContainer width="100%" height={220}>
                        <LineChart data={revenueData}>
                            <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.04)" vertical={false} />
                            <XAxis dataKey="month" stroke="transparent" tick={{ fill: '#3f3f46', fontSize: 11 }} />
                            <YAxis stroke="transparent" tick={{ fill: '#3f3f46', fontSize: 11 }} />
                            <Tooltip {...tooltipStyle} formatter={(v) => [`$${v}`, 'Revenue']} />
                            <Line type="monotone" dataKey="revenue" stroke="rgba(255,255,255,0.7)" strokeWidth={1.5}
                                  dot={{ fill: '#fff', r: 3, strokeWidth: 0 }} activeDot={{ r: 4, fill: '#fff' }} />
                        </LineChart>
                    </ResponsiveContainer>
                </div>
            </div>

            <div className="rounded-xl overflow-hidden" style={{ background: 'rgba(255,255,255,0.02)', border: '1px solid rgba(255,255,255,0.06)' }}>
                <div className="flex items-center justify-between px-5 py-4" style={{ borderBottom: '1px solid rgba(255,255,255,0.06)' }}>
                    <p className="text-white text-sm font-medium">Recent Notifications</p>
                    <button className="text-xs" style={{ color: '#52525b' }}>View all →</button>
                </div>
                {displayNotifications.length > 0 ? displayNotifications.map((n, i) => {
                    const type = n.notificationType || n.type
                    const cfg = notificationColors[type] || notificationColors.RATE_LIMIT_WARNING
                    const status = n.status
                    const label = n.recipientEmail || n.org || '-'
                    const time = n.sentAt
                        ? new Date(n.sentAt).toLocaleString('tr-TR', { hour: '2-digit', minute: '2-digit', day: '2-digit', month: '2-digit' })
                        : n.time
                    return (
                        <div key={n.id} className="flex items-center justify-between px-5 py-3 hover:bg-white/[0.02] transition-colors"
                             style={{ borderBottom: i < displayNotifications.length - 1 ? '1px solid rgba(255,255,255,0.04)' : 'none' }}>
                            <div className="flex items-center gap-3">
                                <div className="w-1.5 h-1.5 rounded-full" style={{ background: cfg.dot }} />
                                <span className="text-xs px-2 py-0.5 rounded-md font-medium"
                                      style={{ background: cfg.bg, color: cfg.color }}>
                                    {type ? type.split('_').join(' ') : '-'}
                                </span>
                                <span className="text-sm" style={{ color: '#a1a1aa' }}>{label}</span>
                            </div>
                            <div className="flex items-center gap-3">
                                <span className="text-xs" style={{ color: '#3f3f46' }}>{time}</span>
                                <span className="text-xs px-2 py-0.5 rounded-md"
                                      style={{
                                          background: status === 'SENT' ? 'rgba(34,197,94,0.08)' : 'rgba(239,68,68,0.08)',
                                          color: status === 'SENT' ? '#22c55e' : '#ef4444'
                                      }}>
                                    {status}
                                </span>
                            </div>
                        </div>
                    )
                }) : (
                    <div className="px-5 py-8 text-center">
                        <p className="text-xs" style={{ color: '#3f3f46' }}>No notifications yet</p>
                    </div>
                )}
            </div>
        </div>
    )
}