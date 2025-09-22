import TaskForm from './TaskForm';
import { createTask } from '../services/api';
import { useNavigate } from 'react-router-dom';
import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import Spinner from '../components/Spinner';

function TaskFormPage({ skipDelay = false }) {
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState(false);
    const navigate = useNavigate();
    const { isVerified } = useAuth();

    const handleCreate = async (form) => {
        setLoading(true);
        setError('');
        setSuccess(false);
        try {
            const dueDateTime = form.dueDate ? new Date(form.dueDate).toISOString() : null;
            await createTask({ ...form, dueDate: dueDateTime });
            setSuccess(true);
            if (skipDelay) {
                navigate('/tasks');
            } else {
                setTimeout(() => {
                    navigate('/tasks');
                }, 1000);
            }
        } catch (error) {
            const status = error?.status || error?.response?.status || error?.response?.data?.status;
            if (status === 403 || !isVerified) {
                setError('Please verify your account to create tasks.');
            } else {
                setError('Failed to create task');
            }
        } finally {
            setLoading(false);
        }
    }

    return (
        <div aria-busy={loading}>
            {loading && <Spinner aria-label="Loading task form..." />}
            <TaskForm
                onSubmit={handleCreate}
                loading={loading}
                error={error}
                success={success && 'Task created!'}
            />
        </div>
    )
}

export default TaskFormPage