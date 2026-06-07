import { BookOpen, Terminal, Key, BarChart3, Bell, CreditCard, ChevronRight, Copy } from 'lucide-react'
import { Link } from 'react-router-dom'

const sections = [
    { icon: BookOpen, title: 'Getting Started', desc: 'Register, create your organization and get your first API key in under 5 minutes.', link: '/docs/getting-started' },
    { icon: Terminal, title: 'API Reference', desc: 'Complete reference for all REST endpoints, request/response schemas and error codes.', link: '#api-reference' },
    { icon: Key, title: 'Authentication', desc: 'Learn how to authenticate using API keys and JWT tokens.', link: '#authentication' },
    { icon: BarChart3, title: 'Analytics', desc: 'Track usage, monitor performance and export reports.', link: '#analytics' },
    { icon: Bell, title: 'Notifications', desc: 'Configure email and webhook alerts for important events.', link: '#notifications' },
    { icon: CreditCard, title: 'Billing & Plans', desc: 'Understand usage-based pricing, invoices and plan upgrades.', link: '#billing' },
]

const CodeBlock = ({ code, lang = 'bash' }) => (
    <div className="rounded-lg overflow-hidden" style={{ background: 'rgba(255,255,255,0.03)', border: '1px solid rgba(255,255,255,0.08)' }}>
        <div className="flex items-center justify-between px-4 py-2" style={{ borderBottom: '1px solid rgba(255,255,255,0.06)' }}>
            <span className="text-xs" style={{ color: '#52525b' }}>{lang}</span>
            <button className="text-xs flex items-center gap-1 transition-colors" style={{ color: '#52525b' }}>
                <Copy size={11} /> Copy
            </button>
        </div>
        <pre className="px-4 py-3 text-xs overflow-x-auto" style={{ color: '#a1a1aa', fontFamily: 'monospace' }}>
      {code}
    </pre>
    </div>
)

