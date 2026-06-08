import { useState, useEffect } from 'react'
import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, PieChart, Pie, Cell } from 'recharts'
import { dailyRequests } from '../data/mockData'
import { TrendingUp, Clock, CheckCircle } from 'lucide-react'
import { getAnalyticsDaily } from '../services/api'

const ORG_ID = '7611c924-e3dd-4ce7-a933-b89efbe9959e'

const endpointData = [
    { name: '/api/v1/users/me', calls: 4200 },
    { name: '/api/v1/organizations/my', calls: 3100 },
    { name: '/api/v1/keys', calls: 2800 },
    { name: '/api/v1/analytics', calls: 1900 },
    { name: '/api/v1/billing', calls: 1200 },
]

const statusData = [
    { name: '2xx Success', value: 89, color: 'rgba(255,255,255,0.7)' },
    { name: '4xx Client Error', value: 8, color: 'rgba(255,255,255,0.25)' },
    { name: '5xx Server Error', value: 3, color: 'rgba(239,68,68,0.6)' },
]

const tooltipStyle = {
    contentStyle: {
        background: '#0a0a0a',
        border: '1px solid rgba(255,255,255,0.1)',
        borderRadius: '8px',
        fontSize: '12px',
        color: '#fff'
    },
    labelStyle: { color: '#a1a1aa' },
    cursor: { fill: 'rgba(255,255,255,0.03)' }
}

export default function Analytics() {
    const [realDaily, setRealDaily] = useState(null)

    useEffect(() => {
        getAnalyticsDaily(ORG_ID)
            .then(res => setRealDaily(res.data))
            .catch(() => setRealDaily(null))
    }, [])

    const chartData = realDaily
        ? (Array.isArray(realDaily) ? realDaily : [realDaily]).map(d => ({
            date: d.date || d._id || 'N/A',
            requests: d.totalRequests || d.requests || 0,
            errors: d.errorCount || d.errors || 0,
        }))
        : dailyRequests

    return (
        <div className="p-6 space-y-5">
            <div className="grid grid-cols-3 gap-3">
                {[
                    { icon: TrendingUp, label: 'Requests Today', value: '3,421', sub: '+12% vs yesterday', positive: true },
                    { icon: Clock, label: 'Avg Response Time', value: '142ms', sub: '-8ms vs yesterday', positive: true },
                    { icon: CheckCircle, label: 'Success Rate', value: '97.7%', sub: '+0.3% vs yesterday', positive: true },
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
                        <p className="text-xs" style={{ color: positive ? '#22c55e' : '#ef4444' }}>{sub}</p>
                    </div>
                ))}
            </div>

            <div className="rounded-xl p-5" style={{ background: 'rgba(255,255,255,0.02)', border: '1px solid rgba(255,255,255,0.06)' }}>
                <p className="text-white text-sm font-medium mb-5">Request Volume — Last 7 Days</p>
                <ResponsiveContainer width="100%" height={260}>
                    <AreaChart data={chartData}>
                        <defs>
                            <linearGradient id="reqGrad" x1="0" y1="0" x2="0" y2="1">
                                <stop offset="5%" stopColor="rgba(255,255,255,0.15)" stopOpacity={1} />
                                <stop offset="95%" stopColor="rgba(255,255,255,0)" stopOpacity={1} />
                            </linearGradient>
                            <linearGradient id="errGrad" x1="0" y1="0" x2="0" y2="1">
                                <stop offset="5%" stopColor="rgba(239,68,68,0.2)" stopOpacity={1} />
                                <stop offset="95%" stopColor="rgba(239,68,68,0)" stopOpacity={1} />
                            </linearGradient>
                        </defs>
                        <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.04)" vertical={false} />
                        <XAxis dataKey="date" stroke="transparent" tick={{ fill: '#3f3f46', fontSize: 11 }} />
                        <YAxis stroke="transparent" tick={{ fill: '#3f3f46', fontSize: 11 }} />
                        <Tooltip {...tooltipStyle} />
                        <Area type="monotone" dataKey="requests" stroke="rgba(255,255,255,0.5)" fill="url(#reqGrad)" strokeWidth={1.5} name="Requests" />
                        <Area type="monotone" dataKey="errors" stroke="rgba(239,68,68,0.6)" fill="url(#errGrad)" strokeWidth={1.5} name="Errors" />
                    </AreaChart>
                </ResponsiveContainer>
            </div>

            <div className="grid grid-cols-2 gap-4">
                <div className="rounded-xl p-5" style={{ background: 'rgba(255,255,255,0.02)', border: '1px solid rgba(255,255,255,0.06)' }}>
                    <p className="text-white text-sm font-medium mb-5">Top Endpoints</p>
                    <div className="space-y-4">
                        {endpointData.map((ep) => (
                            <div key={ep.name}>
                                <div className="flex justify-between mb-1.5">
                                    <span className="text-xs font-mono" style={{ color: '#a1a1aa' }}>{ep.name}</span>
                                    <span className="text-xs" style={{ color: '#52525b' }}>{ep.calls.toLocaleString()}</span>
                                </div>
                                <div className="w-full rounded-full h-1" style={{ background: 'rgba(255,255,255,0.06)' }}>
                                    <div className="h-1 rounded-full" style={{
                                        width: `${(ep.calls / endpointData[0].calls) * 100}%`,
                                        background: 'rgba(255,255,255,0.3)'
                                    }} />
                                </div>
                            </div>
                        ))}
                    </div>
                </div>

                <div className="rounded-xl p-5" style={{ background: 'rgba(255,255,255,0.02)', border: '1px solid rgba(255,255,255,0.06)' }}>
                    <p className="text-white text-sm font-medium mb-5">Response Status Distribution</p>
                    <div className="flex items-center justify-center">
                        <PieChart width={180} height={180}>
                            <Pie data={statusData} cx={90} cy={90} innerRadius={55} outerRadius={80} dataKey="value" strokeWidth={0}>
                                {statusData.map((entry, index) => (
                                    <Cell key={index} fill={entry.color} />
                                ))}
                            </Pie>
                            <Tooltip contentStyle={{ background: '#0a0a0a', border: '1px solid rgba(255,255,255,0.1)', borderRadius: '8px', fontSize: '12px' }} />
                        </PieChart>
                    </div>
                    <div className="space-y-2 mt-2">
                        {statusData.map((s) => (
                            <div key={s.name} className="flex items-center justify-between">
                                <div className="flex items-center gap-2">
                                    <div className="w-2 h-2 rounded-full" style={{ background: s.color }} />
                                    <span className="text-xs" style={{ color: '#a1a1aa' }}>{s.name}</span>
                                </div>
                                <span className="text-xs font-medium text-white">{s.value}%</span>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        </div>
    )
}