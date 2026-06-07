import { Bell, Search, Plus } from 'lucide-react'

export default function Header({ title }) {
    return (
        <header
            className="h-14 flex items-center justify-between px-6"
            style={{
                background: 'rgba(0,0,0,0.8)',
                backdropFilter: 'blur(20px)',
                borderBottom: '1px solid rgba(255,255,255,0.06)',
            }}
        >
            <h1 className="text-white font-semibold text-base">{title}</h1>
            <div className="flex items-center gap-3">
                <div className="relative">
                    <Search size={14} className="absolute left-3 top-1/2 -translate-y-1/2" style={{ color: '#52525b' }} />
                    <input
                        type="text"
                        placeholder="Search..."
                        className="text-sm pl-8 pr-4 py-1.5 rounded-lg text-white placeholder-zinc-600 outline-none w-52 transition-all"
                        style={{
                            background: 'rgba(255,255,255,0.04)',
                            border: '1px solid rgba(255,255,255,0.08)',
                            fontSize: '13px',
                        }}
                    />
                </div>
                <button
                    className="flex items-center gap-1.5 text-xs font-medium px-3 py-1.5 rounded-lg text-white transition-all hover:bg-white/10"
                    style={{
                        background: 'rgba(255,255,255,0.06)',
                        border: '1px solid rgba(255,255,255,0.1)',
                    }}
                >
                    <Plus size={13} />
                    New
                </button>
                <button className="relative p-1.5 rounded-lg hover:bg-white/5 transition-colors" style={{ color: '#52525b' }}>
                    <Bell size={17} />
                    <span className="absolute top-1 right-1 w-1.5 h-1.5 bg-white rounded-full"></span>
                </button>
            </div>
        </header>
    )
}