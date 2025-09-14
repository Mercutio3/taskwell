import React, { useEffect, useState } from 'react';

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

    if (loading) return <div>Loading summary...</div>;
    if (error) return <div style={{ color: 'red' }}>{error}</div>;

    return (
        <div className="widget task-summary-widget">
            <h3>Task Summary</h3>
            <ul>
                <li>Total tasks: {summary.total}</li>
                <li>Completed: {summary.complete}</li>
                <li>Uncompleted: {summary.pending}</li>
            </ul>
        </div>
    );
}

export default TaskSummaryWidget;
