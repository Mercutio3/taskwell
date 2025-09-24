import Navbar from "../components/Navbar";
import { useEffect, useState } from "react";
import { fetchCurrentUser } from "../services/user";
import { verifyCurrentUser } from "../services/user";
import { updateEmail, updateUsername } from "../services/api";
import { isValidEmail } from "../utils/validation";
import { isValidPassword } from "../utils/validation";
import { isValidUsername } from "../utils/validation";
import StatusMessage from "../components/StatusMessage";

function Profile() {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(""); // For global errors (fetch, verify)
  const [usernameError, setUsernameError] = useState("");
  const [emailError, setEmailError] = useState("");
  const [passwordError, setPasswordError] = useState("");
  const [success, setSuccess] = useState("");

  const [showUsernameForm, setShowUsernameForm] = useState(false);
  const [newUsername, setNewUsername] = useState("");
  const [currentPassword, setCurrentPassword] = useState("");
  const handleUsernameChange = (e) => setNewUsername(e.target.value);
  const handleCurrentPasswordChange = (e) => setCurrentPassword(e.target.value);
  const handleUsernameSubmit = async (e) => {
    e.preventDefault();
    setLoading(false);
    setUsernameError("");
    setSuccess("");
    if (!isValidUsername(newUsername)) {
      setUsernameError(
        "Username must be 3-50 characters, only letters, numbers, dots, underscores; no consecutive dots/underscores.",
      );
      return;
    }
    if (!currentPassword.trim()) {
      setUsernameError("Current password is required.");
      return;
    }
    setLoading(true);
    try {
      await updateUsername(user.id, newUsername, currentPassword);
      setSuccess("Username updated successfully!");
      const updatedUser = await fetchCurrentUser();
      setUser(updatedUser);
      setShowUsernameForm(false);
      setCurrentPassword("");
    } catch (err) {
      setUsernameError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const [showEmailForm, setShowEmailForm] = useState(false);
  const [newEmail, setNewEmail] = useState("");
  const [currentEmailPassword, setCurrentEmailPassword] = useState("");
  const handleEmailChange = (e) => {
    setNewEmail(e.target.value);
  };
  const handleCurrentEmailPasswordChange = (e) => {
    setCurrentEmailPassword(e.target.value);
  };
  const handleEmailSubmit = async (e) => {
    e.preventDefault();
    setLoading(false);
    setEmailError("");
    setSuccess("");
    if (!isValidEmail(newEmail)) {
      setEmailError("Please enter a valid email address.");
      setTimeout(() => {}, 0);
      return;
    }
    if (!currentEmailPassword.trim()) {
      setEmailError("Current password is required.");
      return;
    }
    setLoading(true);
    try {
      await updateEmail(user.id, newEmail, currentEmailPassword);
      setSuccess("Email updated successfully!");
      const updatedUser = await fetchCurrentUser();
      setUser(updatedUser);
      setShowEmailForm(false);
      setCurrentEmailPassword("");
    } catch (err) {
      setEmailError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const [showPasswordForm, setShowPasswordForm] = useState(false);
  const [newPassword, setNewPassword] = useState("");
  const [currentPasswordForUpdate, setCurrentPasswordForUpdate] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const handlePasswordChange = (e) => setNewPassword(e.target.value);
  const handleCurrentPasswordForUpdateChange = (e) =>
    setCurrentPasswordForUpdate(e.target.value);
  const handleConfirmPasswordChange = (e) => setConfirmPassword(e.target.value);
  const handlePasswordSubmit = async (e) => {
    e.preventDefault();
    setPasswordError("");
    setSuccess("");
    if (!currentPasswordForUpdate.trim()) {
      setPasswordError("Current password is required.");
      return;
    }
    if (!isValidPassword(newPassword)) {
      setPasswordError(
        "Password must be 8-50 characters, include uppercase, lowercase, number, and special character.",
      );
      return;
    }
    if (newPassword !== confirmPassword) {
      setPasswordError("New password and confirmation do not match.");
      return;
    }
    try {
      await import("../services/api").then(({ updatePassword }) =>
        updatePassword(user.id, newPassword, currentPasswordForUpdate),
      );
      setSuccess("Password updated successfully!");
      setShowPasswordForm(false);
      setCurrentPasswordForUpdate("");
      setNewPassword("");
      setConfirmPassword("");
    } catch (err) {
      setPasswordError(err.message);
    }
  };

  const handleVerify = async () => {
    setLoading(true);
    setError("");
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
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, []);

  return (
    <>
      <Navbar />
      <div
        className="profile-container"
        aria-busy={loading}
        aria-label="Profile Page"
        role="main"
      >
        <h1>Profile</h1>
        <p>
          Welcome to your profile! Here you can see and edit your personal
          information.
        </p>
        <StatusMessage
          loading={loading}
          error={
            showUsernameForm
              ? usernameError
              : showEmailForm
                ? emailError
                : showPasswordForm
                  ? passwordError
                  : error
          }
          success={success}
        />
        {user && (
          <div className="profile-info" aria-label="User Information">
            <h2>Account Information</h2>
            <p>
              <strong>Username:</strong> {user.username}
            </p>
            <p>
              <strong>Email:</strong> {user.email}
            </p>
            <p>
              <strong>Verified:</strong> {user.verified ? "Yes" : "No"}
            </p>
          </div>
        )}
        <div>
          <button onClick={() => setShowUsernameForm((v) => !v)}>
            {showUsernameForm ? "Cancel" : "Edit Username"}
          </button>
        </div>
        {showUsernameForm && (
          <form
            className="profile-edit-form"
            onSubmit={handleUsernameSubmit}
            aria-label="Edit Username Form"
          >
            <label htmlFor="newusername">New Username</label>
            <input
              id="newusername"
              name="newusername"
              value={newUsername}
              onChange={handleUsernameChange}
              aria-describedby="username-req"
              aria-label="New Username"
            />
            <span id="username-req">
              Username must be 3-50 characters and can only contain letters,
              numbers, dots, underscores; no consecutive dots/underscores.
            </span>
            <label htmlFor="currentPassword">Current Password</label>
            <input
              id="currentPassword"
              name="currentPassword"
              type="password"
              value={currentPassword}
              onChange={handleCurrentPasswordChange}
              aria-label="Current Password"
            />
            <span id="current-password-req">
              Enter your current password to confirm changes.
            </span>
            <button type="submit">Save</button>
          </form>
        )}
        <div>
          <button onClick={() => setShowEmailForm((v) => !v)}>
            {showEmailForm ? "Cancel" : "Edit Email"}
          </button>
        </div>
        {showEmailForm && (
          <form
            className="profile-edit-form"
            onSubmit={handleEmailSubmit}
            aria-label="Edit Email Form"
          >
            <label htmlFor="newemail">New Email</label>
            <input
              id="newemail"
              name="newemail"
              type="text"
              value={newEmail}
              onChange={handleEmailChange}
              aria-describedby="email-req"
              aria-label="New Email"
            />
            <span id="email-req">Email must be a valid email address.</span>
            <label htmlFor="currentEmailPassword">Current Password</label>
            <input
              id="currentEmailPassword"
              name="currentEmailPassword"
              type="password"
              value={currentEmailPassword}
              onChange={handleCurrentEmailPasswordChange}
              aria-label="Current Password"
            />
            <span id="current-email-password-req">
              Enter your current password to confirm changes.
            </span>
            <button type="submit">Save</button>
          </form>
        )}
        <div>
          <button onClick={() => setShowPasswordForm((v) => !v)}>
            {showPasswordForm ? "Cancel" : "Edit Password"}
          </button>
        </div>
        {showPasswordForm && (
          <form
            className="profile-edit-form"
            onSubmit={handlePasswordSubmit}
            aria-label="Edit Password Form"
          >
            <label htmlFor="currentPasswordForUpdate">Current Password</label>
            <input
              id="currentPasswordForUpdate"
              name="currentPasswordForUpdate"
              type="password"
              value={currentPasswordForUpdate}
              onChange={handleCurrentPasswordForUpdateChange}
              aria-label="Current Password"
            />
            <span id="current-password-update-req">
              Enter your current password to confirm changes.
            </span>
            <label htmlFor="newpassword">New Password</label>
            <input
              id="newpassword"
              name="newpassword"
              type="password"
              value={newPassword}
              onChange={handlePasswordChange}
              aria-describedby="password-req"
              aria-label="New Password"
            />
            <span id="password-req">
              Password must be at least 8 characters and include at least one
              uppercase, lowercase, number, and special character.
            </span>
            <label htmlFor="confirmPassword">Confirm New Password</label>
            <input
              id="confirmPassword"
              name="confirmPassword"
              type="password"
              value={confirmPassword}
              onChange={handleConfirmPasswordChange}
              aria-label="Confirm New Password"
            />
            <span id="confirm-password-req">
              Re-enter your new password for confirmation.
            </span>
            <button type="submit">Save</button>
          </form>
        )}
        <div>
          <button onClick={handleVerify}>Verify Account</button>
        </div>
        <div
          className="profile-settings"
          aria-label="Settings Panel"
          role="region"
        >
          [Settings Panel]
        </div>
      </div>
    </>
  );
}

export default Profile;
