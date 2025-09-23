import { useEffect, useState } from 'react'
import Navbar from '../components/Navbar'
import { useNavigate } from 'react-router-dom';
import StatusMessage from '../components/StatusMessage';

function NotFound() {
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
            <div className="page-center" role="main" aria-label="404 Not Found Page">
                <h1>404 - Not Found</h1>
                <StatusMessage error="The page you are looking for does not exist." />
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

export default NotFound