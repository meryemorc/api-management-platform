import { useState, useEffect } from 'react'
import { recentNotifications } from '../data/mockData'
import { Bell, Mail, Webhook, CheckCircle, XCircle, AlertTriangle, Key, TrendingDown, Link, Send } from 'lucide-react'
import { getNotificationLogs, getPreferences, getWebhooks } from '../services/api'
import api from '../services/api'

const ORG_ID = 'bd207ad8-fe5a-4a82-8cef-3c0d34338968'

const typeConfig = {
    RATE_LIMIT_WARNING: { icon: AlertTriangle, bg: 'rgba(234,179,8,0.08)', color: '#ca8a04', label: 'Rate Limit Warning' },
    HIGH_ERROR_RATE: { icon: TrendingDown, bg: 'rgba(239,68,68,0.08)', color: '#ef4444', label: 'High Error Rate' },
    API_KEY_EXPIRING: { icon: Key, bg: 'rgba(59,130,246,0.08)', color: '#3b82f6', label: 'API Key Expiring' },
    RATE_LIMIT_EXCEEDED: { icon: AlertTriangle, bg: 'rgba(249,115,22,0.08)', color: '#f97316', label: 'Rate Limit Exceeded' },
}

const mockPreferences = [
    { type: 'Rate Limit Warning', desc: 'Alert when usage exceeds 80%', email: true, webhook: false },
    { type: 'Rate Limit Exceeded', desc: 'Alert when limit is reached', email: true, webhook: true },
    { type: 'API Key Expiring', desc: 'Alert 7 days before expiry', email: true, webhook: false },
    { type: 'High Error Rate', desc: 'Alert when error rate exceeds 10%', email: true, webhook: true },
    { type: 'Daily Report', desc: 'Daily usage summary email', email: true, webhook: false },
]

const NOTIFICATION_TYPES = ['RATE_LIMIT_WARNING', 'HIGH_ERROR_RATE', 'API_KEY_EXPIRING', 'RATE_LIMIT_EXCEEDED']

