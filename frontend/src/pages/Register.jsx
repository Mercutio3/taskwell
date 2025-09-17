import { useState } from 'react'
import { Link } from 'react-router-dom'
import StatusMessage from '../components/StatusMessage'
//import "./Register.css"
import { registerUser } from '../services/api'
import { useNavigate } from 'react-router-dom'
import { isValidEmail } from '../utils/validation'
import { isValidPassword } from '../utils/validation'
import { isValidUsername } from '../utils/validation'

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

    const [confirmPassword, setConfirmPassword] = useState('');

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
        // Confirm password validation
        if(!isValidUsername(form.username)){
            setError('Username must be 3-50 characters, only letters, numbers, dots, underscores; no consecutive dots/underscores.');
            setLoading(false);
            return;
        }
        if(!isValidEmail(form.email)){
            setError('Please enter a valid email address.');
            setLoading(false);
            return;
        }
        if (form.password !== confirmPassword) {
            setError('Passwords do not match');
            setLoading(false);
            return;
        }
        if(!isValidPassword(form.password)){
            setError('Password must be at least 8 characters, include uppercase, lowercase, number, and special character.');
            setLoading(false);
            return;
        }
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
        <div className="page-center">
            <h1>Registration</h1>
            <StatusMessage loading={loading} error={error} success={success} />
            <form onSubmit={handleSubmit}>
                <input name="username" value={form.username} onChange={handleChange} placeholder="Username" required />
                <input name="email" type="email" value={form.email} onChange={handleChange} placeholder="Email" required />
                <input name="password" type="password" value={form.password} onChange={handleChange} placeholder="Password" required />
                <input name="confirmPassword" type="password" value={confirmPassword} onChange={e => setConfirmPassword(e.target.value)} placeholder="Confirm Password" required />
                <button type="submit">Register</button>
            </form>
            <p>Already have an account? <Link to="/login">Login</Link></p>
        </div>
    )
}

export default Register