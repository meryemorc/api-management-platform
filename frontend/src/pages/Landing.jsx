import { Zap, Shield, BarChart3, Bell, CreditCard, ArrowRight, Check } from 'lucide-react'
import { Link } from 'react-router-dom'

const features = [
    {
        icon: Zap,
        title: 'Lightning Fast',
        desc: 'Sub-millisecond API key validation with Redis caching. Handle millions of requests without breaking a sweat.'
    },
    {
        icon: Shield,
        title: 'Enterprise Security',
        desc: 'JWT authentication, rate limiting, and API key rotation built-in. Your APIs stay protected at all times.'
    },
    {
        icon: BarChart3,
        title: 'Real-time Analytics',
        desc: 'Track every request, monitor error rates, and analyze usage patterns with beautiful dashboards.'
    },
    {
        icon: Bell,
        title: 'Smart Notifications',
        desc: 'Automated alerts for rate limits, API key expiry, and anomalies via email and webhooks.'
    },
    {
        icon: CreditCard,
        title: 'Flexible Billing',
        desc: 'Usage-based pricing with overage protection. Scale from free tier to enterprise without friction.'
    },
    {
        icon: Shield,
        title: 'Multi-tenant',
        desc: 'Manage multiple organizations, teams, and projects from a single unified platform.'
    },
]

const plans = [
    {
        name: 'Free',
        price: '$0',
        period: '/month',
        desc: 'Perfect for side projects',
        features: ['1,000 requests/month', '1 API key', 'Basic analytics', 'Community support'],
        cta: 'Get started',
        highlight: false,
    },
    {
        name: 'Starter',
        price: '$29',
        period: '/month',
        desc: 'For growing teams',
        features: ['10,000 requests/month', '5 API keys', 'Advanced analytics', 'Email notifications', 'Email support'],
        cta: 'Start free trial',
        highlight: true,
    },
    {
        name: 'Pro',
        price: '$99',
        period: '/month',
        desc: 'For production workloads',
        features: ['100,000 requests/month', 'Unlimited API keys', 'Real-time analytics', 'Webhooks', 'Priority support'],
        cta: 'Start free trial',
        highlight: false,
    },
    {
        name: 'Enterprise',
        price: '$299',
        period: '/month',
        desc: 'For large organizations',
        features: ['1M+ requests/month', 'Custom limits', 'Dedicated support', 'SLA guarantee', 'Custom contracts'],
        cta: 'Contact sales',
        highlight: false,
    },
]

