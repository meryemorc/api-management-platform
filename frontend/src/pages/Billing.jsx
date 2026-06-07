import { invoices, plans } from '../data/mockData'
import { CheckCircle, Clock, XCircle, TrendingUp, DollarSign, Users, CreditCard } from 'lucide-react'

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

const tooltipStyle = {
    contentStyle: {
        background: '#0a0a0a',
        border: '1px solid rgba(255,255,255,0.1)',
        borderRadius: '8px',
        fontSize: '12px',
        color: '#fff'
    },
}

export default function Billing() {
    const totalRevenue = plans.reduce((acc, p) => acc + p.revenue, 0)
    const totalSubscribers = plans.reduce((acc, p) => acc + p.subscribers, 0)

    return (
        <div className="p-6 space-y-5">
            {/* Top Stats */}
            <div className="grid grid-cols-3 gap-3">
                {[
                    { icon: DollarSign, label: 'Monthly Revenue', value: `$${totalRevenue.toLocaleString()}`, sub: '+8.2% vs last month', positive: true },
                    { icon: Users, label: 'Total Subscribers', value: totalSubscribers, sub: '+3 this month', positive: true },
                    { icon: CreditCard, label: 'Open Invoices', value: '2', sub: '$151.04 pending', positive: false },
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
                {plans.map((plan) => (
                    <div key={plan.name} className="rounded-xl p-5 transition-all"
                         style={{ background: 'rgba(255,255,255,0.02)', border: '1px solid rgba(255,255,255,0.06)' }}
                         onMouseEnter={e => e.currentTarget.style.border = '1px solid rgba(255,255,255,0.12)'}
                         onMouseLeave={e => e.currentTarget.style.border = '1px solid rgba(255,255,255,0.06)'}>
                        <div className="flex items-center justify-between mb-3">
              <span className="text-xs font-medium px-2 py-0.5 rounded-md"
                    style={{ background: planColors[plan.name].bg, color: planColors[plan.name].color }}>
                {plan.name}
              </span>
                            <TrendingUp size={13} style={{ color: '#3f3f46' }} />
                        </div>
                        <p className="text-white text-2xl font-semibold mb-0.5">{plan.subscribers}</p>
                        <p className="text-xs mb-3" style={{ color: '#52525b' }}>subscribers</p>
                        <div className="w-full rounded-full h-1" style={{ background: 'rgba(255,255,255,0.06)' }}>
                            <div className="h-1 rounded-full" style={{
                                width: `${(plan.subscribers / 18) * 100}%`,
                                background: 'rgba(255,255,255,0.25)'
                            }} />
                        </div>
                        <p className="text-xs mt-2 font-medium" style={{ color: '#22c55e' }}>
                            ${plan.revenue.toLocaleString()}/mo
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
                        {['Invoice', 'Organization', 'Plan', 'Amount', 'Status', 'Date'].map((h) => (
                            <th key={h} className="text-left px-5 py-3 text-xs font-medium" style={{ color: '#3f3f46' }}>{h}</th>
                        ))}
                    </tr>
                    </thead>
                    <tbody>
                    {invoices.map((inv, i) => {
                        const { icon: StatusIcon, color, bg } = statusConfig[inv.status] || statusConfig.OPEN
                        return (
                            <tr key={inv.id}
                                className="transition-colors"
                                style={{ borderBottom: i < invoices.length - 1 ? '1px solid rgba(255,255,255,0.04)' : 'none' }}
                                onMouseEnter={e => e.currentTarget.style.background = 'rgba(255,255,255,0.02)'}
                                onMouseLeave={e => e.currentTarget.style.background = 'transparent'}>
                                <td className="px-5 py-3 text-sm font-mono" style={{ color: '#a1a1aa' }}>{inv.id}</td>
                                <td className="px-5 py-3 text-sm text-white">{inv.org}</td>
                                <td className="px-5 py-3">
                    <span className="text-xs px-2 py-0.5 rounded-md font-medium"
                          style={{ background: planColors[inv.plan].bg, color: planColors[inv.plan].color }}>
                      {inv.plan}
                    </span>
                                </td>
                                <td className="px-5 py-3 text-sm font-medium text-white">${inv.amount}</td>
                                <td className="px-5 py-3">
                    <span className="flex items-center gap-1.5 text-xs font-medium w-fit px-2 py-0.5 rounded-md"
                          style={{ background: bg, color }}>
                      <StatusIcon size={11} />
                        {inv.status}
                    </span>
                                </td>
                                <td className="px-5 py-3 text-xs" style={{ color: '#52525b' }}>{inv.date}</td>
                            </tr>
                        )
                    })}
                    </tbody>
                </table>
            </div>
        </div>
    )
}