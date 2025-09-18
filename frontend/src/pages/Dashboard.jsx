import Navbar from '../components/Navbar'
import TaskSummaryWidget from '../components/widgets/TaskSummaryWidget';
import UpcomingTasksWidget from '../components/widgets/UpcomingTasksWidget';
import OverdueTasksWidget from '../components/widgets/OverdueTasksWidget';
import ProductivityChartWidget from '../components/widgets/ProductivityChartWidget';
import CategoryPieChart from '../components/widgets/CategoryPieChartWidget';

function Dashboard() {
    return (
        <>
            <Navbar />
            <div className="dashboard-container">
                <h1>Dashboard</h1>
                <p>Welcome to your dashboard! Here you can see an overview of your tasks and activity.</p>
                <div className="dashboard-widgets">
                    <TaskSummaryWidget />
                    <UpcomingTasksWidget />
                    <OverdueTasksWidget />
                    <ProductivityChartWidget />
                    <CategoryPieChart />
                </div>
            </div>
        </>
    )
}
export default Dashboard