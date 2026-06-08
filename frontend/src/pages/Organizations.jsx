import { useState, useEffect } from 'react'
import { organizations as mockOrgs } from '../data/mockData'
import { Building2, Users, Key, Activity, Shield, ChevronDown, ChevronUp } from 'lucide-react'
import { getMyOrganizations, getOrgMembers } from '../services/api'

const planColors = {
    FREE: { bg: 'rgba(255,255,255,0.04)', color: '#71717a' },
    STARTER: { bg: 'rgba(59,130,246,0.08)', color: '#3b82f6' },
    PRO: { bg: 'rgba(255,255,255,0.08)', color: '#a1a1aa' },
    ENTERPRISE: { bg: 'rgba(168,85,247,0.08)', color: '#a855f7' },
}

const roleConfig = {
    ORG_OWNER: { bg: 'rgba(168,85,247,0.08)', color: '#a855f7', label: 'Owner' },
    ORG_ADMIN: { bg: 'rgba(59,130,246,0.08)', color: '#3b82f6', label: 'Admin' },
    ORG_DEVELOPER: { bg: 'rgba(255,255,255,0.06)', color: '#a1a1aa', label: 'Developer' },
}

function OrgCard({ org }) {
    const [members, setMembers] = useState(null)
    const [expanded, setExpanded] = useState(false)

    const plan = org.plan || 'FREE'
    const slug = org.slug || org.name?.toLowerCase().replace(' ', '-')
    const status = org.isActive !== undefined ? (org.isActive ? 'active' : 'inactive') : org.status
    const createdAt = org.createdAt ? new Date(org.createdAt).toLocaleDateString('tr-TR') : '-'

    const handleExpand = () => {
        if (!expanded && !members) {
            getOrgMembers(slug)
                .then(res => setMembers(Array.isArray(res.data) ? res.data : []))
                .catch(() => setMembers([]))
        }
        setExpanded(!expanded)
    }

    return (
        <div className="rounded-xl transition-all cursor-pointer"
             style={{ background: 'rgba(255,255,255,0.02)', border: '1px solid rgba(255,255,255,0.06)' }}
             onMouseEnter={e => e.currentTarget.style.border = '1px solid rgba(255,255,255,0.12)'}
             onMouseLeave={e => e.currentTarget.style.border = '1px solid rgba(255,255,255,0.06)'}>
            <div className="p-5">
                <div className="flex items-start justify-between mb-4">
                    <div className="flex items-center gap-3">
                        <div className="w-9 h-9 rounded-lg flex items-center justify-center text-sm font-bold text-white"
                             style={{ background: 'rgba(255,255,255,0.08)', border: '1px solid rgba(255,255,255,0.1)' }}>
                            {org.name?.[0] || '?'}
                        </div>
                        <div>
                            <p className="text-white font-medium text-sm">{org.name}</p>
                            <p className="text-xs" style={{ color: '#52525b' }}>/{slug}</p>
                        </div>
                    </div>
                    <div className="flex items-center gap-2">
                        <span className="text-xs px-2 py-0.5 rounded-md font-medium"
                              style={{ background: planColors[plan]?.bg, color: planColors[plan]?.color }}>
                            {plan}
                        </span>
                        <span className="text-xs px-2 py-0.5 rounded-md"
                              style={{
                                  background: status === 'active' ? 'rgba(34,197,94,0.08)' : 'rgba(255,255,255,0.04)',
                                  color: status === 'active' ? '#22c55e' : '#52525b'
                              }}>
                            {status}
                        </span>
                    </div>
                </div>

                <div className="grid grid-cols-3 gap-3">
                    {[
                        { icon: Users, label: 'Members', value: org.members || org.memberCount || (members ? members.length : 0) },
                        { icon: Key, label: 'API Keys', value: org.apiKeys || 0 },
                        { icon: Activity, label: 'Requests', value: (org.requests || 0).toLocaleString() },
                    ].map(({ icon: Icon, label, value }) => (
                        <div key={label} className="rounded-lg p-3" style={{ background: 'rgba(255,255,255,0.03)' }}>
                            <div className="flex items-center gap-1.5 mb-1">
                                <Icon size={11} style={{ color: '#3f3f46' }} />
                                <span className="text-xs" style={{ color: '#3f3f46' }}>{label}</span>
                            </div>
                            <p className="text-white text-sm font-semibold">{value}</p>
                        </div>
                    ))}
                </div>

                <div className="flex items-center justify-between mt-3">
                    <p className="text-xs" style={{ color: '#3f3f46' }}>Created {createdAt}</p>
                    <button className="flex items-center gap-1 text-xs transition-colors"
                            style={{ color: '#52525b' }}
                            onClick={handleExpand}>
                        {expanded ? <ChevronUp size={12} /> : <ChevronDown size={12} />}
                        {expanded ? 'Hide' : 'Show'} Members
                    </button>
                </div>
            </div>

            {expanded && (
                <div style={{ borderTop: '1px solid rgba(255,255,255,0.06)' }}>
                    {members === null ? (
                        <div className="px-5 py-3">
                            <p className="text-xs" style={{ color: '#3f3f46' }}>Loading...</p>
                        </div>
                    ) : members.length === 0 ? (
                        <div className="px-5 py-3">
                            <p className="text-xs" style={{ color: '#3f3f46' }}>No members found</p>
                        </div>
                    ) : members.map((member, i) => {
                        const role = roleConfig[member.role] || roleConfig.ORG_DEVELOPER
                        return (
                            <div key={member.id} className="flex items-center justify-between px-5 py-2.5 transition-colors"
                                 style={{ borderBottom: i < members.length - 1 ? '1px solid rgba(255,255,255,0.04)' : 'none' }}
                                 onMouseEnter={e => e.currentTarget.style.background = 'rgba(255,255,255,0.02)'}
                                 onMouseLeave={e => e.currentTarget.style.background = 'transparent'}>
                                <div className="flex items-center gap-2">
                                    <div className="w-6 h-6 rounded-full flex items-center justify-center text-xs font-bold text-white"
                                         style={{ background: 'rgba(255,255,255,0.08)' }}>
                                        {member.userId?.toString().substring(0, 1).toUpperCase() || 'U'}
                                    </div>
                                    <span className="text-xs" style={{ color: '#a1a1aa' }}>
                                        {member.userId?.toString().substring(0, 8)}...
                                    </span>
                                </div>
                                <div className="flex items-center gap-1.5">
                                    <span className="flex items-center gap-1 text-xs px-2 py-0.5 rounded-md"
                                          style={{ background: role.bg, color: role.color }}>
                                        <Shield size={9} />
                                        {role.label}
                                    </span>
                                    <span className="text-xs" style={{ color: '#3f3f46' }}>
                                        {member.joinedAt ? new Date(member.joinedAt).toLocaleDateString('tr-TR') : '-'}
                                    </span>
                                </div>
                            </div>
                        )
                    })}
                </div>
            )}
        </div>
    )
}

export default function Organizations() {
    const [realOrgs, setRealOrgs] = useState(null)

    useEffect(() => {
        getMyOrganizations()
            .then(res => setRealOrgs(Array.isArray(res.data) ? res.data : null))
            .catch(() => setRealOrgs(null))
    }, [])

    const activeOrgs = realOrgs || mockOrgs
    const activeCount = activeOrgs.filter(o => o.status === 'active' || o.isActive).length
    const totalMembers = activeOrgs.reduce((acc, o) => acc + (o.members || o.memberCount || 0), 0)
    const totalRequests = activeOrgs.reduce((acc, o) => acc + (o.requests || 0), 0)

    return (
        <div className="p-6 space-y-5">
            <div className="grid grid-cols-3 gap-3">
                {[
                    { icon: Building2, label: 'Total Organizations', value: activeOrgs.length, sub: `${activeCount} active` },
                    { icon: Users, label: 'Total Members', value: totalMembers, sub: 'Across all orgs' },
                    { icon: Activity, label: 'Total Requests', value: totalRequests.toLocaleString(), sub: 'All time' },
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
                {activeOrgs.map(org => <OrgCard key={org.id} org={org} />)}
            </div>
        </div>
    )
}