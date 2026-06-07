import { Key, Copy, Trash2, Plus } from 'lucide-react'

const apiKeys = [
    { id: 1, name: 'Production Key', key: 'pk_live_3ea3fc6d1f2d401cbb91fbc09b11e75a', org: 'Acme Corp', requests: 45230, limit: 100000, active: true },
    { id: 2, name: 'Staging Key', key: 'pk_live_0863c2d92e0a41cc91cbc76335004de2', org: 'TechStart', requests: 8920, limit: 10000, active: true },
    { id: 3, name: 'Dev Key', key: 'pk_live_abc123def456ghi789jkl012mno345p', org: 'DevCo', requests: 320, limit: 1000, active: false },
]

export default function ApiKeys() {
    return (
        <div className="p-6 space-y-6">
            <div className="flex justify-end">
                <button className="flex items-center gap-2 bg-indigo-600 hover:bg-indigo-700 text-white text-sm px-4 py-2 rounded-lg transition-colors">
                    <Plus size={16} />
                    Create API Key
                </button>
            </div>

            <div className="space-y-3">
                {apiKeys.map((k) => (
                    <div key={k.id} className="bg-gray-900 rounded-xl p-5 border border-gray-800">
                        <div className="flex items-start justify-between">
                            <div className="flex items-center gap-3">
                                <div className="w-10 h-10 rounded-lg bg-indigo-600/20 flex items-center justify-center">
                                    <Key size={18} className="text-indigo-400" />
                                </div>
                                <div>
                                    <div className="flex items-center gap-2">
                                        <p className="text-white font-medium text-sm">{k.name}</p>
                                        <span className={`text-xs px-2 py-0.5 rounded-full ${
                                            k.active ? 'bg-emerald-500/10 text-emerald-400' : 'bg-gray-500/10 text-gray-400'
                                        }`}>
                      {k.active ? 'Active' : 'Inactive'}
                    </span>
                                    </div>
                                    <p className="text-gray-500 text-xs mt-0.5">{k.org}</p>
                                </div>
                            </div>
                            <div className="flex items-center gap-2">
                                <button className="p-2 text-gray-500 hover:text-gray-300 transition-colors">
                                    <Copy size={16} />
                                </button>
                                <button className="p-2 text-gray-500 hover:text-red-400 transition-colors">
                                    <Trash2 size={16} />
                                </button>
                            </div>
                        </div>

                        <div className="mt-4 font-mono text-xs text-gray-500 bg-gray-800 rounded-lg px-3 py-2">
                            {k.key.substring(0, 20)}••••••••••••••••••••
                        </div>

                        <div className="mt-4">
                            <div className="flex justify-between text-xs text-gray-400 mb-1.5">
                                <span>Usage</span>
                                <span>{k.requests.toLocaleString()} / {k.limit.toLocaleString()}</span>
                            </div>
                            <div className="w-full bg-gray-800 rounded-full h-1.5">
                                <div
                                    className={`h-1.5 rounded-full ${
                                        (k.requests / k.limit) > 0.8 ? 'bg-red-500' : 'bg-indigo-500'
                                    }`}
                                    style={{ width: `${Math.min((k.requests / k.limit) * 100, 100)}%` }}
                                />
                            </div>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    )
}