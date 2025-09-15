import { Link } from 'react-router-dom';
import "./Navbar.css";

function Navbar() {
  return (
    <nav className="navbar">
      <h1 className="navbar-logo">Taskwell</h1>
      <ul className="navbar-links">
        <li><Link to="/dashboard" className="navbar-link">Dashboard</Link></li>
        <li><Link to="/tasks" className="navbar-link">Task List</Link></li>
        <li><Link to="/tasks/new" className="navbar-link">New Task</Link></li>
        <li><Link to="/profile" className="navbar-link">Profile</Link></li>
        <li><Link to="/logout" className="navbar-link">Logout</Link></li>
      </ul>
    </nav>
  );
}

export default Navbar;