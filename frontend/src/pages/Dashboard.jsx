import Navbar from '../components/Navbar'

function Dashboard() {
    return (
        <>
            <Navbar />
            <div className="dashboard-container">
                <h1>Dashboard</h1>
                                <p>Welcome to your dashboard! Here you can see an overview of your tasks and activity.</p>
                                {/* Placeholder for future dashboard widgets and stats */}
                                <div className="dashboard-widgets">
                                    <div className="widget-placeholder">[Task Summary Widget]</div>
                                    <div className="widget-placeholder">[Productivity Chart]</div>
                                    <div className="widget-placeholder">[Upcoming Deadlines]</div>
                                </div>
            </div>
        </>
    )
}
export default Dashboard