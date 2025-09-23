import Navbar from '../components/Navbar'
import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom';
import StatusMessage from '../components/StatusMessage';

function Forbidden() {
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        fetch('http://localhost:8080/api/users/me', {
            credentials: 'include',
        })
            .then(res => setIsLoggedIn(res.ok))
            .catch(() => setIsLoggedIn(false));
    }, []);

    return (
        <>
            <Navbar />
            <div className="forbidden-container" style={{ textAlign: 'center', marginTop: '3rem' }} role="main" aria-label="Forbidden Page">
                <h1>403 - Forbidden</h1>
                <StatusMessage error="You do not have permission to access this page or resource." />
                <div style={{ margin: '2rem 0' }}>
                    {isLoggedIn === null ? (
                        <span>Checking login status...</span>
                    ) : isLoggedIn ? (
                        <button onClick={() => navigate("/dashboard")} className="btn">Go to Dashboard</button>
                    ) : (
                        <button onClick={() => navigate("/login")} className="btn">Login</button>
                    )}
                </div>
                <p>If you believe this is an error, please contact support or try logging in with a different account.</p>
            </div>
        </>
    )
}

export default Forbidden