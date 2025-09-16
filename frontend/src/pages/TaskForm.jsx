import Navbar from '../components/Navbar'
import StatusMessage from '../components/StatusMessage'
import { useState, useEffect } from 'react'
import "./TaskForm.css"

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

    useEffect(() => {
        if(initialTask) {
            setForm({
                ...initialTask,
                dueDate: initialTask.dueDate ? initialTask.dueDate.slice(0,10) : ''
            });
        }
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
            <div className="taskform-container">
                <h1>{initialTask ? 'Edit Task' : 'New Task'}</h1>
                <p>Welcome to your task form! Here you can create a new task.</p>
                <StatusMessage loading={loading} error={error} success={success} />
                {localError && <div style={{ color: 'red', marginBottom: '1em' }}>{localError}</div>}
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
                    <button type="submit" disabled={loading}>{initialTask ? 'Update Task' : 'Create Task'}</button>
                </form>
            </div>
        </>
    )
}

export default TaskForm