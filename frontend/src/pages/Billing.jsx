import { invoices as mockInvoices, plans as mockPlans } from '../data/mockData'
import { CheckCircle, Clock, XCircle, TrendingUp, DollarSign, Users, CreditCard } from 'lucide-react'
import { useState, useEffect } from 'react'
import { getBillingPlans, getInvoices, getSubscription, getCurrentUsage } from '../services/api'

const ORG_ID = '7611c924-e3dd-4ce7-a933-b89efbe9959e'

const planColors = {
    FREE: { bg: 'rgba(255,255,255,0.04)', color: '#71717a' },
    STARTER: { bg: 'rgba(59,130,246,0.08)', color: '#3b82f6' },
    PRO: { bg: 'rgba(255,255,255,0.08)', color: '#a1a1aa' },
    ENTERPRISE: { bg: 'rgba(168,85,247,0.08)', color: '#a855f7' },
}

const statusConfig = {
    PAID: { icon: CheckCircle, color: '#22c55e', bg: 'rgba(34,197,94,0.08)' },
    OPEN: { icon: Clock, color: '#f59e0b', bg: 'rgba(245,158,11,0.08)' },
    VOID: { icon: XCircle, color: '#ef4444', bg: 'rgba(239,68,68,0.08)' },
}

export default function Billing() {
    const [realPlans, setRealPlans] = useState(null)
    const [realInvoices, setRealInvoices] = useState(null)
    const [subscription, setSubscription] = useState(null)
    const [usage, setUsage] = useState(null)

    useEffect(() => {
        getBillingPlans()
            .then(res => setRealPlans(res.data))
            .catch(() => setRealPlans(null))

        getInvoices(ORG_ID)
            .then(res => setRealInvoices(Array.isArray(res.data) ? res.data : null))
            .catch(() => setRealInvoices(null))

        getSubscription(ORG_ID)
            .then(res => setSubscription(res.data))
            .catch(() => setSubscription(null))

        getCurrentUsage(ORG_ID)
            .then(res => setUsage(res.data))
            .catch(() => setUsage(null))
    }, [])

    const activePlans = realPlans || mockPlans
    const activeInvoices = realInvoices || mockInvoices
    const openInvoices = activeInvoices.filter(i => i.status === 'OPEN').length
    const totalRevenue = mockPlans.reduce((acc, p) => acc + p.revenue, 0)
    const totalSubscribers = mockPlans.reduce((acc, p) => acc + p.subscribers, 0)

    return (
        <div className="p-6 space-y-5">
            {/* Top Stats */}
            <div className="grid grid-cols-3 gap-3">
                {[
                    {
                        icon: DollarSign,
                        label: 'Monthly Revenue',
                        value: subscription ? `$${subscription.plan?.monthlyPrice ?? totalRevenue}` : `$${totalRevenue.toLocaleString()}`,
                        sub: '+8.2% vs last month',
                        positive: true
                    },
                    {
                        icon: Users,
                        label: 'Active Plan',
                        value: subscription ? subscription.plan?.name : totalSubscribers,
                        sub: subscription ? subscription.billingCycle : '+3 this month',
                        positive: true
                    },
                    {
                        icon: CreditCard,
                        label: 'Open Invoices',
                        value: openInvoices,
                        sub: usage ? `${usage.totalRequests} requests used` : '$151.04 pending',
                        positive: false
                    },
                ].map(({ icon: Icon, label, value, sub, positive }) => (
                    <div key={label} className="rounded-xl p-5 transition-all"
                         style={{ background: 'rgba(255,255,255,0.02)', border: '1px solid rgba(255,255,255,0.06)' }}
                         onMouseEnter={e => e.currentTarget.style.border = '1px solid rgba(255,255,255,0.12)'}
                         onMouseLeave={e => e.currentTarget.style.border = '1px solid rgba(255,255,255,0.06)'}>
                        <div className="flex items-center justify-between mb-4">
                            <p className="text-xs font-medium" style={{ color: '#71717a' }}>{label}</p>
                            <div className="w-8 h-8 rounded-lg flex items-center justify-center"
                                 style={{ background: 'rgba(255,255,255,0.06)', border: '1px solid rgba(255,255,255,0.08)' }}>
                                <Icon size={15} className="text-white" />
                            </div>
                        </div>
                        <p className="text-white text-2xl font-semibold tracking-tight mb-1">{value}</p>
                        <p className="text-xs" style={{ color: positive ? '#22c55e' : '#f59e0b' }}>{sub}</p>
                    </div>
                ))}
            </div>

            {/* Plan Distribution */}
            <div className="grid grid-cols-4 gap-3">
                {activePlans.map((plan) => (
                    <div key={plan.name} className="rounded-xl p-5 transition-all"
                         style={{ background: 'rgba(255,255,255,0.02)', border: '1px solid rgba(255,255,255,0.06)' }}
                         onMouseEnter={e => e.currentTarget.style.border = '1px solid rgba(255,255,255,0.12)'}
                         onMouseLeave={e => e.currentTarget.style.border = '1px solid rgba(255,255,255,0.06)'}>
                        <div className="flex items-center justify-between mb-3">
                            <span className="text-xs font-medium px-2 py-0.5 rounded-md"
                                  style={{ background: planColors[plan.name]?.bg, color: planColors[plan.name]?.color }}>
                                {plan.name}
                            </span>
                            <TrendingUp size={13} style={{ color: '#3f3f46' }} />
                        </div>
                        <p className="text-white text-2xl font-semibold mb-0.5">
                            {plan.subscribers ?? plan.requestLimit?.toLocaleString()}
                        </p>
                        <p className="text-xs mb-3" style={{ color: '#52525b' }}>
                            {plan.subscribers ? 'subscribers' : 'req/month limit'}
                        </p>
                        <div className="w-full rounded-full h-1" style={{ background: 'rgba(255,255,255,0.06)' }}>
                            <div className="h-1 rounded-full" style={{
                                width: `${Math.min((plan.subscribers ?? plan.requestLimit) / (realPlans ? 1000000 : 18) * 100, 100)}%`,
                                background: 'rgba(255,255,255,0.25)'
                            }} />
                        </div>
                        <p className="text-xs mt-2 font-medium" style={{ color: '#22c55e' }}>
                            ${plan.revenue ?? plan.monthlyPrice}/mo
                        </p>
                    </div>
                ))}
            </div>

            {/* Invoices Table */}
            <div className="rounded-xl overflow-hidden" style={{ background: 'rgba(255,255,255,0.02)', border: '1px solid rgba(255,255,255,0.06)' }}>
                <div className="flex items-center justify-between px-5 py-4" style={{ borderBottom: '1px solid rgba(255,255,255,0.06)' }}>
                    <p className="text-white text-sm font-medium">Recent Invoices</p>
                    <button className="text-xs px-3 py-1.5 rounded-lg font-medium transition-all"
                            style={{ background: 'rgba(255,255,255,0.06)', border: '1px solid rgba(255,255,255,0.1)', color: '#fff' }}>
                        Generate Invoice
                    </button>
                </div>
                <table className="w-full">
                    <thead>
                    <tr style={{ borderBottom: '1px solid rgba(255,255,255,0.04)' }}>
                        {['Invoice', 'Amount', 'Status', 'Period', 'Date'].map((h) => (
                            <th key={h} className="text-left px-5 py-3 text-xs font-medium" style={{ color: '#3f3f46' }}>{h}</th>
                        ))}
                    </tr>
                    </thead>
                    <tbody>
                    {activeInvoices.map((inv, i) => {
                        const status = inv.status || 'OPEN'
                        const { icon: StatusIcon, color, bg } = statusConfig[status] || statusConfig.OPEN
                        const invoiceNum = inv.invoiceNumber || inv.id
                        const amount = inv.total ?? inv.amount
                        const date = inv.createdAt
                            ? new Date(inv.createdAt).toLocaleDateString('tr-TR')
                            : inv.date
                        const period = inv.periodStart
                            ? `${new Date(inv.periodStart).toLocaleDateString('tr-TR')} - ${new Date(inv.periodEnd).toLocaleDateString('tr-TR')}`
                            : '-'

                        return (
                            <tr key={inv.id} className="transition-colors"
                                style={{ borderBottom: i < activeInvoices.length - 1 ? '1px solid rgba(255,255,255,0.04)' : 'none' }}
                                onMouseEnter={e => e.currentTarget.style.background = 'rgba(255,255,255,0.02)'}
                                onMouseLeave={e => e.currentTarget.style.background = 'transparent'}>
                                <td className="px-5 py-3 text-sm font-mono" style={{ color: '#a1a1aa' }}>{invoiceNum}</td>
                                <td className="px-5 py-3 text-sm font-medium text-white">${amount}</td>
                                <td className="px-5 py-3">
                                        <span className="flex items-center gap-1.5 text-xs font-medium w-fit px-2 py-0.5 rounded-md"
                                              style={{ background: bg, color }}>
                                            <StatusIcon size={11} />
                                            {status}
                                        </span>
                                </td>
                                <td className="px-5 py-3 text-xs" style={{ color: '#a1a1aa' }}>{period}</td>
                                <td className="px-5 py-3 text-xs" style={{ color: '#52525b' }}>{date}</td>
                            </tr>
                        )
                    })}
                    </tbody>
                </table>
            </div>
        </div>
    )
}