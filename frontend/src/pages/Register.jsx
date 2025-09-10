import { useState } from 'react'
import { Link } from 'react-router-dom'
import StatusMessage from '../components/StatusMessage'
import "./Register.css"

function Register () {
    const [form, setForm] = useState({
        username: '',
        email: '',
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

    const handleSubmit = (e) => {
        e.preventDefault()
        // Handle registration logic here
        console.log('Registering user:', form)
    }

    return (
        <div>
            <h1>Registration</h1>
            <StatusMessage loading={loading} error={error} />
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