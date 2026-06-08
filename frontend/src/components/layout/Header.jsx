import { Bell, Search, Plus, LogOut } from 'lucide-react'
import { useAuth } from '../../context/AuthContext'
import { useNavigate } from 'react-router-dom'

export default function Header({ title }) {
    const { user, logoutUser } = useAuth()
    const navigate = useNavigate()

    const handleLogout = () => {
        logoutUser()
        navigate('/login')
    }

    return (
        <header className="h-14 flex items-center justify-between px-6"
                style={{ background: 'rgba(0,0,0,0.8)', backdropFilter: 'blur(20px)', borderBottom: '1px solid rgba(255,255,255,0.06)' }}>
            <h1 className="text-white font-semibold text-base">{title}</h1>
            <div className="flex items-center gap-3">
                <div className="relative">
                    <Search size={14} className="absolute left-3 top-1/2 -translate-y-1/2" style={{ color: '#52525b' }} />
                    <input type="text" placeholder="Search..."
                           className="text-sm pl-8 pr-4 py-1.5 rounded-lg text-white placeholder-zinc-600 outline-none w-52 transition-all"
                           style={{ background: 'rgba(255,255,255,0.04)', border: '1px solid rgba(255,255,255,0.08)', fontSize: '13px' }} />
                </div>
                <button className="flex items-center gap-1.5 text-xs font-medium px-3 py-1.5 rounded-lg text-white transition-all hover:bg-white/10"
                        style={{ background: 'rgba(255,255,255,0.06)', border: '1px solid rgba(255,255,255,0.1)' }}>
                    <Plus size={13} />
                    New
                </button>
                <button className="relative p-1.5 rounded-lg hover:bg-white/5 transition-colors" style={{ color: '#52525b' }}>
                    <Bell size={17} />
                    <span className="absolute top-1 right-1 w-1.5 h-1.5 bg-white rounded-full"></span>
                </button>
                {user && (
                    <div className="flex items-center gap-2 pl-2" style={{ borderLeft: '1px solid rgba(255,255,255,0.08)' }}>
                        <div className="w-6 h-6 rounded-full flex items-center justify-center text-xs font-bold text-white"
                             style={{ background: 'rgba(255,255,255,0.1)' }}>
                            {user.username?.[0]?.toUpperCase() || user.email?.[0]?.toUpperCase()}
                        </div>
                        <span className="text-xs" style={{ color: '#a1a1aa' }}>
                            {user.username || user.email}
                        </span>
                        <button onClick={handleLogout} className="p-1.5 rounded-lg hover:bg-white/5 transition-colors"
                                style={{ color: '#52525b' }}>
                            <LogOut size={13} />
                        </button>
                    </div>
                )}
            </div>
        </header>
    )
}