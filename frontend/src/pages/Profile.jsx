import Navbar from '../components/Navbar'
import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { fetchCurrentUser } from '../services/user'
import { verifyCurrentUser } from '../services/user'
import { updateEmail, updateUsername } from '../services/api'
import { isValidEmail } from '../utils/validation'
import { isValidPassword } from '../utils/validation'
import { isValidUsername } from '../utils/validation'
import Spinner from '../components/Spinner'

function Profile () {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const navigate = useNavigate();

    const [showUsernameForm, setShowUsernameForm] = useState(false);
    const [newUsername, setNewUsername] = useState('');
    const handleUsernameChange = (e) => setNewUsername(e.target.value);
    const handleUsernameSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');
        setSuccess('');
        if(!isValidUsername(newUsername)){
            setError('Username must be 3-50 characters, only letters, numbers, dots, underscores; no consecutive dots/underscores.');
            return;
        }
        try {
            await updateUsername(user.id, newUsername);
            setSuccess('Username updated successfully!');
            const updatedUser = await fetchCurrentUser();
            setUser(updatedUser);
            setShowUsernameForm(false);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    }

    const [showEmailForm, setShowEmailForm] = useState(false);
    const [newEmail, setNewEmail] = useState('');
    const handleEmailChange = (e) => setNewEmail(e.target.value);
    const handleEmailSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');
        setSuccess('');
        if(!isValidEmail(newEmail)){
            setError('Please enter a valid email address.');
            return;
        }
        try {
            await updateEmail(user.id, newEmail);
            setSuccess('Email updated successfully!');
            const updatedUser = await fetchCurrentUser();
            setUser(updatedUser);
            setShowEmailForm(false);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    }

    const [showPasswordForm, setShowPasswordForm] = useState(false);
    const [newPassword, setNewPassword] = useState('');
    const handlePasswordChange = (e) => setNewPassword(e.target.value);
    const handlePasswordSubmit = (e) => {
        e.preventDefault();
        setError('');
        if (!isValidPassword(newPassword)) {
            setError('Password must be 8-50 characters, include uppercase, lowercase, number, and special character.');
            return;
        }
        // Handle password update logic here
    }

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
            .catch(err => setError(err.message))
            .finally(() => setLoading(false));
    }, []);

    return (
        <>
            <Navbar />
            <div className="profile-container" aria-busy={loading} aria-label="Profile Page" role="main">
                <h1>Profile</h1>
                <p>Welcome to your profile! Here you can see and edit your personal information.</p>
                {loading && <Spinner aria-label="Loading profile..." />}
                {error && <div style={{ color: 'red' }}>{error}</div>}
                {user && (
                    <div className="profile-info" aria-label="User Information">
                        <h2>Account Information</h2>
                        <p><strong>Username:</strong> {user.username}</p>
                        <p><strong>Email:</strong> {user.email}</p>
                        <p><strong>Verified:</strong> {user.verified ? 'Yes' : 'No'}</p>
                    </div>
                )}
                <div><button onClick={() => setShowUsernameForm(v => !v)}>
                    {showUsernameForm ? 'Cancel' : 'Edit Username'}
                </button></div>
                {showUsernameForm && (
                    <form className="profile-edit-form" onSubmit={handleUsernameSubmit} aria-label="Edit Username Form">
                        <input name="newusername" value={newUsername} onChange={handleUsernameChange} aria-describedby="username-req" aria-label="New Username"/>
                        <span id="username-req">Username must be 3-50 characters and can only contain letters, numbers, dots, underscores; no consecutive dots/underscores.</span>
                        <button type="submit">Save</button>
                    </form>
                )}
                <div><button onClick={() => setShowEmailForm(v => !v)}>
                    {showEmailForm ? 'Cancel' : 'Edit Email'}
                </button></div>
                {showEmailForm && (
                    <form className="profile-edit-form" onSubmit={handleEmailSubmit} aria-label="Edit Email Form">
                        <input name="newemail" value={newEmail} onChange={handleEmailChange} aria-describedby="email-req" aria-label="New Email" />
                        <span id="email-req">Email must be a valid email address.</span>
                        <button type="submit">Save</button>
                    </form>
                )}
                <div><button onClick={() => setShowPasswordForm(v => !v)}>
                    {showPasswordForm ? 'Cancel' : 'Edit Password'}
                </button></div>
                {showPasswordForm && (
                    <form className="profile-edit-form" onSubmit={handlePasswordSubmit} aria-label="Edit Password Form">
                        <input name="newpassword" type="password" value={newPassword} onChange={handlePasswordChange} aria-describedby="password-req" />
                        <span id="password-req">Password must be at least 8 characters and include at least one uppercase, lowercase, number, and special character.</span>
                        <button type="submit">Save</button>
                    </form>
                )}
                <div><button onClick={handleVerify}>Verify Account</button></div>
                <div className="profile-settings" aria-label="Settings Panel" role="region">[Settings Panel]</div>
                {error && <div style={{ color: 'red' }} aria-live="assertive">{error}</div>}
                {success && <div style={{ color: 'green' }} aria-live="polite">{success}</div>}
            </div>
        </>
    )
}

export default Profile