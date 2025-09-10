import { useState } from 'react'
import { Link } from 'react-router-dom'
import StatusMessage from '../components/StatusMessage'

function Login () {
    const [form, setForm] = useState({
        username: '',
        password: ''
    })

    const [loading, setLoading] = useState(false)
    const [error, setError] = useState('')

    const handleChange = (e) => {
        setForm({
            ...form,
            [e.target.name]: e.target.value
        })
    }

    const handleSubmit = async (e) => {
        e.preventDefault()
        setLoading(true)
        setError('')
        try {
            await new Promise((resolve, reject) => setTimeout(resolve, 1000))
            console.log('Logging in user:', form)
        } catch (error) {
            console.error('Login failed:', error)
            setError('Login failed. Please try again.')
        } finally {
            setLoading(false)
        }
    }

    return (
        <div>
            <h1>Login</h1>
            <StatusMessage loading={loading} error={error} />
            <form onSubmit={handleSubmit}>
                <input name="username" value={form.username} onChange={handleChange} placeholder="Username" required />
                <input name="password" type="password" value={form.password} onChange={handleChange} placeholder="Password" required />
                <button type="submit" disabled={loading}>{loading ? 'Logging in...' : 'Login'}</button>
            </form>
            <p>Don't have an account? <Link to="/register">Register</Link></p>
        </div>
    )
}

export default Login