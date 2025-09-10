import Navbar from '../components/Navbar'
import StatusMessage from '../components/StatusMessage'
import { useState } from 'react'
import "./TaskForm.css"

function TaskForm() {
    const [form, setForm] = useState({
            title: '',
            description: '',
            status: 'pending',
            priority: 'MEDIUM',
            dueDate: '',
            category: ''
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
        <>
            <Navbar />
            <div className="taskform-container">
                <h1>New Task</h1>
                <p>Welcome to your task form! Here you can create a new task.</p>
                <StatusMessage loading={loading} error={error} />
                <form className="taskform" onSubmit={handleSubmit}>
                    <input name="title" value={form.title} onChange={handleChange} placeholder="Task Title" required />
                    <input name="description" value={form.description} onChange={handleChange} placeholder="Task Description" />
                    <input name="dueDate" type="date" value={form.dueDate} onChange={handleChange} placeholder="Due Date" required />
                    <select name="status" value={form.status} onChange={handleChange} placeholder="Status">
                        <option value="PENDING">Pending</option>
                        <option value="IN_PROGRESS">In Progress</option>
                        <option value="COMPLETE">Completed</option>
                    </select>
                    <select name="priority" value={form.priority} onChange={handleChange} placeholder="Priority">
                        <option value="LOW">Low</option>
                        <option value="MEDIUM">Medium</option>
                        <option value="HIGH">High</option>
                    </select>
                    <input name="category" value={form.category} onChange={handleChange} placeholder="Category" />
                    <button type="submit">Create Task</button>
                </form>
            </div>
        </>
    )
}

export default TaskForm