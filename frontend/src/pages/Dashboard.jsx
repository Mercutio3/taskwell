import Navbar from '../components/Navbar'
import TaskSummaryWidget from '../components/widgets/TaskSummaryWidget';
import UpcomingTasksWidget from '../components/widgets/UpcomingTasksWidget';
import OverdueTasksWidget from '../components/widgets/OverdueTasksWidget';
import ProductivityChartWidget from '../components/widgets/ProductivityChartWidget';
import CategoryPieChart from '../components/widgets/CategoryPieChartWidget';
import Spinner from '../components/Spinner';
import React, { useState, useEffect } from 'react';

function Dashboard() {
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        setTimeout(() => setLoading(false), 1000);
    }, []);

    return (
        <>
            <Navbar />
            <div className="dashboard-container" aria-busy={loading} aria-label="Dashboard Overview" role="main">
                <h1>Dashboard</h1>
                <p>Welcome to your dashboard! Here you can see an overview of your tasks and activity.</p>
                {loading ? (
                    <Spinner aria-label="Loading dashboard widgets..." />
                ) : (
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