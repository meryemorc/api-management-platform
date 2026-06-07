import { BrowserRouter, Routes, Route } from 'react-router-dom'
import Sidebar from './components/layout/Sidebar'
import Header from './components/layout/Header'
import Dashboard from './pages/Dashboard'
import Analytics from './pages/Analytics'
import Billing from './pages/Billing'
import Notifications from './pages/Notifications'
import ApiKeys from './pages/ApiKeys'
import Landing from './pages/Landing'
import Docs from './pages/Docs'
import Users from './pages/Users'
import Organizations from './pages/Organizations'

const dashboardRoutes = [
    { path: '/', component: Dashboard, title: 'Dashboard' },
    { path: '/analytics', component: Analytics, title: 'Analytics' },
    { path: '/billing', component: Billing, title: 'Billing' },
    { path: '/notifications', component: Notifications, title: 'Notifications' },
    { path: '/api-keys', component: ApiKeys, title: 'API Keys' },
    { path: '/users', component: Users, title: 'Users' },
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
        <BrowserRouter>
            <Routes>
                {dashboardRoutes.map(({ path, component, title }) => (
                    <Route key={path} path={path} element={<Layout component={component} title={title} />} />
                ))}
                <Route path="/landing" element={<Landing />} />
                <Route path="/docs" element={<Docs />} />
            </Routes>
        </BrowserRouter>
    )
}