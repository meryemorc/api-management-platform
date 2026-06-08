import { useState, useEffect } from 'react'
import { Key, Copy, Trash2, Plus } from 'lucide-react'
import { getApiKeys } from '../services/api'
import { apiKeys as mockApiKeys } from '../data/mockData'

const ORG_ID = '7611c924-e3dd-4ce7-a933-b89efbe9959e'

export default function ApiKeys() {
    const [realKeys, setRealKeys] = useState(null)

    useEffect(() => {
        getApiKeys(ORG_ID)
            .then(res => setRealKeys(Array.isArray(res.data) ? res.data : null))
            .catch(() => setRealKeys(null))
    }, [])

    const activeKeys = realKeys || mockApiKeys

    return (
        <div className="p-6 space-y-5">
            <div className="flex justify-end">
                <button className="flex items-center gap-2 text-sm px-4 py-2 rounded-lg font-medium transition-all"
                        style={{ background: 'rgba(255,255,255,0.06)', border: '1px solid rgba(255,255,255,0.1)', color: '#fff' }}>
                    <Plus size={14} />
                    Create API Key
                </button>
            </div>

            <div className="space-y-3">
                {activeKeys.map((k) => {
                    const keyValue = k.keyValue || k.key
                    const name = k.name
                    const org = k.organizationId || k.org
                    const active = k.isActive ?? k.active
                    const used = k.requests || 0
                    const limit = k.dailyRequestLimit || k.limit || 1000

                    return (
                        <div key={k.id} className="rounded-xl p-5 transition-all"
                             style={{ background: 'rgba(255,255,255,0.02)', border: '1px solid rgba(255,255,255,0.06)' }}
                             onMouseEnter={e => e.currentTarget.style.border = '1px solid rgba(255,255,255,0.12)'}
                             onMouseLeave={e => e.currentTarget.style.border = '1px solid rgba(255,255,255,0.06)'}>
                            <div className="flex items-start justify-between">
                                <div className="flex items-center gap-3">
                                    <div className="w-9 h-9 rounded-lg flex items-center justify-center"
                                         style={{ background: 'rgba(255,255,255,0.06)', border: '1px solid rgba(255,255,255,0.08)' }}>
                                        <Key size={15} className="text-white" />
                                    </div>
                                    <div>
                                        <div className="flex items-center gap-2">
                                            <p className="text-white font-medium text-sm">{name}</p>
                                            <span className="text-xs px-2 py-0.5 rounded-md"
                                                  style={{
                                                      background: active ? 'rgba(34,197,94,0.08)' : 'rgba(255,255,255,0.04)',
                                                      color: active ? '#22c55e' : '#52525b'
                                                  }}>
                                                {active ? 'Active' : 'Inactive'}
                                            </span>
                                        </div>
                                        <p className="text-xs mt-0.5" style={{ color: '#52525b' }}>
                                            {typeof org === 'string' && org.includes('-') ? `org: ${org.substring(0, 8)}...` : org}
                                        </p>
                                    </div>
                                </div>
                                <div className="flex items-center gap-1">
                                    <button className="p-2 rounded-lg transition-colors hover:bg-white/5"
                                            style={{ color: '#52525b' }}
                                            onClick={() => navigator.clipboard.writeText(keyValue)}>
                                        <Copy size={14} />
                                    </button>
                                    <button className="p-2 rounded-lg transition-colors hover:bg-red-500/10"
                                            style={{ color: '#52525b' }}>
                                        <Trash2 size={14} />
                                    </button>
                                </div>
                            </div>

                            <div className="mt-4 font-mono text-xs px-3 py-2 rounded-lg"
                                 style={{ background: 'rgba(255,255,255,0.03)', border: '1px solid rgba(255,255,255,0.06)', color: '#52525b' }}>
                                {keyValue ? keyValue.substring(0, 20) : ''}••••••••••••••••••••
                            </div>

                            <div className="mt-4">
                                <div className="flex justify-between text-xs mb-1.5"
                                     style={{ color: '#52525b' }}>
                                    <span>Daily Usage</span>
                                    <span>{used.toLocaleString()} / {limit.toLocaleString()}</span>
                                </div>
                                <div className="w-full rounded-full h-1" style={{ background: 'rgba(255,255,255,0.06)' }}>
                                    <div className="h-1 rounded-full transition-all"
                                         style={{
                                             width: `${Math.min((used / limit) * 100, 100)}%`,
                                             background: (used / limit) > 0.8 ? 'rgba(239,68,68,0.6)' : 'rgba(255,255,255,0.3)'
                                         }} />
                                </div>
                            </div>
                        </div>
                    )
                })}
            </div>
        </div>
    )
}