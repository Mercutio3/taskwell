import React, { useEffect, useState } from 'react';

function UpcomingTasksWidget() {
    const [tasks, setTasks] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        fetch('http://localhost:8080/api/tasks', { credentials: 'include' })
            .then(res => {
                if (!res.ok) throw new Error('Failed to fetch tasks');
                return res.json();
            })
            .then(tasks => {
                const now = new Date();
                const upcoming = tasks
                    .filter(t => t.dueDate && new Date(t.dueDate) >= now && t.status !== 'COMPLETE')
                    .sort((a, b) => new Date(a.dueDate) - new Date(b.dueDate))
                    .slice(0, 5);
                setTasks(upcoming);
            })
            .catch(err => setError(err.message))
            .finally(() => setLoading(false));
    }, []);

    if(loading) return <div>Loading upcoming tasks...</div>;
    if(error) return <div style={{ color: 'red' }}>{error}</div>;

    return (
        <div className="widget upcoming-tasks-widget">
            <h3>Upcoming Tasks</h3>
            {tasks.length === 0 ? (
                <p>No upcoming tasks!</p>
            ) : (
                <ul>
                    {tasks.map(task => (
                        <li key={task.id}>
                            <strong>{task.title}</strong> - Due {new Date(task.dueDate).toLocaleDateString()}
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
}

export default UpcomingTasksWidget;