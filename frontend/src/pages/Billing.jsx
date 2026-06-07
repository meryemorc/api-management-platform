import { invoices, plans } from '../data/mockData'
import { CheckCircle, Clock, XCircle } from 'lucide-react'

const statusConfig = {
    PAID: { icon: CheckCircle, color: 'text-emerald-400', bg: 'bg-emerald-500/10' },
    OPEN: { icon: Clock, color: 'text-yellow-400', bg: 'bg-yellow-500/10' },
    VOID: { icon: XCircle, color: 'text-red-400', bg: 'bg-red-500/10' },
}

const planColors = {
    FREE: 'bg-gray-500/10 text-gray-400',
    STARTER: 'bg-blue-500/10 text-blue-400',
    PRO: 'bg-indigo-500/10 text-indigo-400',
    ENTERPRISE: 'bg-purple-500/10 text-purple-400',
}

export default function Billing() {
    return (
        <div className="p-6 space-y-6">
            {/* Plan Distribution */}
            <div className="grid grid-cols-4 gap-4">
                {plans.map((plan) => (
                    <div key={plan.name} className="bg-gray-900 rounded-xl p-5 border border-gray-800">
            <span className={`text-xs font-medium px-2.5 py-1 rounded-full ${planColors[plan.name]}`}>
              {plan.name}
            </span>
                        <p className="text-white text-2xl font-bold mt-3">{plan.subscribers}</p>
                        <p className="text-gray-400 text-sm">subscribers</p>
                        <p className="text-emerald-400 text-sm font-medium mt-1">
                            ${plan.revenue.toLocaleString()}/mo
                        </p>
                    </div>
                ))}
            </div>

            {/* Invoices Table */}
            <div className="bg-gray-900 rounded-xl border border-gray-800">
                <div className="flex items-center justify-between px-5 py-4 border-b border-gray-800">
                    <h3 className="text-white font-semibold">Recent Invoices</h3>
                    <button className="text-xs bg-indigo-600 hover:bg-indigo-700 text-white px-3 py-1.5 rounded-lg transition-colors">
                        Generate Invoice
                    </button>
                </div>
                <table className="w-full">
                    <thead>
                    <tr className="border-b border-gray-800">
                        {['Invoice', 'Organization', 'Plan', 'Amount', 'Status', 'Date'].map((h) => (
                            <th key={h} className="text-left text-gray-500 text-xs font-medium px-5 py-3">{h}</th>
                        ))}
                    </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-800">
                    {invoices.map((inv) => {
                        const { icon: StatusIcon, color, bg } = statusConfig[inv.status] || statusConfig.OPEN
                        return (
                            <tr key={inv.id} className="hover:bg-gray-800/50 transition-colors">
                                <td className="px-5 py-3 text-indigo-400 text-sm font-mono">{inv.id}</td>
                                <td className="px-5 py-3 text-gray-300 text-sm">{inv.org}</td>
                                <td className="px-5 py-3">
                                    <span className={`text-xs px-2.5 py-1 rounded-full ${planColors[inv.plan]}`}>{inv.plan}</span>
                                </td>
                                <td className="px-5 py-3 text-white text-sm font-medium">${inv.amount}</td>
                                <td className="px-5 py-3">
                    <span className={`flex items-center gap-1.5 text-xs font-medium w-fit px-2.5 py-1 rounded-full ${bg} ${color}`}>
                      <StatusIcon size={12} />
                        {inv.status}
                    </span>
                                </td>
                                <td className="px-5 py-3 text-gray-400 text-sm">{inv.date}</td>
                            </tr>
                        )
                    })}
                    </tbody>
                </table>
            </div>
        </div>
    )
}
