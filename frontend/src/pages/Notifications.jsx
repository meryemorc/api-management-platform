import { recentNotifications } from '../data/mockData'
import { Bell, Mail, Webhook, CheckCircle, XCircle, AlertTriangle, Key, TrendingDown } from 'lucide-react'

const typeConfig = {
    RATE_LIMIT_WARNING: { icon: AlertTriangle, bg: 'rgba(234,179,8,0.08)', color: '#ca8a04', dot: '#ca8a04', label: 'Rate Limit Warning' },
    HIGH_ERROR_RATE: { icon: TrendingDown, bg: 'rgba(239,68,68,0.08)', color: '#ef4444', dot: '#ef4444', label: 'High Error Rate' },
    API_KEY_EXPIRING: { icon: Key, bg: 'rgba(59,130,246,0.08)', color: '#3b82f6', dot: '#3b82f6', label: 'API Key Expiring' },
    RATE_LIMIT_EXCEEDED: { icon: AlertTriangle, bg: 'rgba(249,115,22,0.08)', color: '#f97316', dot: '#f97316', label: 'Rate Limit Exceeded' },
}

const preferences = [
    { type: 'Rate Limit Warning', desc: 'Alert when usage exceeds 80%', email: true, webhook: false },
    { type: 'Rate Limit Exceeded', desc: 'Alert when limit is reached', email: true, webhook: true },
    { type: 'API Key Expiring', desc: 'Alert 7 days before expiry', email: true, webhook: false },
    { type: 'High Error Rate', desc: 'Alert when error rate exceeds 10%', email: true, webhook: true },
    { type: 'Daily Report', desc: 'Daily usage summary email', email: true, webhook: false },
]

export default function Notifications() {
    return (
        <div className="p-6 space-y-5">
            {/* Stats */}
            <div className="grid grid-cols-3 gap-3">
                {[
                    { icon: Bell, label: 'Total Sent', value: '1,284', sub: 'All time' },
                    { icon: Mail, label: 'Email Delivered', value: '1,241', sub: '96.7% delivery rate' },
                    { icon: Webhook, label: 'Webhook Sent', value: '43', sub: '100% success rate' },
                ].map(({ icon: Icon, label, value, sub }) => (
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
                        <p className="text-xs" style={{ color: '#52525b' }}>{sub}</p>
                    </div>
                ))}
            </div>

            <div className="grid grid-cols-2 gap-4">
                {/* Notification Log */}
                <div className="rounded-xl overflow-hidden" style={{ background: 'rgba(255,255,255,0.02)', border: '1px solid rgba(255,255,255,0.06)' }}>
                    <div className="px-5 py-4" style={{ borderBottom: '1px solid rgba(255,255,255,0.06)' }}>
                        <p className="text-white text-sm font-medium">Recent Notifications</p>
                    </div>
                    <div>
                        {recentNotifications.map((n, i) => {
                            const cfg = typeConfig[n.type]
                            const Icon = cfg.icon
                            return (
                                <div key={n.id} className="flex items-center justify-between px-5 py-3 transition-colors"
                                     style={{ borderBottom: i < recentNotifications.length - 1 ? '1px solid rgba(255,255,255,0.04)' : 'none' }}
                                     onMouseEnter={e => e.currentTarget.style.background = 'rgba(255,255,255,0.02)'}
                                     onMouseLeave={e => e.currentTarget.style.background = 'transparent'}>
                                    <div className="flex items-center gap-3">
                                        <div className="w-7 h-7 rounded-lg flex items-center justify-center"
                                             style={{ background: cfg.bg }}>
                                            <Icon size={13} style={{ color: cfg.color }} />
                                        </div>
                                        <div>
                                            <p className="text-xs font-medium text-white">{cfg.label}</p>
                                            <p className="text-xs" style={{ color: '#52525b' }}>{n.org}</p>
                                        </div>
                                    </div>
                                    <div className="flex items-center gap-2">
                                        <span className="text-xs" style={{ color: '#3f3f46' }}>{n.time}</span>
                                        <span className="flex items-center gap-1 text-xs px-2 py-0.5 rounded-md"
                                              style={{
                                                  background: n.status === 'SENT' ? 'rgba(34,197,94,0.08)' : 'rgba(239,68,68,0.08)',
                                                  color: n.status === 'SENT' ? '#22c55e' : '#ef4444'
                                              }}>
                      {n.status === 'SENT' ? <CheckCircle size={10} /> : <XCircle size={10} />}
                                            {n.status}
                    </span>
                                    </div>
                                </div>
                            )
                        })}
                    </div>
                </div>

                {/* Preferences */}
                <div className="rounded-xl overflow-hidden" style={{ background: 'rgba(255,255,255,0.02)', border: '1px solid rgba(255,255,255,0.06)' }}>
                    <div className="px-5 py-4" style={{ borderBottom: '1px solid rgba(255,255,255,0.06)' }}>
                        <p className="text-white text-sm font-medium">Notification Preferences</p>
                    </div>
                    <div>
                        {preferences.map((pref, i) => (
                            <div key={pref.type} className="flex items-center justify-between px-5 py-3 transition-colors"
                                 style={{ borderBottom: i < preferences.length - 1 ? '1px solid rgba(255,255,255,0.04)' : 'none' }}
                                 onMouseEnter={e => e.currentTarget.style.background = 'rgba(255,255,255,0.02)'}
                                 onMouseLeave={e => e.currentTarget.style.background = 'transparent'}>
                                <div>
                                    <p className="text-xs font-medium text-white">{pref.type}</p>
                                    <p className="text-xs" style={{ color: '#52525b' }}>{pref.desc}</p>
                                </div>
                                <div className="flex items-center gap-2">
                  <span className="flex items-center gap-1 text-xs px-2 py-0.5 rounded-md"
                        style={{
                            background: pref.email ? 'rgba(34,197,94,0.08)' : 'rgba(255,255,255,0.04)',
                            color: pref.email ? '#22c55e' : '#3f3f46'
                        }}>
                    <Mail size={10} /> Email
                  </span>
                                    <span className="flex items-center gap-1 text-xs px-2 py-0.5 rounded-md"
                                          style={{
                                              background: pref.webhook ? 'rgba(34,197,94,0.08)' : 'rgba(255,255,255,0.04)',
                                              color: pref.webhook ? '#22c55e' : '#3f3f46'
                                          }}>
                    <Webhook size={10} /> Webhook
                  </span>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        </div>
    )
}