import Navbar from '../components/Navbar'

function Profile () {
    return (
        <>
            <Navbar />
            <div className="profile-container">
                <h1>Profile</h1>
                <p>Welcome to your profile! Here you can see and edit your personal information.</p>
                {/* Placeholder for future user info and settings */}
                <div className="profile-info">[User Info Card]</div>
                <div className="profile-settings">[Settings Panel]</div>
            </div>
        </>
    )
}

export default Profile