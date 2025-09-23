import Navbar from '../components/Navbar'
import StatusMessage from '../components/StatusMessage'
import { useState, useEffect } from 'react'
import "./TaskForm.css"
import { formatCategory } from '../utils/formatting'

function TaskForm( {initialTask, onSubmit, loading, error, success}) {
    const [form, setForm] = useState({
        title: '',
        description: '',
        status: 'PENDING',
        priority: 'MEDIUM',
        dueDate: '',
        category: ''
    });
    const [localError, setLocalError] = useState('');
    const [categories, setCategories] = useState([]);

    useEffect(() => {
        if(initialTask) {
            setForm({
                ...initialTask,
                dueDate: initialTask.dueDate ? initialTask.dueDate.slice(0,10) : ''
            });
        }
        fetch('http://localhost:8080/api/tasks/categories', { credentials: 'include' })
            .then(res => res.json())
            .then(setCategories)
            .catch(() => setCategories([]));
    }, [initialTask]);

    const handleChange = (e) => {
        setForm({
            ...form,
            [e.target.name]: e.target.value
        });
        if (localError) setLocalError('');
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if(!form.title.trim()) {
            setLocalError('Title is required');
            return;
        }
        if(!form.dueDate) {
            setLocalError('Due date is required');
            return;
        }
        // Due date validation: must not be in the past
        const today = new Date();
        today.setHours(0,0,0,0);
        const dueDate = new Date(form.dueDate);
        if (dueDate < today) {
            setLocalError('Due date cannot be in the past.');
            return;
        }
        await onSubmit(form);
    };

    return (
        <>
            <Navbar />
            <div className="taskform-container" role="main">
                <h1>{initialTask ? 'Edit Task' : 'New Task'}</h1>
                <p>Welcome to your task form! Here you can create a new task.</p>
                <StatusMessage loading={loading} error={localError || error} success={success} />
                <form className="taskform" onSubmit={handleSubmit} aria-busy={loading} aria-label="Task Form">
                    <label htmlFor="title">Title</label>
                    <input id="title" name="title" value={form.title} onChange={handleChange} placeholder="Task Title" />

                    <label htmlFor="description">Description</label>
                    <input id="description" name="description" value={form.description} onChange={handleChange} placeholder="Task Description" />
                    
                    <label htmlFor="dueDate">Due Date</label>
                    <input id="dueDate" name="dueDate" type="date" value={form.dueDate} onChange={handleChange} placeholder="Due Date" />

                    <label htmlFor="status">Status</label>
                    <select id="status" name="status" value={form.status} onChange={handleChange} placeholder="Status" aria-label="Task Status">
                        <option value="PENDING">Pending</option>
                        <option value="IN_PROGRESS">In Progress</option>
                        <option value="COMPLETE">Completed</option>
                    </select>

                    <label htmlFor="priority">Priority</label>
                    <select id="priority" name="priority" value={form.priority} onChange={handleChange} placeholder="Priority" aria-label="Task Priority">
                        <option value="LOW">Low</option>
                        <option value="MEDIUM">Medium</option>
                        <option value="HIGH">High</option>
                    </select>

                    <label htmlFor="category">Category</label>
                    <select id="category" name="category" value={form.category} onChange={handleChange} placeholder="Category" aria-label="Task Category">
                        <option value="">Select Category</option>
                        {categories.map(cat => (
                            <option key={cat} value={cat}>{formatCategory(cat)}</option>
                        ))}
                    </select>

                    <button type="submit" disabled={loading}>
                        {initialTask ? 'Update Task' : 'Create Task'}
                    </button>
                </form>
            </div>
        </>
    )
}

export default TaskForm