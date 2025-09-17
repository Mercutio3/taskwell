import TaskForm from './TaskForm';
import { createTask } from '../services/api';
import { useNavigate } from 'react-router-dom';
import { useState, useEffect } from 'react';

function TaskFormPage() {
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        fetch('http://localhost:8080/api/users/me', { credentials: 'include' })
            .then(res => {
                if (res.status === 401) {
                    navigate('/unauthorized');
                    return null;
                }
                // ...existing logic
            })
    }, [navigate]);

    const handleCreate = async (form) => {
        setLoading(true);
        setError('');
        setSuccess(false);
        try {
            const dueDateTime = form.dueDate ? new Date(form.dueDate).toISOString() : null;
            await createTask({ ...form, dueDate: dueDateTime });
            setSuccess(true);
            setTimeout(() => {
                navigate('/tasks');
            }, 1000);
        } catch (error) {
            if(error.status === 403) {
                setError('Please verify your account to create tasks.');
            } else {
                setError('Failed to create task');
            }
        } finally {
            setLoading(false);
        }
    }

    return (
        <TaskForm
            onSubmit={handleCreate}
            loading={loading}
            error={error}
            success={success && 'Task created!'}
        />
    )
}

export default TaskFormPage