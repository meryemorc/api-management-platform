import { users } from '../data/mockData'
import { Users as UsersIcon, UserCheck, UserX, Shield } from 'lucide-react'

const roleConfig = {
    ORG_OWNER: { bg: 'rgba(168,85,247,0.08)', color: '#a855f7', label: 'Owner' },
    ORG_ADMIN: { bg: 'rgba(59,130,246,0.08)', color: '#3b82f6', label: 'Admin' },
    ORG_DEVELOPER: { bg: 'rgba(255,255,255,0.06)', color: '#a1a1aa', label: 'Developer' },
}

export default function Users() {
    const activeCount = users.filter(u => u.status === 'active').length
    const inactiveCount = users.filter(u => u.status === 'inactive').length

    return (
        <div className="p-6 space-y-5">
            {/* Stats */}
            <div className="grid grid-cols-3 gap-3">
                {[
                    { icon: UsersIcon, label: 'Total Users', value: users.length, sub: 'Registered accounts' },
                    { icon: UserCheck, label: 'Active Users', value: activeCount, sub: 'Currently active' },
                    { icon: UserX, label: 'Inactive Users', value: inactiveCount, sub: 'Deactivated accounts' },
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

            {/* Users Table */}
            <div className="rounded-xl overflow-hidden" style={{ background: 'rgba(255,255,255,0.02)', border: '1px solid rgba(255,255,255,0.06)' }}>
                <div className="flex items-center justify-between px-5 py-4" style={{ borderBottom: '1px solid rgba(255,255,255,0.06)' }}>
                    <p className="text-white text-sm font-medium">All Users</p>
                    <button className="text-xs px-3 py-1.5 rounded-lg font-medium transition-all"
                            style={{ background: 'rgba(255,255,255,0.06)', border: '1px solid rgba(255,255,255,0.1)', color: '#fff' }}>
                        + Invite User
                    </button>
                </div>
                <table className="w-full">
                    <thead>
                    <tr style={{ borderBottom: '1px solid rgba(255,255,255,0.04)' }}>
                        {['User', 'Organization', 'Role', 'Joined', 'Status'].map(h => (
                            <th key={h} className="text-left px-5 py-3 text-xs font-medium" style={{ color: '#3f3f46' }}>{h}</th>
                        ))}
                    </tr>
                    </thead>
                    <tbody>
                    {users.map((user, i) => {
                        const role = roleConfig[user.role]
                        return (
                            <tr key={user.id} className="transition-colors"
                                style={{ borderBottom: i < users.length - 1 ? '1px solid rgba(255,255,255,0.04)' : 'none' }}
                                onMouseEnter={e => e.currentTarget.style.background = 'rgba(255,255,255,0.02)'}
                                onMouseLeave={e => e.currentTarget.style.background = 'transparent'}>
                                <td className="px-5 py-3">
                                    <div className="flex items-center gap-3">
                                        <div className="w-7 h-7 rounded-full flex items-center justify-center text-xs font-bold text-white"
                                             style={{ background: 'rgba(255,255,255,0.1)', border: '1px solid rgba(255,255,255,0.1)' }}>
                                            {user.username[0].toUpperCase()}
                                        </div>
                                        <div>
                                            <p className="text-sm text-white font-medium">{user.username}</p>
                                            <p className="text-xs" style={{ color: '#52525b' }}>{user.email}</p>
                                        </div>
                                    </div>
                                </td>
                                <td className="px-5 py-3 text-sm" style={{ color: '#a1a1aa' }}>{user.org}</td>
                                <td className="px-5 py-3">
                    <span className="flex items-center gap-1.5 text-xs font-medium w-fit px-2 py-0.5 rounded-md"
                          style={{ background: role.bg, color: role.color }}>
                      <Shield size={10} />
                        {role.label}
                    </span>
                                </td>
                                <td className="px-5 py-3 text-xs" style={{ color: '#52525b' }}>{user.joinedAt}</td>
                                <td className="px-5 py-3">
                    <span className="text-xs px-2 py-0.5 rounded-md"
                          style={{
                              background: user.status === 'active' ? 'rgba(34,197,94,0.08)' : 'rgba(255,255,255,0.04)',
                              color: user.status === 'active' ? '#22c55e' : '#52525b'
                          }}>
                      {user.status}
                    </span>
                                </td>
                            </tr>
                        )
                    })}
                    </tbody>
                </table>
            </div>
        </div>
    )
}