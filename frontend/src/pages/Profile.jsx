import Navbar from '../components/Navbar'
import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { fetchCurrentUser } from '../services/user'
import { verifyCurrentUser } from '../services/user'
import { updateEmail, updateUsername } from '../services/api'
import { isValidEmail } from '../utils/validation'
import { isValidPassword } from '../utils/validation'
import { isValidUsername } from '../utils/validation'
import StatusMessage from '../components/StatusMessage'

function Profile () {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(''); // For global errors (fetch, verify)
    const [usernameError, setUsernameError] = useState('');
    const [emailError, setEmailError] = useState('');
    const [passwordError, setPasswordError] = useState('');
    const [success, setSuccess] = useState('');
    const navigate = useNavigate();

    const [showUsernameForm, setShowUsernameForm] = useState(false);
    const [newUsername, setNewUsername] = useState('');
    const handleUsernameChange = (e) => setNewUsername(e.target.value);
    const handleUsernameSubmit = async (e) => {
        e.preventDefault();
        setLoading(false);
        setUsernameError('');
        setSuccess('');
        if(!isValidUsername(newUsername)){
            setUsernameError('Username must be 3-50 characters, only letters, numbers, dots, underscores; no consecutive dots/underscores.');
            return;
        }
        setLoading(true);
        try {
            await updateUsername(user.id, newUsername);
            setSuccess('Username updated successfully!');
            const updatedUser = await fetchCurrentUser();
            setUser(updatedUser);
            setShowUsernameForm(false);
        } catch (err) {
            setUsernameError(err.message);
        } finally {
            setLoading(false);
        }
    }

    const [showEmailForm, setShowEmailForm] = useState(false);
    const [newEmail, setNewEmail] = useState('');
    const handleEmailChange = (e) => {
        setNewEmail(e.target.value);
    };
    const handleEmailSubmit = async (e) => {
    e.preventDefault();
        setLoading(false);
        setEmailError('');
        setSuccess('');
        // Debug log for test
        if(!isValidEmail(newEmail)){
            setEmailError('Please enter a valid email address.');
            // Log after setting error
            setTimeout(() => {
            }, 0);
            return;
        }
        setLoading(true);
        try {
            await updateEmail(user.id, newEmail);
            setSuccess('Email updated successfully!');
            const updatedUser = await fetchCurrentUser();
            setUser(updatedUser);
            setShowEmailForm(false);
        } catch (err) {
            setEmailError(err.message);
        } finally {
            setLoading(false);
        }
    }

    const [showPasswordForm, setShowPasswordForm] = useState(false);
    const [newPassword, setNewPassword] = useState('');
    const handlePasswordChange = (e) => setNewPassword(e.target.value);
    const handlePasswordSubmit = (e) => {
        e.preventDefault();
        setPasswordError('');
        if (!isValidPassword(newPassword)) {
            setPasswordError('Password must be 8-50 characters, include uppercase, lowercase, number, and special character.');
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

    // Debug: log newEmail on every render

    return (
        <>
            <Navbar />
            <div className="profile-container" aria-busy={loading} aria-label="Profile Page" role="main">
                <h1>Profile</h1>
                <p>Welcome to your profile! Here you can see and edit your personal information.</p>
                <StatusMessage
                    loading={loading}
                    error={showUsernameForm ? usernameError : showEmailForm ? emailError : showPasswordForm ? passwordError : error}
                    success={success}
                />
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
                        <label htmlFor="newusername">New Username</label>
                        <input id="newusername" name="newusername" value={newUsername} onChange={handleUsernameChange} aria-describedby="username-req" aria-label="New Username"/>
                        <span id="username-req">Username must be 3-50 characters and can only contain letters, numbers, dots, underscores; no consecutive dots/underscores.</span>
                        <button type="submit">Save</button>
                    </form>
                )}
                <div><button onClick={() => setShowEmailForm(v => !v)}>
                    {showEmailForm ? 'Cancel' : 'Edit Email'}
                </button></div>
                {showEmailForm && (
                    <form className="profile-edit-form" onSubmit={handleEmailSubmit} aria-label="Edit Email Form">
                        <label htmlFor="newemail">New Email</label>
                        <input id="newemail" name="newemail" type="text" value={newEmail} onChange={handleEmailChange} aria-describedby="email-req" aria-label="New Email" />
                        <span id="email-req">Email must be a valid email address.</span>
                        <button type="submit">Save</button>
                    </form>
                )}
                <div><button onClick={() => setShowPasswordForm(v => !v)}>
                    {showPasswordForm ? 'Cancel' : 'Edit Password'}
                </button></div>
                {showPasswordForm && (
                    <form className="profile-edit-form" onSubmit={handlePasswordSubmit} aria-label="Edit Password Form">
                        <label htmlFor="newpassword">New Password</label>
                        <input id="newpassword" name="newpassword" type="password" value={newPassword} onChange={handlePasswordChange} aria-describedby="password-req" aria-label="New Password"/>
                        <span id="password-req">Password must be at least 8 characters and include at least one uppercase, lowercase, number, and special character.</span>
                        <button type="submit">Save</button>
                    </form>
                )}
                <div><button onClick={handleVerify}>Verify Account</button></div>
                <div className="profile-settings" aria-label="Settings Panel" role="region">[Settings Panel]</div>
            </div>
        </>
    )
}

export default Profile