export default function Docs() {
    return (
        <div className="min-h-screen" style={{ background: '#000', color: '#fff' }}>
            {/* Nav */}
            <nav className="flex items-center justify-between px-8 py-4 sticky top-0 z-50"
                 style={{ background: 'rgba(0,0,0,0.8)', backdropFilter: 'blur(20px)', borderBottom: '1px solid rgba(255,255,255,0.06)' }}>
                <div className="flex items-center gap-6">
                    <Link to="/landing" className="text-sm font-semibold">API Platform</Link>
                    <div className="flex items-center gap-4">
                        {['Guides', 'API Reference', 'SDKs', 'Changelog'].map(item => (
                            <a key={item} href="#" className="text-sm transition-colors" style={{ color: '#71717a' }}
                               onMouseEnter={e => e.target.style.color = '#fff'}
                               onMouseLeave={e => e.target.style.color = '#71717a'}>
                                {item}
                            </a>
                        ))}
                    </div>
                </div>
                <Link to="/" className="text-sm px-3 py-1.5 rounded-lg" style={{ background: 'rgba(255,255,255,0.06)', border: '1px solid rgba(255,255,255,0.1)' }}>
                    Dashboard →
                </Link>
            </nav>

            <div className="max-w-7xl mx-auto px-8 py-16">
                {/* Header */}
                <div className="mb-16">
                    <p className="text-xs font-medium mb-3" style={{ color: '#52525b' }}>DOCUMENTATION</p>
                    <h1 className="text-4xl font-bold mb-4">API Platform Docs</h1>
                    <p className="text-base" style={{ color: '#71717a' }}>
                        Everything you need to integrate, manage and scale your APIs.
                    </p>
                </div>

                {/* Quick Start Cards */}
                <div className="grid grid-cols-3 gap-4 mb-16">
                    {sections.map(({ icon: Icon, title, desc, link }) => (
                        <a key={title} href={link}
                           className="p-5 rounded-xl block transition-all group"
                           style={{ background: 'rgba(255,255,255,0.02)', border: '1px solid rgba(255,255,255,0.06)' }}
                           onMouseEnter={e => e.currentTarget.style.border = '1px solid rgba(255,255,255,0.12)'}
                           onMouseLeave={e => e.currentTarget.style.border = '1px solid rgba(255,255,255,0.06)'}>
                            <div className="flex items-start justify-between mb-3">
                                <div className="w-8 h-8 rounded-lg flex items-center justify-center"
                                     style={{ background: 'rgba(255,255,255,0.06)', border: '1px solid rgba(255,255,255,0.08)' }}>
                                    <Icon size={15} className="text-white" />
                                </div>
                                <ChevronRight size={14} style={{ color: '#3f3f46' }} className="group-hover:text-white transition-colors" />
                            </div>
                            <h3 className="font-semibold text-sm mb-1.5">{title}</h3>
                            <p className="text-xs leading-relaxed" style={{ color: '#71717a' }}>{desc}</p>
                        </a>
                    ))}
                </div>

                {/* Quick Start */}
                <div id="authentication" className="mb-16">
                    <h2 className="text-xl font-bold mb-2">Quick Start</h2>
                    <p className="text-sm mb-6" style={{ color: '#71717a' }}>Make your first API call in minutes.</p>

                    <div className="space-y-4">
                        <div>
                            <p className="text-sm font-medium mb-2">1. Register and get your API key</p>
                            <CodeBlock lang="bash" code={`curl -X POST https://api.yourplatform.com/api/v1/auth/register \\
  -H "Content-Type: application/json" \\
  -d '{
    "username": "yourname",
    "email": "you@example.com",
    "password": "your-password"
  }'`} />
                        </div>

                        <div>
                            <p className="text-sm font-medium mb-2">2. Create an organization</p>
                            <CodeBlock lang="bash" code={`curl -X POST https://api.yourplatform.com/api/v1/organizations \\
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \\
  -H "Content-Type: application/json" \\
  -d '{
    "name": "My Organization",
    "slug": "my-org"
  }'`} />
                        </div>

                        <div>
                            <p className="text-sm font-medium mb-2">3. Create an API key</p>
                            <CodeBlock lang="bash" code={`curl -X POST https://api.yourplatform.com/api/v1/keys \\
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \\
  -H "Content-Type: application/json" \\
  -d '{
    "name": "Production Key",
    "organizationId": "YOUR_ORG_ID",
    "dailyRequestLimit": 10000,
    "monthlyRequestLimit": 300000
  }'`} />
                        </div>

                        <div>
                            <p className="text-sm font-medium mb-2">4. Make authenticated requests</p>
                            <CodeBlock lang="bash" code={`curl -X GET https://api.yourplatform.com/api/v1/your-endpoint \\
  -H "X-API-Key: pk_live_your_api_key_here"`} />
                        </div>
                    </div>
                </div>

                {/* API Reference */}
                <div id="api-reference" className="mb-16">
                    <h2 className="text-xl font-bold mb-6">API Reference</h2>
                    <div className="space-y-3">
                        {[
                                    // Auth
                                    { method: 'POST', path: '/api/v1/auth/register', desc: 'Register a new user account' },
                            { method: 'POST', path: '/api/v1/auth/login', desc: 'Login and receive JWT token' },
                            { method: 'GET', path: '/api/v1/users/by-email/:email', desc: 'Get user by email' },
                            // Organizations
                            { method: 'POST', path: '/api/v1/organizations', desc: 'Create a new organization' },
                            { method: 'GET', path: '/api/v1/organizations/my', desc: 'Get your organizations' },
                            { method: 'GET', path: '/api/v1/organizations/:slug/members', desc: 'List organization members' },
                            { method: 'POST', path: '/api/v1/organizations/:slug/members', desc: 'Add member to organization' },
                            { method: 'DELETE', path: '/api/v1/organizations/:slug/members/:userId', desc: 'Remove member' },
                            // API Keys
                            { method: 'POST', path: '/api/v1/keys', desc: 'Create a new API key' },
                            { method: 'GET', path: '/api/v1/keys/:organizationId', desc: 'List API keys for organization' },
                            { method: 'DELETE', path: '/api/v1/keys/:keyId', desc: 'Delete an API key' },
                            // Analytics
                            { method: 'GET', path: '/api/v1/analytics/:orgId/daily', desc: 'Get daily request analytics' },
                            { method: 'GET', path: '/api/v1/analytics/:orgId/monthly', desc: 'Get monthly request analytics' },
                            { method: 'GET', path: '/api/v1/analytics/:orgId/recent', desc: 'Get recent request logs' },
                            { method: 'GET', path: '/api/v1/analytics/:orgId/endpoints', desc: 'Get endpoint breakdown' },
                            { method: 'GET', path: '/api/v1/analytics/:orgId/response-time', desc: 'Get average response times' },
                            // Notifications
                            { method: 'GET', path: '/api/v1/notifications/:orgId/preferences', desc: 'Get notification preferences' },
                            { method: 'PUT', path: '/api/v1/notifications/:orgId/preferences', desc: 'Update notification preferences' },
                            { method: 'GET', path: '/api/v1/notifications/:orgId/logs', desc: 'Get notification history' },
                            { method: 'POST', path: '/api/v1/notifications/:orgId/webhooks', desc: 'Create a webhook' },
                            { method: 'GET', path: '/api/v1/notifications/:orgId/webhooks', desc: 'List webhooks' },
                            // Billing
                            { method: 'GET', path: '/api/v1/billing/plans', desc: 'List available billing plans' },
                            { method: 'POST', path: '/api/v1/billing/subscriptions/:orgId', desc: 'Create a subscription' },
                            { method: 'GET', path: '/api/v1/billing/subscriptions/:orgId', desc: 'Get subscription details' },
                            { method: 'PUT', path: '/api/v1/billing/subscriptions/:orgId/plan', desc: 'Change subscription plan' },
                            { method: 'DELETE', path: '/api/v1/billing/subscriptions/:orgId', desc: 'Cancel subscription' },
                            { method: 'POST', path: '/api/v1/billing/invoices/:orgId/generate', desc: 'Generate monthly invoice' },
                            { method: 'POST', path: '/api/v1/billing/invoices/:invoiceId/pay', desc: 'Pay an invoice' },
                            { method: 'GET', path: '/api/v1/billing/invoices/:orgId', desc: 'List invoices' },
                            { method: 'GET', path: '/api/v1/billing/usage/:orgId/current', desc: 'Get current usage' },
                            { method: 'GET', path: '/api/v1/billing/usage/:orgId/history', desc: 'Get usage history' },
                        ].map(({ method, path, desc }) => (
                            <div key={path} className="flex items-center gap-4 px-4 py-3 rounded-lg"
                                 style={{ background: 'rgba(255,255,255,0.02)', border: '1px solid rgba(255,255,255,0.06)' }}>
                <span className="text-xs font-bold w-12 text-center px-2 py-0.5 rounded"
                      style={{
                          background: method === 'GET' ? 'rgba(34,197,94,0.1)' : method === 'POST' ? 'rgba(59,130,246,0.1)' : 'rgba(239,68,68,0.1)',
                          color: method === 'GET' ? '#22c55e' : method === 'POST' ? '#3b82f6' : '#ef4444',
                      }}>
                  {method}
                </span>
                                <code className="text-sm font-mono" style={{ color: '#a1a1aa' }}>{path}</code>
                                <span className="text-xs ml-auto" style={{ color: '#52525b' }}>{desc}</span>
                            </div>
                        ))}
                    </div>
                </div>

                {/* Error Codes */}
                <div className="mb-16">
                    <h2 className="text-xl font-bold mb-6">Error Codes</h2>
                    <div className="rounded-xl overflow-hidden" style={{ border: '1px solid rgba(255,255,255,0.06)' }}>
                        {[
                            { code: '400', name: 'Bad Request', desc: 'Invalid request body or parameters' },
                            { code: '401', name: 'Unauthorized', desc: 'Missing or invalid authentication token' },
                            { code: '403', name: 'Forbidden', desc: 'Insufficient permissions for this resource' },
                            { code: '404', name: 'Not Found', desc: 'The requested resource does not exist' },
                            { code: '429', name: 'Too Many Requests', desc: 'Rate limit exceeded. Check your plan limits.' },
                            { code: '500', name: 'Internal Server Error', desc: 'Something went wrong on our end' },
                        ].map(({ code, name, desc }, i, arr) => (
                            <div key={code} className="flex items-center gap-6 px-5 py-3"
                                 style={{ borderBottom: i < arr.length - 1 ? '1px solid rgba(255,255,255,0.04)' : 'none' }}>
                <span className="text-sm font-mono font-bold w-10"
                      style={{ color: code.startsWith('4') || code.startsWith('5') ? '#ef4444' : '#22c55e' }}>
                  {code}
                </span>
                                <span className="text-sm font-medium w-40">{name}</span>
                                <span className="text-xs" style={{ color: '#71717a' }}>{desc}</span>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        </div>
    )
}