import React, { useEffect, useState } from 'react';
import Spinner from '../Spinner'; 

function TaskSummaryWidget() {
    const [summary, setSummary] = useState({ total: 0, complete: 0, pending: 0 });
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        fetch('http://localhost:8080/api/tasks', { credentials: 'include' })
            .then(res => {
                if (!res.ok) throw new Error('Failed to fetch tasks');
                return res.json();
            })
            .then(tasks => {
                const total = tasks.length;
                const complete = tasks.filter(t => t.status === 'COMPLETE').length;
                const pending = total - complete;
                setSummary({ total, complete, pending });
            })
            .catch(err => setError(err.message))
            .finally(() => setLoading(false));
    }, []);

    if (loading) return <Spinner aria-label="Loading task summary..." />;
    if (error) return <div style={{ color: 'red' }} aria-live="assertive">{error}</div>;

    return (
        <div className="widget task-summary-widget" aria-label="Task Summary Widget" role="region">
            <h3>Task Summary</h3>
            <ul role="list">
                <li role="listitem">Total tasks: {summary.total}</li>
                <li role="listitem">Completed: {summary.complete}</li>
                <li role="listitem">Uncompleted: {summary.pending}</li>
            </ul>
        </div>
    );
}

export default TaskSummaryWidget;
