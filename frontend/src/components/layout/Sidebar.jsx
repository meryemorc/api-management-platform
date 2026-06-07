import { NavLink } from 'react-router-dom'
import { LayoutDashboard, BarChart3, CreditCard, Bell, Key, Settings, Zap, ChevronRight, Globe, BookOpen, Users, Building2 } from 'lucide-react'

const navItems = [
    { to: '/', icon: LayoutDashboard, label: 'Dashboard' },
    { to: '/analytics', icon: BarChart3, label: 'Analytics' },
    { to: '/organizations', icon: Building2, label: 'Organizations' },
    { to: '/users', icon: Users, label: 'Users' },
    { to: '/billing', icon: CreditCard, label: 'Billing' },
    { to: '/notifications', icon: Bell, label: 'Notifications' },
    { to: '/api-keys', icon: Key, label: 'API Keys' },
    { to: '/landing', icon: Globe, label: 'Landing Page' },
    { to: '/docs', icon: BookOpen, label: 'Documentation' },
    { to: '/settings', icon: Settings, label: 'Settings' },
]
export default function Sidebar() {
    return (
        <div
            className="w-60 min-h-screen flex flex-col"
            style={{
                background: 'rgba(255,255,255,0.02)',
                backdropFilter: 'blur(20px)',
                borderRight: '1px solid rgba(255,255,255,0.06)',
            }}
        >
            {/* Logo */}
            <div className="flex items-center gap-3 px-5 py-5"
                 style={{ borderBottom: '1px solid rgba(255,255,255,0.06)' }}>
                <div className="w-7 h-7 rounded-lg flex items-center justify-center"
                     style={{ background: 'rgba(255,255,255,0.1)', border: '1px solid rgba(255,255,255,0.15)' }}>
                    <Zap size={14} className="text-white" />
                </div>
                <div>
                    <p className="text-white font-semibold text-sm leading-none">API Platform</p>
                    <p className="text-xs mt-0.5" style={{ color: '#52525b' }}>Management</p>
                </div>
            </div>

            {/* Nav */}
            <nav className="flex-1 px-3 py-4 space-y-0.5">
                {navItems.map(({ to, icon: Icon, label }) => (
                    <NavLink
                        key={to}
                        to={to}
                        end={to === '/'}
                        className={({ isActive }) =>
                            `flex items-center justify-between px-3 py-2 rounded-lg text-sm transition-all group ${
                                isActive ? 'text-white' : 'text-zinc-500 hover:text-white'
                            }`
                        }
                        style={({ isActive }) => isActive ? {
                            background: 'rgba(255,255,255,0.08)',
                            border: '1px solid rgba(255,255,255,0.1)',
                        } : {}}
                    >
                        {({ isActive }) => (
                            <>
                                <div className="flex items-center gap-2.5">
                                    <Icon size={15} />
                                    <span className="font-medium">{label}</span>
                                </div>
                                {isActive && <ChevronRight size={13} className="text-zinc-500" />}
                            </>
                        )}
                    </NavLink>
                ))}
            </nav>

            {/* Footer */}
            <div className="px-4 py-4" style={{ borderTop: '1px solid rgba(255,255,255,0.06)' }}>
                <div className="flex items-center gap-3 px-2 py-2 rounded-lg cursor-pointer hover:bg-white/5 transition-colors">
                    <div className="w-7 h-7 rounded-full flex items-center justify-center text-xs font-bold text-white"
                         style={{ background: 'rgba(255,255,255,0.1)', border: '1px solid rgba(255,255,255,0.1)' }}>
                        A
                    </div>
                    <div>
                        <p className="text-white text-xs font-medium leading-none">Admin</p>
                        <p className="text-xs mt-0.5" style={{ color: '#52525b' }}>admin@platform.com</p>
                    </div>
                </div>
            </div>
        </div>
    )
}