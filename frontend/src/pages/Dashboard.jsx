import Navbar from '../components/Navbar'
import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

function Dashboard() {
    const navigate = useNavigate();

    useEffect(() => {
        fetch('http://localhost:8080/api/users/me', { credentials: 'include' })
            .then(res => {
                if (res.status === 401) {
                    navigate('/unauthorized');
                    return null;
                }
                // ...existing logic
            })
    }, [navigate]);

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