export default function Notifications() {
    const [realLogs, setRealLogs] = useState(null)
    const [realPrefs, setRealPrefs] = useState(null)
    const [realWebhooks, setRealWebhooks] = useState(null)
    const [sending, setSending] = useState(false)
    const [sent, setSent] = useState(false)
    const [selectedType, setSelectedType] = useState('RATE_LIMIT_WARNING')
    const [showTestModal, setShowTestModal] = useState(false)
    const [testEmail, setTestEmail] = useState('meryyemoruc.c@gmail.com')

    const fetchLogs = () => {
        getNotificationLogs(ORG_ID)
            .then(res => setRealLogs(Array.isArray(res.data) ? res.data : null))
            .catch(() => setRealLogs(null))
    }

    useEffect(() => {
        fetchLogs()
        getPreferences(ORG_ID)
            .then(res => setRealPrefs(res.data))
            .catch(() => setRealPrefs(null))
        getWebhooks(ORG_ID)
            .then(res => setRealWebhooks(Array.isArray(res.data) ? res.data : null))
            .catch(() => setRealWebhooks(null))
    }, [])

    const handleSendTest = async () => {
        setSending(true)
        try {
            await api.post('/api/v1/notifications/test', {
                organizationId: ORG_ID,
                notificationType: selectedType,
                recipientEmail: testEmail
            })
            setSent(true)
            setShowTestModal(false)
            setTimeout(() => {
                setSent(false)
                fetchLogs()
            }, 2000)
        } catch (e) {
            alert('Failed to send notification')
        } finally {
            setSending(false)
        }
    }

    const activeLogs = realLogs || recentNotifications
    const totalSent = realLogs ? realLogs.length : 1284
    const emailSent = realLogs ? realLogs.filter(n => n.channel === 'EMAIL').length : 1241
    const webhookSent = realLogs ? realLogs.filter(n => n.channel === 'WEBHOOK').length : 43

    const displayPreferences = realPrefs ? [
        { type: 'Rate Limit Warning', desc: 'Alert when usage exceeds 80%', email: realPrefs.emailEnabled && realPrefs.rateLimitWarningEnabled, webhook: realPrefs.webhookEnabled && realPrefs.rateLimitWarningEnabled },
        { type: 'Rate Limit Exceeded', desc: 'Alert when limit is reached', email: realPrefs.emailEnabled && realPrefs.rateLimitExceededEnabled, webhook: realPrefs.webhookEnabled && realPrefs.rateLimitExceededEnabled },
        { type: 'API Key Expiring', desc: 'Alert 7 days before expiry', email: realPrefs.emailEnabled && realPrefs.apiKeyExpiringEnabled, webhook: realPrefs.webhookEnabled && realPrefs.apiKeyExpiringEnabled },
        { type: 'High Error Rate', desc: 'Alert when error rate exceeds 10%', email: realPrefs.emailEnabled && realPrefs.highErrorRateEnabled, webhook: realPrefs.webhookEnabled && realPrefs.highErrorRateEnabled },
        { type: 'Daily Report', desc: 'Daily usage summary email', email: realPrefs.emailEnabled && realPrefs.dailyReportEnabled, webhook: false },
    ] : mockPreferences

    return (
        <>
            {showTestModal && (
                <div className="fixed inset-0 z-50 flex items-center justify-center"
                     style={{ background: 'rgba(0,0,0,0.7)' }}>
                    <div className="rounded-xl p-6 w-full max-w-md"
                         style={{ background: '#0a0a0a', border: '1px solid rgba(255,255,255,0.1)' }}>
                        <div className="flex items-center justify-between mb-5">
                            <p className="text-white font-medium">Send Test Notification</p>
                            <button onClick={() => setShowTestModal(false)} style={{ color: '#52525b' }}>✕</button>
                        </div>

                        <div className="mb-4">
                            <label className="block text-xs font-medium mb-1.5" style={{ color: '#a1a1aa' }}>Notification Type</label>
                            <select value={selectedType} onChange={e => setSelectedType(e.target.value)}
                                    className="w-full px-3 py-2 rounded-lg text-sm text-white outline-none"
                                    style={{ background: 'rgba(255,255,255,0.04)', border: '1px solid rgba(255,255,255,0.08)' }}>
                                {NOTIFICATION_TYPES.map(t => (
                                    <option key={t} value={t}>{t.split('_').join(' ')}</option>
                                ))}
                            </select>
                        </div>

                        <div className="mb-4">
                            <label className="block text-xs font-medium mb-1.5" style={{ color: '#a1a1aa' }}>Recipient Email</label>
                            <input type="email" value={testEmail} onChange={e => setTestEmail(e.target.value)}
                                   className="w-full px-3 py-2 rounded-lg text-sm text-white outline-none"
                                   style={{ background: 'rgba(255,255,255,0.04)', border: '1px solid rgba(255,255,255,0.08)' }}
                                   onFocus={e => e.target.style.border = '1px solid rgba(255,255,255,0.2)'}
                                   onBlur={e => e.target.style.border = '1px solid rgba(255,255,255,0.08)'} />
                        </div>

                        <button onClick={handleSendTest} disabled={sending}
                                className="w-full py-2 rounded-lg text-sm font-medium"
                                style={{ background: sending ? 'rgba(255,255,255,0.1)' : '#fff', color: '#000' }}>
                            {sending ? 'Sending...' : 'Send Notification'}
                        </button>
                    </div>
                </div>
            )}

            <div className="p-6 space-y-5">
                <div className="flex justify-end">
                    <button onClick={() => setShowTestModal(true)}
                            className="flex items-center gap-2 text-sm px-4 py-2 rounded-lg font-medium transition-all"
                            style={{
                                background: sent ? 'rgba(34,197,94,0.08)' : 'rgba(255,255,255,0.06)',
                                border: sent ? '1px solid rgba(34,197,94,0.3)' : '1px solid rgba(255,255,255,0.1)',
                                color: sent ? '#22c55e' : '#fff'
                            }}>
                        <Send size={13} />
                        {sent ? 'Notification Sent!' : 'Send Test Notification'}
                    </button>
                </div>

                <div className="grid grid-cols-3 gap-3">
                    {[
                        { icon: Bell, label: 'Total Sent', value: totalSent.toLocaleString(), sub: 'All time' },
                        { icon: Mail, label: 'Email Delivered', value: emailSent.toLocaleString(), sub: '96.7% delivery rate' },
                        { icon: Webhook, label: 'Webhook Sent', value: webhookSent.toLocaleString(), sub: '100% success rate' },
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
                    <div className="rounded-xl overflow-hidden" style={{ background: 'rgba(255,255,255,0.02)', border: '1px solid rgba(255,255,255,0.06)' }}>
                        <div className="px-5 py-4" style={{ borderBottom: '1px solid rgba(255,255,255,0.06)' }}>
                            <p className="text-white text-sm font-medium">Recent Notifications</p>
                        </div>
                        <div>
                            {activeLogs.map((n, i) => {
                                const type = n.notificationType || n.type
                                const cfg = typeConfig[type] || typeConfig.RATE_LIMIT_WARNING
                                const Icon = cfg.icon
                                const status = n.status
                                const label = n.recipientEmail || n.org || '-'
                                const time = n.sentAt
                                    ? new Date(n.sentAt).toLocaleString('tr-TR', { hour: '2-digit', minute: '2-digit', day: '2-digit', month: '2-digit' })
                                    : n.time
                                return (
                                    <div key={n.id} className="flex items-center justify-between px-5 py-3 transition-colors"
                                         style={{ borderBottom: i < activeLogs.length - 1 ? '1px solid rgba(255,255,255,0.04)' : 'none' }}
                                         onMouseEnter={e => e.currentTarget.style.background = 'rgba(255,255,255,0.02)'}
                                         onMouseLeave={e => e.currentTarget.style.background = 'transparent'}>
                                        <div className="flex items-center gap-3">
                                            <div className="w-7 h-7 rounded-lg flex items-center justify-center" style={{ background: cfg.bg }}>
                                                <Icon size={13} style={{ color: cfg.color }} />
                                            </div>
                                            <div>
                                                <p className="text-xs font-medium text-white">{cfg.label}</p>
                                                <p className="text-xs" style={{ color: '#52525b' }}>{label}</p>
                                            </div>
                                        </div>
                                        <div className="flex items-center gap-2">
                                            <span className="text-xs" style={{ color: '#3f3f46' }}>{time}</span>
                                            <span className="flex items-center gap-1 text-xs px-2 py-0.5 rounded-md"
                                                  style={{
                                                      background: status === 'SENT' ? 'rgba(34,197,94,0.08)' : 'rgba(239,68,68,0.08)',
                                                      color: status === 'SENT' ? '#22c55e' : '#ef4444'
                                                  }}>
                                                {status === 'SENT' ? <CheckCircle size={10} /> : <XCircle size={10} />}
                                                {status}
                                            </span>
                                        </div>
                                    </div>
                                )
                            })}
                        </div>
                    </div>

                    <div className="space-y-3">
                        <div className="rounded-xl overflow-hidden" style={{ background: 'rgba(255,255,255,0.02)', border: '1px solid rgba(255,255,255,0.06)' }}>
                            <div className="px-5 py-4" style={{ borderBottom: '1px solid rgba(255,255,255,0.06)' }}>
                                <p className="text-white text-sm font-medium">Notification Preferences</p>
                            </div>
                            <div>
                                {displayPreferences.map((pref, i) => (
                                    <div key={pref.type} className="flex items-center justify-between px-5 py-3 transition-colors"
                                         style={{ borderBottom: i < displayPreferences.length - 1 ? '1px solid rgba(255,255,255,0.04)' : 'none' }}
                                         onMouseEnter={e => e.currentTarget.style.background = 'rgba(255,255,255,0.02)'}
                                         onMouseLeave={e => e.currentTarget.style.background = 'transparent'}>
                                        <div>
                                            <p className="text-xs font-medium text-white">{pref.type}</p>
                                            <p className="text-xs" style={{ color: '#52525b' }}>{pref.desc}</p>
                                        </div>
                                        <div className="flex items-center gap-2">
                                            <span className="flex items-center gap-1 text-xs px-2 py-0.5 rounded-md"
                                                  style={{ background: pref.email ? 'rgba(34,197,94,0.08)' : 'rgba(255,255,255,0.04)', color: pref.email ? '#22c55e' : '#3f3f46' }}>
                                                <Mail size={10} /> Email
                                            </span>
                                            <span className="flex items-center gap-1 text-xs px-2 py-0.5 rounded-md"
                                                  style={{ background: pref.webhook ? 'rgba(34,197,94,0.08)' : 'rgba(255,255,255,0.04)', color: pref.webhook ? '#22c55e' : '#3f3f46' }}>
                                                <Webhook size={10} /> Webhook
                                            </span>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </div>

                        <div className="rounded-xl overflow-hidden" style={{ background: 'rgba(255,255,255,0.02)', border: '1px solid rgba(255,255,255,0.06)' }}>
                            <div className="px-5 py-4" style={{ borderBottom: '1px solid rgba(255,255,255,0.06)' }}>
                                <p className="text-white text-sm font-medium">Active Webhooks</p>
                            </div>
                            {realWebhooks && realWebhooks.length > 0 ? realWebhooks.map((wh, i) => (
                                <div key={wh.id} className="flex items-center justify-between px-5 py-3 transition-colors"
                                     style={{ borderBottom: i < realWebhooks.length - 1 ? '1px solid rgba(255,255,255,0.04)' : 'none' }}
                                     onMouseEnter={e => e.currentTarget.style.background = 'rgba(255,255,255,0.02)'}
                                     onMouseLeave={e => e.currentTarget.style.background = 'transparent'}>
                                    <div className="flex items-center gap-2">
                                        <Link size={12} style={{ color: '#52525b' }} />
                                        <span className="text-xs font-mono" style={{ color: '#a1a1aa' }}>{wh.url}</span>
                                    </div>
                                    <span className="text-xs px-2 py-0.5 rounded-md"
                                          style={{ background: wh.isActive ? 'rgba(34,197,94,0.08)' : 'rgba(255,255,255,0.04)', color: wh.isActive ? '#22c55e' : '#52525b' }}>
                                        {wh.isActive ? 'Active' : 'Inactive'}
                                    </span>
                                </div>
                            )) : (
                                <div className="px-5 py-4">
                                    <p className="text-xs" style={{ color: '#3f3f46' }}>No webhooks configured</p>
                                </div>
                            )}
                        </div>
                    </div>
                </div>
            </div>
        </>
    )
}