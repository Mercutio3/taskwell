import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import Navbar from '../components/Navbar'

function NotFound() {
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    
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
            <div className="page-center" role="alert" aria-label="404 Not Found Page">
                <h1>404 - Not Found</h1>
                <p>The page you are looking for does not exist.</p>
                <div style={{ margin: '2rem 0' }}>
                    {isLoggedIn === null ? (
                        <span>Checking login status...</span>
                    ) : isLoggedIn ? (
                        <Link to="/dashboard" className="btn">Go to Dashboard</Link>
                    ) : (
                        <Link to="/login" className="btn">Login</Link>
                    )}
                </div>
                <p>If you believe this is an error, please contact support or try logging in with a different account.</p>
            </div>
        </>
    )
}

export default NotFound