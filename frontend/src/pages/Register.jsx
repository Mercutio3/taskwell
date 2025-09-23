import { useState } from "react";
import { Link } from "react-router-dom";
import StatusMessage from "../components/StatusMessage";
import { registerUser } from "../services/api";
import { useNavigate } from "react-router-dom";
import { isValidEmail } from "../utils/validation";
import { isValidPassword } from "../utils/validation";
import { isValidUsername } from "../utils/validation";

function Register() {
  const [form, setForm] = useState({
    username: "",
    email: "",
    password: "",
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const navigate = useNavigate();

  const [confirmPassword, setConfirmPassword] = useState("");

  const handleChange = (e) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    // Empty fields check
    if (
      !form.username.trim() ||
      !form.email.trim() ||
      !form.password.trim() ||
      !confirmPassword.trim()
    ) {
      setError("All fields are required.");
      setLoading(false);
      return;
    }
    // Confirm password validation
    if (!isValidUsername(form.username)) {
      setError(
        "Username must be 3-50 characters, only letters, numbers, dots, underscores; no consecutive dots/underscores.",
      );
      setLoading(false);
      return;
    }
    if (!isValidEmail(form.email)) {
      setError("Please enter a valid email address.");
      setLoading(false);
      return;
    }
    if (form.password !== confirmPassword) {
      setError("Passwords do not match");
      setLoading(false);
      return;
    }
    if (!isValidPassword(form.password)) {
      setError(
        "Password must be at least 8 characters, include uppercase, lowercase, number, and special character.",
      );
      setLoading(false);
      return;
    }
    try {
      await registerUser(form);
      setSuccess("Registration successful! Please log in.");
      setTimeout(() => navigate("/login"), 1000);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page-center">
      <h1>Registration</h1>
      <StatusMessage loading={loading} error={error} success={success} />
      <div aria-live="assertLive" style={{ color: "red" }}>
        {error}
      </div>
      <form
        onSubmit={handleSubmit}
        aria-busy={loading}
        aria-label="Registration Form"
      >
        <label htmlFor="username">Username</label>
        <input
          id="username"
          name="username"
          value={form.username}
          onChange={handleChange}
          placeholder="Username"
          aria-describedby="username-desc"
        />
        <span id="username-desc">
          Username must be between 3-50 characters, only letters, numbers, dots,
          and underscores; no consecutive dots/underscores.
        </span>

        <label htmlFor="email">Email</label>
        <input
          id="email"
          name="email"
          type="email"
          value={form.email}
          onChange={handleChange}
          placeholder="Email"
          aria-describedby="email-desc"
        />
        <span id="email-desc">Enter your email address</span>

        <label htmlFor="password">Password</label>
        <input
          id="password"
          name="password"
          type="password"
          value={form.password}
          onChange={handleChange}
          placeholder="Password"
          aria-describedby="password-desc"
        />
        <span id="password-desc">
          Password must be at least 8 characters and include at least one
          uppercase, lowercase, number, and special character.
        </span>

        <label htmlFor="confirmPassword">Confirm Password</label>
        <input
          id="confirmPassword"
          name="confirmPassword"
          type="password"
          value={confirmPassword}
          onChange={(e) => setConfirmPassword(e.target.value)}
          placeholder="Confirm Password"
          aria-describedby="confirm-password-desc"
        />
        <span id="confirm-password-desc">Confirm your password</span>

        <button type="submit" disabled={loading}>
          Register
        </button>
      </form>
      <p>
        Already have an account? <Link to="/login">Login</Link>
      </p>
    </div>
  );
}

export default Register;
