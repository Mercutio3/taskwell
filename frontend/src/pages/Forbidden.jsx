import Navbar from '../components/Navbar'
import { Link } from 'react-router-dom'
import { useEffect, useState } from 'react'

function Forbidden() {
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
            <div className="forbidden-container" style={{ textAlign: 'center', marginTop: '3rem' }}>
                <h1>403 - Forbidden</h1>
                <p>You do not have permission to access this page or resource.</p>
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

export default Forbidden