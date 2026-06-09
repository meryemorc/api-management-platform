import { useState, useEffect } from 'react'
import { Key, Copy, Trash2, Plus, X, Zap } from 'lucide-react'
import { getApiKeys, createApiKey, deleteApiKey } from '../services/api'
import { apiKeys as mockApiKeys } from '../data/mockData'

const ORG_ID = 'bd207ad8-fe5a-4a82-8cef-3c0d34338968'

export default function ApiKeys() {
    const [realKeys, setRealKeys] = useState(null)
    const [showCreate, setShowCreate] = useState(false)
    const [keyName, setKeyName] = useState('')
    const [dailyLimit, setDailyLimit] = useState('1000')
    const [monthlyLimit, setMonthlyLimit] = useState('30000')
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState('')
    const [copiedId, setCopiedId] = useState(null)
    const [testingId, setTestingId] = useState(null)
    const [testedId, setTestedId] = useState(null)

    const fetchKeys = () => {
        getApiKeys(ORG_ID)
            .then(res => setRealKeys(Array.isArray(res.data) ? res.data : null))
            .catch(() => setRealKeys(null))
    }

    useEffect(() => { fetchKeys() }, [])

    const handleCreate = async () => {
        if (!keyName) return
        setLoading(true)
        setError('')
        try {
            await createApiKey(keyName, ORG_ID, parseInt(dailyLimit), parseInt(monthlyLimit))
            fetchKeys()
            setShowCreate(false)
            setKeyName('')
        } catch (e) {
            setError(e.response?.data?.message || 'Failed to create API key')
        } finally {
            setLoading(false)
        }
    }

    const handleDelete = async (keyId) => {
        try {
            await deleteApiKey(keyId)
            fetchKeys()
        } catch (e) {
            alert('Failed to delete key')
        }
    }

    const handleCopy = (keyValue, id) => {
        navigator.clipboard.writeText(keyValue)
        setCopiedId(id)
        setTimeout(() => setCopiedId(null), 2000)
    }

    const handleTestKey = async (keyValue, id) => {
        setTestingId(id)
        try {
            await fetch(`http://localhost:8080/api/v1/analytics/${ORG_ID}/daily`, {
                headers: { 'X-API-Key': keyValue }
            })
            setTestedId(id)
            setTimeout(() => setTestedId(null), 2000)
        } catch (e) {
            alert('Request failed')
        } finally {
            setTestingId(null)
        }
    }

    const activeKeys = realKeys || mockApiKeys

    return (
        <>
            {showCreate && (
                <div className="fixed inset-0 z-50 flex items-center justify-center"
                     style={{ background: 'rgba(0,0,0,0.7)' }}>
                    <div className="rounded-xl p-6 w-full max-w-md"
                         style={{ background: '#0a0a0a', border: '1px solid rgba(255,255,255,0.1)' }}>
                        <div className="flex items-center justify-between mb-5">
                            <p className="text-white font-medium">Create API Key</p>
                            <button onClick={() => setShowCreate(false)} style={{ color: '#52525b' }}><X size={16} /></button>
                        </div>

                        <div className="mb-4">
                            <label className="block text-xs font-medium mb-1.5" style={{ color: '#a1a1aa' }}>Key Name</label>
                            <input type="text" value={keyName} onChange={e => setKeyName(e.target.value)}
                                   placeholder="Production Key"
                                   className="w-full px-3 py-2 rounded-lg text-sm text-white outline-none"
                                   style={{ background: 'rgba(255,255,255,0.04)', border: '1px solid rgba(255,255,255,0.08)' }}
                                   onFocus={e => e.target.style.border = '1px solid rgba(255,255,255,0.2)'}
                                   onBlur={e => e.target.style.border = '1px solid rgba(255,255,255,0.08)'} />
                        </div>

                        <div className="grid grid-cols-2 gap-3 mb-4">
                            <div>
                                <label className="block text-xs font-medium mb-1.5" style={{ color: '#a1a1aa' }}>Daily Limit</label>
                                <input type="number" value={dailyLimit} onChange={e => setDailyLimit(e.target.value)}
                                       className="w-full px-3 py-2 rounded-lg text-sm text-white outline-none"
                                       style={{ background: 'rgba(255,255,255,0.04)', border: '1px solid rgba(255,255,255,0.08)' }}
                                       onFocus={e => e.target.style.border = '1px solid rgba(255,255,255,0.2)'}
                                       onBlur={e => e.target.style.border = '1px solid rgba(255,255,255,0.08)'} />
                            </div>
                            <div>
                                <label className="block text-xs font-medium mb-1.5" style={{ color: '#a1a1aa' }}>Monthly Limit</label>
                                <input type="number" value={monthlyLimit} onChange={e => setMonthlyLimit(e.target.value)}
                                       className="w-full px-3 py-2 rounded-lg text-sm text-white outline-none"
                                       style={{ background: 'rgba(255,255,255,0.04)', border: '1px solid rgba(255,255,255,0.08)' }}
                                       onFocus={e => e.target.style.border = '1px solid rgba(255,255,255,0.2)'}
                                       onBlur={e => e.target.style.border = '1px solid rgba(255,255,255,0.08)'} />
                            </div>
                        </div>

                        {error && <p className="text-xs mb-3" style={{ color: '#ef4444' }}>{error}</p>}

                        <button onClick={handleCreate} disabled={loading}
                                className="w-full py-2 rounded-lg text-sm font-medium"
                                style={{ background: loading ? 'rgba(255,255,255,0.1)' : '#fff', color: '#000' }}>
                            {loading ? 'Creating...' : 'Create API Key'}
                        </button>
                    </div>
                </div>
            )}

            <div className="p-6 space-y-5">
                <div className="flex justify-end">
                    <button onClick={() => setShowCreate(true)}
                            className="flex items-center gap-2 text-sm px-4 py-2 rounded-lg font-medium transition-all"
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
                                        <button className="flex items-center gap-1 px-2 py-1.5 rounded-lg transition-colors text-xs"
                                                style={{
                                                    background: testedId === k.id ? 'rgba(34,197,94,0.08)' : 'rgba(255,255,255,0.04)',
                                                    color: testedId === k.id ? '#22c55e' : '#52525b'
                                                }}
                                                onClick={() => handleTestKey(keyValue, k.id)}
                                                disabled={testingId === k.id}>
                                            <Zap size={11} />
                                            {testedId === k.id ? 'Sent!' : testingId === k.id ? '...' : 'Test'}
                                        </button>
                                        <button className="p-2 rounded-lg transition-colors hover:bg-white/5"
                                                style={{ color: copiedId === k.id ? '#22c55e' : '#52525b' }}
                                                onClick={() => handleCopy(keyValue, k.id)}>
                                            <Copy size={14} />
                                        </button>
                                        <button className="p-2 rounded-lg transition-colors hover:bg-red-500/10"
                                                style={{ color: '#52525b' }}
                                                onClick={() => handleDelete(k.id)}>
                                            <Trash2 size={14} />
                                        </button>
                                    </div>
                                </div>

                                <div className="mt-4 font-mono text-xs px-3 py-2 rounded-lg"
                                     style={{ background: 'rgba(255,255,255,0.03)', border: '1px solid rgba(255,255,255,0.06)', color: '#52525b' }}>
                                    {keyValue ? keyValue.substring(0, 20) : ''}••••••••••••••••••••
                                </div>

                                <div className="mt-4">
                                    <div className="flex justify-between text-xs mb-1.5" style={{ color: '#52525b' }}>
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
        </>
    )
}