import { Link } from 'react-router-dom';
import "./Navbar.css";

function Navbar() {
  return (
    <nav className="navbar" role="navigation" aria-label="Main Navigation">
      <h1 className="navbar-logo">Taskwell</h1>
      <ul className="navbar-links" role="menubar">
        <li role="menuitem"><Link to="/dashboard" className="navbar-link">Dashboard</Link></li>
        <li role="menuitem"><Link to="/tasks" className="navbar-link">Task List</Link></li>
        <li role="menuitem"><Link to="/tasks/new" className="navbar-link">New Task</Link></li>
        <li role="menuitem"><Link to="/profile" className="navbar-link">Profile</Link></li>
        <li role="menuitem"><Link to="/logout" className="navbar-link">Logout</Link></li>
      </ul>
    </nav>
  );
}

export default Navbar;