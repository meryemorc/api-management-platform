import { createContext, useContext, useState, useEffect } from 'react'

const AuthContext = createContext()

export function AuthProvider({ children }) {
    const [user, setUser] = useState(null)
    const [token, setToken] = useState(localStorage.getItem('token'))

    useEffect(() => {
        if (token) {
            try {
                const payload = JSON.parse(atob(token.split('.')[1]))
                setUser({ email: payload.sub || payload.email, username: payload.username || payload.sub })
            } catch {
                setUser(null)
            }
        }
    }, [token])

    const loginUser = (accessToken) => {
        localStorage.setItem('token', accessToken)
        setToken(accessToken)
    }

    const logoutUser = () => {
        localStorage.removeItem('token')
        setToken(null)
        setUser(null)
    }

    return (
        <AuthContext.Provider value={{ user, token, loginUser, logoutUser }}>
            {children}
        </AuthContext.Provider>
    )
}

export const useAuth = () => useContext(AuthContext)