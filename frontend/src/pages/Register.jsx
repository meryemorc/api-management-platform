import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { Zap, Eye, EyeOff } from 'lucide-react'
import { register } from '../services/api'

export default function Register() {
    const [username, setUsername] = useState('')
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [showPassword, setShowPassword] = useState(false)
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState('')
    const navigate = useNavigate()

    const handleSubmit = async (e) => {
        e.preventDefault()
        setLoading(true)
        setError('')
        try {
            await register(username, email, password)
            navigate('/login')
        } catch (err) {
            setError(err.response?.data?.message || 'Registration failed')
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className="min-h-screen flex items-center justify-center" style={{ background: '#000' }}>
            <div className="w-full max-w-sm">
                <div className="flex items-center justify-center gap-2 mb-8">
                    <div className="w-8 h-8 rounded-lg flex items-center justify-center"
                         style={{ background: 'rgba(255,255,255,0.1)', border: '1px solid rgba(255,255,255,0.15)' }}>
                        <Zap size={16} className="text-white" />
                    </div>
                    <span className="text-white font-semibold">API Platform</span>
                </div>

                <div className="rounded-xl p-8" style={{ background: 'rgba(255,255,255,0.02)', border: '1px solid rgba(255,255,255,0.08)' }}>
                    <h1 className="text-white font-semibold text-lg mb-1">Create account</h1>
                    <p className="text-sm mb-6" style={{ color: '#52525b' }}>Get started for free</p>

                    {error && (
                        <div className="mb-4 px-3 py-2 rounded-lg text-xs" style={{ background: 'rgba(239,68,68,0.08)', color: '#ef4444', border: '1px solid rgba(239,68,68,0.15)' }}>
                            {error}
                        </div>
                    )}

                    <form onSubmit={handleSubmit} className="space-y-4">
                        <div>
                            <label className="block text-xs font-medium mb-1.5" style={{ color: '#a1a1aa' }}>Username</label>
                            <input
                                type="text"
                                value={username}
                                onChange={e => setUsername(e.target.value)}
                                placeholder="johndoe"
                                required
                                className="w-full px-3 py-2 rounded-lg text-sm text-white outline-none transition-all"
                                style={{ background: 'rgba(255,255,255,0.04)', border: '1px solid rgba(255,255,255,0.08)' }}
                                onFocus={e => e.target.style.border = '1px solid rgba(255,255,255,0.2)'}
                                onBlur={e => e.target.style.border = '1px solid rgba(255,255,255,0.08)'}
                            />
                        </div>

                        <div>
                            <label className="block text-xs font-medium mb-1.5" style={{ color: '#a1a1aa' }}>Email</label>
                            <input
                                type="email"
                                value={email}
                                onChange={e => setEmail(e.target.value)}
                                placeholder="you@example.com"
                                required
                                className="w-full px-3 py-2 rounded-lg text-sm text-white outline-none transition-all"
                                style={{ background: 'rgba(255,255,255,0.04)', border: '1px solid rgba(255,255,255,0.08)' }}
                                onFocus={e => e.target.style.border = '1px solid rgba(255,255,255,0.2)'}
                                onBlur={e => e.target.style.border = '1px solid rgba(255,255,255,0.08)'}
                            />
                        </div>

                        <div>
                            <label className="block text-xs font-medium mb-1.5" style={{ color: '#a1a1aa' }}>Password</label>
                            <div className="relative">
                                <input
                                    type={showPassword ? 'text' : 'password'}
                                    value={password}
                                    onChange={e => setPassword(e.target.value)}
                                    placeholder="••••••••"
                                    required
                                    className="w-full px-3 py-2 rounded-lg text-sm text-white outline-none transition-all pr-10"
                                    style={{ background: 'rgba(255,255,255,0.04)', border: '1px solid rgba(255,255,255,0.08)' }}
                                    onFocus={e => e.target.style.border = '1px solid rgba(255,255,255,0.2)'}
                                    onBlur={e => e.target.style.border = '1px solid rgba(255,255,255,0.08)'}
                                />
                                <button type="button" onClick={() => setShowPassword(!showPassword)}
                                        className="absolute right-3 top-1/2 -translate-y-1/2"
                                        style={{ color: '#52525b' }}>
                                    {showPassword ? <EyeOff size={14} /> : <Eye size={14} />}
                                </button>
                            </div>
                        </div>

                        <button type="submit" disabled={loading}
                                className="w-full py-2 rounded-lg text-sm font-medium transition-all mt-2"
                                style={{ background: loading ? 'rgba(255,255,255,0.1)' : '#fff', color: '#000' }}>
                            {loading ? 'Creating account...' : 'Create account'}
                        </button>
                    </form>

                    <p className="text-xs text-center mt-4" style={{ color: '#52525b' }}>
                        Already have an account?{' '}
                        <Link to="/login" className="text-white hover:underline">Sign in</Link>
                    </p>
                </div>
            </div>
        </div>
    )
}