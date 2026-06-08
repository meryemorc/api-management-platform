import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'
import ProtectedRoute from './components/ProtectedRoute'
import Sidebar from './components/layout/Sidebar'
import Header from './components/layout/Header'
import Dashboard from './pages/Dashboard'
import Analytics from './pages/Analytics'
import Billing from './pages/Billing'
import Notifications from './pages/Notifications'
import ApiKeys from './pages/ApiKeys'
import Organizations from './pages/Organizations'
import Landing from './pages/Landing'
import Docs from './pages/Docs'
import Login from './pages/Login'
import Register from './pages/Register'

const dashboardRoutes = [
    { path: '/', component: Dashboard, title: 'Dashboard' },
    { path: '/analytics', component: Analytics, title: 'Analytics' },
    { path: '/billing', component: Billing, title: 'Billing' },
    { path: '/notifications', component: Notifications, title: 'Notifications' },
    { path: '/api-keys', component: ApiKeys, title: 'API Keys' },
    { path: '/organizations', component: Organizations, title: 'Organizations' },
]

function Layout({ component: Component, title }) {
    return (
        <div className="flex min-h-screen bg-black">
            <Sidebar />
            <div className="flex-1 flex flex-col">
                <Header title={title} />
                <main className="flex-1 overflow-auto bg-black">
                    <Component />
                </main>
            </div>
        </div>
    )
}

export default function App() {
    return (
        <AuthProvider>
            <BrowserRouter>
                <Routes>
                    <Route path="/login" element={<Login />} />
                    <Route path="/register" element={<Register />} />
                    <Route path="/landing" element={<Landing />} />
                    <Route path="/docs" element={<Docs />} />
                    {dashboardRoutes.map(({ path, component, title }) => (
                        <Route key={path} path={path} element={
                            <ProtectedRoute>
                                <Layout component={component} title={title} />
                            </ProtectedRoute>
                        } />
                    ))}
                    <Route path="*" element={<Navigate to="/login" replace />} />
                </Routes>
            </BrowserRouter>
        </AuthProvider>
    )
}