import { useState } from 'react'
import { Link } from 'react-router-dom'
import StatusMessage from '../components/StatusMessage'
import "./Register.css"
import { registerUser } from '../services/api'
import { useNavigate } from 'react-router-dom'

function Register () {
    const [form, setForm] = useState({
        username: '',
        email: '',
        password: ''
    })

    const [loading, setLoading] = useState(false)
    const [error, setError] = useState('')
    const [success, setSuccess] = useState('')
    const navigate = useNavigate();

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
            const user = await registerUser(form);
            setSuccess('Registration successful! Please log in.');
            setTimeout(() => navigate('/login'), 1000)
            console.log('User registered successfully:', user);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div>
            <h1>Registration</h1>
            <StatusMessage loading={loading} error={error} success={success} />
            <form onSubmit={handleSubmit}>
                <input name="username" value={form.username} onChange={handleChange} placeholder="Username" required />
                <input name="email" type="email" value={form.email} onChange={handleChange} placeholder="Email" required />
                <input name="password" type="password" value={form.password} onChange={handleChange} placeholder="Password" required />
                <button type="submit">Register</button>
            </form>
            <p>Already have an account? <Link to="/login">Login</Link></p>
        </div>
    )
}

export default Register