export default function Landing() {
    return (
        <div className="min-h-screen" style={{ background: '#000', color: '#fff' }}>
            {/* Nav */}
            <nav className="flex items-center justify-between px-8 py-5 max-w-7xl mx-auto"
                 style={{ borderBottom: '1px solid rgba(255,255,255,0.06)' }}>
                <div className="flex items-center gap-2">
                    <div className="w-7 h-7 rounded-lg flex items-center justify-center"
                         style={{ background: 'rgba(255,255,255,0.1)', border: '1px solid rgba(255,255,255,0.15)' }}>
                        <Zap size={14} className="text-white" />
                    </div>
                    <span className="font-semibold text-sm">API Platform</span>
                </div>
                <div className="flex items-center gap-6">
                    {['Features', 'Pricing', 'Docs'].map(item => (
                        <a key={item} href={`#${item.toLowerCase()}`}
                           className="text-sm transition-colors"
                           style={{ color: '#71717a' }}
                           onMouseEnter={e => e.target.style.color = '#fff'}
                           onMouseLeave={e => e.target.style.color = '#71717a'}>
                            {item}
                        </a>
                    ))}
                    <Link to="/"
                          className="text-sm px-4 py-1.5 rounded-lg font-medium transition-all"
                          style={{ background: '#fff', color: '#000' }}>
                        Dashboard →
                    </Link>
                </div>
            </nav>

            {/* Hero */}
            <section className="max-w-7xl mx-auto px-8 pt-24 pb-20 text-center">
                <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full text-xs mb-8"
                     style={{ background: 'rgba(255,255,255,0.05)', border: '1px solid rgba(255,255,255,0.1)', color: '#a1a1aa' }}>
                    <span className="w-1.5 h-1.5 rounded-full bg-green-400 animate-pulse" />
                    Now in public beta — free to get started
                </div>
                <h1 className="text-6xl font-bold tracking-tight mb-6 leading-tight">
                    The API Management<br />
                    <span style={{ color: '#52525b' }}>Platform for Modern Teams</span>
                </h1>
                <p className="text-lg mb-10 max-w-2xl mx-auto" style={{ color: '#71717a' }}>
                    Secure, monitor, and monetize your APIs with enterprise-grade tooling.
                    From rate limiting to real-time analytics — everything in one place.
                </p>
                <div className="flex items-center justify-center gap-4">
                    <Link to="/"
                          className="flex items-center gap-2 px-6 py-3 rounded-lg font-medium text-sm transition-all"
                          style={{ background: '#fff', color: '#000' }}>
                        Get started free <ArrowRight size={15} />
                    </Link>
                    <a href="#features"
                       className="px-6 py-3 rounded-lg text-sm transition-all"
                       style={{ background: 'rgba(255,255,255,0.05)', border: '1px solid rgba(255,255,255,0.1)', color: '#fff' }}>
                        View docs
                    </a>
                </div>

                {/* Stats */}
                <div className="flex items-center justify-center gap-12 mt-20">
                    {[
                        { value: '99.9%', label: 'Uptime SLA' },
                        { value: '<1ms', label: 'Avg latency' },
                        { value: '10M+', label: 'Requests/day' },
                        { value: '500+', label: 'Organizations' },
                    ].map(stat => (
                        <div key={stat.label} className="text-center">
                            <p className="text-2xl font-bold">{stat.value}</p>
                            <p className="text-xs mt-1" style={{ color: '#52525b' }}>{stat.label}</p>
                        </div>
                    ))}
                </div>
            </section>

            {/* Features */}
            <section id="features" className="max-w-7xl mx-auto px-8 py-20">
                <div className="text-center mb-14">
                    <p className="text-xs font-medium mb-3" style={{ color: '#52525b' }}>FEATURES</p>
                    <h2 className="text-3xl font-bold">Everything you need to manage APIs</h2>
                </div>
                <div className="grid grid-cols-3 gap-4">
                    {features.map(({ icon: Icon, title, desc }) => (
                        <div key={title} className="p-6 rounded-xl transition-all"
                             style={{ background: 'rgba(255,255,255,0.02)', border: '1px solid rgba(255,255,255,0.06)' }}
                             onMouseEnter={e => e.currentTarget.style.border = '1px solid rgba(255,255,255,0.12)'}
                             onMouseLeave={e => e.currentTarget.style.border = '1px solid rgba(255,255,255,0.06)'}>
                            <div className="w-9 h-9 rounded-lg flex items-center justify-center mb-4"
                                 style={{ background: 'rgba(255,255,255,0.06)', border: '1px solid rgba(255,255,255,0.08)' }}>
                                <Icon size={16} className="text-white" />
                            </div>
                            <h3 className="font-semibold text-sm mb-2">{title}</h3>
                            <p className="text-sm leading-relaxed" style={{ color: '#71717a' }}>{desc}</p>
                        </div>
                    ))}
                </div>
            </section>

            {/* Pricing */}
            <section id="pricing" className="max-w-7xl mx-auto px-8 py-20">
                <div className="text-center mb-14">
                    <p className="text-xs font-medium mb-3" style={{ color: '#52525b' }}>PRICING</p>
                    <h2 className="text-3xl font-bold">Simple, transparent pricing</h2>
                    <p className="mt-3 text-sm" style={{ color: '#71717a' }}>Start free, scale as you grow</p>
                </div>
                <div className="grid grid-cols-4 gap-4">
                    {plans.map(plan => (
                        <div key={plan.name} className="p-6 rounded-xl relative"
                             style={{
                                 background: plan.highlight ? 'rgba(255,255,255,0.05)' : 'rgba(255,255,255,0.02)',
                                 border: plan.highlight ? '1px solid rgba(255,255,255,0.15)' : '1px solid rgba(255,255,255,0.06)',
                             }}>
                            {plan.highlight && (
                                <div className="absolute -top-3 left-1/2 -translate-x-1/2 text-xs px-3 py-1 rounded-full font-medium"
                                     style={{ background: '#fff', color: '#000' }}>
                                    Most popular
                                </div>
                            )}
                            <p className="font-semibold text-sm mb-1">{plan.name}</p>
                            <p className="text-xs mb-4" style={{ color: '#52525b' }}>{plan.desc}</p>
                            <div className="flex items-baseline gap-1 mb-6">
                                <span className="text-3xl font-bold">{plan.price}</span>
                                <span className="text-sm" style={{ color: '#52525b' }}>{plan.period}</span>
                            </div>
                            <ul className="space-y-2.5 mb-6">
                                {plan.features.map(f => (
                                    <li key={f} className="flex items-center gap-2 text-xs" style={{ color: '#a1a1aa' }}>
                                        <Check size={13} className="text-white flex-shrink-0" />
                                        {f}
                                    </li>
                                ))}
                            </ul>
                            <button className="w-full py-2 rounded-lg text-sm font-medium transition-all"
                                    style={{
                                        background: plan.highlight ? '#fff' : 'rgba(255,255,255,0.06)',
                                        color: plan.highlight ? '#000' : '#fff',
                                        border: plan.highlight ? 'none' : '1px solid rgba(255,255,255,0.1)',
                                    }}>
                                {plan.cta}
                            </button>
                        </div>
                    ))}
                </div>
            </section>

            {/* Footer */}
            <footer className="max-w-7xl mx-auto px-8 py-10"
                    style={{ borderTop: '1px solid rgba(255,255,255,0.06)' }}>
                <div className="flex items-center justify-between">
                    <div className="flex items-center gap-2">
                        <div className="w-6 h-6 rounded-md flex items-center justify-center"
                             style={{ background: 'rgba(255,255,255,0.08)' }}>
                            <Zap size={12} className="text-white" />
                        </div>
                        <span className="text-sm font-medium">API Platform</span>
                    </div>
                    <p className="text-xs" style={{ color: '#3f3f46' }}>© 2026 API Management Platform. All rights reserved.</p>
                </div>
            </footer>
        </div>
    )
}