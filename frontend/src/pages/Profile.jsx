import Navbar from '../components/Navbar'
import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { fetchCurrentUser } from '../services/user'
import { verifyCurrentUser } from '../services/user'

function Profile () {
    // For demo: replace with actual username from auth/session
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleVerify = async () => {
        setLoading(true);
        setError('');
        try {
            await verifyCurrentUser();
            // Refresh user data after verification
            const updatedUser = await fetchCurrentUser();
            setUser(updatedUser);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        setLoading(true);
        fetchCurrentUser()
            .then(setUser)
            .catch(err => {
                if (err.status === 401) {
                    navigate('/unauthorized');
                } else {
                    setError(err.message);
                }
            })
            .finally(() => setLoading(false));
    }, [navigate]);

    return (
        <>
            <Navbar />
            <div className="profile-container">
                <h1>Profile</h1>
                <p>Welcome to your profile! Here you can see and edit your personal information.</p>
                {loading && <div>Loading account info...</div>}
                {error && <div style={{ color: 'red' }}>{error}</div>}
                {user && (
                    <div className="profile-info">
                        <h2>Account Information</h2>
                        <p><strong>Username:</strong> {user.username}</p>
                        <p><strong>Email:</strong> {user.email}</p>
                        <p><strong>Verified:</strong> {user.verified ? 'Yes' : 'No'}</p>
                    </div>
                )}
                <div><button onClick={handleVerify}>Verify Account</button></div>
                <div className="profile-settings">[Settings Panel]</div>
            </div>
        </>
    )
}

export default Profile