import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, PieChart, Pie, Cell } from 'recharts'
import { dailyRequests } from '../data/mockData'

const endpointData = [
    { name: '/api/v1/users/me', calls: 4200 },
    { name: '/api/v1/organizations/my', calls: 3100 },
    { name: '/api/v1/keys', calls: 2800 },
    { name: '/api/v1/analytics', calls: 1900 },
    { name: '/api/v1/billing', calls: 1200 },
]

const statusData = [
    { name: '2xx Success', value: 89, color: '#10B981' },
    { name: '4xx Client Error', value: 8, color: '#F59E0B' },
    { name: '5xx Server Error', value: 3, color: '#EF4444' },
]

export default function Analytics() {
    return (
        <div className="p-6 space-y-6">
            {/* Summary Cards */}
            <div className="grid grid-cols-3 gap-4">
                {[
                    { label: 'Requests Today', value: '3,421', sub: '+12% vs yesterday' },
                    { label: 'Avg Response Time', value: '142ms', sub: '-8ms vs yesterday' },
                    { label: 'Success Rate', value: '97.7%', sub: '+0.3% vs yesterday' },
                ].map((card) => (
                    <div key={card.label} className="bg-gray-900 rounded-xl p-5 border border-gray-800">
                        <p className="text-gray-400 text-sm mb-2">{card.label}</p>
                        <p className="text-white text-2xl font-bold">{card.value}</p>
                        <p className="text-emerald-400 text-xs mt-1">{card.sub}</p>
                    </div>
                ))}
            </div>

            {/* Area Chart */}
            <div className="bg-gray-900 rounded-xl p-5 border border-gray-800">
                <h3 className="text-white font-semibold mb-4">Request Volume — Last 7 Days</h3>
                <ResponsiveContainer width="100%" height={280}>
                    <AreaChart data={dailyRequests}>
                        <defs>
                            <linearGradient id="requestGradient" x1="0" y1="0" x2="0" y2="1">
                                <stop offset="5%" stopColor="#6366F1" stopOpacity={0.3} />
                                <stop offset="95%" stopColor="#6366F1" stopOpacity={0} />
                            </linearGradient>
                        </defs>
                        <CartesianGrid strokeDasharray="3 3" stroke="#374151" />
                        <XAxis dataKey="date" stroke="#6B7280" tick={{ fontSize: 12 }} />
                        <YAxis stroke="#6B7280" tick={{ fontSize: 12 }} />
                        <Tooltip
                            contentStyle={{ backgroundColor: '#1F2937', border: '1px solid #374151', borderRadius: '8px' }}
                            labelStyle={{ color: '#F9FAFB' }}
                        />
                        <Area type="monotone" dataKey="requests" stroke="#6366F1" fill="url(#requestGradient)" strokeWidth={2} name="Requests" />
                        <Area type="monotone" dataKey="errors" stroke="#EF4444" fill="none" strokeWidth={2} name="Errors" />
                    </AreaChart>
                </ResponsiveContainer>
            </div>

            <div className="grid grid-cols-2 gap-4">
                {/* Top Endpoints */}
                <div className="bg-gray-900 rounded-xl p-5 border border-gray-800">
                    <h3 className="text-white font-semibold mb-4">Top Endpoints</h3>
                    <div className="space-y-3">
                        {endpointData.map((ep, i) => (
                            <div key={ep.name}>
                                <div className="flex justify-between text-sm mb-1">
                                    <span className="text-gray-300 font-mono text-xs">{ep.name}</span>
                                    <span className="text-gray-400">{ep.calls.toLocaleString()}</span>
                                </div>
                                <div className="w-full bg-gray-800 rounded-full h-1.5">
                                    <div
                                        className="bg-indigo-500 h-1.5 rounded-full"
                                        style={{ width: `${(ep.calls / endpointData[0].calls) * 100}%` }}
                                    />
                                </div>
                            </div>
                        ))}
                    </div>
                </div>

                {/* Status Codes */}
                <div className="bg-gray-900 rounded-xl p-5 border border-gray-800">
                    <h3 className="text-white font-semibold mb-4">Response Status Distribution</h3>
                    <div className="flex items-center justify-center">
                        <PieChart width={200} height={200}>
                            <Pie data={statusData} cx={100} cy={100} innerRadius={60} outerRadius={90} dataKey="value">
                                {statusData.map((entry, index) => (
                                    <Cell key={index} fill={entry.color} />
                                ))}
                            </Pie>
                            <Tooltip
                                contentStyle={{ backgroundColor: '#1F2937', border: '1px solid #374151', borderRadius: '8px' }}
                            />
                        </PieChart>
                    </div>
                    <div className="space-y-2 mt-2">
                        {statusData.map((s) => (
                            <div key={s.name} className="flex items-center justify-between text-sm">
                                <div className="flex items-center gap-2">
                                    <div className="w-3 h-3 rounded-full" style={{ backgroundColor: s.color }} />
                                    <span className="text-gray-300">{s.name}</span>
                                </div>
                                <span className="text-gray-400">{s.value}%</span>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        </div>
    )
}