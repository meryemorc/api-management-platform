import { recentNotifications } from '../data/mockData'
import { Bell, Mail, Webhook, CheckCircle, XCircle } from 'lucide-react'

const typeConfig = {
  RATE_LIMIT_WARNING: { color: 'bg-yellow-500/10 text-yellow-400', label: 'Rate Limit Warning' },
  HIGH_ERROR_RATE: { color: 'bg-red-500/10 text-red-400', label: 'High Error Rate' },
  API_KEY_EXPIRING: { color: 'bg-blue-500/10 text-blue-400', label: 'API Key Expiring' },
  RATE_LIMIT_EXCEEDED: { color: 'bg-orange-500/10 text-orange-400', label: 'Rate Limit Exceeded' },
}

export default function Notifications() {
  return (
    <div className="p-6 space-y-6">
      {/* Stats */}
      <div className="grid grid-cols-3 gap-4">
        {[
          { icon: Bell, label: 'Total Sent', value: '1,284', color: 'bg-indigo-600' },
          { icon: Mail, label: 'Email Delivered', value: '1,241', color: 'bg-emerald-600' },
          { icon: Webhook, label: 'Webhook Sent', value: '43', color: 'bg-blue-600' },
        ].map(({ icon: Icon, label, value, color }) => (
          <div key={label} className="bg-gray-900 rounded-xl p-5 border border-gray-800 flex items-center gap-4">
            <div className={`w-12 h-12 rounded-xl ${color} flex items-center justify-center`}>
              <Icon size={22} className="text-white" />
            </div>
            <div>
              <p className="text-gray-400 text-sm">{label}</p>
              <p className="text-white text-xl font-bold">{value}</p>
            </div>
          </div>
        ))}
      </div>

      {/* Notification Log */}
      <div className="bg-gray-900 rounded-xl border border-gray-800">
        <div className="px-5 py-4 border-b border-gray-800">
          <h3 className="text-white font-semibold">Notification Log</h3>
        </div>
        <div className="divide-y divide-gray-800">
          {recentNotifications.map((n) => {
            const config = typeConfig[n.type]
            return (
              <div key={n.id} className="flex items-center justify-between px-5 py-4 hover:bg-gray-800/50 transition-colors">
                <div className="flex items-center gap-4">
                  <div className="w-9 h-9 rounded-lg bg-gray-800 flex items-center justify-center">
                    <Bell size={16} className="text-gray-400" />
                  </div>
                  <div>
                    <span className={`text-xs font-medium px-2.5 py-1 rounded-full ${config.color}`}>
                      {config.label}
                    </span>
                    <p className="text-gray-300 text-sm mt-1">{n.org}</p>
                  </div>
                </div>
                <div className="flex items-center gap-4">
                  <span className="text-gray-500 text-xs">{n.time}</span>
                  <div className={`flex items-center gap-1.5 text-xs font-medium px-2.5 py-1 rounded-full ${
                    n.status === 'SENT' ? 'bg-emerald-500/10 text-emerald-400' : 'bg-red-500/10 text-red-400'
                  }`}>
                    {n.status === 'SENT' ? <CheckCircle size={12} /> : <XCircle size={12} />}
                    {n.status}
                  </div>
                </div>
              </div>
            )
          })}
        </div>
      </div>
    </div>
  )
}