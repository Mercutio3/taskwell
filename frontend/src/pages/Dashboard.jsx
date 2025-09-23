import Navbar from '../components/Navbar'
import TaskSummaryWidget from '../components/widgets/TaskSummaryWidget';
import UpcomingTasksWidget from '../components/widgets/UpcomingTasksWidget';
import OverdueTasksWidget from '../components/widgets/OverdueTasksWidget';
import ProductivityChartWidget from '../components/widgets/ProductivityChartWidget';
import CategoryPieChart from '../components/widgets/CategoryPieChartWidget';
import StatusMessage from '../components/StatusMessage';
import { useState, useEffect } from 'react';

function Dashboard() {
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        // Simulate async widget loading; add error handling if widgets fetch data
        setTimeout(() => setLoading(false), 1000);
        // Example: If you fetch widgets and catch errors, call setError('Failed to load dashboard widgets')
    }, []);

    return (
        <>
            <Navbar />
            <div className="dashboard-container" aria-busy={loading} aria-label="Dashboard Overview" role="main">
                <h1>Dashboard</h1>
                <p>Welcome to your dashboard! Here you can see an overview of your tasks and activity.</p>
                <StatusMessage loading={loading} error={error} />
                {!loading && !error && (
                    <div className="dashboard-widgets" aria-label="Task Widgets" role="region">
                        <TaskSummaryWidget />
                        <UpcomingTasksWidget />
                        <OverdueTasksWidget />
                        <ProductivityChartWidget />
                        <CategoryPieChart />
                    </div>
                )}
            </div>
        </>
    )
}
export default Dashboard