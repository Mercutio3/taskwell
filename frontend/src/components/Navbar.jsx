import { Link } from 'react-router-dom';

function Navbar() {
  return (
    <nav>
      <h1>Taskwell</h1>
      <ul>
        <li><Link to="/dashboard">Dashboard</Link></li>
        <li><Link to="/tasks">Task List</Link></li>
        <li><Link to="/tasks/new">New Task</Link></li>
        <li><Link to="/profile">Profile</Link></li>
        <li><Link to="/logout">Logout</Link></li>
      </ul>
    </nav>
  );
}

export default Navbar;