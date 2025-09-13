import TaskForm from './TaskForm';
import { updateTask, getTask } from '../services/api';
import { useNavigate, useParams } from 'react-router-dom';
import { useState, useEffect } from 'react';

function TaskEditPage() {
    const { id } = useParams();
    const [task, setTask] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        getTask(id).then(setTask).catch(err => setError('Failed to load task.'));
    }, [id]);
        
    const handleUpdate = async (form) => {
        setLoading(true);
        setError('');
        setSuccess(false);
        try {
            const dueDateTime = form.dueDate ? new Date(form.dueDate).toISOString() : null;
            await updateTask(id, { ...form, dueDate: dueDateTime });
            setSuccess(true);
            setTimeout(() => {
                navigate('/tasks/' + id);
            }, 1000);
        } catch (error) {
            setError('Failed to update task');
        } finally {
            setLoading(false);
        }
    }

    if(!task) return <div>Loading task...</div>;

    return (
        <TaskForm
            initialTask={task}
            onSubmit={handleUpdate}
            loading={loading}
            error={error}
            success={success && 'Task updated!'}
        />
    )
}

export default TaskEditPage;