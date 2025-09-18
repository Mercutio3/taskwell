import { useEffect, useState } from 'react';
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, CartesianGrid } from 'recharts';
import Spinner from '../Spinner';

function ProductivityChartWidget() {
    const [data, setData] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        fetch('http://localhost:8080/api/tasks', { credentials: 'include' })
            .then(res => {
                if (!res.ok) throw new Error('Failed to fetch tasks');
                return res.json();
            })
            .then(tasks => {
                const completedTasks = tasks.filter(t => t.status === 'COMPLETE' && t.completedAt);
                const createdTasks = tasks.filter(t => t.createdAt);
                const counts = {};
                const today = new Date();
                for(let i = 6; i >= 0; i--) {
                    const d = new Date(today);
                    d.setDate(today.getDate() - i);
                    const dateStr = d.toISOString().slice(0, 10);
                    counts[dateStr] = { completed: 0, created: 0 };
                }
                completedTasks.forEach(task => {
                    const date = task.completedAt.slice(0, 10);
                    if(counts[date] !== undefined) {
                        counts[date].completed++;
                    }
                });

                createdTasks.forEach(task => {
                    const date = task.createdAt.slice(0, 10);
                    if(counts[date] !== undefined) {
                        counts[date].created++;
                    }
                });
                const chartData = Object.entries(counts).map(([date, { completed, created }]) => ({
                    date,
                    completed,
                    created,
                }));
                setData(chartData);
            })
            .catch(err => setError(err.message))
            .finally(() => setLoading(false));
    }, []);

    if (loading) return <Spinner aria-label="Loading productivity chart..." />;
    if (error) return <div style={{ color: 'red' }} aria-live="assertive">{error}</div>;

    return (
        <div className="widget productivity-chart-widget" aria-label="Chart of Tasks Created and Completed Over Time" role="region">
            <h3>Productivity (Last 7 Days)</h3>
            <ResponsiveContainer width="100%" height={200}>
                <BarChart data={data} margin={{ top: 20, right: 30, left: 0, bottom: 5 }}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="date" />
                    <YAxis allowDecimals={false} />
                    <Tooltip />
                    <Bar dataKey="created" fill="#82ca9d" />
                    <Bar dataKey="completed" fill="#8884d8" />
                </BarChart>
            </ResponsiveContainer>
        </div>
    );
}

export default ProductivityChartWidget;