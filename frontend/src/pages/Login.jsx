import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import StatusMessage from '../components/StatusMessage'
import { loginUser } from '../services/api'
import { useAuth } from '../context/AuthContext';
import Spinner from '../components/Spinner';

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
            <div aria-live="assertLive" style={{color: 'red'}}>{error}</div>
            <form onSubmit={handleSubmit} aria-busy={loading} aria-label="Login Form">
                <input name="username" value={form.username} onChange={handleChange} placeholder="Username" required aria-describedby="username-desc"/>
                <span id="username-desc">Enter your username</span>
                <input name="password" type="password" value={form.password} onChange={handleChange} placeholder="Password" required aria-describedby="password-desc"/>
                <span id="password-desc">Enter your password</span>
                <button type="submit" disabled={loading}>
                    {loading ? <Spinner /> : 'Login'}
                </button>
            </form>
            <p>Don't have an account? <Link to="/register">Register</Link></p>
        </div>
    )
}

export default Login