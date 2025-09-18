import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import StatusMessage from '../components/StatusMessage'
import { loginUser } from '../services/api'
import { useAuth } from '../context/AuthContext';

function Login () {
    const [form, setForm] = useState({
        username: '',
        password: ''
    })

    const [loading, setLoading] = useState(false)
    const [error, setError] = useState('')
    const navigate = useNavigate();
    const { login } = useAuth();

    const handleChange = (e) => {
        setForm({
            ...form,
            [e.target.name]: e.target.value
        })
    }

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');
        try {
            await loginUser(form);
            login();
            navigate('/dashboard');
        } catch (err) {
            setError('Login failed. Please check your credentials.');
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="page-center">
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